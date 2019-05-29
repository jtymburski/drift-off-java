package com.jordantymburski.driftoff.di;

import com.jordantymburski.driftoff.App;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        ServiceModule.class,
        HomeActivityModule.class
})
public interface AppComponent extends AndroidInjector<App> {
}