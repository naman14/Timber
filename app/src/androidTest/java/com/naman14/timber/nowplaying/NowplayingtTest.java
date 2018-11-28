package com.naman14.timber.nowplaying;


import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.activities.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NowplayingtTest {

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityRule=
            new ActivityTestRule<MainActivity>(MainActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();

                }
            };

    @Before
    public void StartMusicPlayer(){
        //click on item from library to open Now playing
        onView(withId(R.id.nav_library)).perform(click());
    }

    @Test
    public void MusicActionTest() {
        onView(withId(R.id.play)).perform(click());
        assertThat(MusicPlayer.isPlaying()  ,is(equalTo(false)));
        onView(withId(R.id.pause)).perform(click());
        assertThat(MusicPlayer.isPlaying()  ,is(equalTo(true)));

        String song_name = MusicPlayer.getTrackName();
        onView(withId(R.id.next)).perform(click());
        String next_song = MusicPlayer.getTrackName();
        onView(withId(R.id.previous)).perform(click());
        assertThat(MusicPlayer.getTrackName(),is(equalTo(song_name)));

        //After shuffling next song will not be played next unless only 2 songs
        onView(withId(R.id.shuffle));
        onView(withId(R.id.next));
        if(MusicPlayer.getQueue().length > 2){
            assertThat(MusicPlayer.getTrackName()  ,is(not(next_song)));
        }
    }
}