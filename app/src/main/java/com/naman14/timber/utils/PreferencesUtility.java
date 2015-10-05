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
import android.preference.PreferenceManager;

public final class PreferencesUtility {

    private static final String NOW_PLAYING_SELECTOR="now_paying_selector";
    private static final String TOGGLE_ANIMATIONS="toggle_animations";
    private static final String TOGGLE_SYSTEM_ANIMATIONS="toggle_system_animations";
    private static final String TOGGLE_ARTIST_GRID="toggle_artist_grid";
    private static final String THEME_PREFERNCE="theme_preference";

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

    public static boolean getAnimations(){
       return mPreferences.getBoolean(TOGGLE_ANIMATIONS,true);
    }

    public static boolean getSystemAnimations(){
        return mPreferences.getBoolean(TOGGLE_SYSTEM_ANIMATIONS,true);
    }
    public static boolean isArtistsInGrid(){
        return mPreferences.getBoolean(TOGGLE_ARTIST_GRID,false);
    }
    public static String getTheme(){
        return mPreferences.getString(THEME_PREFERNCE, "light");
    }
}