package com.jordantymburski.driftoff.di;

import com.jordantymburski.driftoff.service.AlarmJob;
import com.jordantymburski.driftoff.service.AlarmReceiver;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ServiceModule {
    @ContributesAndroidInjector
    abstract AlarmJob contributeAlarmJob();

    @ContributesAndroidInjector
    abstract AlarmReceiver contributeAlarmReceiver();
}
