package com.naman14.timber.cast;

import android.net.Uri;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.naman14.timber.models.Song;

/**
 * Created by naman on 2/12/17.
 */

public class TimberCastHelper  {

    public static void startCasting(CastSession castSession, Song song) {
        MediaMetadata musicMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MUSIC_TRACK);

        musicMetadata.putString(MediaMetadata.KEY_TITLE, song.title);
        musicMetadata.putString(MediaMetadata.KEY_SUBTITLE, song.artistName);
        musicMetadata.addImage(new WebImage(Uri.parse("http://192.168.1.100:8080/albumart?id="+ song.albumId)));

        try {
            MediaInfo mediaInfo = new MediaInfo.Builder("http://192.168.1.100:8080/song?id=" + song.id)
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setContentType("audio/mpeg")
                    .setMetadata(musicMetadata)
                    .setStreamDuration(song.duration)
                    .build();
            RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
            remoteMediaClient.load(mediaInfo, true, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
