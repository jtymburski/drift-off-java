package com.jordantymburski.driftoff.di;

import com.jordantymburski.driftoff.presentation.HomeActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@SuppressWarnings("unused")
@Module
public abstract class HomeActivityModule {
    @ContributesAndroidInjector
    abstract HomeActivity contributeHomeActivity();
}
