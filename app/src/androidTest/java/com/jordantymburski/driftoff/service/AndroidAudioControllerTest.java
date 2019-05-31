package com.jordantymburski.driftoff.service;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;

import com.jordantymburski.driftoff.common.ContextProvider;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class AndroidAudioControllerTest
        implements AudioManager.OnAudioFocusChangeListener {

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
     * PRIVATE FUNCTIONS
     * ---------------------------------------------- */

    @TargetApi(Build.VERSION_CODES.O)
    private void abandonFocus(AudioManager audioManager) {
        if (mFocusRequest != null) {
            audioManager.abandonAudioFocusRequest(mFocusRequest);
        }
    }

    private AndroidAudioController getAudioController(AudioManager audioManager) {
        return new AndroidAudioController(audioManager);
    }

    private AudioManager getAudioManager() {
        return (AudioManager) ContextProvider.get().getSystemService(Context.AUDIO_SERVICE);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void requestFocus(AudioManager audioManager) {
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

        audioManager.requestAudioFocus(mFocusRequest);
    }

    private void requestFocusLegacy(AudioManager audioManager) {
        audioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
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
     * TEST CASES
     * ---------------------------------------------- */

    @Test
    public void getFocus() throws InterruptedException {
        // Create a fake focus request to stream music
        AudioManager audioManager = getAudioManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestFocus(audioManager);
        } else {
            requestFocusLegacy(audioManager);
        }

        // Now, run the controller implementation and make sure focus is lost on the fake stream
        AndroidAudioController audioController = getAudioController(audioManager);
        audioController.requestFocus();
        mLock.await(5000, TimeUnit.MILLISECONDS);

        // Check the focus change
        assertEquals(AudioManager.AUDIOFOCUS_LOSS, mLastFocusChange);
    }
}
