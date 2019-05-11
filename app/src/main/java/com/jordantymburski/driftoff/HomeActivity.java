package com.jordantymburski.driftoff;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HomeActivity extends Activity
        implements View.OnClickListener,
                   TimePickerDialog.OnTimeSetListener {
    private static final int DEFAULT_HOUR = 21;
    private static final int DEFAULT_MINUTE = 30;
    private static final String PREFERENCE_ALARM = "lastAlarm";
    private static final String PREFERENCE_HOUR = "lastHourSet";
    private static final String PREFERENCE_MINUTE = "lastMinuteSet";
    private static final String PREFERENCE_STORAGE = "PreferenceData";
    private static final long UPDATE_TIME_MS = TimeUnit.SECONDS.toMillis(30);

    // Alarm intent
    private PendingIntent mAlarmIntent;
    private long mAlarmTime = 0;

    // Colors
    private int mColorTextActive;
    private int mColorTextEdit;

    // Preference storage editor
    private SharedPreferences mPreferenceStorage;

    // Time setting
    private int mTimeHour = DEFAULT_HOUR;
    private int mTimeMinute = DEFAULT_MINUTE;

    // UI
    private ImageButton mButtonRun;
    private TextView mTextPeriod;
    private TextView mTextRemaining;
    private TextView mTextTime;

    // Update runnable
    private final Handler mHandler = new Handler();
    private final Runnable mUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateState();
            if (isAlarmActive()) {
                mHandler.postDelayed(mUpdateRunnable, UPDATE_TIME_MS);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Full screen under the status bar at the top and nav bar at the bottom
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // UI references
        mButtonRun = findViewById(R.id.run_button);
        mButtonRun.setOnClickListener(this);
        mTextPeriod = findViewById(R.id.time_period);
        mTextRemaining = findViewById(R.id.time_remaining);
        mTextTime = findViewById(R.id.time_text);
        mTextTime.setOnClickListener(this);

        // Colors
        mColorTextActive = getColor(R.color.colorTextActive);
        mColorTextEdit = getColor(R.color.colorTextEdit);

        // The alarm pending intent
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        mAlarmIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        // Fetch the stored information;
        mPreferenceStorage = getSharedPreferences(PREFERENCE_STORAGE, 0);
        mTimeHour = mPreferenceStorage.getInt(PREFERENCE_HOUR, DEFAULT_HOUR);
        mTimeMinute = mPreferenceStorage.getInt(PREFERENCE_MINUTE, DEFAULT_MINUTE);
        mAlarmTime = mPreferenceStorage.getLong(PREFERENCE_ALARM, 0L);
    }

    /* ==============================================
     * Activity OVERRIDES
     * ============================================== */

    @Override
    public void onPause() {
        super.onPause();

        mHandler.removeCallbacks(mUpdateRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();

        updateTime();
        mHandler.post(mUpdateRunnable);
    }

    /* ==============================================
     * OnClickListener OVERRIDES
     * ============================================== */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.run_button:
                if (isAlarmActive()) {
                    cancelAlarm();
                } else {
                    scheduleAlarm();
                }
                break;
            case R.id.time_text:
                if (!isAlarmActive()) {
                    editTime();
                }
                break;
        }
    }

    /* ==============================================
     * OnTimeSetListener OVERRIDES
     * ============================================== */

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mTimeHour = hourOfDay;
        mTimeMinute = minute;
        mPreferenceStorage.edit()
                .putInt(PREFERENCE_HOUR, mTimeHour)
                .putInt(PREFERENCE_MINUTE, mTimeMinute)
                .apply();
        updateTime();
    }

    /* ==============================================
     * PRIVATE FUNCTIONS
     * ============================================== */

    /**
     * Cancel a scheduled alarm
     */
    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            mAlarmTime = 0L;
            alarmManager.cancel(mAlarmIntent);
            mPreferenceStorage.edit().putLong(PREFERENCE_ALARM, mAlarmTime).apply();
            mHandler.post(mUpdateRunnable);
        }
    }

    /**
     * Edit the alarm time
     */
    private void editTime() {
        new TimePickerDialog(this, this, mTimeHour, mTimeMinute,
                DateFormat.is24HourFormat(getApplicationContext())).show();
    }

    /**
     * Determines if the alarm is currently active and running to stop music in a fixed time
     * @return TRUE if active. FALSE if off
     */
    private boolean isAlarmActive() {
        return (mAlarmTime > System.currentTimeMillis());
    }

    /**
     * Creates a calendar representation of when the alarm is expected to trigger
     * @return the calendar alarm time, as per the time setpoint
     */
    private Calendar getAlarmTime() {
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
     * Assembles the string status for the time that is remaining before the alarm fires
     * @return time remaining text
     */
    private String getTimeTextRemaining() {
        long millisToStop = mAlarmTime - System.currentTimeMillis();

        // Check if it should display in hours
        // This is rounded up: 1 to 60 minutes = 1 hour, 61 to 120 minutes  = 2 hours, etc
        long hoursToStop = TimeUnit.MILLISECONDS.toHours(
                millisToStop + TimeUnit.HOURS.toMillis(1) - 1);
        if (hoursToStop > 1) {
            return getResources().getQuantityString(
                    R.plurals.alarm_notice_hours, (int) hoursToStop, hoursToStop);
        }

        // Otherwise, it should display in minutes
        // This is rounded up: 1 to 60 seconds = 1 minute, 61 to 120 seconds = 2 minutes, etc
        long minutesToStop = TimeUnit.MILLISECONDS.toMinutes(
                millisToStop + TimeUnit.MINUTES.toMillis(1) - 1);
        return getResources().getQuantityString(
                R.plurals.alarm_notice_minutes, (int) minutesToStop, minutesToStop);
    }

    /**
     * Schedule the alarm to go off
     */
    private void scheduleAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            mAlarmTime = getAlarmTime().getTimeInMillis();
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, mAlarmTime, mAlarmIntent);
            mPreferenceStorage.edit().putLong(PREFERENCE_ALARM, mAlarmTime).apply();
            mHandler.post(mUpdateRunnable);
        }
    }

    /**
     * Updates the active state
     */
    private void updateState() {
        if (isAlarmActive()) {
            mButtonRun.setImageResource(R.drawable.ic_stop);
            mTextTime.setTextColor(mColorTextActive);
            mTextPeriod.setTextColor(mColorTextActive);
            mTextRemaining.setText(getTimeTextRemaining());
        } else {
            mButtonRun.setImageResource(R.drawable.ic_play);
            mTextTime.setTextColor(mColorTextEdit);
            mTextPeriod.setTextColor(mColorTextEdit);
            mTextRemaining.setText(null);
        }
    }

    /**
     * Updates the time setting displayed in the UI
     */
    private void updateTime() {
        Calendar alarmTime = getAlarmTime();
        if(DateFormat.is24HourFormat(getApplicationContext())) {
            mTextTime.setText(
                    DateFormat.getTimeFormat(getApplicationContext()).format(alarmTime.getTime()));
            mTextPeriod.setText(null);
        } else {
            mTextTime.setText(DateFormat.format("h:m", alarmTime));
            mTextPeriod.setText(DateFormat.format("a", alarmTime));
        }
    }
}
