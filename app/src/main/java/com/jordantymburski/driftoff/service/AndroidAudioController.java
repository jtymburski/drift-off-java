package com.jordantymburski.driftoff.service;

import android.annotation.TargetApi;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;

import com.jordantymburski.driftoff.domain.adapter.AudioController;

/**
 * Custom audio control and management functionality
 */
public class AndroidAudioController implements AudioController {
    /**
     * Audio manager system service
     */
    private final AudioManager mAudioManager;

    /**
     * Main constructor
     * @param audioManager system audio manager service interface
     */
    public AndroidAudioController(AudioManager audioManager) {
        mAudioManager = audioManager;
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
     * AudioController OVERRIDES
     * ---------------------------------------------- */

    /**
     * Request focus on the audio layer. All listeners will stop playing
     */
    @Override
    public void requestFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestFocusNew();
        } else {
            requestFocusLegacy();
        }
    }
}
