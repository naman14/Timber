package com.naman14.timber.models;

/**
 * Created by naman on 13/06/15.
 */
public class Song {

    private long songId;
    private String title;
    private String artist;
    private String album;
    private long albumId;
    private int duration;
    private int year;


    public Song(long songID, String songTitle, String songArtist, String songAlbum, long songalbumId, int songDuration) {
        songId=songID;
        title=songTitle;
        artist=songArtist;
        album=songAlbum;
        albumId=songalbumId;
        duration=songDuration;

    }

    public long getSongID(){return songId;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getAlbum(){return album;}
    public long getAlbumId(){return albumId;}
    public int getDuration(){return duration;}
    public int getYear(){return year;}
}
