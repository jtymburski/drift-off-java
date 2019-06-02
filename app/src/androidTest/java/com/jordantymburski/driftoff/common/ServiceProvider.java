package com.jordantymburski.driftoff.common;

import android.app.AlarmManager;
import android.content.Context;
import android.media.AudioManager;

public class ServiceProvider {
    public static AlarmManager alarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public static AudioManager audioManager(Context context) {
        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }
}
