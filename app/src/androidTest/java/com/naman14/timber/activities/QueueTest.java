package com.naman14.timber.activities;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class QueueTest {
    @Rule
    public ActivityTestRule<MainActivity> mMainActivityRule=
            new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void AddtoQueue(){
        //Start Playing first Track in list
        onData(anything()).inAdapterView(withId((R.id.song_title)))
                .atPosition(0).perform(click()),
        //Add 4th song to queue
        onData(anything()).inAdapterView(withId((R.id.song_title)))
                .atPosition(3)
                .perform(NavigationViewActions.navigateTo(R.id.dots_vertical_circle)
                .perform(click()));


        //Add 2nd song
        onData(anything()).inAdapterView(withId((R.id.song_title)))
                .atPosition(1)
                .perform(NavigationViewActions.navigateTo(R.id.dots_vertical_circle)
                        .perform(click()));

        String song_name = "Young Forever (Featuring Mr Hudson)";
        //Click next
        onView(withId(R.id.topContainer)).perform(click());
        onView(withId(R.id.play)).perform(click());
        onView(withId(R.id.next)).perform(click());

        //4th song should now be playing
        assertThat(MusicPlayer.getTrackName(),is(equalTo(song_name)));

        onView(withId(R.id.next)).perform(click());
        //2nd song should now be playing
        song_name = "Off That (Featuring Drake)";
        assertThat(MusicPlayer.getTrackName(),is(equalTo(song_name)));
    }



}