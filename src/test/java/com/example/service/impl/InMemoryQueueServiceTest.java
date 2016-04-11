package com.example.service.impl;

import com.example.pojo.DeleteRequest;
import com.example.pojo.Message;
import com.example.pojo.PullRequest;
import com.example.pojo.PushRequest;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Unit tests for {@link InMemoryQueueService}.
 * 
 * @author Swarn Avinash Kumar
 */
public class InMemoryQueueServiceTest {

    /** Object to be tested */
    private InMemoryQueueService service;

    /** Utility constants */
    private static final String QUEUE_URL = "localhost";
    private static final String MESSAGE_BODY = "Message Body for Test";

    /**
     * Cleaning the target on each test.
     *
     * @throws Exception the exception
     * @author Swarn Avinash Kumar
     */
    @Before
    public void setUp() throws Exception {
        this.service = new InMemoryQueueService();
    }

    /**
     * Push() happy path should increase queue size in 1.
     * @author Swarn Avinash Kumar
     */
    @Test
    public final void givenHappyPath_push_shouldIncreaseQueueSize() {
        assertEquals(0, service.getQueueInstance(QUEUE_URL).size());
        pushDefaultMessage();
        assertEquals(1, service.getQueueInstance(QUEUE_URL).size());
        assertEquals(MESSAGE_BODY, service.getQueueInstance(QUEUE_URL).peek().getBody());
    }

    /**
     * Given null request, push() should throw IllegalArgumentException.
     * @author Swarn Avinash Kumar
     */
    @Test(expected = IllegalArgumentException.class)
    public final void givenNullRequest_push_shouldThrowIllegalArgumentException() {
        service.push(null);
    }

    /**
     * Given happy path, pull(), should return a list of messages.
     * @author Swarn Avinash Kumar
     */
    @Test
    public final void givenHappyPath_pull_shouldReturnListOfMessages() {
        // Given
        pushDefaultMessage();
        pushDefaultMessage();
        // When
        PullRequest request = new PullRequest(QUEUE_URL);
        request.setMaxNumberOfMessages(10);
        List<Message> messages = service.pull(request);
        // Then
        assertEquals(2, messages.size());
        assertEquals(MESSAGE_BODY, messages.get(0).getBody().toString());
    }

    /**
     * 
     * Here we test pulling from the Queue and deleting before visibility timeout
     * Then, we must check that pulled message is not going back to the queue
     * 
     * @throws InterruptedException
     */
    @Test
    public final void givenVisibilityTimeoutWithDelete_pull_shouldNotReturnBackTheMessageToQueue()
            throws InterruptedException {
        // Given
        pushDefaultMessage();
        assertEquals(1, service.getQueueInstance(QUEUE_URL).size());
        // When
        PullRequest pullRequest = new PullRequest(QUEUE_URL);
        pullRequest.setVisibilityTimeout(10);
        List<Message> messages = service.pull(pullRequest);
        assertEquals(1, messages.size());
        Message message = messages.get(0);
        DeleteRequest deleteRequest = new DeleteRequest(QUEUE_URL, message.getReceiptHandle());
        service.delete(deleteRequest);
        // Then
        assertTrue(service.getQueueInstance(QUEUE_URL).isEmpty());
    }
    
    @Test
    public final void givenMultiplePullAndVisibilityTimeoutWithDelete_pull_shouldNotReturnBackMessagesToQueue()
            throws InterruptedException {
        // Given
        pushDefaultMessage();
        pushDefaultMessage();
        pushDefaultMessage();
        assertEquals(3, service.getQueueInstance(QUEUE_URL).size());
        // When
        PullRequest pullRequest = new PullRequest(QUEUE_URL);
        pullRequest.setVisibilityTimeout(10);
        pullRequest.setMaxNumberOfMessages(3);
        List<Message> messages = service.pull(pullRequest);
        assertEquals(3, messages.size());
        Message message1 = messages.get(0);
        DeleteRequest deleteRequest1 = new DeleteRequest(QUEUE_URL, message1.getReceiptHandle());
        Message message2 = messages.get(1);
        DeleteRequest deleteRequest2 = new DeleteRequest(QUEUE_URL, message2.getReceiptHandle());
        Message message3 = messages.get(2);
        DeleteRequest deleteRequest3 = new DeleteRequest(QUEUE_URL, message3.getReceiptHandle());
        service.delete(deleteRequest1);
        service.delete(deleteRequest2);
        service.delete(deleteRequest3);
        // Then
        assertTrue(service.getQueueInstance(QUEUE_URL).isEmpty());
    }

    /**
     * Given null request, pull(), should throw IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void givenNullRequest_pull_shouldThrowIllegalArgumentException() {
        service.pull(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void givenMaxMessagesExceeded_pull_shouldThrowIllegalArgumentException() {
        // Given
        pushDefaultMessage();
        // When
        PullRequest request = new PullRequest(QUEUE_URL);
        request.setMaxNumberOfMessages(9999);
        service.pull(request);
    }

    /**
     * Given path - delete(), should decrease queue size.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public final void givenHappyPath_delete_shouldDecreaseQueueSize() throws InterruptedException {
        pushDefaultMessage();
        assertEquals(1, service.getQueueInstance(QUEUE_URL).size());
        List<Message> messages = service.pull(new PullRequest(QUEUE_URL));
        service.delete(new DeleteRequest(QUEUE_URL, messages.get(0).getReceiptHandle()));
        assertTrue(service.getQueueInstance(QUEUE_URL).isEmpty());
    }

    /**
     * Given null request, delete() should throw IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void givenNullRequest_delete_shouldThrowIllegalArgumentException() {
        service.delete(null);
    }

    /**
     * Push default message.
     */
    private void pushDefaultMessage() {
        service.push(new PushRequest(QUEUE_URL, MESSAGE_BODY));
    }

    /**
     * 
     * Here we test pulling from the Queue and waiting for the timeout.
     * Then, we must see the message back to the queue
     * 
     * @throws InterruptedException
     */
    @Test
    public final void givenVisibilityTimeoutWithoutDelete_pull_shouldReturnBackTheMessageToQueue()
            throws InterruptedException {
        // Given
        pushDefaultMessage();
        assertEquals(1, service.getQueueInstance(QUEUE_URL).size());
        // When
        PullRequest request = new PullRequest(QUEUE_URL);
        // One second visibility timeout
        request.setVisibilityTimeout(1);
        service.pull(request);
        // We have to wait two seconds so the message is pushed back to the queue
        Thread.sleep(2000);
        // Then
        assertEquals(1, service.getQueueInstance(QUEUE_URL).size());
        assertEquals(MESSAGE_BODY, service.getQueueInstance(QUEUE_URL).peek().getBody());
    }
    
    @Test
    public final void givenMultiplePullAndVisibilityTimeoutWithoutDelete_pull_shouldReturnBackMessagesToQueue()
            throws InterruptedException {
        // Given
        pushDefaultMessage();
        pushDefaultMessage();
        assertEquals(2, service.getQueueInstance(QUEUE_URL).size());
        // When
        PullRequest request = new PullRequest(QUEUE_URL);
        request.setMaxNumberOfMessages(2);
        // One second visibility timeout
        request.setVisibilityTimeout(1);
        service.pull(request);
        // We have to wait two seconds so the message is pushed back to the queue
        Thread.sleep(2000);
        // Then
        assertEquals(2, service.getQueueInstance(QUEUE_URL).size());
        assertEquals(MESSAGE_BODY, service.getQueueInstance(QUEUE_URL).peek().getBody());
    }

}
