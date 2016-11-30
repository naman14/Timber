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

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.activities.BaseActivity;
import com.naman14.timber.adapters.SongsListAdapter;
import com.naman14.timber.dataloaders.SongLoader;
import com.naman14.timber.helpers.MusicPlaybackTrack;
import com.naman14.timber.listeners.MusicStateListener;
import com.naman14.timber.helpers.Song;
import com.naman14.timber.utils.PreferencesUtility;
import com.naman14.timber.utils.SortOrder;
import com.naman14.timber.widgets.DividerItemDecoration;
import com.naman14.timber.widgets.FastScroller;

import java.util.ArrayList;
import java.util.List;



public class SongsFragment extends Fragment implements MusicStateListener {

    private SongsListAdapter mAdapter;
    private RecyclerView recyclerView;
    private PreferencesUtility mPreferences;
    List<MusicPlaybackTrack> songList;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferencesUtility.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_recyclerview, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FastScroller fastScroller = (FastScroller) rootView.findViewById(R.id.fastscroller);
        fastScroller.setRecyclerView(recyclerView);

//        new loadSongs().execute("");

        songList = new ArrayList<>();
        songList.add(new MusicPlaybackTrack("Tu Hi Hai","http://mp3khan.top/music/indian_movies/Dear%20Zindagi%20(2016)/02%20-%20Tu%20Hi%20Hai%20-%20Dear%20Zindagi%20[DJMaza.Cool].mp3"));
//        songList.add(new MusicPlaybackTrack("Chittiyaan Kalaiyaan","content://media/external/audio/media/1964"));
        songList.add(new MusicPlaybackTrack("Love you Zindagi","http://mp3khan.top/music/indian_movies/Dear%20Zindagi%20(2016)/01%20-%20Love%20You%20Zindagi%20-%20Dear%20Zindagi%20[DJMaza.Cool].mp3"));
//        songList.add(new MusicPlaybackTrack("Waka waka","content://media/external/audio/media/1965"));
        songList.add(new MusicPlaybackTrack("Ae Zindagi","http://mp3khan.top/music/indian_movies/Dear%20Zindagi%20(2016)/07%20-%20Ae%20Zindagi%20Gale%20Laga%20Le%20(Take%201)%20-%20Dear%20Zindagi%20[DJMaza.Cool].mp3"));
        songList.add(new MusicPlaybackTrack("Let's Breakup","http://mp3khan.top/music/indian_movies/Dear%20Zindagi%20(2016)/04%20-%20Lets%20Break%20Up%20-%20Dear%20Zindagi%20[DJMaza.Cool].mp3"));

        mAdapter = new SongsListAdapter((AppCompatActivity) getActivity(), songList, false, false);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        ((BaseActivity) getActivity()).setMusicStateListenerListener(this);

        return rootView;
    }

    public void restartLoader() {

    }

    public void onPlaylistChanged() {

    }

    boolean isPlayListSet = false;
    public void onMetaChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

//    private void reloadAdapter() {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(final Void... unused) {
//                List<Song> songList = SongLoader.getAllSongs(getActivity());
//                mAdapter.updateDataSet(songList);
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                mAdapter.notifyDataSetChanged();
//            }
//        }.execute();
//    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
//        if (!isPlayListSet) {
//            MusicPlayer.setPlayList(getActivity(), songList, 0);
//            isPlayListSet = true;
//        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.song_sort_by, menu);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_sort_by_az:
//                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_A_Z);
//                reloadAdapter();
//                return true;
//            case R.id.menu_sort_by_za:
//                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_Z_A);
//                reloadAdapter();
//                return true;
//            case R.id.menu_sort_by_artist:
//                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_ARTIST);
//                reloadAdapter();
//                return true;
//            case R.id.menu_sort_by_album:
//                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_ALBUM);
//                reloadAdapter();
//                return true;
//            case R.id.menu_sort_by_year:
//                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_YEAR);
//                reloadAdapter();
//                return true;
//            case R.id.menu_sort_by_duration:
//                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_DURATION);
//                reloadAdapter();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

//    private class loadSongs extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            if (getActivity() != null)
//                mAdapter = new SongsListAdapter((AppCompatActivity) getActivity(), SongLoader.getAllSongs(getActivity()), false, false);
//            return "Executed";
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            recyclerView.setAdapter(mAdapter);
//            if (getActivity() != null)
//                recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//        }
//    }
}
