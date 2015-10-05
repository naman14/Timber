/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.naman14.timber.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public final class PreferencesUtility {

    private static final String NOW_PLAYING_SELECTOR="now_paying_selector";
    private static final String TOGGLE_ANIMATIONS="toggle_animations";
    private static final String TOGGLE_SYSTEM_ANIMATIONS="toggle_system_animations";
    private static final String TOGGLE_ARTIST_GRID="toggle_artist_grid";
    private static final String THEME_PREFERNCE="theme_preference";
    private static final String START_PAGE_INDEX = "start_page_index";
    private static final String START_PAGE_PREFERENCE_LASTOPENED= "start_page_preference_latopened";

    private static PreferencesUtility sInstance;

    private static SharedPreferences mPreferences;

    public PreferencesUtility(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public static final PreferencesUtility getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesUtility(context.getApplicationContext());
        }
        return sInstance;
    }


    public void setOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener){
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public boolean getAnimations(){
       return mPreferences.getBoolean(TOGGLE_ANIMATIONS,true);
    }

    public boolean getSystemAnimations(){
        return mPreferences.getBoolean(TOGGLE_SYSTEM_ANIMATIONS,true);
    }
    public boolean isArtistsInGrid(){
        return mPreferences.getBoolean(TOGGLE_ARTIST_GRID,false);
    }
    public String getTheme(){
        return mPreferences.getString(THEME_PREFERNCE, "light");
    }

    public void setStartPageIndex(final int index) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt(START_PAGE_INDEX, index);
                editor.apply();
                return null;
            }
        }.execute();
    }

    public int getStartPageIndex() {
        return mPreferences.getInt(START_PAGE_INDEX, 0);
    }

    public void setLastOpenedAsStartPagePreference(boolean preference) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(START_PAGE_PREFERENCE_LASTOPENED, preference);
        editor.apply();
    }

    public boolean lastOpenedIsStartPagePreference() {
        return mPreferences.getBoolean(START_PAGE_PREFERENCE_LASTOPENED,true);
    }
}