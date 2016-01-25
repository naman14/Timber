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
import com.naman14.timber.dataloaders.ArtistLoader;
import com.naman14.timber.models.Album;
import com.naman14.timber.models.Artist;
import com.naman14.timber.utils.TimberUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by naman on 08/01/16.
 */
@TargetApi(21)
public class WearBrowserService extends MediaBrowserService {

    public static final String MEDIA_ID_ROOT = "__ROOT__";
    public static final int TYPE_ARTIST = 0;
    public static final int TYPE_ALBUM = 1;
    public static final int TYPE_SONG = 2;
    public static final int TYPE_PLAYLIST = 3;
    public static final int TYPE_ARTIST_SONGS = 0;
    public static final int TYPE_ALBUM_SONGS = 1;

    MediaSession mSession;
    public static WearBrowserService sInstance;
    private List<MediaBrowser.MediaItem> mQueryResult = new ArrayList<MediaBrowser.MediaItem>();

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
            setSessionActive();
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
                                "com.naman14.timber/drawable/ic_empty_music2"))
                        .setSubtitle(getString(R.string.artists))
                        .build(), MediaBrowser.MediaItem.FLAG_BROWSABLE
        ));

        mMediaRoot.add(new MediaBrowser.MediaItem(
                new MediaDescription.Builder()
                        .setMediaId(Integer.toString(TYPE_ALBUM))
                        .setTitle(getString(R.string.albums))
                        .setIconUri(Uri.parse("android.resource://" +
                                "com.naman14.timber/drawable/ic_empty_music2"))
                        .setSubtitle(getString(R.string.albums))
                        .build(), MediaBrowser.MediaItem.FLAG_BROWSABLE
        ));

        mMediaRoot.add(new MediaBrowser.MediaItem(
                new MediaDescription.Builder()
                        .setMediaId(Integer.toString(TYPE_SONG))
                        .setTitle(getString(R.string.songs))
                        .setIconUri(Uri.parse("android.resource://" +
                                "com.naman14.timber/drawable/ic_empty_music2"))
                        .setSubtitle(getString(R.string.songs))
                        .build(), MediaBrowser.MediaItem.FLAG_BROWSABLE
        ));


        mMediaRoot.add(new MediaBrowser.MediaItem(
                new MediaDescription.Builder()
                        .setMediaId(Integer.toString(TYPE_PLAYLIST))
                        .setTitle(getString(R.string.playlists))
                        .setIconUri(Uri.parse("android.resource://" +
                                "com.naman14.timber/drawable/ic_empty_music2"))
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
                    switch (Integer.parseInt(parentId)) {
                        case TYPE_ARTIST:

                            List<Artist> artistList = ArtistLoader.getAllArtists(mContext);
                            for (Artist artist : artistList) {
                                String albumNmber = TimberUtils.makeLabel(mContext, R.plurals.Nalbums, artist.albumCount);
                                String songCount = TimberUtils.makeLabel(mContext, R.plurals.Nsongs, artist.songCount);
                                fillMediaItems(mediaItems, Integer.toString(11), artist.name, Uri.parse("android.resource://" +
                                        "com.naman14.timber/drawable/ic_empty_music2"), TimberUtils.makeCombinedString(mContext, albumNmber, songCount));
                            }
                            break;
                        case TYPE_ALBUM:
                            List<Album> albumList = AlbumLoader.getAllAlbums(mContext);
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

    private void fillMediaItems(List<MediaBrowser.MediaItem> mediaItems, String mediaId, String title, Uri icon, String subTitle) {
        mediaItems.add(new MediaBrowser.MediaItem(
                new MediaDescription.Builder()
                        .setMediaId(mediaId)
                        .setTitle(title)
                        .setIconUri(icon)
                        .setSubtitle(subTitle)
                        .build(), MediaBrowser.MediaItem.FLAG_BROWSABLE
        ));
    }

}
