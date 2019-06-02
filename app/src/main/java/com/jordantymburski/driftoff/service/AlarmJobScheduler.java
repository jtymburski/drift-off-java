package com.jordantymburski.driftoff.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.jordantymburski.driftoff.domain.adapter.AlarmScheduler;

/**
 * Manage alarm job scheduling
 */
public class AlarmJobScheduler implements AlarmScheduler {
    /**
     * The pending alarm intent when the alarm fires
     */
    private final PendingIntent mAlarmIntent;

    /**
     * Alarm manager system service
     */
    private final AlarmManager mAlarmManager;

    /**
     * Main constructor
     * @param context android application context
     * @param alarmManager system alarm manager service interface
     * @param broadcastIntent the intent to locally broadcast towards the AlarmReceiver
     */
    public AlarmJobScheduler(Context context, AlarmManager alarmManager, Intent broadcastIntent) {
        mAlarmManager = alarmManager;
        mAlarmIntent = PendingIntent.getBroadcast(context, 0, broadcastIntent, 0);
    }

    /* ----------------------------------------------
     * AlarmScheduler OVERRIDES
     * ---------------------------------------------- */

    /**
     * Cancel any active alarm job
     */
    @Override
    public void cancel() {
        mAlarmManager.cancel(mAlarmIntent);
    }

    /**
     * Schedule a job at the given time
     * @param time epoch unix time
     */
    @Override
    public void schedule(long time) {
        mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, mAlarmIntent);
    }
}
