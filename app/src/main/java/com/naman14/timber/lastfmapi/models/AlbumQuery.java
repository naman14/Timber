package com.naman14.timber.lastfmapi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by naman on 08/07/15.
 */
public class AlbumQuery {

    private static final String ALBUM_NAME = "album";
    private static final String ARTIST_NAME = "artist";

    @SerializedName(ALBUM_NAME)
    public String mALbum;

    @SerializedName(ARTIST_NAME)
    public String mArtist;

    public AlbumQuery(String album, String artist){
        this.mALbum=album;
        this.mArtist=artist;
    }


}
