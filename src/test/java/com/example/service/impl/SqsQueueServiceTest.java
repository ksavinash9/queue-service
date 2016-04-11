package com.example.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageResult;

import com.example.pojo.DeleteRequest;
import com.example.pojo.Message;
import com.example.pojo.PullRequest;
import com.example.pojo.PushRequest;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * Unit tests for {@link SqsQueueService}.
 */
public class SqsQueueServiceTest {

    /** Object to be tested */
    @InjectMocks
    private SqsQueueService sqsService;

    /** Dependency mock */
    @Mock
    private AmazonSQSClient amazonClient;

    /** Utility constants */
    private static final String QUEUE_URL = "localhost";
    private static final String MESSAGE_BODY = "Message Body for Test";
    private static final String MESSAGE_BODY_MD5 = Hashing.md5().hashString(MESSAGE_BODY, Charsets.UTF_8).toString();

    /**
     * Cleaning the target on each test.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Testing push()
     */
    @Test
    public final void givenHappyPath_push_shouldReturnMessageWithMD5OfBody() {
        PushRequest requestMock = mock(PushRequest.class);
        SendMessageResult result = new SendMessageResult();
        result.setMD5OfMessageBody(MESSAGE_BODY_MD5);
        // Given
        given(amazonClient.sendMessage(anyString(), anyString())).willReturn(result);
        // When
        String md5Body = sqsService.push(requestMock);
        // Then
        assertEquals(MESSAGE_BODY_MD5, md5Body);
        verify(amazonClient).sendMessage(anyString(), anyString());
    }

        /**
     * Testing pull()
     */
    @Test
    public final void givenHappyPath_pull_shouldReturnListOfMessages() {
        PullRequest request = new PullRequest(QUEUE_URL);
        Collection<com.amazonaws.services.sqs.model.Message> sqsMessages = new ArrayList<>();
        com.amazonaws.services.sqs.model.Message message = new com.amazonaws.services.sqs.model.Message();
        message.setMessageId(UUID.randomUUID().toString());
        message.setBody(MESSAGE_BODY);
        sqsMessages.add(message);
        ReceiveMessageResult result = new ReceiveMessageResult();
        result.setMessages(sqsMessages);
        // Given
        given(amazonClient.receiveMessage(QUEUE_URL)).willReturn(result);
        // When
        List<Message> messages = sqsService.pull(request);
        // Then
        assertEquals(MESSAGE_BODY, messages.get(0).getBody());
        verify(amazonClient).receiveMessage(QUEUE_URL);

    }

    /**
     * Testing bad pull request params.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void givenNullRequest_pull_shouldThrowIllegalArgumentException() {
        // When
        try {
            sqsService.pull(null);
        } finally {
            verify(amazonClient, never()).receiveMessage(anyString());

        }
    }

    /**
     * Testing bad push request params.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void givenNullRequest_push_shouldThrowIllegalArgumentException() {
        // When
        try {
            sqsService.push(null);
        } finally {
            verify(amazonClient, never()).sendMessage(anyString(), anyString());

        }
    }

    /**
     * Testing delete()
     */
    @Test
    public final void givenHappyPath_delete_shouldExecuteWithoutErrors() {
        final String receiptHandle = UUID.randomUUID().toString();
        DeleteRequest request = new DeleteRequest(QUEUE_URL, receiptHandle);
        // Given
        doNothing().when(amazonClient).deleteMessage(QUEUE_URL, receiptHandle);
        // When
        sqsService.delete(request);
        // Then
        verify(amazonClient).deleteMessage(QUEUE_URL, receiptHandle);
    }

    /**
     * Testing bad delete request params.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void givenNullRequest_delete_shouldThrowIllegalArgumentException() {
        // When
        try {
            sqsService.delete(null);
        } finally {
            verify(amazonClient, never()).deleteMessage(anyString(), anyString());

        }
    }
}
