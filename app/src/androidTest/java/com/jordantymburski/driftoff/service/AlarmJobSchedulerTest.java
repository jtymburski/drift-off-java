package com.jordantymburski.driftoff.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;

import com.jordantymburski.driftoff.common.ContextProvider;

import org.junit.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class AlarmJobSchedulerTest {
    /* ----------------------------------------------
     * PRIVATE FUNCTIONS
     * ---------------------------------------------- */

    private void cleanUp(JobScheduler jobScheduler) {
        jobScheduler.cancelAll();
    }

    private AlarmJobScheduler getAlarmScheduler(Context context, JobScheduler jobScheduler) {
        return new AlarmJobScheduler(context, jobScheduler);
    }

    private JobScheduler getJobScheduler(Context context) {
        final JobScheduler jobScheduler
                = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        cleanUp(jobScheduler);
        return jobScheduler;
    }

    /* ----------------------------------------------
     * TEST CASES
     * ---------------------------------------------- */

    @Test
    public void schedule() {
        final Context context = ContextProvider.get();
        final JobScheduler jobScheduler = getJobScheduler(context);
        final AlarmJobScheduler alarmScheduler = getAlarmScheduler(context, jobScheduler);

        // Schedule an alarm job trigger
        long waitTime = TimeUnit.HOURS.toMillis(6);
        alarmScheduler.schedule(new Date().getTime() + waitTime);

        // Check to make sure it was scheduled
        JobInfo jobInfo = jobScheduler.getPendingJob(AlarmJobScheduler.JOB_ID);
        assertNotNull(jobInfo);
        assertEquals(waitTime, jobInfo.getMinLatencyMillis());
        assertEquals(waitTime, jobInfo.getMaxExecutionDelayMillis());

        cleanUp(jobScheduler);
    }

    @Test
    public void cancel() {
        final Context context = ContextProvider.get();
        final JobScheduler jobScheduler = getJobScheduler(context);
        final AlarmJobScheduler alarmScheduler = getAlarmScheduler(context, jobScheduler);

        // Schedule an alarm job trigger
        long waitTime = TimeUnit.HOURS.toMillis(6);
        alarmScheduler.schedule(new Date().getTime() + waitTime);

        // Check to make sure it was scheduled
        JobInfo beforeCancelInfo = jobScheduler.getPendingJob(AlarmJobScheduler.JOB_ID);
        assertNotNull(beforeCancelInfo);

        // Then, cancel it
        alarmScheduler.cancel();

        // Check to make sure it was cancelled
        JobInfo afterCancelInfo = jobScheduler.getPendingJob(AlarmJobScheduler.JOB_ID);
        assertNull(afterCancelInfo);

        cleanUp(jobScheduler);
    }

    @Test
    public void trigger() throws InterruptedException {
        final Context context = ContextProvider.get();
        final JobScheduler jobScheduler = getJobScheduler(context);
        final AlarmJobScheduler alarmScheduler = getAlarmScheduler(context, jobScheduler);

        // Schedule an alarm job trigger now
        alarmScheduler.schedule(new Date().getTime());

        // Sleep for a bit to wait for it to trigger
        Thread.sleep(5000);

        // Check to make sure it is no longer active
        JobInfo afterWaitInfo = jobScheduler.getPendingJob(AlarmJobScheduler.JOB_ID);
        assertNull(afterWaitInfo);

        cleanUp(jobScheduler);
    }
}
