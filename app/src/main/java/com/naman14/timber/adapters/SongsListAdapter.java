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

package com.naman14.timber.adapters;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.afollestad.appthemeengine.Config;
import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.dialogs.AddPlaylistDialog;
import com.naman14.timber.models.Song;
import com.naman14.timber.utils.Helpers;
import com.naman14.timber.utils.NavigationUtils;
import com.naman14.timber.utils.PreferencesUtility;
import com.naman14.timber.utils.TimberUtils;
import com.naman14.timber.widgets.BubbleTextGetter;
import com.naman14.timber.widgets.MusicVisualizer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SongsListAdapter extends RecyclerView.Adapter<SongsListAdapter.ItemHolder> implements BubbleTextGetter {

    public int currentlyPlayingPosition;
    private List<Song> arrayList;
    private AppCompatActivity mContext;
    private long[] songIDs;
    private boolean isPlaylist;
    private boolean animate;
    private int lastPosition = -1;
    private String ateKey;
    private long playlistId;

    public SongsListAdapter(AppCompatActivity context, List<Song> arrayList, boolean isPlaylistSong, boolean animate) {
        this.arrayList = arrayList;
        this.mContext = context;
        this.isPlaylist = isPlaylistSong;
        this.songIDs = getSongIds();
        this.ateKey = Helpers.getATEKey(context);
        this.animate = animate;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (isPlaylist) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song_playlist, null);
            return new ItemHolder(v);
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song, null);
            return new ItemHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ItemHolder itemHolder, int i) {
        Song localItem = arrayList.get(i);

        itemHolder.title.setText(localItem.title);
        itemHolder.artist.setText(localItem.artistName);

        ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(localItem.albumId).toString(), itemHolder.albumArt, new DisplayImageOptions.Builder().cacheInMemory(true).showImageOnFail(R.drawable.ic_empty_music2).resetViewBeforeLoading(true).build());
        if (MusicPlayer.getCurrentAudioId() == localItem.id) {
            itemHolder.title.setTextColor(Config.accentColor(mContext, ateKey));
            if (MusicPlayer.isPlaying()) {
                itemHolder.visualizer.setColor(Config.accentColor(mContext, ateKey));
                itemHolder.visualizer.setVisibility(View.VISIBLE);
            }
        } else {
            if (isPlaylist)
                itemHolder.title.setTextColor(Color.WHITE);
            else
                itemHolder.title.setTextColor(Config.textColorPrimary(mContext, ateKey));
            itemHolder.visualizer.setVisibility(View.GONE);
        }


        if (isPlaylist) {
            if (MusicPlayer.isPlaying()) {
                if (animate && PreferencesUtility.getInstance(mContext).getAnimations()) {
                    if (TimberUtils.isLollipop())
                        setAnimation(itemHolder.itemView, i);
                    else {
                        if (i > 10)
                            setAnimation(itemHolder.itemView, i);
                    }
                }
            } else {
                stopAnimation(itemHolder.itemView, i);
            }
        }


        setOnPopupMenuListener(itemHolder, i);

    }

    private void stopAnimation(View itemView, int position) {
        if (position > lastPosition) {
            itemView.clearAnimation();
        }
    }

    public void setPlaylistId(long playlistId) {
        this.playlistId = playlistId;
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    private void setOnPopupMenuListener(ItemHolder itemHolder, final int position) {

        itemHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PopupMenu menu = new PopupMenu(mContext, v);

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_song_remove_playlist:
                                TimberUtils.removeFromPlaylist(mContext, arrayList.get(position).id, playlistId);
                                removeSongAt(position);
                                notifyItemRemoved(position);
                                break;
                            case R.id.popup_song_play:
                                MusicPlayer.playAll(mContext, songIDs, position, -1, TimberUtils.IdType.NA, false);
                                break;
                            case R.id.popup_song_play_next:
                                long[] ids = new long[1];
                                ids[0] = arrayList.get(position).id;
                                MusicPlayer.playNext(mContext, ids, -1, TimberUtils.IdType.NA);
                                break;
                            case R.id.popup_song_goto_album:
                                NavigationUtils.goToAlbum(mContext, arrayList.get(position).albumId);
                                break;
                            case R.id.popup_song_goto_artist:
                                NavigationUtils.goToArtist(mContext, arrayList.get(position).artistId);
                                break;
                            case R.id.popup_song_addto_queue:
                                long[] id = new long[1];
                                id[0] = arrayList.get(position).id;
                                MusicPlayer.addToQueue(mContext, id, -1, TimberUtils.IdType.NA);
                                break;
                            case R.id.popup_song_addto_playlist:
                                AddPlaylistDialog.newInstance(arrayList.get(position)).show(mContext.getSupportFragmentManager(), "ADD_PLAYLIST");
                                break;
                            case R.id.popup_song_share:
                                TimberUtils.shareTrack(mContext, arrayList.get(position).id);
                                break;
                            case R.id.popup_song_delete:
                                long[] deleteIds = {arrayList.get(position).id};
                                TimberUtils.showDeleteDialog(mContext, arrayList.get(position).title, deleteIds, SongsListAdapter.this, position);
                                break;
                        }
                        return false;
                    }
                });
                menu.inflate(R.menu.popup_song);
                menu.show();
                if (isPlaylist)
                    menu.getMenu().findItem(R.id.popup_song_remove_playlist).setVisible(true);
            }
        });
    }

    public long[] getSongIds() {
        long[] ret = new long[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            ret[i] = arrayList.get(i).id;
        }

        return ret;
    }

    @Override
    public String getTextToShowInBubble(final int pos) {
        if (arrayList == null || arrayList.size() == 0)
            return "";
        Character ch = arrayList.get(pos).title.charAt(0);
        if (Character.isDigit(ch)) {
            return "#";
        } else
            return Character.toString(ch);
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public void updateDataSet(List<Song> arraylist) {
        this.arrayList = arraylist;
        this.songIDs = getSongIds();
    }

    public void updateDataSet() {
        updateDataSet(this.arrayList);
    }

    public Song getSongAt(int i) {
        return arrayList.get(i);
    }

    public void addSongTo(int i, Song song) {
        arrayList.add(i, song);
    }

    public void removeSongAt(int i) {
        arrayList.remove(i);
    }

    private int getpositionById(Long id) {
        if (id == null) {
            return -1;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            if (id.equals(arrayList.get(i).id)) {
                return i;
            }
        }
        return -1;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.song_title)
        TextView title;
        @BindView(R.id.song_artist)
        TextView artist;
        @BindView(R.id.albumArt)
        ImageView albumArt;
        @BindView(R.id.popup_menu)
        ImageView popupMenu;
        private MusicVisualizer visualizer;

        public ItemHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            visualizer = (MusicVisualizer) view.findViewById(R.id.visualizer);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Long id = MusicPlayer.getCurrentAudioId();
                    final int position = getpositionById(id);
                    if (position == getAdapterPosition()) {
                        MusicPlayer.playOrPause();
                        return;
                    } else {
                        MusicPlayer.playAll(mContext, songIDs, getAdapterPosition(), -1, TimberUtils.IdType.NA, false);
                    }
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!id.equals(-1L)) {
                                notifyItemChanged(position);
                            }
                            notifyItemChanged(getAdapterPosition());
                            currentlyPlayingPosition = getAdapterPosition();
                        }
                    }, 50);
                }
            }, 100);


        }

    }
}


