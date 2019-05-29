package com.jordantymburski.driftoff.domain.usecase;

import com.jordantymburski.driftoff.domain.adapter.AudioController;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Use case to stop playing audio
 */
@Singleton
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
     * Main constructor
     * @param audioController the audio controller
     * @param setInfo the set info use case
     */
    @Inject
    public StopAudio(AudioController audioController, SetInfo setInfo) {
        mAudioController = audioController;
        mSetInfo = setInfo;
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
