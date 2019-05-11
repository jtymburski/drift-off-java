package com.jordantymburski.driftoff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String ACTION_ALARM = "com.jordantymburski.driftoff.ALARM_TRIGGER";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction() != null ? intent.getAction() : "") {
            case ACTION_ALARM:
                AlarmController.getInstance(context).trigger();
                break;
            case Intent.ACTION_TIME_CHANGED:
                AlarmController.getInstance(context).timeChanged();
                break;
        }
    }
}
