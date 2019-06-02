package com.jordantymburski.driftoff.di;

import android.content.Context;

import com.jordantymburski.driftoff.data.PreferenceStorage;
import com.jordantymburski.driftoff.domain.adapter.Storage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
class DataModule {
    @Provides @Singleton
    Storage provideStorage(Context context) {
        return new PreferenceStorage(context);
    }
}
