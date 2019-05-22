package com.jordantymburski.driftoff.domain.usecase;

import android.content.Context;

import com.jordantymburski.driftoff.service.AudioController;

/**
 * Use case to stop playing audio
 */
public class StopAudio {
    /**
     * Audio managing controller port
     */
    private final AudioController mAudioController;

    /**
     * Set alarm info use case to modify existing persisted values
     */
    private final SetInfo mSetInfo;

    /**
     * Instance of the class (singleton)
     * TODO: Replace with DI
     */
    private static StopAudio sInstance;

    /**
     * Internal private constructor
     * @param context android application context
     */
    private StopAudio(Context context) {
        mAudioController = AudioController.getInstance(context);
        mSetInfo = SetInfo.getInstance(context);
    }

    /**
     * Access the singleton instance
     * @param context android application context
     * @return valid instance
     */
    public static StopAudio getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new StopAudio(context);
        }
        return sInstance;
    }

    /* ----------------------------------------------
     * PUBLIC FUNCTIONS
     * ---------------------------------------------- */

    /**
     * Execute the stop audio use case. It will request a stop on the audio controller and update
     * any persisted values
     */
    public void execute() {
        mAudioController.requestFocus();
        mSetInfo.resetAlarm();
    }
}
