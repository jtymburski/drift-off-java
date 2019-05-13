package com.jordantymburski.driftoff;

import android.app.Activity;
import android.app.TimePickerDialog;
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
        implements AlarmListener,
                   View.OnClickListener,
                   TimePickerDialog.OnTimeSetListener {
    private static final long DAY_START_HOUR = 8; // 8:00am inclusive
    private static final long DAY_END_HOUR = 18; // 6:00pm inclusive
    private static final long UPDATE_TIME_MS = TimeUnit.SECONDS.toMillis(30);

    // Controller and model
    private AlarmController mAlarmController;
    private AlarmData mAlarmModel;

    // Colors
    private int mColorTextActive;
    private int mColorTextEdit;

    // UI
    private ImageButton mButtonRun;
    private TextView mTextPeriod;
    private TextView mTextRemaining;
    private TextView mTextTime;

    // UI theme
    private int mTheme;

    // Update runnable
    private final Handler mHandler = new Handler();
    private final Runnable mUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateState();
            if (mAlarmModel.isActive()) {
                mHandler.postDelayed(mUpdateRunnable, UPDATE_TIME_MS);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTheme = getThemeResource();
        setTheme(mTheme);
        setContentView(R.layout.activity_home);

        // Full screen under the status bar at the top and nav bar at the bottom
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Initialize model and controller references
        mAlarmController = AlarmController.getInstance(this);
        mAlarmModel = AlarmData.getInstance(this);

        // Colors
        mColorTextActive = getColor(R.color.textActive);
        mColorTextEdit = getColor(R.color.textEdit);

        // UI references
        mButtonRun = findViewById(R.id.run_button);
        mButtonRun.setOnClickListener(this);
        mTextPeriod = findViewById(R.id.time_period);
        mTextRemaining = findViewById(R.id.time_remaining);
        mTextTime = findViewById(R.id.time_text);
        mTextTime.setOnClickListener(this);
    }

    /* ==============================================
     * Activity OVERRIDES
     * ============================================== */

    @Override
    public void onPause() {
        super.onPause();

        mHandler.removeCallbacks(mUpdateRunnable);
        mAlarmController.removeListener();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check that the theme is still correct
        if (mTheme != getThemeResource()) {
            recreate();
            return;
        }

        // Otherwise, proceed to set-up and show the UI
        mAlarmController.addListener(this);
        updateTime();
        mHandler.post(mUpdateRunnable);
    }

    /* ==============================================
     * AlarmListener OVERRIDES
     * ============================================== */

    @Override
    public void alarmOff() {
        mHandler.post(mUpdateRunnable);
    }

    /* ==============================================
     * OnClickListener OVERRIDES
     * ============================================== */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.run_button:
                if (mAlarmModel.isActive()) {
                    cancelAlarm();
                } else {
                    scheduleAlarm();
                }
                break;
            case R.id.time_text:
                if (!mAlarmModel.isActive()) {
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
        mAlarmModel.setTime(hourOfDay, minute);
        updateTime();
    }

    /* ==============================================
     * PRIVATE FUNCTIONS
     * ============================================== */

    /**
     * Cancel a scheduled alarm
     */
    private void cancelAlarm() {
        mAlarmController.cancel();
        mHandler.post(mUpdateRunnable);
    }

    /**
     * Edit the alarm time
     */
    private void editTime() {
        new TimePickerDialog(this, this,
                mAlarmModel.getTimeHour(), mAlarmModel.getTimeMinute(),
                DateFormat.is24HourFormat(getApplicationContext())).show();
    }

    /**
     * Determines the theme resource that should be used
     * @return style resource ID
     */
    private int getThemeResource() {
        int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        return (hourOfDay >= DAY_START_HOUR && hourOfDay <= DAY_END_HOUR)
                ? R.style.AppTheme_Light : R.style.AppTheme;
    }

    /**
     * Assembles the string status for the time that is remaining before the alarm fires
     * @return time remaining text
     */
    private String getTimeTextRemaining() {
        // Check if it should display in hours
        long hoursToStop = mAlarmModel.getHoursTillAlarm();
        if (hoursToStop > 1) {
            return getResources().getQuantityString(
                    R.plurals.alarm_notice_hours, (int) hoursToStop, hoursToStop);
        }

        // Otherwise, it should display in minutes
        long minutesToStop = mAlarmModel.getMinutesTillAlarm();
        return getResources().getQuantityString(
                R.plurals.alarm_notice_minutes, (int) minutesToStop, minutesToStop);
    }

    /**
     * Schedule the alarm to go off
     */
    private void scheduleAlarm() {
        mAlarmController.set();
        mHandler.post(mUpdateRunnable);
    }

    /**
     * Updates the active state
     */
    private void updateState() {
        if (mAlarmModel.isActive()) {
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
        Calendar alarmTime = mAlarmModel.getTime();
        if(DateFormat.is24HourFormat(getApplicationContext())) {
            mTextTime.setText(
                    DateFormat.getTimeFormat(getApplicationContext()).format(alarmTime.getTime()));
            mTextPeriod.setText(null);
        } else {
            mTextTime.setText(DateFormat.format("h:mm", alarmTime));
            mTextPeriod.setText(DateFormat.format("a", alarmTime).toString()
                    .replace(".", ""));
        }
    }
}
