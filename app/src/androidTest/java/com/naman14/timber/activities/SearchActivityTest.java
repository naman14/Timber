package com.naman14.timber.activities;

import static org.junit.Assert.*;

public class SearchActivityTest {

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