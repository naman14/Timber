package com.naman14.timber.lastfmapi.models;

import com.google.gson.annotations.SerializedName;
import com.naman14.timber.lastfmapi.LastFmClient;

/**
 * Created by christoph on 17.07.16.
 */
public class ScrobbleQuery {
    private static final String ARTIST_NAME = "artist";
    private static final String TRACK_NAME = "track";
    private static final String TIMESTAMP_NAME = "timestamp";

    @SerializedName(ARTIST_NAME)
    public String mArtist;

    @SerializedName(TRACK_NAME)
    public String mTrack;

    @SerializedName(TIMESTAMP_NAME)
    public long mTimestamp;

    public static final String Method = "track.scrobble";

    public ScrobbleQuery(String artist, String track, long timestamp) {
        this.mArtist = artist;
        this.mTrack = track;
        this.mTimestamp = timestamp;
    }

    public String getSignature(String token) {
        return "api_key" + LastFmClient.API_KEY + ARTIST_NAME + this.mArtist + "method" + Method + "sk" + token + TIMESTAMP_NAME + this.mTimestamp + TRACK_NAME + this.mTrack + LastFmClient.API_SECRET;
    }
}
