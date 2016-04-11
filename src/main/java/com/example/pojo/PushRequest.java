package com.example.pojo;

import static com.google.common.base.Preconditions.*;

/**
 * The Push Request encapsulated in a object Data Transfer Object.
 * 
 * @author Swarn Avinash Kumar
 */
public class PushRequest extends QueueRequest {

    private String messageBody;
    private Integer delaySeconds;

    /**
     * The constructor. All mandatory dependencies set here.
     *
     * @param queueUrl the queue url
     * @param messageBody the message body
     * @author Swarn Avinash Kumar
     */
    public PushRequest(final String queueUrl, final String messageBody) {
        checkArgument(queueUrl != null);
        checkArgument(messageBody != null);
        this.setQueueUrl(queueUrl);
        this.messageBody = messageBody;
    }

    /**
     * Gets the message body.
     *
     * @return the message body
     * @author Swarn Avinash Kumar
     */
    public String getMessageBody() {
        return messageBody;
    }

    /**
     * Gets the delay seconds.
     *
     * @return the delay seconds
     * @author Swarn Avinash Kumar
     */
    public Integer getDelaySeconds() {
        return delaySeconds;
    }

    /**
     * Sets the delay seconds.
     *
     * @param delaySeconds the new delay seconds
     * @author Swarn Avinash Kumar
     */
    public void setDelaySeconds(Integer delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((delaySeconds == null) ? 0 : delaySeconds.hashCode());
        result = prime * result + ((messageBody == null) ? 0 : messageBody.hashCode());
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
        PushRequest other = (PushRequest) obj;
        if (delaySeconds == null) {
            if (other.delaySeconds != null) {
                return false;
            }
        } else if (!delaySeconds.equals(other.delaySeconds)) {
            return false;
        }
        if (messageBody == null) {
            if (other.messageBody != null) {
                return false;
            }
        } else if (!messageBody.equals(other.messageBody)) {
            return false;
        }
        return true;
    }

}
