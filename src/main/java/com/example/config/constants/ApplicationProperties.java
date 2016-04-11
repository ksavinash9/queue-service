package com.example.config.constants;

/**
 * Hard-coding application properties for Queue Service
 * 
 * @author Swarn Avinash Kumar
 */
public final class ApplicationProperties {

    public static final String APPLICATION_PROPERTIES_FILENAME = "application.properties";

    public static final String QUEUE_VISIBILITY_TIMEOUT = "queue.visibility.timeout";

    public static final String SCHEDULED_THREAD_POOL_SIZE = "scheduled.thread.pool.size";

    public static final String MAX_MESSAGES_FROM_PULL = "max.number.messages.pull";

    /**
     * Private Constructor to restrict the instantiation of this class
     */
    private ApplicationProperties() {

    }

}
