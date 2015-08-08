package com.naman14.timber.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.naman14.timber.R;
import com.naman14.timber.utils.Constants;
import com.naman14.timber.utils.NavigationUtils;

/**
 * Created by naman on 08/08/15.
 */
public class SettingsFragment extends PreferenceFragment {

    Preference nowPlayingSelector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        nowPlayingSelector=findPreference("now_paying_selector");
        nowPlayingSelector.setIntent(NavigationUtils.getNavigateToStyleSelectorIntent(getActivity(), Constants.SETTINGS_STYLE_SELECTOR_NOWPLAYING));
    }


}
