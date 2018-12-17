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
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityRule=
            new ActivityTestRule<MainActivity>(MainActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();

                }
            };


    @Test
    public void testSwipeMenu(){
        //Swipe to Songs
        onView(withId(R.id.nav_view)).perform(swipeLeft(),
                ViewActions.closeSoftKeyboard());
        //Swipe down through list
        //Swipe to Songs
        onView(withId(R.id.nav_view)).perform(swipeLeft(),
                ViewActions.closeSoftKeyboard());
        //swipe back to albums then artist
        for(int i = 0; i < 2 ; i++) {
            onView(withId(R.id.nav_view)).perform(swipeRight(),
                    ViewActions.closeSoftKeyboard());
            onView(withId(R.id.nav_view)).perform(swipeLeft(),
                    ViewActions.closeSoftKeyboard());
        }
    }


    @Test
    public void testGetActivityTheme() {
        MainActivity main = new MainActivity();
        //Is defult theme returned
        assertEquals(main.getActivityTheme(),"Theme.AppCompat.Light.NoActionBar");
    }


}