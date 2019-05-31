package com.jordantymburski.driftoff.common;

import android.annotation.TargetApi;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FakeAudioFocus implements AudioManager.OnAudioFocusChangeListener {
    /**
     * Audio manager service interface
     */
    private final AudioManager mAudioManager;

    /**
     * The focus request for the fake stream
     */
    private AudioFocusRequest mFocusRequest;

    /**
     * Last focus change
     */
    private int mLastFocusChange = AudioManager.AUDIOFOCUS_NONE;

    /**
     * Lock for waiting for a response
     */
    private CountDownLatch mLock = new CountDownLatch(1);

    /* ----------------------------------------------
     * CONSTRUCTOR
     * ---------------------------------------------- */

    public FakeAudioFocus(AudioManager audioManager) {
        mAudioManager = audioManager;
    }

    /* ----------------------------------------------
     * PRIVATE FUNCTIONS
     * ---------------------------------------------- */

    @TargetApi(Build.VERSION_CODES.O)
    private void abandonNew() {
        if (mFocusRequest != null) {
            mAudioManager.abandonAudioFocusRequest(mFocusRequest);
        }
    }

    private void requestLegacy() {
        mAudioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void requestNew() {
        if (mFocusRequest == null) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(attributes)
                    .setOnAudioFocusChangeListener(this)
                    .build();
        }

        mAudioManager.requestAudioFocus(mFocusRequest);
    }

    /* ----------------------------------------------
     * OnAudioFocusChangeListener OVERRIDES
     * ---------------------------------------------- */

    @Override
    public void onAudioFocusChange(int focusChange) {
        mLastFocusChange = focusChange;
        mLock.countDown();
    }

    /* ----------------------------------------------
     * PUBLIC FUNCTIONS
     * ---------------------------------------------- */

    public void abandon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            abandonNew();
        }
        mLastFocusChange = AudioManager.AUDIOFOCUS_NONE;
    }

    public boolean isLost() {
        return mLastFocusChange == AudioManager.AUDIOFOCUS_LOSS;
    }

    public void request() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestNew();
        } else {
            requestLegacy();
        }
    }

    public void waitForChange() {
        try {
            mLock.await(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ie) {
            // Ignore
        }
    }
}
