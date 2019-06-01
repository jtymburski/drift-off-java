package com.jordantymburski.driftoff.service;

import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.jordantymburski.driftoff.App;
import com.jordantymburski.driftoff.common.ContextProvider;
import com.jordantymburski.driftoff.common.ServiceProvider;
import com.jordantymburski.driftoff.domain.DomainProvider;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AlarmReceiverTest {
    /**
     * Domain direct access for injected use cases
     */
    private final DomainProvider mDomainProvider = new DomainProvider();

    @Before
    public void setup() {
        ((App) ContextProvider.get().getApplicationContext()).component().inject(mDomainProvider);
    }

    @Test
    public void bootCompleted() throws InterruptedException {
        // System service connections
        final Context context = ContextProvider.get();
        final JobScheduler jobScheduler = ServiceProvider.jobScheduler(context);

        // Set a valid alarm
        mDomainProvider.setInfo.setAlarm();

        // Register the receiver for local broadcasts
        final AlarmReceiver receiver = new AlarmReceiver();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, intentFilter);

        // Cancel all existing jobs and make sure the alarm job has been removed
        Thread.sleep(250);
        jobScheduler.cancelAll();
        assertNull(jobScheduler.getPendingJob(AlarmJobScheduler.JOB_ID));

        // Broadcast to force a create with BOOT_COMPLETED
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(new Intent(Intent.ACTION_BOOT_COMPLETED));
        Thread.sleep(2500);

        // Check that the job was recreated
        assertNotNull(jobScheduler.getPendingJob(AlarmJobScheduler.JOB_ID));

        // Clean up
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
        jobScheduler.cancelAll();
    }

    @Test
    public void timeChanged() throws InterruptedException {
        // System service connections
        final Context context = ContextProvider.get();
        final JobScheduler jobScheduler = ServiceProvider.jobScheduler(context);

        // Set a valid alarm
        mDomainProvider.setInfo.setAlarm();

        // Register the receiver for local broadcasts
        final AlarmReceiver receiver = new AlarmReceiver();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, intentFilter);

        // Cancel all existing jobs and make sure the alarm job has been removed
        Thread.sleep(250);
        jobScheduler.cancelAll();
        assertNull(jobScheduler.getPendingJob(AlarmJobScheduler.JOB_ID));

        // Broadcast to force a create with TIME_CHANGED
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(new Intent(Intent.ACTION_TIME_CHANGED));
        Thread.sleep(2500);

        // Check that the job was recreated
        assertNotNull(jobScheduler.getPendingJob(AlarmJobScheduler.JOB_ID));

        // Clean up
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
        jobScheduler.cancelAll();
    }
}
