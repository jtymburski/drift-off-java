package com.jordantymburski.driftoff.domain.usecase;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jordantymburski.driftoff.data.PreferenceStorage;
import com.jordantymburski.driftoff.domain.model.AlarmInfo;

/**
 * Use case to get the current alarm information and observe for changes
 */
public class GetInfo {
    /**
     * Observable wrapper that interfaces can monitor for changes
     */
    private MutableLiveData<AlarmInfo> mInfoObservable;

    /**
     * Connection to the storage layer. Used to fetch current persisted state
     */
    private final PreferenceStorage mStorage;

    /**
     * Instance of the class (singleton)
     * TODO: Replace with DI
     */
    private static GetInfo sInstance;

    /**
     * Internal private constructor
     * @param context android application context
     */
    private GetInfo(Context context) {
        mStorage = PreferenceStorage.getInstance(context);
    }

    /**
     * Access the singleton instance
     * @param context android application context
     * @return valid instance
     */
    public static GetInfo getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new GetInfo(context);
        }
        return sInstance;
    }

    /* ----------------------------------------------
     * PRIVATE FUNCTIONS
     * ---------------------------------------------- */

    /**
     * Create the observable object, if it has not been created already. If its newly created, it
     * will initiate a fetch immediately to fill it with a legitimate value
     * @return TRUE if a new observable was created. FALSE if it already exists
     */
    private boolean createObservable() {
        if (mInfoObservable == null) {
            mInfoObservable = new MutableLiveData<>();
            return true;
        }
        return false;
    }

    /**
     * Fetches the persisted information from the storage, on the current thread. It will
     * post the updates
     * @return the fetched alarm information object
     */
    private AlarmInfo fetch() {
        final AlarmInfo info = mStorage.load();
        post(info);
        return info;
    }

    /**
     * Fetches the persisted information from the storage, asynchronously on a thread. It will
     * post the updates
     */
    private void fetchThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                fetch();
            }
        }).start();
    }

    /* ----------------------------------------------
     * PACKAGE FUNCTIONS
     * ---------------------------------------------- */

    /**
     * Identifies if any value has been fetched last and what it is currently
     * @return info object
     */
    AlarmInfo current() {
        if (createObservable()) {
            return fetch();
        } else {
            return mInfoObservable.getValue();
        }
    }

    /**
     * Posts to any active observer the newly provided info
     * @param info new info object
     */
    void post(AlarmInfo info) {
        if (mInfoObservable != null) {
            mInfoObservable.postValue(info);
        }
    }

    /* ----------------------------------------------
     * PUBLIC FUNCTIONS
     * ---------------------------------------------- */

    /**
     * Creates and returns an active observable object that external modules can watch for changes
     * to the alarm information
     * @return life-cycle aware observable
     */
    public LiveData<AlarmInfo> observable() {
        if (createObservable()) {
            fetchThread();
        }
        return mInfoObservable;
    }
}
