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

import androidx.lifecycle.MutableLiveData;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.rule.ActivityTestRule;

import com.jordantymburski.driftoff.R;
import com.jordantymburski.driftoff.common.ContextProvider;
import com.jordantymburski.driftoff.domain.MockDomain;
import com.jordantymburski.driftoff.domain.model.AlarmInfo;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;

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
     * The domain interception object
     */
    private static MockDomain mDomain;

    /**
     * Primary data observable
     */
    private static MutableLiveData<AlarmInfo> mObservable;

    /**
     * Random number generator instance
     */
    private static final Random mRandom = new Random();

    /**
     * Randomized starting alarm info
     */
    private static AlarmInfo mStartInfo = new AlarmInfo(
            0L, mRandom.nextInt(24), mRandom.nextInt(60));

    @Rule
    public final ActivityTestRule<HomeActivity> activityRule
            = new ActivityTestRule<>(HomeActivity.class);

    @BeforeClass
    public static void preSetup() {
        mDomain = new MockDomain();

        // Observable
        mObservable = new MutableLiveData<>();
        mObservable.postValue(mStartInfo);
        Mockito.when(mDomain.getInfo().observable()).thenReturn(mObservable);
    }

    @Before
    public void setup() {
        mDomain.clearInvocations();
    }

    /* ----------------------------------------------
     * TEST CASES
     * ---------------------------------------------- */

    @Test
    public void t01_initialLoad() throws InterruptedException {
        waitForUpdate();

        // Check the time
        checkTime(ContextProvider.get(), mStartInfo);

        // Check the remaining status
        checkText(getViewTextRemaining(), null);

        // Make sure the button is in play mode
        getViewBtnRun().check(matches(withImageDrawable(R.drawable.ic_play)));
    }

    @Test
    public void t02_timeDialogCancel() throws InterruptedException {
        waitForUpdate();
        final HomeActivity activity = activityRule.getActivity();

        // Check the time
        checkTime(activity, mStartInfo);

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

        // Check the time that no request to change it has been made
        mDomain.verifyZeroInteractions();
    }

    @Test
    public void t03_timeDialogOk() throws InterruptedException {
        waitForUpdate();
        final HomeActivity activity = activityRule.getActivity();

        // Check the time
        checkTime(activity, mStartInfo);

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

        // Check the time that the requested set time matches the existing
        Mockito.verify(mDomain.setInfo()).setTime(mStartInfo.timeHour, mStartInfo.timeMinute);
        mDomain.verifyNoMoreInteractions();
    }

    @Test
    public void t04_timeDialogChange() throws InterruptedException {
        waitForUpdate();
        final HomeActivity activity = activityRule.getActivity();

        // Check the time
        checkTime(activity, mStartInfo);

        // Click on the time to open the calendar mod
        getViewTextTime().perform(click());
        waitForUpdate();

        // Check that the time picker is opened
        final TimePickerDialog timePicker = activity.mTimePicker;
        assertNotNull(timePicker);
        assertTrue(timePicker.isShowing());

        // Modify the time
        final AlarmInfo newInfo = new AlarmInfo(mStartInfo,
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

        // Check the time set that was requested matches the new one randomly defined above
        Mockito.verify(mDomain.setInfo()).setTime(newInfo.timeHour, newInfo.timeMinute);
        mDomain.verifyNoMoreInteractions();

        // Pass it through to the observer and witness the update
        mStartInfo = newInfo;
        mObservable.postValue(newInfo);
        waitForUpdate();
        checkTime(activity, newInfo);
    }

    @Test
    public void t05_startAndStopAlarm() throws InterruptedException {
        waitForUpdate();
        final Context context = ContextProvider.get();

        // Check that the alarm is not active and the starting parameters are still maintained
        checkAlarmInactive(context, mStartInfo);

        // Start the alarm
        getViewBtnRun().perform(click());
        waitForUpdate();

        // Check that the alarm was attempted to be set on the domain
        Mockito.verify(mDomain.setInfo()).setAlarm();
        mDomain.verifyNoMoreInteractions();

        // Set the alarm in the observer and witness the result
        final AlarmInfo setInfo = new AlarmInfo(mStartInfo, mStartInfo.getTimeInMillis());
        mObservable.postValue(setInfo);
        waitForUpdate();
        checkAlarmActive(context, setInfo);

        // Stop the alarm
        getViewBtnRun().perform(click());
        waitForUpdate();

        // Check that the activity requested the alarm to be reset to the domain
        Mockito.verify(mDomain.setInfo()).resetAlarm();
        mDomain.verifyNoMoreInteractions();

        // Check that the alarm is no longer displayed and back to the start
        mObservable.postValue(mStartInfo);
        waitForUpdate();
        checkAlarmInactive(context, mStartInfo);
    }

    @Test
    public void t06_alarmOverHour() throws InterruptedException {
        final HomeActivity activity = activityRule.getActivity();

        // Fetch the time for over 1 hour from now
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 2);
        final AlarmInfo info = new AlarmInfo(
                0L, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

        // Start the alarm
        final AlarmInfo infoActive = new AlarmInfo(info, info.getTimeInMillis());
        mObservable.postValue(infoActive);
        waitForUpdate();

        // Check the time string
        checkAlarmActive(activity, infoActive);
        checkText(getViewTextRemaining(), activity.getResources().getQuantityString(
                R.plurals.alarm_notice_hours, (int) infoActive.getHoursTillAlarm(),
                infoActive.getHoursTillAlarm()));

        // Reset the observable state
        mObservable.postValue(mStartInfo);
    }

    @Test
    public void t07_alarmUnderHour() throws InterruptedException {
        final HomeActivity activity = activityRule.getActivity();

        // Fetch the time for over 1 minute from now
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 2);
        final AlarmInfo info = new AlarmInfo(
                0L, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

        // Start the alarm
        mStartInfo = new AlarmInfo(info, info.getTimeInMillis());
        mObservable.postValue(mStartInfo);
        waitForUpdate();

        // Check the time string
        checkAlarmActive(activity, mStartInfo);
        checkText(getViewTextRemaining(), activity.getResources().getQuantityString(
                R.plurals.alarm_notice_minutes, (int) mStartInfo.getMinutesTillAlarm(),
                mStartInfo.getMinutesTillAlarm()));
    }

    @Test
    public void t08_noTimeDialogIfAlarm() throws InterruptedException {
        waitForUpdate();
        final HomeActivity activity = activityRule.getActivity();

        // Check that an alarm is active
        assertTrue(mStartInfo.isActive());
        checkAlarmActive(activity, mStartInfo);

        // Try to open the dialog
        getViewTextTime().perform(click());
        waitForUpdate();
        assertNull(activity.mTimePicker);
    }

    @Test
    public void t09_alarmFired() throws InterruptedException {
        waitForUpdate();
        final Context context = ContextProvider.get();

        // Check that an alarm is active
        assertTrue(mStartInfo.isActive());
        checkAlarmActive(context, mStartInfo);

        // Change to a non-active alarm and update
        mStartInfo = new AlarmInfo(mStartInfo, 0L);
        assertFalse(mStartInfo.isActive());
        mObservable.postValue(mStartInfo);
        waitForUpdate();

        // Check that the alarm is no longer active
        checkAlarmInactive(context, mStartInfo);
    }

    @Test
    public void t10_setAlarmBackground() throws InterruptedException {
        waitForUpdate();
        final Context context = ContextProvider.get();

        // Make sure the alarm is inactive
        assertFalse(mStartInfo.isActive());
        checkAlarmInactive(context, mStartInfo);

        // Change to an active alarm
        final AlarmInfo activeInfo = new AlarmInfo(mStartInfo, mStartInfo.getTimeInMillis());
        assertTrue(activeInfo.isActive());
        mObservable.postValue(activeInfo);
        waitForUpdate();

        // Check for an active alarm
        checkAlarmActive(context, activeInfo);
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
