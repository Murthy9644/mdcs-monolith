/*
Legacy implementation of worker -> master communication.

Kept temporarily for future reference while the new model is under development or changes are
being done.

DO NOT use in new code.
*/

package com.mdcs.shared.archive.postals;

import java.util.concurrent.CountDownLatch;

// Contains DTOs for worker <-> master message passing

/**
 * workers never communicate directly with each other. These communications are managed by the
 * master if required.
 */

public class Envelope {

    /**
     * 'Mail' is a real-time Data Transfer Object (DTO) between a worker and the master. It is
     * used to pass messages from workers to master and vice-versa.
     *     
     *     mail_id -> unique identifier for each Mail (same for each thread sending different
     *     mails)
     *     latch   -> allows sender thread to block until master finishes processing
     * 
     * latch behavior: Sender would set the latch count to 1 initially and when master is done 
     * processing the mail/request, latch would be set to 0 (decrement) and then the worker
     * continuous.
     */
    public static abstract class Mail{
        public long sender_id;
        public CountDownLatch latch = new CountDownLatch(1);
    }

    public static class Print extends Mail{
        public String line;
    }
}
