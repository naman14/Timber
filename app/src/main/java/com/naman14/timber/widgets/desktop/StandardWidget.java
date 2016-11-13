package com.naman14.timber.widgets.desktop;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.naman14.timber.MusicPlayer;
import com.naman14.timber.MusicService;
import com.naman14.timber.R;
import com.naman14.timber.utils.NavigationUtils;
import com.naman14.timber.utils.TimberUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by nv95 on 08.07.16.
 */

public class StandardWidget extends BaseWidget {

    @Override
    int getLayoutRes() {
        return R.layout.widget_standard;
    }

    @Override
    void onViewsUpdate(Context context, RemoteViews remoteViews, ComponentName serviceName) {
        remoteViews.setOnClickPendingIntent(R.id.image_next, PendingIntent.getService(
                context,
                REQUEST_NEXT,
                new Intent(context, MusicService.class)
                        .setAction(MusicService.NEXT_ACTION)
                        .setComponent(serviceName),
                0
        ));
        remoteViews.setOnClickPendingIntent(R.id.image_prev, PendingIntent.getService(
                context,
                REQUEST_PREV,
                new Intent(context, MusicService.class)
                        .setAction(MusicService.PREVIOUS_ACTION)
                        .setComponent(serviceName),
                0
        ));
        remoteViews.setOnClickPendingIntent(R.id.image_playpause, PendingIntent.getService(
                context,
                REQUEST_PLAYPAUSE,
                new Intent(context, MusicService.class)
                        .setAction(MusicService.TOGGLEPAUSE_ACTION)
                        .setComponent(serviceName),
                0
        ));
        String t = MusicPlayer.getTrackName();
        if (t != null) {
            remoteViews.setTextViewText(R.id.textView_title, t);
        }
        t = MusicPlayer.getArtistName();
        if (t != null) {
            String album = MusicPlayer.getAlbumName();
            if (!TextUtils.isEmpty(album)) {
                t += " - " + album;
            }
            remoteViews.setTextViewText(R.id.textView_subtitle, t);
        }
        remoteViews.setImageViewResource(R.id.image_playpause,
                MusicPlayer.isPlaying() ? R.drawable.ic_pause_white_36dp : R.drawable.ic_play_white_36dp);
        long albumId = MusicPlayer.getCurrentAlbumId();
        if (albumId != -1) {
            Bitmap artwork;
            artwork = ImageLoader.getInstance().loadImageSync(TimberUtils.getAlbumArtUri(albumId).toString());
            if (artwork == null) {
                artwork = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.ic_empty_music2);
            }
            remoteViews.setImageViewBitmap(R.id.imageView_cover, artwork);
        }
        remoteViews.setOnClickPendingIntent(R.id.imageView_cover, PendingIntent.getActivity(
                context,
                0,
                NavigationUtils.getNowPlayingIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT
        ));
    }
}
