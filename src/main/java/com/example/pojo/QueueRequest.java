package com.example.pojo;

/**
 * Abstract class Queue Request. 
 * Superclass of all Queue requests
 * 
 * @author Swarn Avinash Kumar
 */
public abstract class QueueRequest {

    private String queueUrl;

    /**
     * Gets the queue url.
     *
     * @return the queue url
     * @author Swarn Avinash Kumar
     */
    public String getQueueUrl() {
        return queueUrl;
    }

    protected void setQueueUrl(final String queueUrl) {
        this.queueUrl = queueUrl;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((queueUrl == null) ? 0 : queueUrl.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        QueueRequest other = (QueueRequest) obj;
        if (queueUrl == null) {
            if (other.queueUrl != null) {
                return false;
            }
        } else if (!queueUrl.equals(other.queueUrl)) {
            return false;
        }
        return true;
    }

}
