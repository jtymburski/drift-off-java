package com.jordantymburski.driftoff;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

    // Alarm intent
    private boolean mAlarmActive = false;
    private PendingIntent mAlarmIntent;

    // Preference storage editor
    private SharedPreferences mPreferenceStorage;

    // Time setting
    private int mTimeHour = DEFAULT_HOUR;
    private int mTimeMinute = DEFAULT_MINUTE;

    // UI
    private ImageButton mButtonRun;
    private TextView mTextTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // UI references
        mButtonRun = findViewById(R.id.run_button);
        mButtonRun.setOnClickListener(this);
        mTextTime = findViewById(R.id.time_text);
        mTextTime.setOnClickListener(this);

        // The alarm pending intent
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        mAlarmIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        // Fetch the stored information;
        mPreferenceStorage = getSharedPreferences(PREFERENCE_STORAGE, 0);
        mTimeHour = mPreferenceStorage.getInt(PREFERENCE_HOUR, DEFAULT_HOUR);
        mTimeMinute = mPreferenceStorage.getInt(PREFERENCE_MINUTE, DEFAULT_MINUTE);
        mAlarmActive = mPreferenceStorage.getLong(PREFERENCE_ALARM, 0L)
                > System.currentTimeMillis();
        updateUI();
    }

    /* ==============================================
     * OnClickListener OVERRIDES
     * ============================================== */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.run_button:
                if (mAlarmActive) {
                    cancelAlarm();
                } else {
                    scheduleAlarm();
                }
                break;
            case R.id.time_text:
                if (!mAlarmActive) {
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
        updateUI();
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
            alarmManager.cancel(mAlarmIntent);
            mPreferenceStorage.edit().putLong(PREFERENCE_ALARM, 0L).apply();
            mAlarmActive = false;
            updateUI();
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
     * Schedule the alarm to go off
     */
    private void scheduleAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            // Generate the time the alarm should go off
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, mTimeHour);
            calendar.set(Calendar.MINUTE, mTimeMinute);
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            // Schedule the alarm
            long alarmInMillis = calendar.getTimeInMillis();
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, alarmInMillis, mAlarmIntent);
            long firesInMillis = alarmInMillis - System.currentTimeMillis();
            mPreferenceStorage.edit().putLong(PREFERENCE_ALARM, alarmInMillis).apply();
            mAlarmActive = true;
            updateUI();

            // Determine when it will fire
            long minutesToStop = TimeUnit.MILLISECONDS.toMinutes(firesInMillis);
            Toast.makeText(this, getResources().getQuantityString(
                        R.plurals.alarm_notice, (int) minutesToStop, minutesToStop),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Updates the UI on changes
     */
    private void updateUI() {
        // Current time displayed
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, mTimeHour);
        c.set(Calendar.MINUTE, mTimeMinute);
        c.set(Calendar.SECOND, 0);
        mTextTime.setText(DateFormat.getTimeFormat(this).format(c.getTime()));

        // Color based on if its active or not
        mTextTime.setTextColor(
                getColor(mAlarmActive ? R.color.colorTextActive : R.color.colorTextEdit));

        // Button state
        mButtonRun.setImageResource(mAlarmActive ? R.drawable.ic_stop : R.drawable.ic_play);
    }
}
