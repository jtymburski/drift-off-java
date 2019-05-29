package com.jordantymburski.driftoff.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Manage alarm job scheduling
 */
@Singleton
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
     * Main constructor
     * @param context android application context
     * @param jobScheduler system job scheduler service interface
     */
    @Inject
    public AlarmScheduler(Context context, JobScheduler jobScheduler) {
        mJobInfo = new JobInfo.Builder(JOB_ID, new ComponentName(context, AlarmJob.class));
        mJobScheduler = jobScheduler;
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
