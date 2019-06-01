package com.jordantymburski.driftoff.domain.usecase;

import com.jordantymburski.driftoff.domain.adapter.AudioController;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class StopAudioTest {
    /**
     * Audio controller mock class
     */
    @Mock
    private AudioController mAudioController;

    /**
     * Set info mock class
     */
    @Mock
    private SetInfo mSetInfo;

    /**
     * Stop audio use case
     */
    private StopAudio mStopAudio;

    @Before
    public void setup() {
        // Initialize mocks
        MockitoAnnotations.initMocks(this);

        // StopAudio use case set-up
        mStopAudio = new StopAudio(mAudioController, mSetInfo);
    }

    @Test
    public void execute() {
        // Trigger
        mStopAudio.execute();

        // Check on the execution
        Mockito.verify(mAudioController).requestFocus();
        Mockito.verifyNoMoreInteractions(mAudioController);
        Mockito.verify(mSetInfo).resetAlarm();
        Mockito.verifyNoMoreInteractions(mSetInfo);
    }
}
