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

package com.naman14.timber.nowplaying;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naman14.timber.R;
import com.naman14.timber.utils.PreferencesUtility;
import com.naman14.timber.widgets.PlayPauseButton;

public class Timber3 extends BaseNowplayingFragment {

    FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_timber3, container, false);

        PlayPauseButton playPauseButton = (PlayPauseButton) rootView.findViewById(R.id.playpause);
        fab = (FloatingActionButton) rootView.findViewById(R.id.playpausefloating);

        setMusicStateListener();
        setSongDetails(rootView);

        if (playPauseButton != null) {
            if (PreferencesUtility.getInstance(getActivity()).getTheme().equals("light")) {
                playPauseButton.setColor(ContextCompat.getColor(getActivity(), android.R.color.black));
            }
        }

        return rootView;
    }

    @Override
    public void doAlbumArtStuff(Bitmap bitmap) {
//        if (fab!=null) {
//            Palette palette = Palette.generate(bitmap);
//            ColorStateList fabColorStateList = new ColorStateList(
//                    new int[][]{
//                            new int[]{}
//                    },
//                    new int[]{
//                            palette.getMutedColor(Color.parseColor("#66000000")),
//                    }
//            );
//
//            fab.setBackgroundTintList(fabColorStateList);
//        }
    }

}
