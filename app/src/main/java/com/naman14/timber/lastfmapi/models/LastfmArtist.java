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

package com.naman14.timber.lastfmapi.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LastfmArtist {

    private static final String NAME = "name";
    private static final String IMAGE = "image";
    private static final String SIMILAR = "similar";
    private static final String TAGS = "tags";
    private static final String BIO = "bio";

    @SerializedName(NAME)
    public String mName;

    @SerializedName(IMAGE)
    public List<Artwork> mArtwork;

    @SerializedName(SIMILAR)
    public SimilarArtist mSimilarArtist;

    @SerializedName(TAGS)
    public ArtistTag mArtistTags;

    @SerializedName(BIO)
    public ArtistBio mArtistBio;


    public class SimilarArtist {

        public static final String ARTIST = "artist";

        @SerializedName(ARTIST)
        public List<LastfmArtist> mSimilarArtist;
    }

    public class ArtistTag {

        public static final String TAG = "tag";

        @SerializedName(TAG)
        public List<com.naman14.timber.lastfmapi.models.ArtistTag> mTags;
    }


}
