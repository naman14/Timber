package com.naman14.timber.dataloaders;

import android.content.Context;
import android.database.Cursor;

import com.naman14.timber.models.Song;

import java.util.ArrayList;


public class QueueLoader  {


    private static final ArrayList<Song> mSongList = new ArrayList<>();

    private static NowPlayingCursor mCursor;


    public static ArrayList<Song> getQueueSongsList(Context context) {
        mCursor = new NowPlayingCursor(context);
        if (mCursor != null && mCursor.moveToFirst()) {
            do {
                final long id = mCursor.getLong(0);

                final String songName = mCursor.getString(1);

                final String artist = mCursor.getString(2);

                final long albumId = mCursor.getLong(3);

                final String album = mCursor.getString(4);

                final long duration = mCursor.getLong(5);

                final int durationInSecs = (int) duration / 1000;

                final int trackNo = mCursor.getInt(6);

                final long artistID=mCursor.getLong(7);

                final Song song = new Song(id, albumId, artistID, songName, album, artist,durationInSecs, trackNo);

                mSongList.add(song);
            } while (mCursor.moveToNext());
        }
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        return mSongList;
    }

    public static final Cursor makeQueueCursor(final Context context) {
        final Cursor cursor = new NowPlayingCursor(context);
        return cursor;
    }
}
