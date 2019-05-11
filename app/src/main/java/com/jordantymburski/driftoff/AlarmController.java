package com.jordantymburski.driftoff;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

class AlarmController {
    // singleton reference
    private static AlarmController mInstance;

    // internals
    private final PendingIntent mAlarmIntent;
    private final AlarmManager mAlarmManager;
    private final AlarmData mAlarmModel;
    private final JobInfo mJobInfo;
    private final JobScheduler mJobScheduler;

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
     * INTERNAL FUNCTIONS
     * ============================================== */

    private void set(long alarmTime) {
        mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, mAlarmIntent);
        mAlarmModel.setAlarm(alarmTime);
    }

    /* ==============================================
     * FUNCTIONS
     * ============================================== */

    void cancel() {
        mAlarmManager.cancel(mAlarmIntent);
        mAlarmModel.setAlarm(0);
    }

    void set() {
        set(mAlarmModel.getTimeInMillis());
    }

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

    void trigger() {
        mJobScheduler.schedule(mJobInfo);
        cancel();
    }
}
