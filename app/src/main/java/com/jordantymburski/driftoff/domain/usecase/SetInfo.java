package com.jordantymburski.driftoff.domain.usecase;

import com.jordantymburski.driftoff.domain.adapter.AlarmScheduler;
import com.jordantymburski.driftoff.domain.adapter.Storage;
import com.jordantymburski.driftoff.domain.model.AlarmInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Use case to set and update the persisted alarm information
 */
@Singleton
public class SetInfo {
    /**
     * Alarm scheduling port
     */
    private final AlarmScheduler mAlarmScheduler;

    /**
     * Get alarm info use case to fetch existing cached value
     */
    private final GetInfo mGetInfo;

    /**
     * Connection to the storage layer. Used to fetch current persisted state
     */
    private final Storage mStorage;

    /**
     * Main constructor
     * @param alarmScheduler manages alarm job scheduling
     * @param getInfo use case to get the current alarm information
     * @param storage persisted storage implementation
     */
    @Inject
    public SetInfo(AlarmScheduler alarmScheduler, GetInfo getInfo, Storage storage) {
        mAlarmScheduler = alarmScheduler;
        mGetInfo = getInfo;
        mStorage = storage;
    }

    /* ----------------------------------------------
     * PRIVATE FUNCTIONS
     * ---------------------------------------------- */

    /**
     * Reset the alarm (unset). Called when the alarm either goes off or is cancelled. This is the
     * internal async logic
     */
    private void resetAlarmAsync() {
        mAlarmScheduler.cancel();
        update(new AlarmInfo(mGetInfo.current(), 0L));
    }

    /**
     * Set the alarm to the current time setpoint value. This is the internal async logic
     */
    private void setAlarmAsync() {
        final AlarmInfo currentInfo = mGetInfo.current();

        final long alarmTime = currentInfo.getTimeInMillis();
        mAlarmScheduler.schedule(alarmTime);

        update(new AlarmInfo(currentInfo, alarmTime));
    }

    /**
     * Set the time setpoint. This is the internal async logic
     * @param hour 0-23 hour setpoint
     * @param minute minute setpoint
     */
    private void setTimeAsync(final int hour, final int minute) {
        update(new AlarmInfo(mGetInfo.current(), hour, minute));
    }

    /**
     * Update the alarm object in the persisted storage and for any active observers
     * @param info the new alarm info object
     */
    private void update(AlarmInfo info) {
        if (!info.equals(mGetInfo.current())) {
            mGetInfo.post(info);
            mStorage.save(info);
        }
    }

    /* ----------------------------------------------
     * PUBLIC FUNCTIONS
     * ---------------------------------------------- */

    /**
     * Reset the alarm (unset). Called when the alarm either goes off or is cancelled
     */
    public void resetAlarm() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                resetAlarmAsync();
            }
        }).start();
    }

    /**
     * Set the alarm to the current time setpoint value
     */
    public void setAlarm() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                setAlarmAsync();
            }
        }).start();
    }

    /**
     * Set the time setpoint
     * @param hour 0-23 hour setpoint
     * @param minute minute setpoint
     */
    public void setTime(final int hour, final int minute) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                setTimeAsync(hour, minute);
            }
        }).start();
    }
}
