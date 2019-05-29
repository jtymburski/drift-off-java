package com.jordantymburski.driftoff.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import com.jordantymburski.driftoff.domain.adapter.AlarmScheduler;

/**
 * Manage alarm job scheduling
 */
public class AlarmJobScheduler implements AlarmScheduler {
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
    public AlarmJobScheduler(Context context, JobScheduler jobScheduler) {
        mJobInfo = new JobInfo.Builder(JOB_ID, new ComponentName(context, AlarmJob.class));
        mJobScheduler = jobScheduler;
    }

    /* ----------------------------------------------
     * AlarmScheduler OVERRIDES
     * ---------------------------------------------- */

    /**
     * Cancel any active alarm job
     */
    @Override
    public void cancel() {
        mJobScheduler.cancel(JOB_ID);
    }

    /**
     * Schedule a job at the given time
     * @param time epoch unix time
     */
    @Override
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
