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

package com.naman14.timber;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.MediaDescription;
import android.media.browse.MediaBrowser;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.service.media.MediaBrowserService;
import android.support.annotation.Nullable;

import com.naman14.timber.dataloaders.AlbumLoader;
import com.naman14.timber.dataloaders.AlbumSongLoader;
import com.naman14.timber.dataloaders.ArtistAlbumLoader;
import com.naman14.timber.dataloaders.ArtistLoader;
import com.naman14.timber.dataloaders.ArtistSongLoader;
import com.naman14.timber.dataloaders.PlaylistLoader;
import com.naman14.timber.dataloaders.PlaylistSongLoader;
import com.naman14.timber.dataloaders.SongLoader;
import com.naman14.timber.models.Album;
import com.naman14.timber.models.Artist;
import com.naman14.timber.models.Playlist;
import com.naman14.timber.models.Song;
import com.naman14.timber.utils.TimberUtils;

import java.util.ArrayList;
import java.util.List;

@TargetApi(21)
public class WearBrowserService extends MediaBrowserService {

    public static final String MEDIA_ID_ROOT = "__ROOT__";
    public static final int TYPE_ARTIST = 0;
    public static final int TYPE_ALBUM = 1;
    public static final int TYPE_SONG = 2;
    public static final int TYPE_PLAYLIST = 3;
    public static final int TYPE_ARTIST_SONG_ALBUMS = 4;
    public static final int TYPE_ALBUM_SONGS = 5;
    public static final int TYPE_ARTIST_ALL_SONGS = 6;
    public static final int TYPE_PLAYLIST_ALL_SONGS = 7;

    MediaSession mSession;
    public static WearBrowserService sInstance;

    private Context mContext;
    private boolean mServiceStarted;

    public static WearBrowserService getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mContext = this;
        mSession = new MediaSession(this, "WearBrowserService");
        setSessionToken(mSession.getSessionToken());
        mSession.setCallback(new MediaSessionCallback());
        mSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

    }

    @Override
    public int onStartCommand(Intent startIntent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mServiceStarted = false;
        mSession.release();
    }

    @Override
    public void onLoadChildren(String parentId, Result<List<MediaBrowser.MediaItem>> result) {

        result.detach();
        loadChildren(parentId, result);

    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(String clientPackageName, int clientUid, Bundle rootHints) {
        return new BrowserRoot(MEDIA_ID_ROOT, null);
    }

    private final class MediaSessionCallback extends MediaSession.Callback {

        @Override
        public void onPlay() {
            setSessionActive();
        }

        @Override
        public void onSeekTo(long position) {

        }

        @Override
        public void onPlayFromMediaId(final String mediaId, Bundle extras) {
            long songId = Long.parseLong(mediaId);
            setSessionActive();
            MusicPlayer.playAll(mContext, new long[]{songId}, 0, -1, TimberUtils.IdType.NA, false);
        }

        @Override
        public void onPause() {

        }

        @Override
        public void onStop() {
            setSessionInactive();
        }

        @Override
        public void onSkipToNext() {

        }

        @Override
        public void onSkipToPrevious() {

        }

        @Override
        public void onFastForward() {

        }

        @Override
        public void onRewind() {

        }

        @Override
        public void onCustomAction(String action, Bundle extras) {

        }
    }

    private void setSessionActive() {
        if (!mServiceStarted) {
            startService(new Intent(getApplicationContext(), WearBrowserService.class));
            mServiceStarted = true;
        }

        if (!mSession.isActive()) {
            mSession.setActive(true);
        }
    }

    private void setSessionInactive() {
        if (mServiceStarted) {
            stopSelf();
            mServiceStarted = false;
        }

        if (mSession.isActive()) {
            mSession.setActive(false);
        }
    }

    private void addMediaRoots(List<MediaBrowser.MediaItem> mMediaRoot) {
        mMediaRoot.add(new MediaBrowser.MediaItem(
                new MediaDescription.Builder()
                        .setMediaId(Integer.toString(TYPE_ARTIST))
                        .setTitle(getString(R.string.artists))
                        .setIconUri(Uri.parse("android.resource://" +
                                "naman14.timber/drawable/ic_empty_music2"))
                        .setSubtitle(getString(R.string.artists))
                        .build(), MediaBrowser.MediaItem.FLAG_BROWSABLE
        ));

        mMediaRoot.add(new MediaBrowser.MediaItem(
                new MediaDescription.Builder()
                        .setMediaId(Integer.toString(TYPE_ALBUM))
                        .setTitle(getString(R.string.albums))
                        .setIconUri(Uri.parse("android.resource://" +
                                "naman14.timber/drawable/ic_empty_music2"))
                        .setSubtitle(getString(R.string.albums))
                        .build(), MediaBrowser.MediaItem.FLAG_BROWSABLE
        ));

        mMediaRoot.add(new MediaBrowser.MediaItem(
                new MediaDescription.Builder()
                        .setMediaId(Integer.toString(TYPE_SONG))
                        .setTitle(getString(R.string.songs))
                        .setIconUri(Uri.parse("android.resource://" +
                                "naman14.timber/drawable/ic_empty_music2"))
                        .setSubtitle(getString(R.string.songs))
                        .build(), MediaBrowser.MediaItem.FLAG_BROWSABLE
        ));


        mMediaRoot.add(new MediaBrowser.MediaItem(
                new MediaDescription.Builder()
                        .setMediaId(Integer.toString(TYPE_PLAYLIST))
                        .setTitle(getString(R.string.playlists))
                        .setIconUri(Uri.parse("android.resource://" +
                                "naman14.timber/drawable/ic_empty_music2"))
                        .setSubtitle(getString(R.string.playlists))
                        .build(), MediaBrowser.MediaItem.FLAG_BROWSABLE
        ));

    }


    private void loadChildren(final String parentId, final Result<List<MediaBrowser.MediaItem>> result) {

        final List<MediaBrowser.MediaItem> mediaItems = new ArrayList<>();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {

                if (parentId.equals(MEDIA_ID_ROOT)) {
                    addMediaRoots(mediaItems);
                } else {
                    switch (Integer.parseInt(Character.toString(parentId.charAt(0)))) {
                        case TYPE_ARTIST:
                            List<Artist> artistList = ArtistLoader.getAllArtists(mContext);
                            for (Artist artist : artistList) {
                                String albumNmber = TimberUtils.makeLabel(mContext, R.plurals.Nalbums, artist.albumCount);
                                String songCount = TimberUtils.makeLabel(mContext, R.plurals.Nsongs, artist.songCount);
                                fillMediaItems(mediaItems, Integer.toString(TYPE_ARTIST_SONG_ALBUMS) + Long.toString(artist.id), artist.name, Uri.parse("android.resource://" +
                                        "naman14.timber/drawable/ic_empty_music2"), TimberUtils.makeCombinedString(mContext, albumNmber, songCount), MediaBrowser.MediaItem.FLAG_BROWSABLE);
                            }
                            break;
                        case TYPE_ALBUM:
                            List<Album> albumList = AlbumLoader.getAllAlbums(mContext);
                            for (Album album : albumList) {
                                fillMediaItems(mediaItems, Integer.toString(TYPE_ALBUM_SONGS) + Long.toString(album.id), album.title, TimberUtils.getAlbumArtUri(album.id), album.artistName, MediaBrowser.MediaItem.FLAG_BROWSABLE);
                            }
                            break;
                        case TYPE_SONG:
                            List<Song> songList = SongLoader.getAllSongs(mContext);
                            for (Song song : songList) {
                                fillMediaItems(mediaItems, String.valueOf(song.id), song.title, TimberUtils.getAlbumArtUri(song.albumId), song.artistName, MediaBrowser.MediaItem.FLAG_PLAYABLE);
                            }
                            break;
                        case TYPE_ALBUM_SONGS:
                            List<Song> albumSongList = AlbumSongLoader.getSongsForAlbum(mContext, Long.parseLong(parentId.substring(1)));
                            for (Song song : albumSongList) {
                                fillMediaItems(mediaItems, String.valueOf(song.id), song.title, TimberUtils.getAlbumArtUri(song.albumId), song.artistName, MediaBrowser.MediaItem.FLAG_PLAYABLE);
                            }
                            break;
                        case TYPE_ARTIST_SONG_ALBUMS:
                            fillMediaItems(mediaItems, Integer.toString(TYPE_ARTIST_ALL_SONGS) + Long.parseLong(parentId.substring(1)), "All songs", Uri.parse("android.resource://" +
                                    "naman14.timber/drawable/ic_empty_music2"), "All songs by artist", MediaBrowser.MediaItem.FLAG_BROWSABLE);
                            List<Album> artistAlbums = ArtistAlbumLoader.getAlbumsForArtist(mContext, Long.parseLong(parentId.substring(1)));
                            for (Album album : artistAlbums) {
                                String songCount = TimberUtils.makeLabel(mContext, R.plurals.Nsongs, album.songCount);
                                fillMediaItems(mediaItems, Integer.toString(TYPE_ALBUM_SONGS) + Long.toString(album.id), album.title, TimberUtils.getAlbumArtUri(album.id), songCount, MediaBrowser.MediaItem.FLAG_BROWSABLE);

                            }
                            break;
                        case TYPE_ARTIST_ALL_SONGS:
                            List<Song> artistSongs = ArtistSongLoader.getSongsForArtist(mContext, Long.parseLong(parentId.substring(1)));
                            for (Song song : artistSongs) {
                                fillMediaItems(mediaItems, String.valueOf(song.id), song.title, TimberUtils.getAlbumArtUri(song.albumId), song.albumName, MediaBrowser.MediaItem.FLAG_PLAYABLE);
                            }
                            break;
                        case TYPE_PLAYLIST:
                            List<Playlist> playlistList = PlaylistLoader.getPlaylists(mContext, false);
                            for (Playlist playlist : playlistList) {
                                String songCount = TimberUtils.makeLabel(mContext, R.plurals.Nsongs, playlist.songCount);
                                fillMediaItems(mediaItems, Integer.toString(TYPE_PLAYLIST_ALL_SONGS) + Long.toString(playlist.id), playlist.name,
                                        Uri.parse("android.resource://" +
                                                "naman14.timber/drawable/ic_empty_music2"), songCount, MediaBrowser.MediaItem.FLAG_BROWSABLE);
                            }
                            break;
                        case TYPE_PLAYLIST_ALL_SONGS:
                            List<Song> playlistSongs = PlaylistSongLoader.getSongsInPlaylist(mContext, Long.parseLong(parentId.substring(1)));
                            for (Song song : playlistSongs) {
                                fillMediaItems(mediaItems, String.valueOf(song.id), song.title, TimberUtils.getAlbumArtUri(song.albumId), song.albumName, MediaBrowser.MediaItem.FLAG_PLAYABLE);
                            }
                            break;

                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                result.sendResult(mediaItems);
            }
        }.execute();

    }

    private void fillMediaItems(List<MediaBrowser.MediaItem> mediaItems, String mediaId, String title, Uri icon, String subTitle, int playableOrBrowsable) {
        mediaItems.add(new MediaBrowser.MediaItem(
                new MediaDescription.Builder()
                        .setMediaId(mediaId)
                        .setTitle(title)
                        .setIconUri(icon)
                        .setSubtitle(subTitle)
                        .build(), playableOrBrowsable
        ));
    }

}
