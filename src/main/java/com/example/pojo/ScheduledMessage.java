package com.example.pojo;

import java.util.concurrent.ScheduledFuture;

/**
 * This class allows us to implement visibility timeouts in Queue Service.
 * 
 * @author Swarn Avinash Kumar
 */
public class ScheduledMessage {

    private ScheduledFuture future;
    private Runnable runnable;

    /**
     * Instantiates a new scheduled message.
     *
     * @param future the future
     * @param runnable the runnable
     * @author Swarn Avinash Kumar
     */
    public ScheduledMessage(final ScheduledFuture future, final Runnable runnable) {
        this.future = future;
        this.runnable = runnable;
    }

    /**
     * Cancel future.
     *
     * @param mayInterruptIfRunning
     * @author Swarn Avinash Kumar
     */
    public void cancelFuture(boolean mayInterruptIfRunning) {
        this.future.cancel(mayInterruptIfRunning);
    }
}