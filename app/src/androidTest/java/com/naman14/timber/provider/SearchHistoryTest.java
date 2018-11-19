package com.naman14.timber.provider;

import com.naman14.timber.activities.SearchActivity;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class SearchHistoryTest {

    @Test
    public void getRecentSearches() {
        //add to db
        SearchHistory sh0 = new SearchHistory(new SearchActivity());
        SearchHistory sh1 = new SearchHistory(new SearchActivity());
        sh1.addSearchString("Take on me");
        sh1.addSearchString("A Hard Day's Night");
        sh1.addSearchString("Beat It");
        sh1.addSearchString("Don't Stop Believin'");
        sh1.addSearchString("Never Gonna Give You Up");

        SearchHistory sh2 = new SearchHistory(new SearchActivity());
        sh1.addSearchString("A Hard Day's Night");
        sh1.addSearchString("Take on me");
        sh1.addSearchString("Don't Stop Believin'");
        sh1.addSearchString("Beat It");
        sh1.addSearchString("Never Gonna Give You Up");

        //Returns array of recently made choices from systems database
        ArrayList<String> expected = new ArrayList<String>(25);
        ArrayList<String> actual = sh1.getRecentSearches();
        assertEquals(expected, actual);

        expected.add("Take on me");
        expected.add("A Hard Day's Night");
        expected.add("Beat It");
        expected.add( "Don't Stop Believin'");
        expected.add("Never Gonna Give You Up");

        actual = sh1.getRecentSearches();
        assertEquals(expected, actual);

        expected = new ArrayList<String>(25);
        expected.add("A Hard Day's Night");
        expected.add("Take on me");
        expected.add("Don't Stop Believin'");
        expected.add("Beat It");
        expected.add("Never Gonna Give You Up");

        actual = sh2.getRecentSearches();
        assertEquals(expected, actual);
    }
}