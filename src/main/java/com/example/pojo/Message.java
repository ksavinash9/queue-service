package com.example.pojo;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import java.util.UUID;

/**
 * The Message Plain old java object class.
 * 
 * @author Swarn Avinash Kumar
 */
public class Message {

    private final String id;
    private final String body;
    private final String md5Body;
    private String receiptHandle;

    /**
     * The constructor. All mandatory dependencies set here. Use setter of receipt handle if needed.
     *
     * @param id the id
     * @param body the body
     * @author Swarn Avinash Kumar
     */
    public Message(final String id, final String body) {
        checkArgument(id != null);
        checkArgument(body != null);
        this.id = id;
        this.body = body;
        this.md5Body = Hashing.md5().hashString(body, Charsets.UTF_8).toString();
    }

    /**
     * Instantiates a new message.
     *
     * @param String body
     * @author Swarn Avinash Kumar
     */
    public Message(final String body) {
        checkArgument(body != null);
        this.id = UUID.randomUUID().toString();
        this.body = body;
        this.md5Body = Hashing.md5().hashString(body, Charsets.UTF_8).toString();
    }

    /**
     * Sets the receipt handle.
     *
     * @param receiptHandle the new receipt handle
     * @author Swarn Avinash Kumar
     */
    public void setReceiptHandle(String receiptHandle) {
        this.receiptHandle = receiptHandle;
    }

    /**
     * Gets the body.
     *
     * @return the body
     * @author Swarn Avinash Kumar
     */
    public String getBody() {
        return body;
    }

    /**
     * Gets the id.
     *
     * @return the id
     * @author Swarn Avinash Kumar
     */
    public String getId() {
        return id;
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

    public String getMd5Body() {
        return md5Body;
    }

    @Override
    public String toString() {
        return this.body;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((body == null) ? 0 : body.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((receiptHandle == null) ? 0 : receiptHandle.hashCode());
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
        Message other = (Message) obj;
        if (body == null) {
            if (other.body != null) {
                return false;
            }
        } else if (!body.equals(other.body)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
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
