package com.jordantymburski.driftoff.service;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.jordantymburski.driftoff.common.ContextProvider;
import com.jordantymburski.driftoff.domain.MockDomain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class AlarmReceiverTest {
    /**
     * The domain interception object
     */
    private MockDomain mDomain;

    @Before
    public void setup() {
        mDomain = new MockDomain();
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
        mDomain.verifyZeroInteractions();

        // Broadcast to force a create with BOOT_COMPLETED
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(new Intent(Intent.ACTION_BOOT_COMPLETED));
        Thread.sleep(250);

        // Check that just the reschedule was called
        Mockito.verify(mDomain.rescheduleAlarm()).execute();
        mDomain.verifyNoMoreInteractions();

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
        mDomain.verifyZeroInteractions();

        // Broadcast to force a create with BOOT_COMPLETED
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(new Intent(AlarmReceiver.ACTION_STOP_AUDIO));
        Thread.sleep(250);

        // Check that just the reschedule was called
        Mockito.verify(mDomain.stopAudio()).execute();
        mDomain.verifyNoMoreInteractions();

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
        mDomain.verifyZeroInteractions();

        // Broadcast to force a create with BOOT_COMPLETED
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(new Intent(Intent.ACTION_TIME_CHANGED));
        Thread.sleep(250);

        // Check that just the reschedule was called
        Mockito.verify(mDomain.rescheduleAlarm()).execute();
        mDomain.verifyNoMoreInteractions();

        // Clean up
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    }
}
