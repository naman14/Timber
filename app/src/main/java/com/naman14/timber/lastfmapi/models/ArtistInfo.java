package com.naman14.timber.lastfmapi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by naman on 08/07/15.
 */
public class ArtistInfo {

    private static final String ARTIST = "artist";


    @SerializedName(ARTIST)
    public LastfmArtist mArtist;

}
