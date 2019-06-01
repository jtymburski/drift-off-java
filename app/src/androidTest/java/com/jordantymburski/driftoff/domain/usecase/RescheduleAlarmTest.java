package com.jordantymburski.driftoff.domain.usecase;

import com.jordantymburski.driftoff.domain.adapter.AlarmScheduler;
import com.jordantymburski.driftoff.domain.model.AlarmInfo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Random;

public class RescheduleAlarmTest {
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
     * Random number generator instance
     */
    private final Random mRandom = new Random();

    /**
     * Reschedule alarm use case
     */
    private RescheduleAlarm mRescheduleAlarm;

    @Before
    public void setup() {
        // Initialize mocks
        MockitoAnnotations.initMocks(this);

        // RescheduleAlarm use case set-up
        mRescheduleAlarm = new RescheduleAlarm(mAlarmScheduler, mGetInfo);
    }

    @Test
    public void active() {
        // Set up get info with an active alarm object
        final AlarmInfo info = new AlarmInfo(
                new Date().getTime(), mRandom.nextInt(24), mRandom.nextInt(60));
        Mockito.when(mGetInfo.current()).thenReturn(info);

        // Trigger
        mRescheduleAlarm.execute();

        // Check on the execution
        Mockito.verify(mGetInfo, Mockito.atLeastOnce()).current();
        Mockito.verifyNoMoreInteractions(mGetInfo);
        Mockito.verify(mAlarmScheduler).cancel();
        Mockito.verify(mAlarmScheduler).schedule(info.alarm);
        Mockito.verifyNoMoreInteractions(mAlarmScheduler);
    }

    @Test
    public void inactive() {
        // Set up get info with an inactive alarm
        final AlarmInfo info = new AlarmInfo(
                0L, mRandom.nextInt(24), mRandom.nextInt(60));
        Mockito.when(mGetInfo.current()).thenReturn(info);

        // Trigger
        mRescheduleAlarm.execute();

        // Check on the execution
        Mockito.verify(mGetInfo, Mockito.atLeastOnce()).current();
        Mockito.verifyNoMoreInteractions(mGetInfo);
        Mockito.verifyZeroInteractions(mAlarmScheduler);
    }
}
