package com.naman14.timber.nowplaying;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naman14.timber.R;
import com.naman14.timber.utils.PreferencesUtility;
import com.naman14.timber.widgets.PlayPauseButton;

/**
 * Created by naman on 26/07/15.
 */
public class Timber3 extends BaseNowplayingFragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_timber3, container, false);

        PlayPauseButton playPauseButton=(PlayPauseButton)rootView.findViewById(R.id.playpause);
        setMusicStateListener();
        setSongDetails(rootView);
        if (PreferencesUtility.getInstance(getActivity()).getTheme().equals("light")){
            playPauseButton.setColor(getActivity().getResources().getColor(android.R.color.black));
        }

        return rootView;
    }

}
