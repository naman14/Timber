package com.naman14.timber.lastfmapi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by naman on 08/07/15.
 */
public class ArtistBio {

    private static final String PUBLISHED = "published";
    private static final String SUMMARY = "summary";
    private static final String CONTENT = "content";
    private static final String YEARFORMED = "yearformed";

    @SerializedName(PUBLISHED)
    public String mPublished ;

    @SerializedName(SUMMARY)
    public String mSummary ;

    @SerializedName(CONTENT)
    public String mContent ;

    @SerializedName(YEARFORMED)
    public String mYearFormed ;

}
