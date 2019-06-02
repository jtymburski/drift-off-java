package com.jordantymburski.driftoff.service;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.jordantymburski.driftoff.App;
import com.jordantymburski.driftoff.common.ContextProvider;
import com.jordantymburski.driftoff.di.testing.DaggerMockAppComponent;
import com.jordantymburski.driftoff.di.testing.MockDomainModule;
import com.jordantymburski.driftoff.domain.usecase.GetInfo;
import com.jordantymburski.driftoff.domain.usecase.RescheduleAlarm;
import com.jordantymburski.driftoff.domain.usecase.SetInfo;
import com.jordantymburski.driftoff.domain.usecase.StopAudio;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class AlarmReceiverTest {
    /**
     * Get info domain use case mock
     */
    @Mock
    private GetInfo mGetInfo;

    /**
     * Reschedule alarm domain use case mock
     */
    @Mock
    private RescheduleAlarm mRescheduleAlarm;

    /**
     * Set info domain use case mock
     */
    @Mock
    private SetInfo mSetInfo;

    /**
     * Stop audio domain use case mock
     */
    @Mock
    private StopAudio mStopAudio;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        final App application = (App) ContextProvider.get().getApplicationContext();
        application.component(DaggerMockAppComponent.builder()
                .mockDomainModule(new MockDomainModule(
                        mGetInfo, mRescheduleAlarm, mSetInfo, mStopAudio))
                .build());
    }

    @Test
    public void bootCompleted() throws InterruptedException {
        // System service connections
        final Context context = ContextProvider.get();

        // Register the receiver for local broadcasts
        final AlarmReceiver receiver = new AlarmReceiver();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, intentFilter);

        // Make sure no calls have been made to the use cases
        Mockito.verifyZeroInteractions(mGetInfo, mRescheduleAlarm, mSetInfo, mStopAudio);

        // Broadcast to force a create with BOOT_COMPLETED
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(new Intent(Intent.ACTION_BOOT_COMPLETED));
        Thread.sleep(250);

        // Check that just the reschedule was called
        Mockito.verify(mRescheduleAlarm).execute();
        Mockito.verifyNoMoreInteractions(mGetInfo, mRescheduleAlarm, mSetInfo, mStopAudio);

        // Clean up
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    }

    @Test
    public void stopAudio() throws InterruptedException {
        // System service connections
        final Context context = ContextProvider.get();

        // Register the receiver for local broadcasts
        final AlarmReceiver receiver = new AlarmReceiver();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AlarmReceiver.ACTION_STOP_AUDIO);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, intentFilter);

        // Make sure no calls have been made to the use cases
        Mockito.verifyZeroInteractions(mGetInfo, mRescheduleAlarm, mSetInfo, mStopAudio);

        // Broadcast to force a create with BOOT_COMPLETED
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(new Intent(AlarmReceiver.ACTION_STOP_AUDIO));
        Thread.sleep(250);

        // Check that just the reschedule was called
        Mockito.verify(mStopAudio).execute();
        Mockito.verifyNoMoreInteractions(mGetInfo, mRescheduleAlarm, mSetInfo, mStopAudio);

        // Clean up
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    }

    @Test
    public void timeChanged() throws InterruptedException {
        // System service connections
        final Context context = ContextProvider.get();

        // Register the receiver for local broadcasts
        final AlarmReceiver receiver = new AlarmReceiver();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, intentFilter);

        // Make sure no calls have been made to the use cases
        Mockito.verifyZeroInteractions(mGetInfo, mRescheduleAlarm, mSetInfo, mStopAudio);

        // Broadcast to force a create with BOOT_COMPLETED
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(new Intent(Intent.ACTION_TIME_CHANGED));
        Thread.sleep(250);

        // Check that just the reschedule was called
        Mockito.verify(mRescheduleAlarm).execute();
        Mockito.verifyNoMoreInteractions(mGetInfo, mRescheduleAlarm, mSetInfo, mStopAudio);

        // Clean up
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    }
}
