package com.jordantymburski.driftoff.di;

import com.jordantymburski.driftoff.service.AlarmReceiver;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@SuppressWarnings("unused")
@Module
public abstract class ReceiverModule {
    @ContributesAndroidInjector
    abstract AlarmReceiver contributeAlarmReceiver();
}
