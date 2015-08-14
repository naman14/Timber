package com.naman14.timber.dataloaders;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.PlaylistsColumns;

import com.naman14.timber.models.Playlist;
import com.naman14.timber.utils.TimberUtils;

import java.util.ArrayList;
import java.util.List;

public class PlaylistLoader {


    private static final ArrayList<Playlist> mPlaylistList = new ArrayList<>();

    private static Cursor mCursor;

    public static List<Playlist> getPlaylists(Context context) {

        makeDefaultPlaylists(context);

        mCursor = makePlaylistCursor(context);

        if (mCursor != null && mCursor.moveToFirst()) {
            do {

                final long id = mCursor.getLong(0);

                final String name = mCursor.getString(1);

                final int songCount = TimberUtils.getSongCountForPlaylist(context, id);

                final Playlist playlist = new Playlist(id, name, songCount);

                mPlaylistList.add(playlist);
            } while (mCursor.moveToNext());
        }
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        return mPlaylistList;
    }

    private static void makeDefaultPlaylists(Context context) {
        final Resources resources = context.getResources();

        /* Last added list */
        final Playlist lastAdded = new Playlist(TimberUtils.PlaylistType.LastAdded.mId,
                resources.getString(TimberUtils.PlaylistType.LastAdded.mTitleId), -1);
        mPlaylistList.add(lastAdded);

        /* Recently Played */
        final Playlist recentlyPlayed = new Playlist(TimberUtils.PlaylistType.RecentlyPlayed.mId,
                resources.getString(TimberUtils.PlaylistType.RecentlyPlayed.mTitleId), -1);
        mPlaylistList.add(recentlyPlayed);

        /* Top Tracks */
        final Playlist topTracks = new Playlist(TimberUtils.PlaylistType.TopTracks.mId,
                resources.getString(TimberUtils.PlaylistType.TopTracks.mTitleId), -1);
        mPlaylistList.add(topTracks);
    }


    public static final Cursor makePlaylistCursor(final Context context) {
        return context.getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[] {
                        BaseColumns._ID,
                        PlaylistsColumns.NAME
                }, null, null, MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);
    }
}
