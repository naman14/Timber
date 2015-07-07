package com.naman14.timber.models;

/**
 * Created by naman on 13/06/15.
 */
public class Song {

    public final int albumId;
    public final String albumName;
    public final int artistId;
    public final String artistName;
    public final long duration;
    public final int id;
    public final String title;
    public final int trackNumber;

    public Song()
    {
        this.id = -1;
        this.albumId = -1;
        this.artistId = -1;
        this.title = "";
        this.artistName = "";
        this.albumName = "";
        this.duration = -1L;
        this.trackNumber = -1;
    }

    public Song(int _id, int _albumId, int _artistId, String _title, String _artistName, String _albumName, long _duration, int _trackNumber)
    {
        this.id = _id;
        this.albumId = _albumId;
        this.artistId = _artistId;
        this.title = _title;
        this.artistName = _artistName;
        this.albumName = _albumName;
        this.duration = _duration;
        this.trackNumber = _trackNumber;
    }
}
