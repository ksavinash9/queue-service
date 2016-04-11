package com.example.pojo;

import static com.google.common.base.Preconditions.*;

/**
 * The Delete Request encapsulated in a Data Transfer Object.
 * 
 * @author Swarn Avinash Kumar
 */
public class DeleteRequest extends QueueRequest {

    private String receiptHandle;

    /**
     * The constructor. All dependencies set here.
     *
     * @param queueUrl the queue url
     * @param receiptHandle the receipt handle
     * @author Swarn Avinash Kumar
     */
    public DeleteRequest(final String queueUrl, final String receiptHandle) {
        checkArgument(queueUrl != null);
        checkArgument(receiptHandle != null);
        this.setQueueUrl(queueUrl);
        this.receiptHandle = receiptHandle;
    }

    /**
     * Gets the receipt handle.
     *
     * @return the receipt handle
     * @author Swarn Avinash Kumar
     */
    public String getReceiptHandle() {
        return receiptHandle;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((receiptHandle == null) ? 0 : receiptHandle.hashCode());
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
        DeleteRequest other = (DeleteRequest) obj;
        if (receiptHandle == null) {
            if (other.receiptHandle != null) {
                return false;
            }
        } else if (!receiptHandle.equals(other.receiptHandle)) {
            return false;
        }
        return true;
    }

}
