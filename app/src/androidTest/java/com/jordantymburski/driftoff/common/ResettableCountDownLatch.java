package com.jordantymburski.driftoff.common;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ResettableCountDownLatch {

    private final int initialCount;
    private volatile CountDownLatch latch;

    public ResettableCountDownLatch(int  count) {
        initialCount = count;
        reset();
    }

    public void countDown() {
        latch.countDown();
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return latch.await(timeout, unit);
    }

    public void reset() {
        latch = new CountDownLatch(initialCount);
    }

    public boolean resetAndAwait(long timeout, TimeUnit unit) throws InterruptedException {
        reset();
        return await(timeout, unit);
    }
}