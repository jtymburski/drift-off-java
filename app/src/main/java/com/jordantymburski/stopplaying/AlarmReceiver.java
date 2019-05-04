package com.jordantymburski.stopplaying;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Create the job info to schedule
        JobInfo.Builder jobBuilder = new JobInfo.Builder(0,
                new ComponentName(context, AlarmService.class));
        jobBuilder.setMinimumLatency(0);
        jobBuilder.setOverrideDeadline(0);

        // Schedule it to start
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        if (jobScheduler != null) {
            jobScheduler.schedule(jobBuilder.build());
        }
    }
}
