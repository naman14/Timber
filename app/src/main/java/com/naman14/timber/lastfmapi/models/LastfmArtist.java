package com.naman14.timber.lastfmapi.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by naman on 08/07/15.
 */
public class LastfmArtist {

    private static final String NAME = "name";
    private static final String IMAGE  = "image";
    private static final String SIMILAR = "similar";
    private static final String TAGS= "tags";
    private static final String BIO = "bio";

    @SerializedName(NAME)
    public String mName ;

    @SerializedName(IMAGE)
    public List<Artwork> mArtwork ;

    @SerializedName(SIMILAR)
    public List<LastfmArtist> mSimilarArtist ;

    @SerializedName(TAGS)
    public List<ArtistTag> mArtistTags ;

    @SerializedName(BIO)
    public ArtistBio mArtistBio ;



}
