package com.jordantymburski.driftoff;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.lang.ref.WeakReference;

class AlarmController implements AlarmListener {
    // singleton reference
    private static AlarmController mInstance;

    // internals
    private final PendingIntent mAlarmIntent;
    private final AlarmManager mAlarmManager;
    private final AlarmData mAlarmModel;
    private final JobInfo mJobInfo;
    private final JobScheduler mJobScheduler;
    private WeakReference<AlarmListener> mListener;

    /* ==============================================
     * CONSTRUCTOR
     * ============================================== */

    private AlarmController(Context context) {
        // the alarm intent
        Intent coreIntent = new Intent(
                AlarmReceiver.ACTION_ALARM, null, context, AlarmReceiver.class);
        mAlarmIntent = PendingIntent.getBroadcast(context, 0, coreIntent, 0);


        // the alarm manager
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // the alarm model
        mAlarmModel = AlarmData.getInstance(context);

        // job info and scheduler for triggering the alarm
        mJobInfo = new JobInfo.Builder(0, new ComponentName(context, AlarmService.class))
                .setMinimumLatency(0)
                .setOverrideDeadline(0)
                .build();
        mJobScheduler = context.getSystemService(JobScheduler.class);
    }

    /* ==============================================
     * STATIC INSTANCE FETCH
     * ============================================== */

    static AlarmController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AlarmController(context);
        }
        return mInstance;
    }

    /* ==============================================
     * AlarmListener OVERRIDES
     * ============================================== */

    @Override
    public void alarmOff() {
        if (mListener != null && mListener.get() != null) {
            mListener.get().alarmOff();
        }
    }

    /* ==============================================
     * INTERNAL FUNCTIONS
     * ============================================== */

    /**
     * Set an alarm wake up at a given time
     * @param alarmTime alarm time, in milliseconds, correlated to System.currentTimeMillis()
     */
    private void set(long alarmTime) {
        mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, mAlarmIntent);
        mAlarmModel.setAlarm(alarmTime);
    }

    /* ==============================================
     * FUNCTIONS
     * ============================================== */

    /**
     * Add a listener for alarm events
     * @param listener the listener connection
     */
    void addListener(AlarmListener listener) {
        mListener = new WeakReference<>(listener);
    }

    /**
     * Cancels any active pending alarms
     */
    void cancel() {
        mAlarmManager.cancel(mAlarmIntent);
        mAlarmModel.setAlarm(0);
        alarmOff();
    }

    /**
     * Removes any tied listener for alarm events
     */
    void removeListener() {
        mListener = null;
    }

    /**
     * Set an alarm wake up at the set point time chosen by the user
     */
    void set() {
        set(mAlarmModel.getTimeInMillis());
    }

    /**
     * Call if the time was manually changed by the user. This will handle re-scheduling the alarm
     * or triggering instantly if the time was changed past the alarm
     */
    void timeChanged() {
        long alarmTime = mAlarmModel.getAlarm();
        if (alarmTime > 0) {
            // Alarm is still upcoming. Re-set it
            if (mAlarmModel.isActive()) {
                set(alarmTime);
            }
            // Alarm has past and is no longer active. Trigger it now
            else {
                trigger();
            }
        }
    }

    /**
     * Triggers the alarm service job to stop any playing music
     */
    void trigger() {
        mJobScheduler.schedule(mJobInfo);
        cancel();
    }
}
