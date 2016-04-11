package com.example.service;

import com.example.pojo.DeleteRequest;
import com.example.pojo.Message;
import com.example.pojo.PullRequest;
import com.example.pojo.PushRequest;

import java.util.List;

/**
 * Queue Service interface.
 * 
 * @author Swarn Avinash Kumar
 */
public interface QueueService {

    /**
     * Pushes a single message onto a specified queue. A queue strives to deliver each message exactly once to
     * exactly one consumer, but guarantees at-least once delivery
     *
     * @param request the request
     * @return the message body in MD5
     */
    String push(PushRequest request);

    /**
     * Receives a single message from a specified queue. When a consumer receives a message, it is not removed
     * from the queue. Instead, it is temporarily suppressed (becomes "invisible"). If the consumer that
     * received the message does not subsequently delete it within within a timeout period (the
     * "visibility timeout"), the message automatically becomes visible at the head of the queue again, ready
     * to be delivered to another consumer.
     *
     * @param request the request
     * @return the list
     */
    List<Message> pull(PullRequest request);

    /**
     * 
     * Deletes the scheduled task to re-add the message to the queue.
     *
     * @param request the request
     */
    void delete(DeleteRequest request);

}
