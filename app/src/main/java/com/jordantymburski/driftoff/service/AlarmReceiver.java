package com.jordantymburski.driftoff.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jordantymburski.driftoff.domain.usecase.RescheduleAlarm;
import com.jordantymburski.driftoff.domain.usecase.StopAudio;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String ACTION_STOP_AUDIO = "com.jordantymburski.driftoff.STOP_AUDIO";

    /**
     * Reschedule alarm domain use case
     */
    @SuppressWarnings("WeakerAccess")
    @Inject
    RescheduleAlarm mUseRescheduleAlarm;

    /**
     * Stop audio domain use case
     */
    @SuppressWarnings("WeakerAccess")
    @Inject
    StopAudio mUseStopAudio;

    @Override
    public void onReceive(Context context, Intent intent) {
        AndroidInjection.inject(this, context);

        switch (intent.getAction() != null ? intent.getAction() : "") {
            case Intent.ACTION_BOOT_COMPLETED:
            case Intent.ACTION_TIME_CHANGED:
                mUseRescheduleAlarm.execute();
                break;
            case ACTION_STOP_AUDIO:
                mUseStopAudio.execute();
                break;
        }
    }
}
