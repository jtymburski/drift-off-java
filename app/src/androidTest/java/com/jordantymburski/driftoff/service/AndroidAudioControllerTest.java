package com.jordantymburski.driftoff.service;

import android.content.Context;
import android.media.AudioManager;

import com.jordantymburski.driftoff.common.ContextProvider;
import com.jordantymburski.driftoff.common.FakeAudioFocus;

import org.junit.Test;

import static org.junit.Assert.*;

public class AndroidAudioControllerTest {
    /* ----------------------------------------------
     * PRIVATE FUNCTIONS
     * ---------------------------------------------- */

    private AndroidAudioController getAudioController(AudioManager audioManager) {
        return new AndroidAudioController(audioManager);
    }

    private AudioManager getAudioManager() {
        return (AudioManager) ContextProvider.get().getSystemService(Context.AUDIO_SERVICE);
    }

    /* ----------------------------------------------
     * TEST CASES
     * ---------------------------------------------- */

    @Test
    public void getFocus() throws InterruptedException {
        // Create a fake focus request to stream music
        final AudioManager audioManager = getAudioManager();
        final FakeAudioFocus fakeFocus = new FakeAudioFocus(audioManager);
        fakeFocus.request();

        // Now, run the controller implementation and make sure focus is lost on the fake stream
        AndroidAudioController audioController = getAudioController(audioManager);
        audioController.requestFocus();
        fakeFocus.waitForChange();

        // Check the focus change
        assertTrue(fakeFocus.isLost());

        // Release any fake focus resources
        fakeFocus.abandon();
    }
}
