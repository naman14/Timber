package com.naman14.timber.dataloaders;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.naman14.timber.models.Album;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by naman on 07/07/15.
 */
public class AlbumLoader {

    public static Album getAlbum(Context paramContext, int paramInt)
    {
        return getAlbum(makeAlbumCursor(paramContext, "_id=?", new String[] { String.valueOf(paramInt) }));
    }

    public static Album getAlbum(Cursor paramCursor)
    {
        Album localAlbum2 = new Album();
        Album localAlbum1 = localAlbum2;
        if (paramCursor != null)
        {
            localAlbum1 = localAlbum2;
            if (paramCursor.moveToFirst())
                localAlbum1 = new Album(paramCursor.getInt(0), paramCursor.getString(1), paramCursor.getString(2), paramCursor.getInt(3), paramCursor.getInt(4), paramCursor.getInt(5));
        }
        if (paramCursor != null)
            paramCursor.close();
        return localAlbum1;
    }

    public static List<Album> getAlbums(Context paramContext, String paramString)
    {
        return getAlbums(makeAlbumCursor(paramContext, "album LIKE ?", new String[] { "%" + paramString + "%" }));
    }

    public static List<Album> getAlbums(Cursor paramCursor)
    {
        ArrayList localArrayList = new ArrayList();
        if ((paramCursor != null) && (paramCursor.moveToFirst()))
            do
            {
                String str = paramCursor.getString(1);
                localArrayList.add(new Album(paramCursor.getInt(0), str, paramCursor.getString(2), paramCursor.getInt(3), paramCursor.getInt(4), paramCursor.getInt(5)));
            }
            while (paramCursor.moveToNext());
        if (paramCursor != null)
            paramCursor.close();
        return localArrayList;
    }

    public static List<Album> getAllAlbums(Context paramContext)
    {
        return getAlbums(makeAlbumCursor(paramContext, null, null));
    }

    public static Cursor makeAlbumCursor(Context paramContext, String paramString, String[] paramArrayOfString)
    {
        ContentResolver localContentResolver = paramContext.getContentResolver();
        Uri localUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        return localContentResolver.query(localUri, new String[] { "_id", "album", "artist", "artist_id", "numsongs", "minyear" }, paramString, paramArrayOfString, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
    }
}
