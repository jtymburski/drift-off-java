package com.jordantymburski.driftoff.domain.model;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Current alarm information including active requested alarms and set visible time points
 */
public class AlarmInfo {
    /**
     * The unix epoch value of the last requested alarm
     */
    public final long alarm;

    /**
     * The time hour setpoint. 24 hour clock (0-23)
     */
    public final int timeHour;

    /**
     * The time minute setpoint
     */
    public final int timeMinute;

    /**
     * Set constructor for all variables
     * @param alarm unix epoch alarm value
     * @param timeHour 0-23 hour setpoint
     * @param timeMinute minute setpoint
     */
    public AlarmInfo(long alarm, int timeHour, int timeMinute) {
        this.alarm = alarm;
        this.timeHour = timeHour;
        this.timeMinute = timeMinute;
    }

    /**
     * Takes an existing alarm info object and just modifies the alarm value
     * @param existing current alarm info object
     * @param alarm unix epoch alarm value
     */
    public AlarmInfo(AlarmInfo existing, long alarm) {
        this(alarm, existing.timeHour, existing.timeMinute);
    }

    /**
     * Takes an existing alarm info object and just modifies the time values
     * @param existing current alarm info object
     * @param timeHour 0-23 hour setpoint
     * @param timeMinute minute setpoint
     */
    public AlarmInfo(AlarmInfo existing, int timeHour, int timeMinute) {
        this(existing.alarm, timeHour, timeMinute);
    }

    /**
     * Compare this current object to another for content equality
     * @param o the other object to compare
     * @return TRUE if equal. FALSE if not
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof AlarmInfo)) {
            return false;
        }

        AlarmInfo info = (AlarmInfo) o;
        return info.alarm == alarm
                && info.timeHour == timeHour
                && info.timeMinute == timeMinute;
    }

    /* ----------------------------------------------
     * PRIVATE FUNCTIONS
     * ---------------------------------------------- */

    /**
     * Calculates the milliseconds till the alarm trigger
     * @return in milliseconds
     */
    private long getMillisTillAlarm() {
        final long systemTime = System.currentTimeMillis();
        return (alarm > systemTime ? alarm - systemTime : 0L);
    }

    /* ----------------------------------------------
     * PUBLIC FUNCTIONS
     * ---------------------------------------------- */

    /**
     * Calculates the hours till the alarm will trigger. This is rounded up:
     * 1 to 60 minutes = 1 hour, 61 to 120 minutes  = 2 hours, etc
     * @return in hours
     */
    public long getHoursTillAlarm() {
        return TimeUnit.MILLISECONDS.toHours(
                getMillisTillAlarm() + TimeUnit.HOURS.toMillis(1) - 1);
    }

    /**
     * Calculates the minutes till the alarm will trigger. This is rounded up:
     * 1 to 60 seconds = 1 minute, 61 to 120 seconds = 2 minutes, etc
     * @return in minutes
     */
    public long getMinutesTillAlarm() {
        return TimeUnit.MILLISECONDS.toMinutes(
                getMillisTillAlarm() + TimeUnit.MINUTES.toMillis(1) - 1);
    }

    /**
     * Assembles the time setpoint
     * @return a calendar object
     */
    public Calendar getTime() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, timeHour);
        c.set(Calendar.MINUTE, timeMinute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        if (c.getTimeInMillis() <= System.currentTimeMillis()) {
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
        return c;
    }

    /**
     * Calculates the unix epoch time that matches the time setpoint
     * @return in milliseconds
     */
    public long getTimeInMillis() {
        return getTime().getTimeInMillis();
    }

    /**
     * Is the alarm active and waiting to trigger to stop any playing music?
     * @return TRUE if alarm is active. FALSE if off
     */
    public boolean isActive() {
        return (alarm > System.currentTimeMillis());
    }
}
