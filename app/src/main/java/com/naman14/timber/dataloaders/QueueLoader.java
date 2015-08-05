package com.naman14.timber.dataloaders;

import android.content.Context;

import com.naman14.timber.MusicPlayer;
import com.naman14.timber.models.Song;

import java.util.ArrayList;


public class QueueLoader  {


    private static final ArrayList<Song> mSongList = new ArrayList<>();

    public static ArrayList<Song> getQueueSongsList(Context context) {

        if (!mSongList.isEmpty()){
            mSongList.clear();
        }
        long[] songIDLIst= MusicPlayer.getQueue();
        for (int i=0;i<songIDLIst.length;i++){
            mSongList.add(i,SongLoader.getSongForID(context,songIDLIst[i]));
        }
        return mSongList;
    }

}
