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

package com.naman14.timber.subfragments;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.activities.BaseActivity;
import com.naman14.timber.listeners.MusicStateListener;
import com.naman14.timber.nowplaying.BaseNowplayingFragment;
import com.naman14.timber.utils.ImageUtils;
import com.naman14.timber.utils.TimberUtils;
import com.naman14.timber.widgets.PlayPauseButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class QuickControlsFragment extends BaseNowplayingFragment implements MusicStateListener {


    public static View topContainer;
    private static ProgressBar mProgress;
    private PlayPauseButton mPlayPause;
    private TextView mTitle;
    private TextView mArtist;
    private TextView mExtraInfo;
    private ImageView mAlbumArt, mBlurredArt;
    private String mArtUrl;
    private View rootView;
    private View playPauseWrapper;

    private boolean duetoplaypause = false;
    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            duetoplaypause = true;
            ;
            if (!mPlayPause.isPlayed()) {
                mPlayPause.setPlayed(true);
                mPlayPause.startAnimation();
            } else {
                mPlayPause.setPlayed(false);
                mPlayPause.startAnimation();
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.playOrPause();
                }
            }, 200);

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playback_controls, container, false);
        this.rootView = rootView;

        mPlayPause = (PlayPauseButton) rootView.findViewById(R.id.play_pause);
        playPauseWrapper = rootView.findViewById(R.id.play_pause_wrapper);
        mPlayPause.setEnabled(true);
        playPauseWrapper.setOnClickListener(mButtonListener);
        mProgress = (ProgressBar) rootView.findViewById(R.id.song_progress_normal);
        mTitle = (TextView) rootView.findViewById(R.id.title);
        mArtist = (TextView) rootView.findViewById(R.id.artist);
        mAlbumArt = (ImageView) rootView.findViewById(R.id.album_art_nowplayingcard);
        mBlurredArt = (ImageView) rootView.findViewById(R.id.blurredAlbumart);
        topContainer = rootView.findViewById(R.id.topContainer);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mProgress.getLayoutParams();
        mProgress.measure(0, 0);
        layoutParams.setMargins(0, -(mProgress.getMeasuredHeight() / 2), 0, 0);
        mProgress.setLayoutParams(layoutParams);
        mProgress.setScaleY(0.5f);

        if (isThemeIsLight()) {
            mPlayPause.setColor(ContextCompat.getColor(mPlayPause.getContext(), R.color.colorAccent));
        } else if (isThemeIsDark()) {
            mPlayPause.setColor(ContextCompat.getColor(mPlayPause.getContext(), R.color.colorAccentDarkTheme));
        } else
            mPlayPause.setColor(ContextCompat.getColor(mPlayPause.getContext(), R.color.colorAccentBlack));

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ((BaseActivity) getActivity()).setMusicStateListenerListener(this);


        return rootView;
    }

    public void updateControlsFragment() {
        //let basenowplayingfragment take care of this
        setSongDetails(rootView);

    }

    //to update the permanent now playing card at the bottom
    public void updateNowplayingCard() {
        mTitle.setText(MusicPlayer.getTrackName());
        mArtist.setText(MusicPlayer.getArtistName());
        if (!duetoplaypause) {
            ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(MusicPlayer.getCurrentAlbumId()).toString(), mAlbumArt,
                    new DisplayImageOptions.Builder().cacheInMemory(true)
                            .showImageOnFail(R.drawable.ic_empty_music2)
                            .resetViewBeforeLoading(true)
                            .build(), new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            Bitmap failedBitmap = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.ic_empty_music2);
                            if (getActivity() != null)
                                new setBlurredAlbumArt().execute(failedBitmap);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            if (getActivity() != null)
                                new setBlurredAlbumArt().execute(loadedImage);

                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
        }
        duetoplaypause = false;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onResume() {
        super.onResume();
        topContainer = rootView.findViewById(R.id.topContainer);

    }

    public void updateState() {
        if (MusicPlayer.isPlaying()) {
            if (!mPlayPause.isPlayed()) {
                mPlayPause.setPlayed(true);
                mPlayPause.startAnimation();
            }
        } else {
            if (mPlayPause.isPlayed()) {
                mPlayPause.setPlayed(false);
                mPlayPause.startAnimation();
            }
        }
    }

    public void restartLoader() {

    }

    public void onPlaylistChanged() {

    }

    public void onMetaChanged() {
        //only update nowplayingcard,quick controls will be updated by basenowplayingfragment's onMetaChanged
        updateNowplayingCard();
        updateState();
        //TODO
        updateControlsFragment();
    }

    private class setBlurredAlbumArt extends AsyncTask<Bitmap, Void, Drawable> {

        @Override
        protected Drawable doInBackground(Bitmap... loadedImage) {
            Drawable drawable = null;
            try {
                drawable = ImageUtils.createBlurredImageFromBitmap(loadedImage[0], getActivity(), 6);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            if (result != null) {
                if (mBlurredArt.getDrawable() != null) {
                    final TransitionDrawable td =
                            new TransitionDrawable(new Drawable[]{
                                    mBlurredArt.getDrawable(),
                                    result
                            });
                    mBlurredArt.setImageDrawable(td);
                    td.startTransition(400);

                } else {
                    mBlurredArt.setImageDrawable(result);
                }
            }
        }

        @Override
        protected void onPreExecute() {
        }
    }


}
