package com.example.service.impl;

import static com.google.common.base.Preconditions.*;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.example.pojo.DeleteRequest;
import com.example.pojo.Message;
import com.example.pojo.PullRequest;
import com.example.pojo.PushRequest;
import com.example.service.QueueService;

import java.util.ArrayList;
import java.util.List;

/**
 * Production implementation of QueueService using AWS SQS.
 * 
 * @author Swarn Avinash Kumar
 */
public class SqsQueueService implements QueueService {

    private AmazonSQSClient sqsClient;

    public SqsQueueService(AmazonSQSClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    /**
     * Pushes a single message onto a specified queue. A queue strives to deliver each message exactly once to
     * exactly one consumer, but guarantees at-least once delivery
     *
     * @param request the request
     * @return the message body in MD5
     *
     * @see com.example.service.QueueService#push(com.example.pojo.PushRequest)
     */
    @Override
    public String push(final PushRequest request) {
        checkArgument(request != null);
        SendMessageResult result = this.sqsClient.sendMessage(request.getQueueUrl(), request.getMessageBody());
        return result.getMD5OfMessageBody();
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
     */
    @Override
    public List<Message> pull(final PullRequest request) {
        checkArgument(request != null);
        List<Message> messages = new ArrayList<Message>();
        ReceiveMessageResult result = this.sqsClient.receiveMessage(request.getQueueUrl());
        List<com.amazonaws.services.sqs.model.Message> sqsMessages = result.getMessages();
        for (com.amazonaws.services.sqs.model.Message sqsMessage : sqsMessages) {
            Message message = new Message(sqsMessage.getMessageId(), sqsMessage.getBody());
            messages.add(message);
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
     */
    @Override
    public void delete(final DeleteRequest request) {
        checkArgument(request != null);
        this.sqsClient.deleteMessage(request.getQueueUrl(), request.getReceiptHandle());
    }
}
