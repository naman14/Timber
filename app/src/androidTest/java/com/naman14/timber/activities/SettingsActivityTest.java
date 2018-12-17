package com.naman14.timber.activities;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.naman14.timber.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SettingsActivityTest {

    @Rule
    public ActivityTestRule<SettingsActivity> mSettingsActivity =
            new ActivityTestRule<SettingsActivity>(SettingsActivity.class);

    //Theme changes real time
    @Test
    public void testSwipeMenu(){
        onView(withId(R.id.nav_view)).perform(swipeDown(),
                ViewActions.closeSoftKeyboard());

        onView(withId(R.id.nav_view)).perform(swipeUp(),
                ViewActions.closeSoftKeyboard());

        onView(withId(R.id.theme_light_dark)).perform(click());
    }

}