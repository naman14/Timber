package com.naman14.timber.activities;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.rule.ActivityTestRule;

@RunWith(AndroidJUnit4.class)
@LargeTest
class PlaylistDetailActivityTest {

    @Rule
    public ActivityTestRule<PlaylistDetailActivity> mPlaylistRule=
            new ActivityTestRule<PlaylistDetailActivity>(PlaylistDetailActivity.class);

    @Test
    public void PlayPremadePlaylist() {
        //Open 1st playlist

    }


}


