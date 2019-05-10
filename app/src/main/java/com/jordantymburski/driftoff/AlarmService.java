package com.jordantymburski.driftoff;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

public class AlarmService extends JobService {
    private static final String LOG_TAG = JobService.class.getSimpleName();

    /* ==============================================
     * JobService OVERRIDES
     * ============================================== */

    @Override
    public boolean onStartJob(JobParameters params) {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestAudioFocus(am);
            } else {
                requestAudioFocusLegacy(am);
            }
        } else {
            Log.e(LOG_TAG,
                    "Failed to get the audio manager to gain focus of the audio system!");
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    /* ==============================================
     * INTERNAL FUNCTIONS
     * ============================================== */

    @TargetApi(Build.VERSION_CODES.O)
    private void requestAudioFocus(AudioManager am) {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(attributes)
                .build();

        am.requestAudioFocus(focusRequest);
    }

    // deprecated on {@value Build.VERSION_CODES.O}
    private void requestAudioFocusLegacy(AudioManager am) {
        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }
}
