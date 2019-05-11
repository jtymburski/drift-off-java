package com.jordantymburski.driftoff;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

class AlarmData {
    private static final int DEFAULT_HOUR = 21;
    private static final int DEFAULT_MINUTE = 30;
    private static final String PREFERENCE_ALARM = "lastAlarm";
    private static final String PREFERENCE_HOUR = "lastHourSet";
    private static final String PREFERENCE_MINUTE = "lastMinuteSet";
    private static final String PREFERENCE_STORAGE = "PreferenceData";

    // singleton reference
    private static AlarmData mInstance;

    // preference storage editor
    private final SharedPreferences mPreferenceStorage;

    // time settings
    private long mAlarm;
    private int mTimeHour;
    private int mTimeMinute;

    /* ==============================================
     * CONSTRUCTOR
     * ============================================== */

    private AlarmData(Context context) {
        mPreferenceStorage = context.getSharedPreferences(PREFERENCE_STORAGE, 0);
        mAlarm = mPreferenceStorage.getLong(PREFERENCE_ALARM, 0L);
        mTimeHour = mPreferenceStorage.getInt(PREFERENCE_HOUR, DEFAULT_HOUR);
        mTimeMinute = mPreferenceStorage.getInt(PREFERENCE_MINUTE, DEFAULT_MINUTE);
    }

    /* ==============================================
     * STATIC INSTANCE FETCH
     * ============================================== */

    static AlarmData getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AlarmData(context);
        }
        return mInstance;
    }

    /* ==============================================
     * INTERNAL FUNCTIONS
     * ============================================== */

    private long getMillisTillAlarm() {
        return (mAlarm - System.currentTimeMillis());
    }

    /* ==============================================
     * FUNCTIONS
     * ============================================== */

    long getAlarm() {
        return mAlarm;
    }

    // This is rounded up: 1 to 60 minutes = 1 hour, 61 to 120 minutes  = 2 hours, etc
    long getHoursTillAlarm() {
        return TimeUnit.MILLISECONDS.toHours(
                getMillisTillAlarm() + TimeUnit.HOURS.toMillis(1) - 1);
    }

    // This is rounded up: 1 to 60 seconds = 1 minute, 61 to 120 seconds = 2 minutes, etc
    long getMinutesTillAlarm() {
        return TimeUnit.MILLISECONDS.toMinutes(
                getMillisTillAlarm() + TimeUnit.MINUTES.toMillis(1) - 1);
    }

    Calendar getTime() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, mTimeHour);
        c.set(Calendar.MINUTE, mTimeMinute);
        c.set(Calendar.SECOND, 0);
        if (c.getTimeInMillis() <= System.currentTimeMillis()) {
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
        return c;
    }

    int getTimeHour() {
        return mTimeHour;
    }

    long getTimeInMillis() {
        return getTime().getTimeInMillis();
    }

    int getTimeMinute() {
        return mTimeMinute;
    }

    boolean isActive() {
        return (mAlarm > System.currentTimeMillis());
    }

    void setTime(int hour, int minute) {
        mTimeHour = hour;
        mTimeMinute = minute;
        mPreferenceStorage.edit()
                .putInt(PREFERENCE_HOUR, mTimeHour)
                .putInt(PREFERENCE_MINUTE, mTimeMinute)
                .apply();
    }

    void setAlarm(long alarm) {
        mAlarm = alarm;
        mPreferenceStorage.edit()
                .putLong(PREFERENCE_ALARM, mAlarm)
                .apply();
    }
}
