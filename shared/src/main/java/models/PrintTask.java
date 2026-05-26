package models;

import java.util.concurrent.CountDownLatch;

public class PrintTask {
    public String message;
    public CountDownLatch latch;

    public PrintTask(String message, CountDownLatch latch) {
        this.message = message;
        this.latch = latch;
    }
}
