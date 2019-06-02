package com.jordantymburski.driftoff.di.testing;

import com.jordantymburski.driftoff.App;
import com.jordantymburski.driftoff.di.HomeActivityModule;
import com.jordantymburski.driftoff.di.ReceiverModule;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        MockDomainModule.class,
        ReceiverModule.class,
        HomeActivityModule.class
})
public interface MockAppComponent extends AndroidInjector<App> {
}
