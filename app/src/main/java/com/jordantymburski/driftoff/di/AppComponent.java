package com.jordantymburski.driftoff.di;

import com.jordantymburski.driftoff.App;
import com.jordantymburski.driftoff.domain.DomainProvider;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        DataModule.class,
        ServiceModule.class,
        HomeActivityModule.class
})
public interface AppComponent extends AndroidInjector<App> {
    void inject(DomainProvider domainProvider);
}