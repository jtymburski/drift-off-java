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

    /**
     * Fetches the currently configured alarm setpoint (when it will stop next). 0 if off
     * @return alarm setpoint, in milliseconds, correlated to System.currentTimeMillis()
     */
    long getAlarm() {
        return mAlarm;
    }

    /**
     * Calculates the hours till the alarm will trigger. This is rounded up:
     * 1 to 60 minutes = 1 hour, 61 to 120 minutes  = 2 hours, etc
     * @return in hours
     */
    long getHoursTillAlarm() {
        return TimeUnit.MILLISECONDS.toHours(
                getMillisTillAlarm() + TimeUnit.HOURS.toMillis(1) - 1);
    }

    /**
     * Calculates the minutes till the alarm will trigger. This is rounded up:
     * 1 to 60 seconds = 1 minute, 61 to 120 seconds = 2 minutes, etc
     * @return in minutes
     */
    long getMinutesTillAlarm() {
        return TimeUnit.MILLISECONDS.toMinutes(
                getMillisTillAlarm() + TimeUnit.MINUTES.toMillis(1) - 1);
    }

    /**
     * Assembles the user time setpoint of when the alarm could be activated
     * @return a calendar object
     */
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

    /**
     * Fetches the user time hour portion of the setpoint
     * @return in 24 hour clock
     */
    int getTimeHour() {
        return mTimeHour;
    }

    /**
     * Calculates the system time that matches the user time setpoint
     * @return in milliseconds
     */
    long getTimeInMillis() {
        return getTime().getTimeInMillis();
    }

    /**
     * Fetches the user time minute portion of the setpoint
     * @return in minutes
     */
    int getTimeMinute() {
        return mTimeMinute;
    }

    /**
     * Is the alarm active and waiting to trigger to stop any playing music?
     * @return TRUE if alarm is active. FALSE if off
     */
    boolean isActive() {
        return (mAlarm > System.currentTimeMillis());
    }

    /**
     * Sets the user setpoint for possible alarm time
     * @param hour in 24 hours of the day (0-23)
     * @param minute in minutes
     */
    void setTime(int hour, int minute) {
        mTimeHour = hour;
        mTimeMinute = minute;
        mPreferenceStorage.edit()
                .putInt(PREFERENCE_HOUR, mTimeHour)
                .putInt(PREFERENCE_MINUTE, mTimeMinute)
                .apply();
    }

    /**
     * Sets the alarm that has been configured to trigger
     * @param alarm in system time milliseconds. 0 to disable
     */
    void setAlarm(long alarm) {
        mAlarm = alarm;
        mPreferenceStorage.edit()
                .putLong(PREFERENCE_ALARM, mAlarm)
                .apply();
    }
}
