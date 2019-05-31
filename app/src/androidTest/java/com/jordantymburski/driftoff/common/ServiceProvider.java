package com.jordantymburski.driftoff.common;

import android.app.job.JobScheduler;
import android.content.Context;
import android.media.AudioManager;

public class ServiceProvider {
    public static AudioManager audioManager(Context context) {
        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public static JobScheduler jobScheduler(Context context) {
        final JobScheduler jobScheduler
                = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancelAll();
        return jobScheduler;
    }
}
