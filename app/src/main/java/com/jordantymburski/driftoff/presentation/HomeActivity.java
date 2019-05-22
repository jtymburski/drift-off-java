package com.jordantymburski.driftoff.presentation;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;

import com.jordantymburski.driftoff.R;
import com.jordantymburski.driftoff.domain.model.AlarmInfo;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HomeActivity extends FragmentActivity
        implements Observer<AlarmInfo>,
                   View.OnClickListener,
                   TimePickerDialog.OnTimeSetListener {
    private static final long DAY_START_HOUR = 8; // 8:00am inclusive
    private static final long DAY_END_HOUR = 18; // 6:00pm inclusive
    private static final long UPDATE_TIME_MS = TimeUnit.SECONDS.toMillis(30);

    // Colors
    private int mColorTextActive;
    private int mColorTextEdit;

    // Model
    private HomeViewModel mModel;
    private AlarmInfo mModelInfo;

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

        // Initialize the view model and set up the observable
        mModel = HomeViewModel.getInstance(this, getApplication());
        mModel.getInfoObservable().observe(this, this);
    }

    /* ----------------------------------------------
     * Activity OVERRIDES
     * ---------------------------------------------- */

    @Override
    public void onPause() {
        super.onPause();

        mHandler.removeCallbacks(mUpdateRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check that the theme is still correct
        if (mTheme != getThemeResource()) {
            recreate();
            return;
        }

        // Otherwise, proceed to update the UI
        updateView();
    }

    /* ----------------------------------------------
     * Observer OVERRIDES
     * ---------------------------------------------- */

    @Override
    public void onChanged(@Nullable AlarmInfo info) {
        if (info != null && !info.equals(mModelInfo)) {
            mModelInfo = info;
            updateView();
        }
    }

    /* ----------------------------------------------
     * OnClickListener OVERRIDES
     * ---------------------------------------------- */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.run_button:
                if (mModelInfo.isActive()) {
                    mModel.resetAlarm();
                } else {
                    mModel.setAlarm();
                }
                break;
            case R.id.time_text:
                if (!mModelInfo.isActive()) {
                    editTime();
                }
                break;
        }
    }

    /* ----------------------------------------------
     * OnTimeSetListener OVERRIDES
     * ---------------------------------------------- */

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mModel.setTime(hourOfDay, minute);
    }

    /* ----------------------------------------------
     * PRIVATE FUNCTIONS
     * ---------------------------------------------- */

    /**
     * Edit the alarm time
     */
    private void editTime() {
        new TimePickerDialog(this, this,
                mModelInfo.timeHour, mModelInfo.timeMinute,
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
        long hoursToStop = mModelInfo.getHoursTillAlarm();
        if (hoursToStop > 1) {
            return getResources().getQuantityString(
                    R.plurals.alarm_notice_hours, (int) hoursToStop, hoursToStop);
        }

        // Otherwise, it should display in minutes
        long minutesToStop = mModelInfo.getMinutesTillAlarm();
        return getResources().getQuantityString(
                R.plurals.alarm_notice_minutes, (int) minutesToStop, minutesToStop);
    }

    /**
     * Updates the active state
     */
    private void updateState() {
        mHandler.removeCallbacks(mUpdateRunnable);

        if (mModelInfo != null) {
            if (mModelInfo.isActive()) {
                mButtonRun.setImageResource(R.drawable.ic_stop);
                mTextTime.setTextColor(mColorTextActive);
                mTextPeriod.setTextColor(mColorTextActive);
                mTextRemaining.setText(getTimeTextRemaining());

                mHandler.postDelayed(mUpdateRunnable, UPDATE_TIME_MS);
            } else {
                mButtonRun.setImageResource(R.drawable.ic_play);
                mTextTime.setTextColor(mColorTextEdit);
                mTextPeriod.setTextColor(mColorTextEdit);
                mTextRemaining.setText(null);
            }
        }
    }

    /**
     * Updates the time setting displayed in the UI
     */
    private void updateTime() {
        if (mModelInfo != null) {
            Calendar alarmTime = mModelInfo.getTime();
            if (DateFormat.is24HourFormat(getApplicationContext())) {
                mTextTime.setText(DateFormat.getTimeFormat(getApplicationContext())
                        .format(alarmTime.getTime()));
                mTextPeriod.setText(null);
            } else {
                mTextTime.setText(DateFormat.format("h:mm", alarmTime));
                mTextPeriod.setText(DateFormat.format("a", alarmTime).toString()
                        .replace(".", ""));
            }
        }
    }

    /**
     * Updates all views
     */
    private void updateView() {
        updateState();
        updateTime();
    }
}
