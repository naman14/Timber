package com.naman14.timber.activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.naman14.timber.R;
import com.naman14.timber.fragments.SettingsFragment;
import com.naman14.timber.subfragments.StyleSelectorFragment;
import com.naman14.timber.utils.Constants;

/**
 * Created by naman on 07/08/15.
 */
public class SettingsActivity extends AppCompatActivity {

    String action;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        action = getIntent().getAction();

        if (action.equals(Constants.SETTINGS_STYLE_SELECTOR)){
            String what =getIntent().getExtras().getString(Constants.SETTINGS_STYLE_SELECTOR_WHAT);
            Fragment fragment = new StyleSelectorFragment().newInstance(what);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();
        } else {
            PreferenceFragment fragment = new SettingsFragment();
            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }

    }

}
