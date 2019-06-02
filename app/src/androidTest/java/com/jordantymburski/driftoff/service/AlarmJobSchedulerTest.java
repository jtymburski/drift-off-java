package com.jordantymburski.driftoff.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.jordantymburski.driftoff.common.ContextProvider;
import com.jordantymburski.driftoff.common.ResettableCountDownLatch;
import com.jordantymburski.driftoff.common.ServiceProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class AlarmJobSchedulerTest {
    private final String ACTION_SIMULATED = "com.jordantymburski.driftoff.test.SIMULATED";

    /**
     * The alarm scheduler instance to test with
     */
    private AlarmJobScheduler mAlarmScheduler;

    /**
     * Resettable count down latch for synchronization
     */
    private final ResettableCountDownLatch mLock = new ResettableCountDownLatch(1);

    /**
     * Internal custom receiver
     */
    private final BroadcastReceiver mCustomReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(ACTION_SIMULATED)) {
                mLock.countDown();
            }
        }
    };

    /* ----------------------------------------------
     * TEST CASES
     * ---------------------------------------------- */

    @Before
    public void setup() {
        final Context context = ContextProvider.get();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SIMULATED);
        context.registerReceiver(mCustomReceiver, intentFilter);

        final Intent intent = new Intent(ACTION_SIMULATED);
        mAlarmScheduler = new AlarmJobScheduler(
                context, ServiceProvider.alarmManager(context), intent);
    }

    @After
    public void cleanup() {
        ContextProvider.get().unregisterReceiver(mCustomReceiver);
    }


    @Test
    public void cancel() throws InterruptedException {
        // Schedule it in a few seconds
        mLock.reset();
        mAlarmScheduler.schedule(new Date().getTime() + TimeUnit.SECONDS.toMillis(3));

        // Sleep for a bit (less than the alarm above)
        Thread.sleep(1500);

        // Cancel it
        mAlarmScheduler.cancel();

        // Wait for the broadcast (should never happen)
        assertFalse(mLock.await(8, TimeUnit.SECONDS));
    }

    @Test
    public void schedule() throws InterruptedException {
        // Schedule
        mLock.reset();
        mAlarmScheduler.schedule(new Date().getTime());

        // Wait for the broadcast
        assertTrue(mLock.await(8, TimeUnit.SECONDS));
    }

    @Test
    public void reschedule() throws InterruptedException {
        // Schedule far away
        mLock.reset();
        mAlarmScheduler.schedule(new Date().getTime() + TimeUnit.HOURS.toMillis(1));

        // Wait for a bit
        assertFalse(mLock.await(5, TimeUnit.SECONDS));

        // Re-schedule it for now
        schedule();
    }
}
