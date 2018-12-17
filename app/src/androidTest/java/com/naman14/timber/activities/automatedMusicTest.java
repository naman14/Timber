package com.naman14.timber.activities;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.naman14.timber.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class automatedMusicTest {
    @Rule
    public ActivityTestRule<MainActivity> mMainActivityRule=
            new ActivityTestRule<MainActivity>(MainActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();

                }
            };

    //Get a song in the list of files randomly
    @Test
    public void testOnOptionsItemSelected() throws Exception {
        //Select song in list position x option
        int[] pos = getItem();
        for (int x : pos){
            onData(ViewMatchers.withId(R.id.nav_view)).inAdapterView(withId(R.id.text_list_view))
                    .atPosition(x).perform(click());
        }
    }



    public int[] getItem() {
        int items[] = new int[5];
        for(int i = 0; i < 100; i++){
            Random rand = new Random(5);

            int pos = rand.nextInt(20-1) + 1;
            items[i]= pos;
        }

        return items;
    }
}