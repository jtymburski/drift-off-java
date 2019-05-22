package com.jordantymburski.driftoff.service;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;

/**
 * Custom audio control and management functionality
 */
public class AudioController {
    /**
     * Audio manager system service
     */
    private final AudioManager mAudioManager;

    /**
     * Instance of the class (singleton)
     * TODO: Replace with DI
     */
    private static AudioController sInstance;

    /**
     * Internal private constructor
     * @param context android application context
     */
    private AudioController(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * Access the singleton instance
     * @param context android application context
     * @return valid instance
     */
    public static AudioController getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AudioController(context);
        }
        return sInstance;
    }

    /* ----------------------------------------------
     * PRIVATE FUNCTIONS
     * ---------------------------------------------- */

    /**
     * Request focus on the audio layer
     * This functionality is now legacy as of Android O
     */
    private void requestFocusLegacy() {
        mAudioManager.requestAudioFocus(null,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    /**
     * Request focus on the audio layer
     * This functionality is only valid on Android O+
     */
    @TargetApi(Build.VERSION_CODES.O)
    private void requestFocusNew() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(attributes)
                .build();

        mAudioManager.requestAudioFocus(focusRequest);
    }

    /* ----------------------------------------------
     * PUBLIC FUNCTIONS
     * ---------------------------------------------- */

    /**
     * Request focus on the audio layer. All listeners will stop playing
     */
    public void requestFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestFocusNew();
        } else {
            requestFocusLegacy();
        }
    }
}
