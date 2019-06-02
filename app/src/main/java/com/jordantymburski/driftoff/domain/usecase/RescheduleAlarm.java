package com.jordantymburski.driftoff.domain.usecase;

import com.jordantymburski.driftoff.domain.adapter.AlarmScheduler;
import com.jordantymburski.driftoff.domain.model.AlarmInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Use case to reschedule an existing alarm
 */
@Singleton
public class RescheduleAlarm {
    /**
     * Alarm scheduling port
     */
    private final AlarmScheduler mAlarmScheduler;

    /**
     * Get alarm info use case to fetch existing cached value
     */
    private final GetInfo mGetInfo;

    /**
     * Main constructor
     * @param alarmScheduler manages alarm job scheduling
     * @param getInfo use case to get the current alarm information
     */
    @SuppressWarnings("unused")
    @Inject
    RescheduleAlarm(AlarmScheduler alarmScheduler, GetInfo getInfo) {
        mAlarmScheduler = alarmScheduler;
        mGetInfo = getInfo;
    }

    /* ----------------------------------------------
     * PUBLIC FUNCTIONS
     * ---------------------------------------------- */

    /**
     * Execute a reschedule if there is an existing alarm. If the time has past, it will trigger
     * the job immediately
     */
    public void execute() {
        AlarmInfo info = mGetInfo.current();
        if (info.alarm > 0) {
            mAlarmScheduler.cancel();
            mAlarmScheduler.schedule(info.alarm);
        }
    }
}
