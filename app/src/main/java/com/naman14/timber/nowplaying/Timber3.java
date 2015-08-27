package com.naman14.timber.nowplaying;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.graphics.Palette;
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

    FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_timber3, container, false);

        PlayPauseButton playPauseButton=(PlayPauseButton)rootView.findViewById(R.id.playpause);
        fab =(FloatingActionButton) rootView.findViewById(R.id.playpausefloating);

        setMusicStateListener();
        setSongDetails(rootView);

        if (playPauseButton!=null) {
            if (PreferencesUtility.getInstance(getActivity()).getTheme().equals("light")) {
                playPauseButton.setColor(getActivity().getResources().getColor(android.R.color.black));
            }
        }

        return rootView;
    }

    @Override
    public void doAlbumArtStuff(Bitmap bitmap){
        if (fab!=null) {
            Palette palette = Palette.generate(bitmap);
            ColorStateList fabColorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{}
                    },
                    new int[]{
                            palette.getMutedColor(Color.parseColor("#66000000")),
                    }
            );

            fab.setBackgroundTintList(fabColorStateList);
        }
    }

}
