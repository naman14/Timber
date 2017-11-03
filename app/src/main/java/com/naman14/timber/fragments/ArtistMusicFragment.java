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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naman14.timber.R;
import com.naman14.timber.adapters.AlbumAdapter;
import com.naman14.timber.adapters.ArtistAlbumAdapter;
import com.naman14.timber.dataloaders.ArtistAlbumLoader;
import com.naman14.timber.models.Album;
import com.naman14.timber.utils.Constants;

import java.util.ArrayList;

public class ArtistMusicFragment extends Fragment {

    public static RecyclerView albumsRecyclerView;
    long artistID = -1;
    AlbumAdapter albumAdapter;

    public static ArtistMusicFragment newInstance(long id) {
        ArtistMusicFragment fragment = new ArtistMusicFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.ARTIST_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artistID = getArguments().getLong(Constants.ARTIST_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_artist_music, container, false);

        albumsRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_albums);

        setUpAlbums();


        return rootView;
    }


    private void setUpAlbums() {
        albumsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        ArrayList<Album> albumList = ArtistAlbumLoader.getAlbumsForArtist(getActivity(), artistID);
        ArtistAlbumAdapter albumAdapter = new ArtistAlbumAdapter(getActivity(), albumList);
        albumsRecyclerView.setAdapter(albumAdapter);
    }


}
