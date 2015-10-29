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

package com.naman14.timber.lastfmapi;

import android.content.Context;
import android.util.Log;

import com.naman14.timber.lastfmapi.callbacks.ArtistInfoListener;
import com.naman14.timber.lastfmapi.models.AlbumInfo;
import com.naman14.timber.lastfmapi.models.AlbumQuery;
import com.naman14.timber.lastfmapi.models.ArtistInfo;
import com.naman14.timber.lastfmapi.models.ArtistQuery;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LastFmClient {

    public static final String BASE_API_URL = "http://ws.audioscrobbler.com/2.0";

    private static LastFmClient sInstance;
    private LastFmRestService mRestService;

    private static final Object sLock = new Object();

    public static LastFmClient getInstance(Context context) {
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new LastFmClient();
                sInstance.mRestService = RestServiceFactory.create(context, BASE_API_URL, LastFmRestService.class);
            }
            return sInstance;
        }
    }

    public void getAlbumInfo(AlbumQuery albumQuery) {
        mRestService.getAlbumInfo(albumQuery.mArtist, albumQuery.mALbum, new Callback<AlbumInfo>() {
            @Override
            public void success(AlbumInfo albumInfo, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

                error.printStackTrace();
            }
        });
    }

    public void getArtistInfo(ArtistQuery artistQuery, final ArtistInfoListener listener) {
        mRestService.getArtistInfo(artistQuery.mArtist, new Callback<ArtistInfo>() {
            @Override
            public void success(ArtistInfo artistInfo, Response response) {
                listener.artistInfoSucess(artistInfo.mArtist);
            }

            @Override
            public void failure(RetrofitError error) {
                listener.artistInfoFailed();
                Log.d("lol", "failed");
                error.printStackTrace();
            }
        });
    }
}
