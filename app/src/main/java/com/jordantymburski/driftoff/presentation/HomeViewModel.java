package com.jordantymburski.driftoff.presentation;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.jordantymburski.driftoff.domain.model.AlarmInfo;
import com.jordantymburski.driftoff.domain.usecase.GetInfo;
import com.jordantymburski.driftoff.domain.usecase.SetInfo;

/**
 * View model manager of the home activity
 */
public class HomeViewModel extends AndroidViewModel {
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
     * @param app application reference
     */
    public HomeViewModel(Application app) {
        super(app);

        mInfoObservable = GetInfo.getInstance(app).observable();
        mUseSetInfo = SetInfo.getInstance(app);
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

    static HomeViewModel getInstance(ViewModelStoreOwner owner, Application app) {
        return new ViewModelProvider(owner,
                ViewModelProvider.AndroidViewModelFactory.getInstance(app))
                .get(HomeViewModel.class);
    }
}
