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

/**
 * In-memory Message Queue Service Implementation. 
 *
 * @author Swarn Avinash Kumar
 * 
 */
public class InMemoryQueueService implements QueueService {

    /** This map stores all different queues indexed by URL */
    private final Map<String, Queue<Message>> queueMap;

    /** This object will be used to schedule visibility timeout and push delay commands. */
    private final ScheduledExecutorService executor;

    private Integer visibilityTimeout;

    /** We will use this map to store Future objects that may have to be restored to the queue */
    private final Map<String, ScheduledMessage> receivedMessages;

    // This object helps us to simulate a container of values to be injected (since we cannot use DI libraries)
    private final Environment environment = Environment.getInstance();

    /**
     * Instantiates a new in-memory queue service.
     */
    public InMemoryQueueService() {
        this.visibilityTimeout = Integer.valueOf(environment.getPropertyValue(QUEUE_VISIBILITY_TIMEOUT));
        this.executor =
                Executors.newScheduledThreadPool(Integer.valueOf(environment
                        .getPropertyValue(SCHEDULED_THREAD_POOL_SIZE)));

        this.queueMap = new HashMap<>();
        this.receivedMessages = new HashMap<>();
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
        final Queue<Message> queue = this.getQueueInstance(request.getQueueUrl());
        final String body = request.getMessageBody();
        final Message message = new Message(body);

        if (request.getDelaySeconds() == null) {
            queue.add(message);
        } else {
            Runnable task = new Runnable() {
                public void run() {
                    queue.add(message);
                }
            };
            executor.schedule(task, request.getDelaySeconds(), TimeUnit.SECONDS);
        }

        return message.getMd5Body();
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
    public List<Message> pull(final PullRequest request) {
        checkArgument(request != null);
        final Queue<Message> queue = this.getQueueInstance(request.getQueueUrl());
        List<Message> messages = new ArrayList<>();

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
            final Message pulledMessage = queue.poll();

            if (pulledMessage != null) {
                final String receiptHandle = UUID.randomUUID().toString();

                Message message = new Message(pulledMessage.getId(), pulledMessage.getBody());
                message.setReceiptHandle(receiptHandle);
                messages.add(message);

                Runnable command = new Runnable() {
                    public void run() {
                        queue.add(pulledMessage);
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
     * 
     * @see com.example.service.QueueService#delete(com.example.pojo.DeleteRequest)
     * @author Swarn Avinash Kumar
     */
    public void delete(final DeleteRequest request) {
        checkArgument(request != null);
        ScheduledMessage scheduled = receivedMessages.remove(request.getReceiptHandle());
        checkState(scheduled != null, "The message doesn't exist");
        scheduled.cancelFuture(true);
    }

    /**
     * Gets a queue instance given a URL.
     *
     * @param url the url
     * @return the queue instance
     * @author Swarn Avinash Kumar
     */
    @VisibleForTesting
    protected Queue<Message> getQueueInstance(final String url) {
        checkArgument(url != null);
        Queue<Message> queue = queueMap.get(url);

        // Double Check Strategy for concurrent access
        if (queue == null) {
            synchronized (queueMap) {
                queue = queueMap.get(url);
                if (queue == null) {
                    queue = new LinkedBlockingDeque<>();
                    queueMap.put(url, queue);
                }
            }
        }

        return queue;
    }

}