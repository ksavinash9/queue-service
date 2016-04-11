package com.example.pojo;

import static com.google.common.base.Preconditions.*;

/**
 * The Pull Request encapsulated in a object Data Transfer Object.
 * 
 * @author Swarn Avinash Kumar
 */
public class PullRequest extends QueueRequest {

    private Integer maxNumberOfMessages;
    private Integer visibilityTimeout;
    private Integer waitTimeSeconds;

    /**
     * The constructor. All mandatory dependencies set here.
     *
     * @param queueUrl the queue url
     * @author Swarn Avinash Kumar
     */
    public PullRequest(final String queueUrl) {
        checkArgument(queueUrl != null);
        this.setQueueUrl(queueUrl);
        this.maxNumberOfMessages = 1;
    }

    /**
     * Gets the max number of messages.
     *
     * @return the max number of messages
     * @author Swarn Avinash Kumar
     */
    public Integer getMaxNumberOfMessages() {
        return maxNumberOfMessages;
    }

    /**
     * Sets the max number of messages.
     *
     * @param maxNumberOfMessages the new max number of messages
     * @author Swarn Avinash Kumar
     */
    public void setMaxNumberOfMessages(Integer maxNumberOfMessages) {
        this.maxNumberOfMessages = maxNumberOfMessages;
    }

    /**
     * Gets the visibility timeout.
     *
     * @return the visibility timeout
     * @author Swarn Avinash Kumar
     */
    public Integer getVisibilityTimeout() {
        return visibilityTimeout;
    }

    /**
     * Sets the visibility timeout.
     *
     * @param visibilityTimeout the new visibility timeout
     * @author Swarn Avinash Kumar
     */
    public void setVisibilityTimeout(Integer visibilityTimeout) {
        this.visibilityTimeout = visibilityTimeout;
    }

    /**
     * Gets the wait time seconds.
     *
     * @return the wait time seconds
     * @author Swarn Avinash Kumar
     */
    public Integer getWaitTimeSeconds() {
        return waitTimeSeconds;
    }

    /**
     * Sets the wait time seconds.
     *
     * @param waitTimeSeconds the new wait time seconds
     * @author Swarn Avinash Kumar
     */
    public void setWaitTimeSeconds(Integer waitTimeSeconds) {
        this.waitTimeSeconds = waitTimeSeconds;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((maxNumberOfMessages == null) ? 0 : maxNumberOfMessages.hashCode());
        result = prime * result + ((visibilityTimeout == null) ? 0 : visibilityTimeout.hashCode());
        result = prime * result + ((waitTimeSeconds == null) ? 0 : waitTimeSeconds.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PullRequest other = (PullRequest) obj;
        if (maxNumberOfMessages == null) {
            if (other.maxNumberOfMessages != null) {
                return false;
            }
        } else if (!maxNumberOfMessages.equals(other.maxNumberOfMessages)) {
            return false;
        }
        if (visibilityTimeout == null) {
            if (other.visibilityTimeout != null) {
                return false;
            }
        } else if (!visibilityTimeout.equals(other.visibilityTimeout)) {
            return false;
        }
        if (waitTimeSeconds == null) {
            if (other.waitTimeSeconds != null) {
                return false;
            }
        } else if (!waitTimeSeconds.equals(other.waitTimeSeconds)) {
            return false;
        }
        return true;
    }

}
