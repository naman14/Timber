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

package com.naman14.timber.fragments;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.adapters.AlbumSongsAdapter;
import com.naman14.timber.dataloaders.AlbumLoader;
import com.naman14.timber.dataloaders.AlbumSongLoader;
import com.naman14.timber.listeners.SimplelTransitionListener;
import com.naman14.timber.models.Album;
import com.naman14.timber.models.Song;
import com.naman14.timber.utils.Constants;
import com.naman14.timber.utils.FabAnimationUtils;
import com.naman14.timber.utils.NavigationUtils;
import com.naman14.timber.utils.PreferencesUtility;
import com.naman14.timber.utils.SortOrder;
import com.naman14.timber.utils.TimberUtils;
import com.naman14.timber.widgets.DividerItemDecoration;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.util.List;

public class AlbumDetailFragment extends Fragment {

    long albumID = -1;

    ImageView albumArt, artistArt;
    TextView albumTitle, albumDetails;

    RecyclerView recyclerView;
    AlbumSongsAdapter mAdapter;

    Toolbar toolbar;
    CoordinatorLayout coordinatorLayout;

    Album album;

    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;
    FloatingActionButton fab;

    private boolean loadFailed = false;
    private boolean isDarkTheme;

    private PreferencesUtility mPreferences;

    public static AlbumDetailFragment newInstance(long id) {
        AlbumDetailFragment fragment = new AlbumDetailFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.ALBUM_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumID = getArguments().getLong(Constants.ALBUM_ID);
        }
        isDarkTheme = PreferencesUtility.getInstance(getActivity()).getTheme().equals("black");
        mPreferences = PreferencesUtility.getInstance(getActivity());
    }

    @TargetApi(21)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(
                R.layout.fragment_album_detail, container, false);

        albumArt = (ImageView) rootView.findViewById(R.id.album_art);
        artistArt = (ImageView) rootView.findViewById(R.id.artist_art);
        albumTitle = (TextView) rootView.findViewById(R.id.album_title);
        albumDetails = (TextView) rootView.findViewById(R.id.album_details);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        collapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        appBarLayout = (AppBarLayout) rootView.findViewById(R.id.app_bar);
        recyclerView.setEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        album = AlbumLoader.getAlbum(getActivity(), albumID);

        setAlbumart();

        if (TimberUtils.isLollipop() && PreferencesUtility.getInstance(getActivity()).getAnimations()) {
            getActivity().postponeEnterTransition();
            getActivity().getWindow().getEnterTransition().addListener(new EnterTransitionListener());
            getActivity().getWindow().getReturnTransition().addListener(new ReturnTransitionListener());
        } else {
            setUpEverything();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AlbumSongsAdapter adapter = (AlbumSongsAdapter) recyclerView.getAdapter();
                        MusicPlayer.playAll(getActivity(), adapter.getSongIds(), 0, albumID, TimberUtils.IdType.Album, true);
                        NavigationUtils.navigateToNowplaying(getActivity(), false);
                    }
                }, 150);
            }
        });

        return rootView;
    }

    private void setupToolbar() {

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout.setTitle(album.title);

    }

    private void setAlbumart() {
        ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(albumID).toString(), albumArt,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnFail(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true)
                        .build(), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        loadFailed = true;
                        if (TimberUtils.isLollipop() && PreferencesUtility.getInstance(getActivity()).getAnimations())
                            scheduleStartPostponedTransition(albumArt);


                        if (isDarkTheme) {
                            MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(getActivity())
                                    .setIcon(MaterialDrawableBuilder.IconValue.SHUFFLE)
                                    .setColor(Color.BLACK);
                            fab.setImageDrawable(builder.build());

                        }
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        new Palette.Builder(loadedImage).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                collapsingToolbarLayout.setContentScrimColor(palette.getVibrantColor(Color.parseColor("#66000000")));
                                collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkVibrantColor(Color.parseColor("#66000000")));

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
                        });

                        if (TimberUtils.isLollipop() && PreferencesUtility.getInstance(getActivity()).getAnimations())
                            scheduleStartPostponedTransition(albumArt);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                    }

                });
    }

    private void setAlbumDetails() {

        String songCount = TimberUtils.makeLabel(getActivity(), R.plurals.Nsongs, album.songCount);

        String year = (album.year != 0) ? (" - " + String.valueOf(album.year)) : "";

        albumTitle.setText(album.title);
        albumDetails.setText(album.artistName + " - " + songCount + year);


    }

    private void setUpAlbumSongs() {

        List<Song> songList = AlbumSongLoader.getSongsForAlbum(getActivity(), albumID);
        mAdapter = new AlbumSongsAdapter(getActivity(), songList, albumID);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(mAdapter);

    }

    private void setUpEverything() {
        setupToolbar();
        setAlbumDetails();
        setUpAlbumSongs();
        FabAnimationUtils.scaleIn(fab);
        MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(getActivity())
                .setIcon(MaterialDrawableBuilder.IconValue.SHUFFLE);
        if ((loadFailed && isDarkTheme))
            builder.setColor(Color.BLACK);
        else builder.setColor(Color.WHITE);
        fab.setImageDrawable(builder.build());
        enableViews();
    }

    private void reloadAdapter() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                List<Song> songList = AlbumSongLoader.getSongsForAlbum(getActivity(), albumID);
                mAdapter.updateDataSet(songList);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.album_song_sort_by, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_by_az:
                mPreferences.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_A_Z);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_za:
                mPreferences.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_Z_A);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_year:
                mPreferences.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_YEAR);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_duration:
                mPreferences.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_DURATION);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_track_number:
                mPreferences.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST);
                reloadAdapter();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void enableViews() {
        recyclerView.setEnabled(true);
    }

    private void disableView() {
        recyclerView.setEnabled(false);
    }

    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            transition.excludeTarget(R.id.album_art, true);
            transition.setInterpolator(new LinearOutSlowInInterpolator());
            transition.setDuration(300);
            getActivity().getWindow().setEnterTransition(transition);
        }
    }

    @TargetApi(21)
    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        getActivity().startPostponedEnterTransition();
                        return true;
                    }
                });
    }

    private class EnterTransitionListener extends SimplelTransitionListener {

        @TargetApi(21)
        public void onTransitionEnd(Transition paramTransition) {
            setUpEverything();
        }

        public void onTransitionStart(Transition paramTransition) {
            FabAnimationUtils.scaleOut(fab, 50, null);
            disableView();
        }

    }

    private class ReturnTransitionListener extends SimplelTransitionListener {

        @TargetApi(21)
        public void onTransitionEnd(Transition paramTransition) {
        }

        public void onTransitionStart(Transition paramTransition) {
            FabAnimationUtils.scaleOut(fab, 50, null);
            disableView();
        }

    }


}
