package com.jordantymburski.driftoff.service;

import android.content.Context;

import com.jordantymburski.driftoff.common.ContextProvider;
import com.jordantymburski.driftoff.common.FakeAudioFocus;
import com.jordantymburski.driftoff.common.ServiceProvider;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class AlarmJobTest {
    @Test
    public void run() throws InterruptedException {
        // Create a fake focus hold
        final Context context = ContextProvider.get();
        final FakeAudioFocus fakeFocus = new FakeAudioFocus(ServiceProvider.audioManager(context));
        fakeFocus.request();

        // Schedule a job to happen soon
        final AlarmJobScheduler alarmScheduler
                = new AlarmJobScheduler(context, ServiceProvider.jobScheduler(context));
        alarmScheduler.schedule(new Date().getTime());

        // Wait for it to run and focus to be pulled
        Thread.sleep(5000);
        fakeFocus.waitForChange();

        // Check that focus was lost
        assertTrue(fakeFocus.isLost());

        // Clean up
        fakeFocus.abandon();
    }
}
