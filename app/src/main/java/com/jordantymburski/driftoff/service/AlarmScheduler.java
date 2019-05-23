package com.jordantymburski.driftoff.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

/**
 * Manage alarm job scheduling
 */
public class AlarmScheduler {
    private static final int JOB_ID = 866825119;

    /**
     * Job info partially built object with constant settings defined
     */
    private final JobInfo.Builder mJobInfo;

    /**
     * Job scheduler system service
     */
    private final JobScheduler mJobScheduler;

    /**
     * Instance of the class (singleton)
     * TODO: Replace with DI
     */
    private static AlarmScheduler sInstance;

    /**
     * Internal private constructor
     * @param context android application context
     */
    private AlarmScheduler(Context context) {
        mJobInfo = new JobInfo.Builder(JOB_ID, new ComponentName(context, AlarmJob.class));

        mJobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    /**
     * Access the singleton instance
     * @param context android application context
     * @return valid instance
     */
    public static AlarmScheduler getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AlarmScheduler(context);
        }
        return sInstance;
    }

    /* ----------------------------------------------
     * PUBLIC FUNCTIONS
     * ---------------------------------------------- */

    /**
     * Cancel any active alarm job
     */
    public void cancel() {
        mJobScheduler.cancel(JOB_ID);
    }

    /**
     * Schedule a job at the given time
     * @param time epoch unix time
     */
    public void schedule(long time) {
        long triggerTime = time - System.currentTimeMillis();
        if (triggerTime < 0) {
            triggerTime = 0;
        }

        mJobScheduler.schedule(
                mJobInfo.setMinimumLatency(triggerTime)
                        .setOverrideDeadline(triggerTime)
                        .build());
    }
}
