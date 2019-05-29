package com.jordantymburski.driftoff.di;

import android.app.job.JobScheduler;
import android.content.Context;
import android.media.AudioManager;

import com.jordantymburski.driftoff.App;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private final App application;

    public AppModule(App application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    AudioManager provideAudioManager() {
        return (AudioManager) application.getSystemService(Context.AUDIO_SERVICE);
    }

    @Provides
    @Singleton
    JobScheduler provideJobScheduler() {
        return (JobScheduler) application.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }
}
