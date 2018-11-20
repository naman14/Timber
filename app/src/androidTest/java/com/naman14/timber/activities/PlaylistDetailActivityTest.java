package com.naman14.timber.activities;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.rule.ActivityTestRule;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PlaylistDetailActivityTest {

    @Rule
    public ActivityTestRule<PlaylistDetailActivity> mMainActivityRule=
            new ActivityTestRule<PlaylistDetailActivity>(PlaylistDetailActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();

                }
            };

    @Before
    public void SetVariables(){

    }

    @Test
    public void getActivityTheme() {

    }

    @Test
    public void onMetaChanged() {
    }

    @Test
    public void getToolbarColor() {
    }

    @Test
    public void getLightToolbarMode() {
    }
}