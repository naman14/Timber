package com.naman14.timber.dataloaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.naman14.timber.models.Song;

import java.util.ArrayList;

/**
 * Created by naman on 07/07/15.
 */
public class SongLoader {

    private static final long[] sEmptyList=new long[0];

    private static final String BASE_SELECTION = "is_music=1 AND title != ''";

    public static ArrayList<Song> getAllSongs(Context paramContext)
    {
        return getSongs(makeSongCursor(paramContext, null, null));
    }

    public static ArrayList<Song> getSongs(Context paramContext, String paramString)
    {
        return getSongs(makeSongCursor(paramContext, "title LIKE ?", new String[] { "%" + paramString + "%" }));
    }

    public static ArrayList<Song> getSongs(Cursor paramCursor)
    {
        ArrayList localArrayList = new ArrayList();
        if ((paramCursor != null) && (paramCursor.moveToFirst()))
            do
            {
                String str1 = paramCursor.getString(1);
                int i = paramCursor.getInt(0);
                String str2 = paramCursor.getString(2);
                String str3 = paramCursor.getString(3);
                long l = paramCursor.getLong(4);
                int j = paramCursor.getInt(5);
                int k = paramCursor.getInt(6);
                localArrayList.add(new Song(i, paramCursor.getInt(7), k, str1, str2, str3, l, j));
            }
            while (paramCursor.moveToNext());
        if (paramCursor != null)
            paramCursor.close();
        return localArrayList;
    }

    public static final long[] getSongListForCursor(Cursor cursor) {
        if (cursor == null) {
            return sEmptyList;
        }
        final int len = cursor.getCount();
        final long[] list = new long[len];
        cursor.moveToFirst();
        int columnIndex = -1;
        try {
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID);
        } catch (final IllegalArgumentException notaplaylist) {
            columnIndex = cursor.getColumnIndexOrThrow(BaseColumns._ID);
        }
        for (int i = 0; i < len; i++) {
            list[i] = cursor.getLong(columnIndex);
            cursor.moveToNext();
        }
        cursor.close();
        cursor = null;
        return list;
    }

    public static Cursor makeSongCursor(Context paramContext, String paramString, String[] paramArrayOfString)
    {
        Object localObject2 = "is_music=1 AND title != ''";
        Object localObject1 = localObject2;
        if (paramString != null)
        {
            localObject1 = localObject2;
            if (!paramString.trim().equals(""))
                localObject1 = "is_music=1 AND title != ''" + " AND " + paramString;
        }
        localObject2 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        return  paramContext.getContentResolver().query((Uri)localObject2, new String[] { "_id", "title", "artist", "album", "duration", "track", "artist_id", "album_id" }, (String)localObject1, paramArrayOfString, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    }

}
