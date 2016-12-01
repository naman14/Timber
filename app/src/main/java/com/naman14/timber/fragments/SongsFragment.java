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

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naman14.timber.R;
import com.naman14.timber.activities.BaseActivity;
import com.naman14.timber.adapters.SongsListAdapter;
import com.naman14.timber.helpers.MusicPlaybackTrack;
import com.naman14.timber.listeners.MusicStateListener;
import com.naman14.timber.utils.FileCrypto;
import com.naman14.timber.utils.PreferencesUtility;
import com.naman14.timber.widgets.DividerItemDecoration;
import com.naman14.timber.widgets.FastScroller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;



public class SongsFragment extends Fragment implements MusicStateListener {

    private SongsListAdapter mAdapter;
    private RecyclerView recyclerView;
    private PreferencesUtility mPreferences;
    List<MusicPlaybackTrack> songList;
    public static String fileName = "Jabra FAN.mp4";
    public static String encrypted_fileName = "encry_" + fileName;

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

        encrypt();

//        new loadSongs().execute("");

        songList = new ArrayList<>();
        songList.add(new MusicPlaybackTrack("Jabra FAN", encrypted_fileName));
        songList.add(new MusicPlaybackTrack("Tere Naal Ishqa","http://www.mp3khan.in/files/Bollywood Mp3 and Videos/New Relesed Bollywood/Shivaay Movies Songs/04 - Tere Naal Ishqa - Shivaay  [ Mp3Khan.in ] .mp3"));
//        songList.add(new MusicPlaybackTrack("Chittiyaan Kalaiyaan","content://media/external/audio/media/1964"));
        songList.add(new MusicPlaybackTrack("O Janiya","http://www.mp3khan.in/files/Bollywood Mp3 and Videos/New Relesed Bollywood/Force 2 Movies Songs/02 - O Janiya - Force 2.mp3"));
//        songList.add(new MusicPlaybackTrack("Waka waka","content://media/external/audio/media/1965"));
        songList.add(new MusicPlaybackTrack("Dil Mein Chhupa Loonga","http://www.mp3khan.in/files/Bollywood Mp3 and Videos/New Relesed Bollywood/Wajah Tum Ho Movies Songs/03 - Dil Mein Chhupa Loonga - Wajah Tum Ho.mp3"));
        songList.add(new MusicPlaybackTrack("Let's Breakup","http://mp3khan.top/music/indian_movies/Dear%20Zindagi%20(2016)/04%20-%20Lets%20Break%20Up%20-%20Dear%20Zindagi%20[DJMaza.Cool].mp3"));
        songList.add(new MusicPlaybackTrack("Haanikaarak Bapu","http://www.mp3khan.in/files/Bollywood Mp3 and Videos/New Relesed Bollywood/Dangal Movies Songs/Haanikaarak Bapu - 128Kbps.mp3"));

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

    private void encrypt() {
        try {

            // encrypt audio file send as second argument and corresponding key in first argument.
            byte[] encrypt = FileCrypto.encrypt(getAudioFile());

            FileOutputStream fos = new FileOutputStream(new File(getActivity().getExternalFilesDir(null), encrypted_fileName));
            fos.write(encrypt);
            fos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public byte[] getAudioFile() throws FileNotFoundException {

        byte[] audio_data = null;

        byte[] inarry = null;

        AssetManager am = getActivity().getApplicationContext().getAssets();

        try {

            InputStream is = am.open(fileName); // use recorded file instead of getting file from assets folder.

            int length = is.available();

            audio_data = new byte[length];

            int bytesRead;

            ByteArrayOutputStream output = new ByteArrayOutputStream();

            while ((bytesRead = is.read(audio_data)) != -1) {
                output.write(audio_data, 0, bytesRead);
            }

            inarry = output.toByteArray();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return inarry;

    }
}
