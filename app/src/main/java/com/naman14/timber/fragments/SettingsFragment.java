package com.naman14.timber.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
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
    private static final String KEY_ABOUT="preference_about";
    private static final String KEY_SOURCE="preference_source";
    private static final String KEY_THEME = "theme_preference";
    private static final String TOGGLE_ANIMATIONS="toggle_animations";
    private static final String TOGGLE_SYSTEM_ANIMATIONS="toggle_system_animations";

    Preference nowPlayingSelector;
    SwitchPreference toggleAnimations;
    ListPreference themePreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        nowPlayingSelector = findPreference(NOW_PLAYING_SELECTOR);
        themePreference = (ListPreference) findPreference(KEY_THEME);
        toggleAnimations=(SwitchPreference)findPreference(TOGGLE_ANIMATIONS);

        nowPlayingSelector.setIntent(NavigationUtils.getNavigateToStyleSelectorIntent(getActivity(), Constants.SETTINGS_STYLE_SELECTOR_NOWPLAYING));

        PreferencesUtility.getInstance(getActivity()).setOnSharedPreferenceChangeListener(this);
        setPrefernceCickListeners();

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
    }

    private void setPrefernceCickListeners() {

        themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Intent i = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return true;
            }
        });
    }


}
