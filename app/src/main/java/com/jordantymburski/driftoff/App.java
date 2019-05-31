package com.jordantymburski.driftoff;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;

import com.jordantymburski.driftoff.di.AppComponent;
import com.jordantymburski.driftoff.di.AppModule;
import com.jordantymburski.driftoff.di.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasBroadcastReceiverInjector;
import dagger.android.HasServiceInjector;

@SuppressWarnings("WeakerAccess")
public class App extends Application
        implements HasActivityInjector,
                   HasBroadcastReceiverInjector,
                   HasServiceInjector {
    @Inject
    DispatchingAndroidInjector<Activity> dispatchingActivityInjector;

    @Inject
    DispatchingAndroidInjector<BroadcastReceiver> dispatchingReceiverInjector;

    @Inject
    DispatchingAndroidInjector<Service> dispatchingServiceInjector;

    /**
     * Built DI app component
     */
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        appComponent.inject(this);
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return dispatchingActivityInjector;
    }

    @Override
    public DispatchingAndroidInjector<BroadcastReceiver> broadcastReceiverInjector() {
        return dispatchingReceiverInjector;
    }

    @Override
    public DispatchingAndroidInjector<Service> serviceInjector() {
        return dispatchingServiceInjector;
    }

    /**
     * Fetches the core DI component for the entire application
     * @return built component
     */
    public AppComponent component() {
        return appComponent;
    }
}
