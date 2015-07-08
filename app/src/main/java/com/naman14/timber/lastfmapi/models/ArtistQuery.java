package com.naman14.timber.lastfmapi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by naman on 08/07/15.
 */
public class ArtistQuery {

    private static final String ARTIST_NAME = "artist";


    @SerializedName(ARTIST_NAME)
    public String mArtist;

    public ArtistQuery(String artist){
        this.mArtist=artist;
    }


}
