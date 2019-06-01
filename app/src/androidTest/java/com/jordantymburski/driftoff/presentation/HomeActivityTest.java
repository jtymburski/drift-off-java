package com.jordantymburski.driftoff.presentation;

import android.content.Context;
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

import com.jordantymburski.driftoff.R;
import com.jordantymburski.driftoff.common.ContextProvider;
import com.jordantymburski.driftoff.data.PreferenceStorage;
import com.jordantymburski.driftoff.domain.model.AlarmInfo;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Calendar;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HomeActivityTest {
    @Rule
    public ActivityTestRule<HomeActivity> activityRule = new ActivityTestRule<>(HomeActivity.class);

    @BeforeClass
    public static void preSetup() {
        // Delete all existing storage to start fresh
        new PreferenceStorage(ContextProvider.get()).deleteAll();
    }

    /* ----------------------------------------------
     * TEST CASES
     * ---------------------------------------------- */

    @Test
    public void t1_initialLoad() throws InterruptedException {
        waitForUpdate();

        // Fetch the starting data
        final Context context = ContextProvider.get();
        final AlarmInfo startInfo = new PreferenceStorage(context).load();
        final Calendar startTime = startInfo.getTime();

        // Check the time
        if (DateFormat.is24HourFormat(context)) {
            checkText(getViewTextTime(),
                    DateFormat.getTimeFormat(context).format(startTime.getTime()));
            checkText(getViewTextPeriod(), null);
        } else {
            checkText(getViewTextTime(),
                    DateFormat.format("h:mm", startTime).toString());
            checkText(getViewTextPeriod(), DateFormat.format("a", startTime).toString()
                    .replace(".", ""));
        }

        // Check the remaining status
        checkText(getViewTextRemaining(), null);

        // Make sure the button is in play mode
        getViewBtnRun().check(matches(withImageDrawable(R.drawable.ic_play)));
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
        Thread.sleep(250);
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
