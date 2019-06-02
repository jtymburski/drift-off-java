package com.jordantymburski.driftoff.presentation;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.jordantymburski.driftoff.domain.usecase.GetInfo;
import com.jordantymburski.driftoff.domain.usecase.SetInfo;

import javax.inject.Inject;

class HomeViewModelFactory implements ViewModelProvider.Factory {
    private final GetInfo mUseGetInfo;
    private final SetInfo mUseSetInfo;

    @Inject
    HomeViewModelFactory(GetInfo getInfo, SetInfo setInfo) {
        mUseGetInfo = getInfo;
        mUseSetInfo = setInfo;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new HomeViewModel(mUseGetInfo, mUseSetInfo);
    }
}
