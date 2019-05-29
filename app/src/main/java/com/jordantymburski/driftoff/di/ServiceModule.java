package com.jordantymburski.driftoff.di;

import android.app.job.JobScheduler;
import android.content.Context;
import android.media.AudioManager;

import com.jordantymburski.driftoff.domain.adapter.AlarmScheduler;
import com.jordantymburski.driftoff.domain.adapter.AudioController;
import com.jordantymburski.driftoff.service.AlarmJob;
import com.jordantymburski.driftoff.service.AlarmJobScheduler;
import com.jordantymburski.driftoff.service.AlarmReceiver;
import com.jordantymburski.driftoff.service.AndroidAudioController;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

@SuppressWarnings("unused")
@Module
abstract class ServiceModule {
    @ContributesAndroidInjector
    abstract AlarmJob contributeAlarmJob();

    @ContributesAndroidInjector
    abstract AlarmReceiver contributeAlarmReceiver();

    @Provides @Singleton
    static AlarmScheduler provideAlarmScheduler(Context context, JobScheduler jobScheduler) {
        return new AlarmJobScheduler(context, jobScheduler);
    }

    @Provides @Singleton
    static AudioController provideAudioController(AudioManager audioManager) {
        return new AndroidAudioController(audioManager);
    }
}
