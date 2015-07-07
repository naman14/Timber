package com.naman14.timber.models;

/**
 * Created by naman on 07/07/15.
 */
public class Album {
    public final int artistId;
    public final String artistName;
    public final int id;
    public final int songCount;
    public final String title;
    public final int year;

    public Album()
    {
        this.id = -1;
        this.title = "";
        this.artistName = "";
        this.artistId = -1;
        this.songCount = -1;
        this.year = -1;
    }

    public Album(int _id, String _title, String _artistName, int _artistId, int _songCount, int _year)
    {
        this.id = _id;
        this.title = _title;
        this.artistName = _artistName;
        this.artistId = _artistId;
        this.songCount = _songCount;
        this.year = _year;
    }


}
