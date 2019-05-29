package com.jordantymburski.driftoff.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jordantymburski.driftoff.domain.usecase.RescheduleAlarm;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class AlarmReceiver extends BroadcastReceiver {
    /**
     * Reschedule alarm domain use case
     */
    @Inject
    public RescheduleAlarm mUseRescheduleAlarm;

    @Override
    public void onReceive(Context context, Intent intent) {
        AndroidInjection.inject(this, context);

        switch (intent.getAction() != null ? intent.getAction() : "") {
            case Intent.ACTION_BOOT_COMPLETED:
            case Intent.ACTION_TIME_CHANGED:
                mUseRescheduleAlarm.execute();
                break;
        }
    }
}
