/*
 * Copyright (C) 2012 Andrew Neal
 * Copyright (C) 2014 The CyanogenMod Project
 * Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.naman14.timber.utils;

import android.provider.MediaStore;

/**
 * Holds all of the sort orders for each list type.
 * 
 * @author Andrew Neal (andrewdneal@gmail.com)
 */
public final class SortOrder {

    /** This class is never instantiated */
    public SortOrder() {
    }

    /**
     * Artist sort order entries.
     */
    public static interface ArtistSortOrder {
        /* Artist sort order A-Z */
        public final static String ARTIST_A_Z = MediaStore.Audio.Artists.DEFAULT_SORT_ORDER;

        /* Artist sort order Z-A */
        public final static String ARTIST_Z_A = ARTIST_A_Z + " DESC";

        /* Artist sort order number of songs */
        public final static String ARTIST_NUMBER_OF_SONGS = MediaStore.Audio.Artists.NUMBER_OF_TRACKS
                + " DESC";

        /* Artist sort order number of albums */
        public final static String ARTIST_NUMBER_OF_ALBUMS = MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
                + " DESC";
    }

    /**
     * Album sort order entries.
     */
    public static interface AlbumSortOrder {
        /* Album sort order A-Z */
        public final static String ALBUM_A_Z = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER;

        /* Album sort order Z-A */
        public final static String ALBUM_Z_A = ALBUM_A_Z + " DESC";

        /* Album sort order songs */
        public final static String ALBUM_NUMBER_OF_SONGS = MediaStore.Audio.Albums.NUMBER_OF_SONGS
                + " DESC";

        /* Album sort order artist */
        public final static String ALBUM_ARTIST = MediaStore.Audio.Albums.ARTIST;

        /* Album sort order year */
        public final static String ALBUM_YEAR = MediaStore.Audio.Albums.FIRST_YEAR + " DESC";

    }

    /**
     * Song sort order entries.
     */
    public static interface SongSortOrder {
        /* Song sort order A-Z */
        public final static String SONG_A_Z = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

        /* Song sort order Z-A */
        public final static String SONG_Z_A = SONG_A_Z + " DESC";

        /* Song sort order artist */
        public final static String SONG_ARTIST = MediaStore.Audio.Media.ARTIST;

        /* Song sort order album */
        public final static String SONG_ALBUM = MediaStore.Audio.Media.ALBUM;

        /* Song sort order year */
        public final static String SONG_YEAR = MediaStore.Audio.Media.YEAR + " DESC";

        /* Song sort order duration */
        public final static String SONG_DURATION = MediaStore.Audio.Media.DURATION + " DESC";

        /* Song sort order date */
        public final static String SONG_DATE = MediaStore.Audio.Media.DATE_ADDED + " DESC";

        /* Song sort order filename */
        public final static String SONG_FILENAME = MediaStore.Audio.Media.DATA;
    }

    /**
     * Album song sort order entries.
     */
    public static interface AlbumSongSortOrder {
        /* Album song sort order A-Z */
        public final static String SONG_A_Z = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

        /* Album song sort order Z-A */
        public final static String SONG_Z_A = SONG_A_Z + " DESC";

        /* Album song sort order track list */
        public final static String SONG_TRACK_LIST = MediaStore.Audio.Media.TRACK + ", "
                + MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

        /* Album song sort order duration */
        public final static String SONG_DURATION = SongSortOrder.SONG_DURATION;

        /* Album Song sort order year */
        public final static String SONG_YEAR = MediaStore.Audio.Media.YEAR + " DESC";

        /* Album song sort order filename */
        public final static String SONG_FILENAME = SongSortOrder.SONG_FILENAME;
    }

    /**
     * Artist song sort order entries.
     */
    public static interface ArtistSongSortOrder {
        /* Artist song sort order A-Z */
        public final static String SONG_A_Z = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

        /* Artist song sort order Z-A */
        public final static String SONG_Z_A = SONG_A_Z + " DESC";

        /* Artist song sort order album */
        public final static String SONG_ALBUM = MediaStore.Audio.Media.ALBUM;

        /* Artist song sort order year */
        public final static String SONG_YEAR = MediaStore.Audio.Media.YEAR + " DESC";

        /* Artist song sort order duration */
        public final static String SONG_DURATION = MediaStore.Audio.Media.DURATION + " DESC";

        /* Artist song sort order date */
        public final static String SONG_DATE = MediaStore.Audio.Media.DATE_ADDED + " DESC";

        /* Artist song sort order filename */
        public final static String SONG_FILENAME = SongSortOrder.SONG_FILENAME;
    }

    /**
     * Artist album sort order entries.
     */
    public static interface ArtistAlbumSortOrder {
        /* Artist album sort order A-Z */
        public final static String ALBUM_A_Z = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER;

        /* Artist album sort order Z-A */
        public final static String ALBUM_Z_A = ALBUM_A_Z + " DESC";

        /* Artist album sort order songs */
        public final static String ALBUM_NUMBER_OF_SONGS = MediaStore.Audio.Artists.Albums.NUMBER_OF_SONGS
                + " DESC";

        /* Artist album sort order year */
        public final static String ALBUM_YEAR = MediaStore.Audio.Artists.Albums.FIRST_YEAR
                + " DESC";
    }

}
