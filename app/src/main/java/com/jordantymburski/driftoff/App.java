package com.jordantymburski.driftoff;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;

import com.jordantymburski.driftoff.di.AppModule;
import com.jordantymburski.driftoff.di.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasBroadcastReceiverInjector;

@SuppressWarnings("WeakerAccess")
public class App extends Application
        implements HasActivityInjector,
                   HasBroadcastReceiverInjector {
    @Inject
    DispatchingAndroidInjector<Activity> dispatchingActivityInjector;

    @Inject
    DispatchingAndroidInjector<BroadcastReceiver> dispatchingReceiverInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        component(DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build());
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return dispatchingActivityInjector;
    }

    @Override
    public DispatchingAndroidInjector<BroadcastReceiver> broadcastReceiverInjector() {
        return dispatchingReceiverInjector;
    }

    /**
     * Sets the main component to be used for all android entity injections
     * @param newComponent new android injection component
     */
    public void component(AndroidInjector<App> newComponent) {
        newComponent.inject(this);
    }
}
