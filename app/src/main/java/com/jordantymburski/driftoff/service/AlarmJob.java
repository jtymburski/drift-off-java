package com.jordantymburski.driftoff.service;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.jordantymburski.driftoff.domain.usecase.StopAudio;

/**
 * Alarm job that the app will execute at the set time
 */
public class AlarmJob extends JobService {
    /**
     * Stop audio domain use case
     */
    private StopAudio mUseStopAudio;

    @Override
    public void onCreate() {
        mUseStopAudio = StopAudio.getInstance(this);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        mUseStopAudio.execute();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
