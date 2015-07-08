package com.naman14.timber.lastfmapi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by naman on 08/07/15.
 */
public class AlbumInfo {

    private static final String ALBUM = "album";


    @SerializedName(ALBUM)
    public LastfmAlbum mAlbum;
}
