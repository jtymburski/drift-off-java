package com.jordantymburski.driftoff.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jordantymburski.driftoff.domain.usecase.RescheduleAlarm;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction() != null ? intent.getAction() : "") {
            case Intent.ACTION_BOOT_COMPLETED:
            case Intent.ACTION_TIME_CHANGED:
                RescheduleAlarm.getInstance(context).execute();
                break;
        }
    }
}
