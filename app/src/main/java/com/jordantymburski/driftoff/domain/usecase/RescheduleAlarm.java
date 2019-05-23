package com.jordantymburski.driftoff.domain.usecase;

import android.content.Context;

import com.jordantymburski.driftoff.domain.model.AlarmInfo;
import com.jordantymburski.driftoff.service.AlarmScheduler;

/**
 * Use case to reschedule an existing alarm
 */
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
     * Instance of the class (singleton)
     * TODO: Replace with DI
     */
    private static RescheduleAlarm sInstance;

    /**
     * Internal private constructor
     * @param context android application context
     */
    private RescheduleAlarm(Context context) {
        mAlarmScheduler = AlarmScheduler.getInstance(context);
        mGetInfo = GetInfo.getInstance(context);
    }

    /**
     * Access the singleton instance
     * @param context android application context
     * @return valid instance
     */
    public static RescheduleAlarm getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new RescheduleAlarm(context);
        }
        return sInstance;
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
