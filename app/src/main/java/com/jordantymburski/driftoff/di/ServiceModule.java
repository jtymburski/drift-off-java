package com.jordantymburski.driftoff.di;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.jordantymburski.driftoff.domain.adapter.AlarmScheduler;
import com.jordantymburski.driftoff.domain.adapter.AudioController;
import com.jordantymburski.driftoff.service.AlarmJobScheduler;
import com.jordantymburski.driftoff.service.AlarmReceiver;
import com.jordantymburski.driftoff.service.AndroidAudioController;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@SuppressWarnings("unused")
@Module
abstract class ServiceModule {
    @Provides @Singleton
    static AlarmScheduler provideAlarmScheduler(Context context, AlarmManager alarmManager) {
        final Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(AlarmReceiver.ACTION_STOP_AUDIO);
        return new AlarmJobScheduler(context, alarmManager, intent);
    }

    @Provides @Singleton
    static AudioController provideAudioController(AudioManager audioManager) {
        return new AndroidAudioController(audioManager);
    }
}
