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

import com.naman14.timber.lastfmapi.models.AlbumInfo;
import com.naman14.timber.lastfmapi.models.ArtistInfo;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;

public interface LastFmRestService {

    String BASE_PARAMETERS_ALBUM = "/?method=album.getinfo&api_key=fdb3a51437d4281d4d64964d333531d4&format=json";
    String BASE_PARAMETERS_ARTIST = "/?method=artist.getinfo&api_key=fdb3a51437d4281d4d64964d333531d4&format=json";

    @Headers("Cache-Control: public")
    @GET(BASE_PARAMETERS_ALBUM)
    void getAlbumInfo(@Query("artist") String artist, @Query("album") String album, Callback<AlbumInfo> callback);

    @Headers("Cache-Control: public")
    @GET(BASE_PARAMETERS_ARTIST)
    void getArtistInfo(@Query("artist") String artist, Callback<ArtistInfo> callback);

}
