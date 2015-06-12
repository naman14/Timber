package com.naman14.timber.models;

/**
 * Created by naman on 13/06/15.
 */
public class SongModel {

    private long id;
    private String title;
    private String artist;


    public SongModel(long songID, String songTitle, String songArtist) {
        id=songID;
        title=songTitle;
        artist=songArtist;
    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
}
