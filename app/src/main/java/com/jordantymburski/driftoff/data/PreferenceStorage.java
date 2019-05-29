package com.jordantymburski.driftoff.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.jordantymburski.driftoff.domain.model.AlarmInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Implementation of storage using the SharedPreferences android interface
 */
@Singleton
public class PreferenceStorage {
    private static final long DEFAULT_ALARM = 0L;
    private static final int DEFAULT_TIME_HOUR = 21;
    private static final int DEFAULT_TIME_MINUTE = 30;

    private static final String KEY_ALARM = "alarm";
    private static final String KEY_TIME_HOUR = "timeHour";
    private static final String KEY_TIME_MINUTE = "timeMinute";

    private static final String STORE_NAME = "DataStorage";

    /**
     * Shared preference database connection
     */
    private final SharedPreferences mDatabase;

    /**
     * Main constructor
     * @param context android application context
     */
    @Inject
    public PreferenceStorage(Context context) {
        mDatabase = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
    }

    /* ----------------------------------------------
     * PUBLIC FUNCTIONS
     * ---------------------------------------------- */

    /**
     * Load all data from the shared preference storage
     * @return stored alarm info
     */
    public AlarmInfo load() {
        return new AlarmInfo(
                mDatabase.getLong(KEY_ALARM, DEFAULT_ALARM),
                mDatabase.getInt(KEY_TIME_HOUR, DEFAULT_TIME_HOUR),
                mDatabase.getInt(KEY_TIME_MINUTE, DEFAULT_TIME_MINUTE));
    }

    /**
     * Save all changes to the shared preference storage
     * @param info new alarm info
     */
    public void save(AlarmInfo info) {
        mDatabase.edit()
                .putLong(KEY_ALARM, info.alarm)
                .putInt(KEY_TIME_HOUR, info.timeHour)
                .putInt(KEY_TIME_MINUTE, info.timeMinute)
                .apply();
    }
}
