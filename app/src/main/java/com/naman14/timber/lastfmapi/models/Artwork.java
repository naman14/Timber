package com.naman14.timber.lastfmapi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by naman on 08/07/15.
 */
public class Artwork {

    private static final String URL   = "#text";
    private static final String SIZE   = "size";

    @SerializedName(URL)
    public String mUrl ;

    @SerializedName(SIZE)
    public String mSize ;
}
