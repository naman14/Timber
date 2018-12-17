package com.naman14.timber.activities;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.naman14.timber.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;

@RunWith(AndroidJUnit4.class)
@LargeTest
class PlaylistTest {

    @Rule
    public ActivityTestRule<MainActivity> MainActivityRule=
            new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setup() {
        //Swipe to Songs
        onView(withId(R.id.nav_view)).perform(swipeLeft(),
                ViewActions.closeSoftKeyboard());
    }

    @Test
    public void AddtoPlaylist() {
        //Swipe to Songs
        onView(withId(R.id.nav_view)).perform(swipeLeft(),
                ViewActions.closeSoftKeyboard());
        //Add songs to playlist
        onView(withId(R.id.nav_view)).perform(swipeLeft(),
                ViewActions.closeSoftKeyboard());

        onData(anything()).inAdapterView(withId((R.id.match_parent)))
                .atPosition(1)
                .onChildView(withId(R.id.dots_vertical_circle)).
                perform(click());

        onView(withId((R.id.popup_song_addto_playlist))).perform(click());

        //Create new playlist
        onView(withId((R.id.action_new_playlist))).perform(click());
        onView(withId(R.id.action_search)).perform(typeTextIntoFocusedView("ATest"));
        onView(withText("CREATE")).perform(click());

    }

    @Test
    public void Add1000toPlaylist() {

        //Add songs to playlist
        onView(withId(R.id.nav_view)).perform(swipeLeft(),
                ViewActions.closeSoftKeyboard());

        onData(anything()).inAdapterView(withId((R.id.match_parent)))
                .atPosition(1)
                .onChildView(withId(R.id.dots_vertical_circle)).
                perform(click());

        onView(withId((R.id.popup_song_addto_playlist))).perform(click());

        //Create new playlist
        onView(withId((R.id.action_new_playlist))).perform(click());
        onView(withId(R.id.popup_song_addto_playlist)).perform(typeTextIntoFocusedView("longTest"));
        onView(withText("CREATE")).perform(click());

        for(int i = 0; i < 1000; i++){
            //Keep adding to playlist created
            onView(withId(R.id.nav_view)).perform(swipeLeft(),
                    ViewActions.closeSoftKeyboard());

            onData(anything()).inAdapterView(withId((R.id.match_parent)))
                    .atPosition(1)
                    .onChildView(withId(R.id.dots_vertical_circle)).
                    perform(click());

            onView(withId((R.id.popup_song_addto_playlist))).perform(click());
            onView(withText("longTest")).perform(click());
        }

    }


}