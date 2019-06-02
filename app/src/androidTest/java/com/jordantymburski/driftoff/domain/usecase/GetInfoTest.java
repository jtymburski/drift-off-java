package com.jordantymburski.driftoff.domain.usecase;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.test.annotation.UiThreadTest;

import com.jordantymburski.driftoff.common.ResettableCountDownLatch;
import com.jordantymburski.driftoff.domain.adapter.Storage;
import com.jordantymburski.driftoff.domain.model.AlarmInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GetInfoTest implements Observer<AlarmInfo> {
    /**
     * Get info use case
     */
    private GetInfo mGetInfo;

    /**
     * Initialized info
     */
    private AlarmInfo mInitInfo;

    /**
     * Reset capable count down latch
     */
    private final ResettableCountDownLatch mLock = new ResettableCountDownLatch(1);

    /**
     * The last observed info from the live data subscription
     */
    private AlarmInfo mObservedInfo;

    /**
     * Random number generator instance
     */
    private final Random mRandom = new Random();

    /**
     * Storage mock class
     */
    private Storage mStorage;

    @Rule
    public TestRule syncRule = new InstantTaskExecutorRule();

    @Override
    public void onChanged(AlarmInfo info) {
        mObservedInfo = info;
        mLock.countDown();
    }

    @Before
    public void setup() {
        // Alarm objects
        mInitInfo = new AlarmInfo(
                0L, mRandom.nextInt(24), mRandom.nextInt(60));
        mObservedInfo = null;

        // Lock reset
        mLock.reset();

        // Storage mock
        mStorage = Mockito.mock(Storage.class);
        Mockito.when(mStorage.load()).thenReturn(mInitInfo);

        // GetInfo use case set-up and observe
        mGetInfo = new GetInfo(mStorage);
        mGetInfo.observable().observeForever(this);
    }

    @After
    public void cleanUp() {
        mGetInfo.observable().removeObserver(this);
    }

    @Test
    @UiThreadTest
    public void t1_observe() throws InterruptedException {
        // First update (initial)
        waitForResponse();
        assertNotNull(mObservedInfo);
        assertEquals(mInitInfo, mObservedInfo);

        // No more updates should happen since no data was changed
        assertFalse(mLock.resetAndAwait(2000, TimeUnit.MILLISECONDS));

        // Make sure it did not call anything else in storage
        Mockito.verify(mStorage, Mockito.never()).save(mInitInfo);
    }

    @Test
    @UiThreadTest
    public void t2_current() throws InterruptedException {
        // First update (initial)
        waitForResponse();

        // Fetch the current after it was already observed. Should fetch from live data internally
        assertEquals(mInitInfo, mGetInfo.current());

        // Create a new use case but do not observe. It should still properly fetch
        final AlarmInfo fetchedFreshInfo = new GetInfo(mStorage).current();
        assertNotNull(fetchedFreshInfo);
        assertEquals(mInitInfo, fetchedFreshInfo);
    }

    @Test
    @UiThreadTest
    public void t3_post() throws InterruptedException {
        // First update (initial)
        waitForResponse();

        // New alarm info. Post it and wait for the observe update
        final AlarmInfo newInfo = new AlarmInfo(
                new Date().getTime(), mRandom.nextInt(24), mRandom.nextInt(60));
        mLock.reset();
        mGetInfo.post(newInfo);
        waitForResponse();

        // Check result
        assertNotNull(mObservedInfo);
        assertEquals(newInfo, mObservedInfo);

        // Try and post on a fresh object. This should do nothing but NOT crash
        new GetInfo(mStorage).post(newInfo);
    }

    /**
     * Waits for a response from the observer for a fixed period of time
     * @throws InterruptedException exception thrown if thread requested to shut down
     */
    private void waitForResponse() throws InterruptedException {
        assertTrue(mLock.await(5000, TimeUnit.MILLISECONDS));
    }
}
