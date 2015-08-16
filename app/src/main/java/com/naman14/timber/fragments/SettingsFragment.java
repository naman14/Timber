package com.naman14.timber.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.naman14.timber.R;
import com.naman14.timber.utils.Constants;
import com.naman14.timber.utils.NavigationUtils;
import com.naman14.timber.utils.PreferencesUtility;

/**
 * Created by naman on 08/08/15.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String NOW_PLAYING_SELECTOR="now_paying_selector";
    private static final String TOGGLE_ANIMATIONS="toggle_animations";
    private static final String TOGGLE_SYSTEM_ANIMATIONS="toggle_system_animations";

    Preference nowPlayingSelector;
    SwitchPreference toggleAnimations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        nowPlayingSelector=findPreference(NOW_PLAYING_SELECTOR);
        toggleAnimations=(SwitchPreference)findPreference(TOGGLE_ANIMATIONS);

        nowPlayingSelector.setIntent(NavigationUtils.getNavigateToStyleSelectorIntent(getActivity(), Constants.SETTINGS_STYLE_SELECTOR_NOWPLAYING));

        PreferencesUtility.getInstance(getActivity()).setOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
    }


}
