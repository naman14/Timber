package com.naman14.timber.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.naman14.timber.MusicPlayer;
import com.naman14.timber.activities.MainActivity;
import com.naman14.timber.models.Song;
import com.naman14.timber.utils.TimberUtils;

/**
 * Created by naman on 7/12/17.
 */

public class BaseSongAdapter<V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {

    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(V holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        public ItemHolder(View view) {
            super(view);
        }

    }

    public void playAll(final Activity context, final long[] list, int position,
                        final long sourceId, final TimberUtils.IdType sourceType,
                        final boolean forceShuffle, final Song currentSong) {

        if (context instanceof MainActivity) {
            CastSession castSession = ((MainActivity) context).getCastSession();
            if (castSession != null) {

                MediaMetadata musicMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MUSIC_TRACK);

                musicMetadata.putString(MediaMetadata.KEY_TITLE, currentSong.title);
                musicMetadata.putString(MediaMetadata.KEY_SUBTITLE, currentSong.artistName);
                musicMetadata.addImage(new WebImage(TimberUtils.getAlbumArtUri(currentSong.albumId)));

                MediaInfo mediaInfo = new MediaInfo.Builder("url")
                        .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                        .setContentType("audio/mpeg")
                        .setMetadata(musicMetadata)
                        .setStreamDuration(currentSong.duration * 1000)
                        .build();
                RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
                remoteMediaClient.load(mediaInfo, true, position);

            } else {
                MusicPlayer.playAll(context, list, position, -1, TimberUtils.IdType.NA, false);
            }
        } else {
            MusicPlayer.playAll(context, list, position, -1, TimberUtils.IdType.NA, false);
        }


    }


}
