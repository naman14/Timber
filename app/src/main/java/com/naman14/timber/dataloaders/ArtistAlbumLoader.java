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

import com.naman14.timber.models.Album;
import com.naman14.timber.models.Song;

import java.util.ArrayList;
import java.util.List;

public class ArtistAlbumLoader {

    public static ArrayList<Album> getAlbumsForArtist(Context context, long artistID) {

        if (artistID == -1)
            return null;

        List<Album> allAlbums = AlbumLoader.getAllAlbums(context);
        ArrayList<Album> artistAlbums = new ArrayList<>();
        for (Album album: allAlbums) {
            if (album.artistId == artistID) {
                artistAlbums.add(album);
            }
        }
        return artistAlbums;
    }

}
