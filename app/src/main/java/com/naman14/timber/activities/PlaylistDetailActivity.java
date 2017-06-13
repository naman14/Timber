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

package com.naman14.timber.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.customizers.ATEActivityThemeCustomizer;
import com.afollestad.appthemeengine.customizers.ATEToolbarCustomizer;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.adapters.SongsListAdapter;
import com.naman14.timber.dataloaders.LastAddedLoader;
import com.naman14.timber.dataloaders.PlaylistLoader;
import com.naman14.timber.dataloaders.PlaylistSongLoader;
import com.naman14.timber.dataloaders.SongLoader;
import com.naman14.timber.dataloaders.TopTracksLoader;
import com.naman14.timber.listeners.SimplelTransitionListener;
import com.naman14.timber.models.Song;
import com.naman14.timber.utils.Constants;
import com.naman14.timber.utils.PreferencesUtility;
import com.naman14.timber.utils.TimberUtils;
import com.naman14.timber.widgets.DividerItemDecoration;
import com.naman14.timber.widgets.DragSortRecycler;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaylistDetailActivity extends BaseActivity implements ATEActivityThemeCustomizer, ATEToolbarCustomizer {

    private static final String ARG_PLAY_LIST_IMAGE = "ARG_PLAY_LIST_IMAGE";
    String action;
    long playlistID;
    HashMap<String, Runnable> playlistsMap = new HashMap<>();
    Runnable playlistLastAdded = new Runnable() {
        public void run() {
            new loadLastAdded().execute("");
        }
    };
    Runnable playlistRecents = new Runnable() {
        @Override
        public void run() {
            new loadRecentlyPlayed().execute("");

        }
    };
    Runnable playlistToptracks = new Runnable() {
        @Override
        public void run() {
            new loadTopTracks().execute("");
        }
    };
    Runnable playlistUsercreated = new Runnable() {
        @Override
        public void run() {
            new loadUserCreatedPlaylist().execute("");

        }
    };
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.blurFrame)
    ImageView blurFrame;
    @BindView(R.id.name)
    TextView playlistName;
    @BindView(R.id.foreground)
    View foreground;
    private AppCompatActivity mContext = PlaylistDetailActivity.this;
    private SongsListAdapter mAdapter;
    private int colorForeground = 0;
    private String urImage;
    private boolean animate;

    @TargetApi(21)
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);
        ButterKnife.bind(this);

        action = getIntent().getAction();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        playlistsMap.put(Constants.NAVIGATE_PLAYLIST_LASTADDED, playlistLastAdded);
        playlistsMap.put(Constants.NAVIGATE_PLAYLIST_RECENT, playlistRecents);
        playlistsMap.put(Constants.NAVIGATE_PLAYLIST_TOPTRACKS, playlistToptracks);
        playlistsMap.put(Constants.NAVIGATE_PLAYLIST_USERCREATED, playlistUsercreated);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setAlbumArt(savedInstanceState);

        animate = getIntent().getBooleanExtra(Constants.ACTIVITY_TRANSITION, false);
        if (savedInstanceState == null && animate && TimberUtils.isLollipop() && PreferencesUtility.getInstance(this).getAnimations()) {
            getWindow().getEnterTransition().addListener(new EnterTransitionListener());
        } else {
            setUpSongs();
        }

    }

    private void setAlbumArt(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            playlistName.setText(savedInstanceState.getString(Constants.PLAYLIST_NAME));
            colorForeground = savedInstanceState.getInt(Constants.PLAYLIST_FOREGROUND_COLOR);
            urImage = savedInstanceState.getString(ARG_PLAY_LIST_IMAGE);
        } else {
            playlistName.setText(getIntent().getStringExtra(Constants.PLAYLIST_NAME));
            colorForeground = getIntent().getIntExtra(Constants.PLAYLIST_FOREGROUND_COLOR, 0);
            urImage = TimberUtils.getAlbumArtUri(getIntent().getExtras().getLong(Constants.ALBUM_ID)).toString();
        }
        Picasso.with(this).load(urImage).error(R.drawable.ic_empty_music2).into(blurFrame);
        foreground.setBackgroundColor(colorForeground);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.PLAYLIST_NAME, playlistName.getText().toString());
        outState.putInt(Constants.PLAYLIST_FOREGROUND_COLOR, colorForeground);
        outState.putString(ARG_PLAY_LIST_IMAGE, urImage);
        super.onSaveInstanceState(outState);
    }

    private void setUpSongs() {
        Runnable navigation = playlistsMap.get(action);
        if (navigation != null) {
            navigation.run();

            DragSortRecycler dragSortRecycler = new DragSortRecycler();
            dragSortRecycler.setViewHandleId(R.id.reorder);

            dragSortRecycler.setOnItemMovedListener(new DragSortRecycler.OnItemMovedListener() {
                @Override
                public void onItemMoved(int from, int to) {
                    Log.d("playlist", "onItemMoved " + from + " to " + to);
                    Song song = mAdapter.getSongAt(from);
                    mAdapter.removeSongAt(from);
                    mAdapter.addSongTo(to, song);
                    mAdapter.updateDataSet();
                    mAdapter.notifyDataSetChanged();
                    MusicPlayer.moveQueueItem(from, to);
//                    MediaStore.Audio.Playlists.Members.moveItem(getContentResolver(),
//                            playlistID, from, to);
                }
            });

            recyclerView.addItemDecoration(dragSortRecycler);
            recyclerView.addOnItemTouchListener(dragSortRecycler);
            recyclerView.addOnScrollListener(dragSortRecycler.getScrollListener());

        } else {
            Log.d("PlaylistDetail", "mo action specified");
        }
    }

    private void setRecyclerViewAapter() {
        recyclerView.setAdapter(mAdapter);
        if (animate && TimberUtils.isLollipop() && PreferencesUtility.getInstance(mContext).getAnimations()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST, R.drawable.item_divider_white));
                }
            }, 250);
        } else
            recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST, R.drawable.item_divider_white));
    }

    @StyleRes
    @Override
    public int getActivityTheme() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false) ? R.style.AppTheme_FullScreen_Dark : R.style.AppTheme_FullScreen_Light;

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.menu_playlist_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (action.equals(Constants.NAVIGATE_PLAYLIST_USERCREATED)) {
            menu.findItem(R.id.action_delete_playlist).setVisible(true);
            menu.findItem(R.id.action_clear_auto_playlist).setVisible(false);
        } else {
            menu.findItem(R.id.action_delete_playlist).setVisible(false);
            menu.findItem(R.id.action_clear_auto_playlist).setTitle("Clear " + playlistName.getText().toString());
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_delete_playlist:
                showDeletePlaylistDialog();
                break;
            case R.id.action_clear_auto_playlist:
                clearAutoPlaylists();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeletePlaylistDialog() {
        new MaterialDialog.Builder(this)
                .title("Delete playlist?")
                .content("Are you sure you want to delete playlist " + playlistName.getText().toString() + " ?")
                .positiveText("Delete")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        PlaylistLoader.deletePlaylists(PlaylistDetailActivity.this, playlistID);
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void clearAutoPlaylists() {
        switch (action) {
            case Constants.NAVIGATE_PLAYLIST_LASTADDED:
                TimberUtils.clearLastAdded(this);
                break;
            case Constants.NAVIGATE_PLAYLIST_RECENT:
                TimberUtils.clearRecent(this);
                break;
            case Constants.NAVIGATE_PLAYLIST_TOPTRACKS:
                TimberUtils.clearTopTracks(this);
                break;
        }
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public int getToolbarColor() {
        return Color.TRANSPARENT;
    }

    @Override
    public int getLightToolbarMode() {
        return Config.LIGHT_TOOLBAR_AUTO;
    }

    private class loadLastAdded extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            List<Song> lastadded = LastAddedLoader.getLastAddedSongs(mContext);
            mAdapter = new SongsListAdapter(mContext, lastadded, true, animate);
            mAdapter.setPlaylistId(playlistID);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            setRecyclerViewAapter();
        }

        @Override
        protected void onPreExecute() {
        }
    }

    private class loadRecentlyPlayed extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            TopTracksLoader loader = new TopTracksLoader(mContext, TopTracksLoader.QueryType.RecentSongs);
            List<Song> recentsongs = SongLoader.getSongsForCursor(TopTracksLoader.getCursor());
            mAdapter = new SongsListAdapter(mContext, recentsongs, true, animate);
            mAdapter.setPlaylistId(playlistID);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            setRecyclerViewAapter();

        }

        @Override
        protected void onPreExecute() {
        }
    }

    private class loadTopTracks extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            TopTracksLoader loader = new TopTracksLoader(mContext, TopTracksLoader.QueryType.TopTracks);
            List<Song> toptracks = SongLoader.getSongsForCursor(TopTracksLoader.getCursor());
            mAdapter = new SongsListAdapter(mContext, toptracks, true, animate);
            mAdapter.setPlaylistId(playlistID);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            setRecyclerViewAapter();
        }

        @Override
        protected void onPreExecute() {
        }
    }

    private class loadUserCreatedPlaylist extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            playlistID = getIntent().getExtras().getLong(Constants.PLAYLIST_ID);
            List<Song> playlistsongs = PlaylistSongLoader.getSongsInPlaylist(mContext, playlistID);
            mAdapter = new SongsListAdapter(mContext, playlistsongs, true, animate);
            mAdapter.setPlaylistId(playlistID);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            setRecyclerViewAapter();
        }

        @Override
        protected void onPreExecute() {
        }
    }

    private class EnterTransitionListener extends SimplelTransitionListener {

        @TargetApi(21)
        public void onTransitionEnd(Transition paramTransition) {
            setUpSongs();
        }

        public void onTransitionStart(Transition paramTransition) {
        }

    }
}
