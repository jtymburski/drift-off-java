package com.jordantymburski.driftoff.domain.usecase;

import com.jordantymburski.driftoff.domain.adapter.AlarmScheduler;
import com.jordantymburski.driftoff.domain.adapter.Storage;
import com.jordantymburski.driftoff.domain.model.AlarmInfo;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Random;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SetInfoTest {
    /**
     * Alarm scheduler mock class
     */
    @Mock
    private AlarmScheduler mAlarmScheduler;

    /**
     * Get info mock class
     */
    @Mock
    private GetInfo mGetInfo;

    /**
     * Initialized info
     */
    private AlarmInfo mInitInfo;

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
    @Mock
    private Storage mStorage;

    @Before
    public void setup() {
        // Initialize mocks
        MockitoAnnotations.initMocks(this);

        // Alarm starting point
        setInitInfo(
                new AlarmInfo(0L, mRandom.nextInt(24), mRandom.nextInt(60)));

        // SetInfo use case set-up
        mSetInfo = new SetInfo(mAlarmScheduler, mGetInfo, mStorage);
    }

    @Test
    public void t1_setTime() throws InterruptedException {
        // Generate a modified time setpoint
        final AlarmInfo newInfo = new AlarmInfo(
                mInitInfo, mRandom.nextInt(24), mRandom.nextInt(60));

        // Set it and wait for the update
        resetState();
        mSetInfo.setTime(newInfo.timeHour, newInfo.timeMinute);

        // Check it
        Thread.sleep(250);
        assertNotEquals(mInitInfo, newInfo);
        Mockito.verifyZeroInteractions(mAlarmScheduler);
        Mockito.verify(mGetInfo, Mockito.atLeastOnce()).current();
        Mockito.verify(mGetInfo).post(newInfo);
        Mockito.verifyNoMoreInteractions(mGetInfo);
        Mockito.verify(mStorage).save(newInfo);
        Mockito.verifyNoMoreInteractions(mStorage);

        // Generate again
        final AlarmInfo newInfo2 = new AlarmInfo(
                newInfo, mRandom.nextInt(24), mRandom.nextInt(60));

        // Set it and wait for the update
        resetState();
        mSetInfo.setTime(newInfo2.timeHour, newInfo2.timeMinute);

        // Check it
        Thread.sleep(250);
        assertNotEquals(mInitInfo, newInfo2);
        Mockito.verifyZeroInteractions(mAlarmScheduler);
        Mockito.verify(mGetInfo, Mockito.atLeastOnce()).current();
        Mockito.verify(mGetInfo).post(newInfo2);
        Mockito.verifyNoMoreInteractions(mGetInfo);
        Mockito.verify(mStorage).save(newInfo2);
        Mockito.verifyNoMoreInteractions(mStorage);
    }

    @Test
    public void t2_setAlarm() throws InterruptedException {
        // Set it and wait for the update
        resetState();
        mSetInfo.setAlarm();

        // Generate the expected info
        Thread.sleep(250);
        final AlarmInfo freshAlarmInfo = new AlarmInfo(mInitInfo, mInitInfo.getTimeInMillis());

        // Check
        Mockito.verify(mAlarmScheduler).schedule(freshAlarmInfo.alarm);
        Mockito.verifyNoMoreInteractions(mAlarmScheduler);

        Mockito.verify(mGetInfo, Mockito.atLeastOnce()).current();
        Mockito.verify(mGetInfo).post(freshAlarmInfo);
        Mockito.verifyNoMoreInteractions(mGetInfo);

        Mockito.verify(mStorage).save(freshAlarmInfo);
        Mockito.verifyNoMoreInteractions(mStorage);
    }

    @Test
    public void t3_resetAlarm() throws InterruptedException {
        // Swap the default set-up for one that already has an alarm ready
        setInitInfo(new AlarmInfo(mInitInfo, mInitInfo.getTimeInMillis()));

        // Reset it and wait for the update
        resetState();
        mSetInfo.resetAlarm();

        // Generate the expected info
        Thread.sleep(250);
        final AlarmInfo emptyInfo = new AlarmInfo(mInitInfo, 0L);

        // Check
        Mockito.verify(mAlarmScheduler).cancel();
        Mockito.verifyNoMoreInteractions(mAlarmScheduler);

        Mockito.verify(mGetInfo, Mockito.atLeastOnce()).current();
        Mockito.verify(mGetInfo).post(emptyInfo);
        Mockito.verifyNoMoreInteractions(mGetInfo);

        Mockito.verify(mStorage).save(emptyInfo);
        Mockito.verifyNoMoreInteractions(mStorage);
    }

    /**
     * Resets any pending objects in preparation for the next test assert
     */
    private void resetState() {
        Mockito.clearInvocations(mAlarmScheduler);
        Mockito.clearInvocations(mGetInfo);
        Mockito.clearInvocations(mStorage);
    }

    /**
     * Sets the initialized alarm object
     * @param alarmInfo custom alarm info
     */
    private void setInitInfo(AlarmInfo alarmInfo) {
        mInitInfo = alarmInfo;
        Mockito.when(mGetInfo.current()).thenReturn(alarmInfo);
        Mockito.when(mStorage.load()).thenReturn(alarmInfo);
    }
}
