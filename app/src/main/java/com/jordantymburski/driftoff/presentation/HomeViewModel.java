package com.jordantymburski.driftoff.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.jordantymburski.driftoff.domain.model.AlarmInfo;
import com.jordantymburski.driftoff.domain.usecase.GetInfo;
import com.jordantymburski.driftoff.domain.usecase.SetInfo;

/**
 * View model manager of the home activity
 */
class HomeViewModel extends ViewModel {
    /**
     * Observable wrapper that the connected activity can monitor for changes
     */
    private final LiveData<AlarmInfo> mInfoObservable;

    /**
     * Set alarm info domain use case
     */
    private final SetInfo mUseSetInfo;

    /**
     * Initializing constructor
     * @param getInfo use case to get the current alarm information
     * @param setInfo use case to set and update the persisted alarm information
     */
    HomeViewModel(GetInfo getInfo, SetInfo setInfo) {
        mInfoObservable = getInfo.observable();
        mUseSetInfo = setInfo;
    }

    /* ----------------------------------------------
     * PACKAGE FUNCTIONS
     * ---------------------------------------------- */

    /**
     * Fetches the set info observable for usage by the holding activity
     * @return life-cycle aware observable
     */
    LiveData<AlarmInfo> getInfoObservable() {
        return mInfoObservable;
    }

    /**
     * Reset the alarm (unset). Called when the alarm either goes off or is cancelled
     */
    void resetAlarm() {
        mUseSetInfo.resetAlarm();
    }

    /**
     * Set the alarm
     */
    void setAlarm() {
        mUseSetInfo.setAlarm();
    }

    /**
     * Set the time setpoint
     * @param hour 0-23 hour setpoint
     * @param minute minute setpoint
     */
    void setTime(int hour, int minute) {
        mUseSetInfo.setTime(hour, minute);
    }

    /* ----------------------------------------------
     * STATIC CREATE
     * ---------------------------------------------- */

    static HomeViewModel getInstance(ViewModelStoreOwner owner, ViewModelProvider.Factory factory) {
        return new ViewModelProvider(owner, factory)
                .get(HomeViewModel.class);
    }
}
