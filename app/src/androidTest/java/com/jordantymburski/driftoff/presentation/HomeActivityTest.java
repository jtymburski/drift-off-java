package com.jordantymburski.driftoff.presentation;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.rule.ActivityTestRule;

import com.jordantymburski.driftoff.App;
import com.jordantymburski.driftoff.R;
import com.jordantymburski.driftoff.common.ContextProvider;
import com.jordantymburski.driftoff.data.PreferenceStorage;
import com.jordantymburski.driftoff.domain.DomainProvider;
import com.jordantymburski.driftoff.domain.model.AlarmInfo;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Calendar;
import java.util.Random;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HomeActivityTest {
    /**
     * Random number generator instance
     */
    private final Random mRandom = new Random();

    @Rule
    public final ActivityTestRule<HomeActivity> activityRule
            = new ActivityTestRule<>(HomeActivity.class);

    @BeforeClass
    public static void preSetup() {
        // Delete all existing storage to start fresh
        new PreferenceStorage(ContextProvider.get()).deleteAll();
    }

    /* ----------------------------------------------
     * TEST CASES
     * ---------------------------------------------- */

    @Test
    public void t01_initialLoad() throws InterruptedException {
        waitForUpdate();

        // Fetch the starting data
        final Context context = ContextProvider.get();
        final AlarmInfo startInfo = new PreferenceStorage(context).load();

        // Check the time
        checkTime(context, startInfo);

        // Check the remaining status
        checkText(getViewTextRemaining(), null);

        // Make sure the button is in play mode
        getViewBtnRun().check(matches(withImageDrawable(R.drawable.ic_play)));
    }

    @Test
    public void t02_timeDialogCancel() throws InterruptedException {
        waitForUpdate();
        final HomeActivity activity = activityRule.getActivity();

        // Fetch the starting data
        final Context context = ContextProvider.get();
        final AlarmInfo startInfo = new PreferenceStorage(context).load();

        // Check the time
        checkTime(context, startInfo);

        // Click on the time to open the calendar mod
        getViewTextTime().perform(click());
        waitForUpdate();

        // Check that the time picker is opened
        final TimePickerDialog timePicker = activity.mTimePicker;
        assertNotNull(timePicker);
        assertTrue(timePicker.isShowing());

        // Modify the time
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timePicker.cancel();
            }
        });
        waitForUpdate();
        assertFalse(timePicker.isShowing());

        // Check the time that it still hasn't changed
        checkTime(context, startInfo);
    }

    @Test
    public void t03_timeDialogOk() throws InterruptedException {
        waitForUpdate();
        final HomeActivity activity = activityRule.getActivity();

        // Fetch the starting data
        final Context context = ContextProvider.get();
        final AlarmInfo startInfo = new PreferenceStorage(context).load();

        // Check the time
        checkTime(context, startInfo);

        // Click on the time to open the calendar mod
        getViewTextTime().perform(click());
        waitForUpdate();

        // Check that the time picker is opened
        final TimePickerDialog timePicker = activity.mTimePicker;
        assertNotNull(timePicker);
        assertTrue(timePicker.isShowing());

        // Modify the time
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timePicker.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
            }
        });
        waitForUpdate();
        assertFalse(timePicker.isShowing());

        // Check the time that it still hasn't changed
        checkTime(context, startInfo);
    }

    @Test
    public void t04_timeDialogChange() throws InterruptedException {
        waitForUpdate();
        final HomeActivity activity = activityRule.getActivity();

        // Fetch the starting data
        final Context context = ContextProvider.get();
        final AlarmInfo startInfo = new PreferenceStorage(context).load();

        // Check the time
        checkTime(context, startInfo);

        // Click on the time to open the calendar mod
        getViewTextTime().perform(click());
        waitForUpdate();

        // Check that the time picker is opened
        final TimePickerDialog timePicker = activity.mTimePicker;
        assertNotNull(timePicker);
        assertTrue(timePicker.isShowing());

        // Modify the time
        final AlarmInfo newInfo = new AlarmInfo(startInfo,
                mRandom.nextInt(24), mRandom.nextInt(60));
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timePicker.updateTime(newInfo.timeHour, newInfo.timeMinute);
                timePicker.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
            }
        });
        waitForUpdate();
        assertFalse(timePicker.isShowing());

        // Check the time was updated
        checkTime(context, newInfo);
    }

    @Test
    public void t05_startAndStopAlarm() throws InterruptedException {
        waitForUpdate();

        // Fetch the starting data
        final Context context = ContextProvider.get();
        final AlarmInfo startInfo = new PreferenceStorage(context).load();

        // Check that the alarm is not active and the starting parameters are still maintained
        checkAlarmInactive(context, startInfo);

        // Start the alarm
        getViewBtnRun().perform(click());
        waitForUpdate();

        // Check that the alarm is now active and displayed
        checkAlarmActive(context, startInfo);

        // Stop the alarm
        getViewBtnRun().perform(click());
        waitForUpdate();

        // Check that the alarm is no longer displayed and back to the start
        checkAlarmInactive(context, startInfo);
    }

    @Test
    public void t06_alarmOverHour() throws InterruptedException {
        waitForUpdate();

        // Set the time to over 1 hour from now
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 2);
        final AlarmInfo info = new AlarmInfo(
                0L, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

        // Set it in the activity and connected backend
        final HomeActivity activity = activityRule.getActivity();
        activity.onTimeSet(null, info.timeHour, info.timeMinute);
        waitForUpdate();

        // Start the alarm
        final AlarmInfo infoActive = new AlarmInfo(info, info.getTimeInMillis());
        getViewBtnRun().perform(click());
        waitForUpdate();

        // Check the time string
        checkAlarmActive(ContextProvider.get(), info);
        checkText(getViewTextRemaining(), activity.getResources().getQuantityString(
                R.plurals.alarm_notice_hours, (int) infoActive.getHoursTillAlarm(),
                infoActive.getHoursTillAlarm()));

        // Stop the alarm
        getViewBtnRun().perform(click());
    }

    @Test
    public void t07_alarmUnderHour() throws InterruptedException {
        waitForUpdate();

        // Set the time to over 1 minute from now
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 2);
        final AlarmInfo info = new AlarmInfo(
                0L, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

        // Set it in the activity and connected backend
        final HomeActivity activity = activityRule.getActivity();
        activity.onTimeSet(null, info.timeHour, info.timeMinute);
        waitForUpdate();

        // Start the alarm
        final AlarmInfo infoActive = new AlarmInfo(info, info.getTimeInMillis());
        getViewBtnRun().perform(click());
        waitForUpdate();

        // Check the time string
        checkAlarmActive(ContextProvider.get(), info);
        checkText(getViewTextRemaining(), activity.getResources().getQuantityString(
                R.plurals.alarm_notice_minutes, (int) infoActive.getMinutesTillAlarm(),
                infoActive.getMinutesTillAlarm()));

        // Stop the alarm
        getViewBtnRun().perform(click());
    }

    @Test
    public void t08_noTimeDialogIfAlarm() throws InterruptedException {
        waitForUpdate();

        // Start the alarm
        getViewBtnRun().check(matches(withImageDrawable(R.drawable.ic_play)));
        getViewBtnRun().perform(click());
        waitForUpdate();

        // Try to open the dialog
        getViewTextTime().perform(click());
        waitForUpdate();

        // Check that the dialog was not opened
        getViewBtnRun().check(matches(withImageDrawable(R.drawable.ic_stop)));
        assertNull(activityRule.getActivity().mTimePicker);
    }

    @Test
    public void t09_alarmFired() throws InterruptedException {
        waitForUpdate();

        // Fetch the starting data
        final Context context = ContextProvider.get();
        final AlarmInfo startInfo = new PreferenceStorage(context).load();
        assertTrue(startInfo.isActive());

        // Check that the alarm is still active from the previous test case
        checkAlarmActive(context, startInfo);

        // Force the fire from the back-end domain use case
        final DomainProvider domainProvider = new DomainProvider();
        ((App) ContextProvider.get().getApplicationContext()).component().inject(domainProvider);
        domainProvider.stopAudio.execute();
        waitForUpdate();

        // Check that the alarm is no longer active
        checkAlarmInactive(context, startInfo);
    }

    @Test
    public void t10_setAlarmBackground() throws InterruptedException {
        waitForUpdate();

        // Fetch the starting data
        final Context context = ContextProvider.get();
        final AlarmInfo startInfo = new PreferenceStorage(context).load();
        assertFalse(startInfo.isActive());

        // Check that there is no active alarm
        checkAlarmInactive(context, startInfo);

        // Force the fire from the back-end domain use case
        final DomainProvider domainProvider = new DomainProvider();
        ((App) ContextProvider.get().getApplicationContext()).component().inject(domainProvider);
        domainProvider.setInfo.setAlarm();
        waitForUpdate();

        // Check that the alarm is now active
        checkAlarmActive(context, startInfo);
        getViewBtnRun().perform(click());
    }

    /* ----------------------------------------------
     * INTERNAL FUNCTIONS
     * ---------------------------------------------- */

    /**
     * Run a text comparison
     * @param view the espresso view to check on
     * @param text the text that should be in the field
     */
    private void checkText(ViewInteraction view, String text) {
        if (text != null) {
            view.check(matches(withText(text)));
        } else {
            view.check(matches(withText("")));
        }
    }

    /**
     * Checks if the alarm is active and displayed to the user
     * @param context application reference context
     * @param alarmInfo the alarm info that should be displayed
     */
    private void checkAlarmActive(Context context, AlarmInfo alarmInfo) {
        checkTime(context, alarmInfo);
        getViewTextRemaining().check(matches(CoreMatchers.not(withText(""))));
        getViewBtnRun().check(matches(withImageDrawable(R.drawable.ic_stop)));
    }

    /**
     * Checks if the alarm is inactive and displayed to the user
     * @param context application reference context
     * @param alarmInfo the alarm info that should be displayed
     */
    private void checkAlarmInactive(Context context, AlarmInfo alarmInfo) {
        checkTime(context, alarmInfo);
        checkText(getViewTextRemaining(), null);
        getViewBtnRun().check(matches(withImageDrawable(R.drawable.ic_play)));
    }

    /**
     * Check the displayed time in the UI
     * @param context application reference context
     * @param alarmInfo the alarm info that should be displayed
     */
    private void checkTime(Context context, AlarmInfo alarmInfo) {
        final Calendar alarmCalendar = alarmInfo.getTime();

        if (DateFormat.is24HourFormat(context)) {
            checkText(getViewTextTime(),
                    DateFormat.getTimeFormat(context).format(alarmCalendar.getTime()));
            checkText(getViewTextPeriod(), null);
        } else {
            checkText(getViewTextTime(),
                    DateFormat.format("h:mm", alarmCalendar).toString());
            checkText(getViewTextPeriod(), DateFormat.format("a", alarmCalendar).toString()
                    .replace(".", ""));
        }
    }

    /**
     * Fetch the run button
     * @return espresso view for testing
     */
    private ViewInteraction getViewBtnRun() {
        return onView(withId(R.id.run_button));
    }

    /**
     * Fetch the time period text field (am/pm)
     * @return espresso view for testing
     */
    private ViewInteraction getViewTextPeriod() {
        return onView(withId(R.id.time_period));
    }

    /**
     * Fetch the time remaining text (when the alarm is active and running)
     * @return espresso view for testing
     */
    private ViewInteraction getViewTextRemaining() {
        return onView(withId(R.id.time_remaining));
    }

    /**
     * Fetch the time text field
     * @return espresso view for testing
     */
    private ViewInteraction getViewTextTime() {
        return onView(withId(R.id.time_text));
    }

    /**
     * Wait for the UI to update due to the async LiveData response system
     * @throws InterruptedException if the wait ended early
     */
    private void waitForUpdate() throws InterruptedException {
        Thread.sleep(350);
    }

    /* ----------------------------------------------
     * STATIC ESPRESSO HELPERS
     * ---------------------------------------------- */

    /**
     * Converts a vector drawable into a bitmap. Clean up is responsibility of the caller (.recycle)
     * @param vectorDrawable the valid vector drawable
     * @return the new bitmap
     */
    private static Bitmap createBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    /**
     * Static matcher comparison to check that an image drawable matches a noted resource ID
     * @param resourceId the expected resource ID
     * @return the espresso matcher
     */
    private static Matcher<View> withImageDrawable(final int resourceId) {
        return new BoundedMatcher<View, ImageButton>(ImageButton.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has image drawable resource " + resourceId);
            }

            @Override
            public boolean matchesSafely(ImageButton imageButton) {
                Drawable expectedDrawable = imageButton.getContext().getDrawable(resourceId);
                if (imageButton.getDrawable() instanceof VectorDrawable
                        && expectedDrawable instanceof VectorDrawable) {
                    final Bitmap current = createBitmap((VectorDrawable) imageButton.getDrawable());
                    final Bitmap expected = createBitmap((VectorDrawable) expectedDrawable);
                    final boolean isSame = current.sameAs(expected);

                    current.recycle();
                    expected.recycle();

                    return isSame;
                }
                return false;
            }
        };
    }
}
