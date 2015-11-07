/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.naman14.timber.dataloaders;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.naman14.timber.models.Artist;
import com.naman14.timber.utils.PreferencesUtility;

import java.util.ArrayList;
import java.util.List;

public class ArtistLoader {

    public static Artist getArtist(Cursor cursor) {
        Artist artist = new Artist();
        if (cursor != null) {
            if (cursor.moveToFirst())
                artist = new Artist(cursor.getLong(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3));
        }
        if (cursor != null)
            cursor.close();
        return artist;
    }

    public static List<Artist> getArtistsForCursor(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        if ((cursor != null) && (cursor.moveToFirst()))
            do {
                arrayList.add(new Artist(cursor.getLong(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3)));
            }
            while (cursor.moveToNext());
        if (cursor != null)
            cursor.close();
        return arrayList;
    }

    public static List<Artist> getAllArtists(Context context) {
        return getArtistsForCursor(makeArtistCursor(context, null, null));
    }

    public static Artist getArtist(Context context, long id) {
        return getArtist(makeArtistCursor(context, "_id=?", new String[]{String.valueOf(id)}));
    }

    public static List<Artist> getArtists(Context context, String paramString) {
        return getArtistsForCursor(makeArtistCursor(context, "artist LIKE ?", new String[]{"%" + paramString + "%"}));
    }


    public static Cursor makeArtistCursor(Context context, String selection, String[] paramArrayOfString) {
        final String artistSortOrder = PreferencesUtility.getInstance(context).getArtistSortOrder();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, new String[]{"_id", "artist", "number_of_albums", "number_of_tracks"}, selection, paramArrayOfString, artistSortOrder);
        return cursor;
    }
}
