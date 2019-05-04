package com.jordantymburski.driftoff;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

public class AlarmService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        } else {
            Log.e("DriftOff",
                    "Failed to get the audio manager to gain focus of the audio system!");
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
