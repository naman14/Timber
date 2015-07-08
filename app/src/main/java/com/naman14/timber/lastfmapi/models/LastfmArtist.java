package com.naman14.timber.lastfmapi.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by naman on 08/07/15.
 */
public class LastfmArtist {

    private static final String IMAGE  = "image";

    @SerializedName(IMAGE)
    public List<Artwork> mArtwork ;

}
