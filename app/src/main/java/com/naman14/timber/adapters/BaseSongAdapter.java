package com.naman14.timber.adapters;

import android.app.Activity;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.cast.framework.CastSession;
import com.naman14.timber.MusicPlayer;
import com.naman14.timber.activities.BaseActivity;
import com.naman14.timber.cast.TimberCastHelper;
import com.naman14.timber.models.Song;
import com.naman14.timber.utils.NavigationUtils;
import com.naman14.timber.utils.TimberUtils;

import java.util.List;

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
                        final boolean forceShuffle, final Song currentSong, boolean navigateNowPlaying) {

        if (context instanceof BaseActivity) {
            CastSession castSession = ((BaseActivity) context).getCastSession();
            if (castSession != null) {
                navigateNowPlaying = false;
                TimberCastHelper.startCasting(castSession, currentSong);
            } else {
                MusicPlayer.playAll(context, list, position, -1, TimberUtils.IdType.NA, false);
            }
        } else {
            MusicPlayer.playAll(context, list, position, -1, TimberUtils.IdType.NA, false);
        }

        if (navigateNowPlaying) {
            NavigationUtils.navigateToNowplaying(context, true);
        }


    }
    public void removeSongAt(int i){}
    public void updateDataSet(List<Song> arraylist) {}

}
