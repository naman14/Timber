package com.naman14.timber.activities;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.naman14.timber.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class SearchActivityTest {

    //declaring ArrayList with initial size n
    ArrayList<String> searchKey = new ArrayList<String>();
    String Song1 = "Off That (Featuring Drake)";
    String Song2 = "Young Forever (Featuring Mr Hudson)";
    @Before
    public void setupVer(){
            searchKey.add("O");
            searchKey.add("Off");
            searchKey.add("Off That");
            searchKey.add("Young");
            searchKey.add("Young Forever");

    }

    @Test
    public void onQueryTextChangeTest(){
        //Search results updates as text changes
         for(int i = 0; i <= 4 ; i++) {
             //Only one result per search for simplicity
             onView(withId(R.id.action_search)).perform(typeTextIntoFocusedView(searchKey.get(i)));
             //Match first result results
             if(i < 3)
                onView(withId(R.id.menu_search)).check(matches(withText(Song1)));
             else
                 onView(withId(R.id.menu_search)).check(matches(withText(Song2)));
             //check if it is still there

         }
    }
    @org.junit.Test
    public void onQueryTextSubmit() {
        //Song1 - .mp4 /Song2 - .wav /song3 - .ogg / song4 - Not in system
        String input[] = {"Song1", "Song2", "Song3", "Song4"};
        boolean output;
        boolean[] expected = {true, true, true, false};

        SearchActivity search = new SearchActivity();
        for(int i =0; i < input.length; i++) {
            output = search.onQueryTextSubmit(input[i]);
            assertEquals(output, expected[i]);
        }
    }

}