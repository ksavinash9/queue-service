package com.example.service.impl;

import static com.example.config.constants.ApplicationProperties.*;
import static com.google.common.base.Preconditions.*;

import com.example.config.Environment;
import com.example.pojo.DeleteRequest;
import com.example.pojo.Message;
import com.example.pojo.PullRequest;
import com.example.pojo.PushRequest;
import com.example.pojo.ScheduledMessage;
import com.example.service.QueueService;
import com.google.common.annotations.VisibleForTesting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * File-based implementation of QueueService.
 * Not a complete implementation and rather hacky code. 
 * 
 * @author Swarn Avinash Kumar
 */
public class FileQueueService implements QueueService {

    /** This object is File which will store the entire queue */
    private final File queue;

    /** The object is a String will stores the name of the queue */
    private final String queueName = "diskQueue";

    /** This object will be used to schedule visibility timeout and push delay commands. */
    private final ScheduledExecutorService executor;

    private Integer visibilityTimeout;

    /** We will use this map to store Future objects that may have to be restored to the queue */
    private final Map<String, ScheduledMessage> receivedMessages;

    /**  This object helps us to simulate a container of values to be injected (since we cannot use DI libraries) */
    private final Environment environment = Environment.getInstance();

    /** The object writes the queue in a File */
    private final Writer output;

    /** The object stores the filepath */
    private final String filePath;

    @Override
    public FileQueue() {
        this.filePath = "sqs/";
        this.queue = new File(filePath + queueName);
        try {
            this.queue.createNewFile();
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    /**
     * Pushes a single message onto a specified queue. A queue strives to deliver each message exactly once to
     * exactly one consumer, but guarantees at-least once delivery
     *
     * @param request the request
     * @return the message body in MD5
     *
     * @see com.example.service.QueueService#push(com.example.pojo.PushRequest)
     * @author Swarn Avinash Kumar
     */
    public String push(final PushRequest request) {
        checkArgument(request != null);
        final String body = request.getMessageBody();
        final String messageId = UUID.randomUUID().toString();
        final String combinedMessage = messageId + "$" + body + System.lineSeparator();

        if (request.getDelaySeconds() == null) {
            addQueue(message);
        } else {
            Runnable task = new Runnable() {
                public void run() {
                    addQueue(combinedMessage);
                }
            };
            executor.schedule(task, request.getDelaySeconds(), TimeUnit.SECONDS);
        }

        return combinedMessage;
    }

    /**
     *
     * Receives a single message from a specified queue. When a consumer receives a message, it is not removed
     * from the queue. Instead, it is temporarily suppressed (becomes "invisible"). If the consumer that
     * received the message does not subsequently delete it within within a timeout period (the
     * "visibility timeout"), the message automatically becomes visible at the head of the queue again, ready
     * to be delivered to another consumer.
     *
     * @param request the request
     * @return the list
     *
     * @see com.example.service.QueueService#pull(com.example.pojo.PullRequest) 
     * @author Swarn Avinash Kumar
     */
    @Override
    public List<Message> pull(final PullRequest request) {
        checkArgument(request != null);
        List<Message> messages = new ArrayList<>();

        File tempFile = new File(filePath + "temporaryFile");
        File inputFile = new File(filePath + queueName);

        Integer max = request.getMaxNumberOfMessages();
        if (max == null) {
            max = 0;
        }
        
        checkArgument(max <= Integer.valueOf(environment.getPropertyValue(MAX_MESSAGES_FROM_PULL)) && max > 0);

        Integer visibilityTimeout = request.getVisibilityTimeout();
        if (visibilityTimeout == null) {
            visibilityTimeout = this.visibilityTimeout;
        }

        for (int i = 0; i < max; ++i) {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String firstLine = reader.readLine();
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                writer.write(currentLine + System.lineSeparator());
            }
            writer.close();
            reader.close();

            boolean successful = tempFile.renameTo(inputFile);
            String[] firstLineSplitonDelimeter = firstLine.split("\\$");
            String messageId = firstLineSplitonDelimeter[0];
            String messageBody = firstLineSplitonDelimeter[1].trim();
            Message message = new Message(messageId, messageBody);

            if (message != null) {
                final String receiptHandle = UUID.randomUUID().toString();
                message.setReceiptHandle(receiptHandle);
                messages.add(message);

                Runnable command = new Runnable() {
                    public void run() {
                        reAddQueue(firstLine);
                        receivedMessages.remove(receiptHandle);
                    }
                };

                ScheduledFuture future = executor.schedule(command, visibilityTimeout, TimeUnit.SECONDS);

                ScheduledMessage scheduled = new ScheduledMessage(future, command);

                receivedMessages.put(message.getReceiptHandle(), scheduled);
            }
        }

        return messages;
    }

    /**
     * 
     * Deletes the scheduled task to re-add the message to the queue.
     *
     * @param request the request
     *
     * @see com.example.service.QueueService#delete(com.example.pojo.DeleteRequest)
     * @author Swarn Avinash Kumar
     */
    @Override
    public void delete(DeleteRequest request) {
        checkArgument(request != null);
        ScheduledMessage scheduled = receivedMessages.remove(request.getReceiptHandle());
        checkState(scheduled != null, "The message doesn't exist");
        scheduled.cancelFuture(true);
        // TODO Auto-generated method stub
    }

    /**
    * Adds a line in the queue.
    *
    * @param string message
    * @return nil
    * @author Swarn Avinash Kumar
    */
    @VisibleForTesting
    protected void addQueue(final String message) {
        checkArgument(message != null);

        output = new BufferedWriter(new FileWriter(filePath + queueName, true));
        output.append(message);
        output.close();

        return;
    }
    /**
    * Adds the pulled message back into the queue in case of timeout
    *
    * @param string message
    * @return nil
    * @author Swarn Avinash Kumar
    */
    protected void reAddQueue(final String message) {
        File tempFile = new File(filePath + "temporaryFile");
        File inputFile = new File(filePath + queueName);

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String firstLine = reader.readLine();
        String currentLine;

        writer.write(message + System.lineSeparator());

        while ((currentLine = reader.readLine()) != null) {
            writer.write(currentLine + System.lineSeparator());
        }
        writer.close();
        reader.close();

        tempFile.renameTo(inputFile);

        return;
    }
}
