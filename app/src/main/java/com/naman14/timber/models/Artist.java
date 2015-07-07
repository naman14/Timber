package com.naman14.timber.models;

/**
 * Created by naman on 07/07/15.
 */
public class Artist {

    public final int albumCount;
    public final int id;
    public final String name;
    public final int songCount;

    public Artist()
    {
        this.id = -1;
        this.name = "";
        this.songCount = -1;
        this.albumCount = -1;
    }

    public Artist(int int1, String paramString, int int2, int int3)
    {
        this.id = int1;
        this.name = paramString;
        this.songCount = int3;
        this.albumCount = int2;
    }

}
