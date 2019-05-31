package com.jordantymburski.driftoff.service;

import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.jordantymburski.driftoff.common.ContextProvider;
import com.jordantymburski.driftoff.common.ServiceProvider;
import com.jordantymburski.driftoff.data.PreferenceStorage;
import com.jordantymburski.driftoff.domain.usecase.GetInfo;
import com.jordantymburski.driftoff.domain.usecase.SetInfo;

import org.junit.Test;

import static org.junit.Assert.*;

public class AlarmReceiverTest {
    @Test
    public void trigger() throws InterruptedException {
        // Register the receiver for local broadcasts
        final AlarmReceiver receiver = new AlarmReceiver();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        final Context context = ContextProvider.get();
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, intentFilter);

        // Schedule a normal job alarm through the domain
        final JobScheduler jobScheduler = ServiceProvider.jobScheduler(context);
        final PreferenceStorage storage = new PreferenceStorage(context);
        final GetInfo getInfo = new GetInfo(storage);
        final SetInfo setInfo = new SetInfo(
                new AlarmJobScheduler(context, jobScheduler), getInfo, storage);
        setInfo.setAlarm();

        // Cancel all existing jobs and make sure the alarm job has been removed
        jobScheduler.cancelAll();
        assertNull(jobScheduler.getPendingJob(AlarmJobScheduler.JOB_ID));

        // Broadcast to force a create with BOOT_COMPLETED
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(new Intent(Intent.ACTION_BOOT_COMPLETED));
        Thread.sleep(2500);

        // Check that the job was recreated
        assertNotNull(jobScheduler.getPendingJob(AlarmJobScheduler.JOB_ID));

        // Cancel all again and make sure the alarm job has been removed
        jobScheduler.cancelAll();
        assertNull(jobScheduler.getPendingJob(AlarmJobScheduler.JOB_ID));

        // Broadcast to force a create with BOOT_COMPLETED
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
