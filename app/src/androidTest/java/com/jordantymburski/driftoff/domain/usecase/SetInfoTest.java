package com.jordantymburski.driftoff.domain.usecase;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.test.annotation.UiThreadTest;

import com.jordantymburski.driftoff.common.ResettableCountDownLatch;
import com.jordantymburski.driftoff.domain.adapter.AlarmScheduler;
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

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SetInfoTest implements Observer<AlarmInfo> {
    /**
     * Alarm scheduler mock class
     */
    private AlarmScheduler mAlarmScheduler;

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
     * Set info use case
     */
    private SetInfo mSetInfo;

    /**
     * Storage mock class
     */
    private Storage mStorage;

    @Override
    public void onChanged(AlarmInfo info) {
        mObservedInfo = info;
        mLock.countDown();
    }

    @Rule
    public TestRule syncRule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        // Alarm objects
        mInitInfo = new AlarmInfo(
                0L, mRandom.nextInt(24), mRandom.nextInt(60));
        mObservedInfo = null;

        // Lock reset
        mLock.reset();

        // Alarm scheduler mock
        mAlarmScheduler = Mockito.mock(AlarmScheduler.class);

        // Storage mock
        mStorage = Mockito.mock(Storage.class);
        Mockito.when(mStorage.load()).thenReturn(mInitInfo);

        // GetInfo use case set-up and observe
        mGetInfo = new GetInfo(mStorage);
        mGetInfo.observable().observeForever(this);

        // SetInfo use case set-up
        mSetInfo = new SetInfo(mAlarmScheduler, mGetInfo, mStorage);
    }

    @After
    public void cleanUp() {
        mGetInfo.observable().removeObserver(this);
    }

    @Test
    @UiThreadTest
    public void t1_setTime() throws InterruptedException {
        // First update (initial)
        waitForResponse();

        // Generate a modified time setpoint
        final AlarmInfo newInfo = new AlarmInfo(
                mInitInfo, mRandom.nextInt(24), mRandom.nextInt(60));

        // Set it and wait for the update
        resetState();
        mSetInfo.setTime(newInfo.timeHour, newInfo.timeMinute);
        waitForResponse();

        // Check it
        Thread.sleep(250);
        Mockito.verifyZeroInteractions(mAlarmScheduler);
        Mockito.verify(mStorage).save(newInfo);
        Mockito.verifyNoMoreInteractions(mStorage);
        assertNotNull(mObservedInfo);
        assertEquals(newInfo, mObservedInfo);

        // Generate again
        final AlarmInfo newInfo2 = new AlarmInfo(
                newInfo, mRandom.nextInt(24), mRandom.nextInt(60));

        // Set it and wait for the update
        resetState();
        mSetInfo.setTime(newInfo2.timeHour, newInfo2.timeMinute);
        waitForResponse();

        // Check it
        Thread.sleep(250);
        Mockito.verifyZeroInteractions(mAlarmScheduler);
        Mockito.verify(mStorage).save(newInfo2);
        Mockito.verifyNoMoreInteractions(mStorage);
        assertNotNull(mObservedInfo);
        assertEquals(newInfo2, mObservedInfo);
    }

    @Test
    @UiThreadTest
    public void t2_setAlarm() throws InterruptedException {
        // First update (initial)
        waitForResponse();

        // Make sure that there was no previous alarm set by the before
        assertNotNull(mObservedInfo);
        assertEquals(0L, mObservedInfo.alarm);

        // Set it and wait for the update
        resetState();
        mSetInfo.setAlarm();
        waitForResponse();

        // Generate the expected info and check
        Thread.sleep(250);
        final AlarmInfo freshAlarmInfo = new AlarmInfo(mInitInfo, mInitInfo.getTimeInMillis());
        Mockito.verify(mAlarmScheduler).schedule(freshAlarmInfo.alarm);
        Mockito.verifyNoMoreInteractions(mAlarmScheduler);
        Mockito.verify(mStorage).save(freshAlarmInfo);
        Mockito.verifyNoMoreInteractions(mStorage);
        assertNotNull(mObservedInfo);
        assertEquals(freshAlarmInfo, mObservedInfo);
    }

    @Test
    @UiThreadTest
    public void t3_resetAlarm() throws InterruptedException {
        // First update (initial)
        waitForResponse();

        // Set an alarm
        resetState();
        mSetInfo.setAlarm();
        waitForResponse();

        // Make sure an alarm has been set
        Thread.sleep(250);
        assertNotNull(mObservedInfo);
        assertNotEquals(mInitInfo, mObservedInfo);
        assertTrue(mObservedInfo.alarm > 0L);

        // Reset the alarm
        resetState();
        mSetInfo.resetAlarm();
        waitForResponse();

        // Check on it. It should be back to the init info
        Thread.sleep(250);
        Mockito.verify(mAlarmScheduler).cancel();
        Mockito.verifyNoMoreInteractions(mAlarmScheduler);
        Mockito.verify(mStorage).save(mInitInfo);
        Mockito.verifyNoMoreInteractions(mStorage);
        assertNotNull(mObservedInfo);
        assertEquals(mInitInfo, mObservedInfo);
    }

    /**
     * Resets any pending objects in preparation for the next test assert
     */
    private void resetState() {
        Mockito.clearInvocations(mAlarmScheduler);
        Mockito.clearInvocations(mStorage);

        mLock.reset();
        mObservedInfo = null;
    }

    /**
     * Waits for a response from the observer for a fixed period of time
     * @throws InterruptedException exception thrown if thread requested to shut down
     */
    private void waitForResponse() throws InterruptedException {
        assertTrue(mLock.await(5000, TimeUnit.MILLISECONDS));
    }
}
