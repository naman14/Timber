package com.naman14.timber.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.appthemeengine.Config;
import com.naman14.timber.R;
import com.naman14.timber.dataloaders.LastAddedLoader;
import com.naman14.timber.dataloaders.PlaylistSongLoader;
import com.naman14.timber.dataloaders.SongLoader;
import com.naman14.timber.dataloaders.TopTracksLoader;
import com.naman14.timber.models.Playlist;
import com.naman14.timber.models.Song;
import com.naman14.timber.utils.Constants;
import com.naman14.timber.utils.Helpers;
import com.naman14.timber.utils.NavigationUtils;
import com.naman14.timber.utils.PreferencesUtility;
import com.naman14.timber.utils.TimberUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;
import java.util.Random;

/**
 * Created by naman on 31/10/16.
 */
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ItemHolder> {

    private List<Playlist> arraylist;
    private Activity mContext;
    private boolean isGrid;
    private boolean showAuto;
    private int songCountInt;
    private long totalRuntime;
    private long firstAlbumID = -1;
    private int foregroundColor;
    int[] foregroundColors = {R.color.pink_transparent, R.color.green_transparent, R.color.blue_transparent, R.color.red_transparent, R.color.purple_transparent};

    public PlaylistAdapter(Activity context, List<Playlist> arraylist) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.isGrid = PreferencesUtility.getInstance(mContext).getPlaylistView() == Constants.PLAYLIST_VIEW_GRID;
        this.showAuto = PreferencesUtility.getInstance(mContext).showAutoPlaylist();
        Random random = new Random();
        int rndInt = random.nextInt(foregroundColors.length);
        foregroundColor = foregroundColors[rndInt];

    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (isGrid) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album_grid, null);
            ItemHolder ml = new ItemHolder(v);
            return ml;
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album_list, null);
            ItemHolder ml = new ItemHolder(v);
            return ml;
        }
    }

    @Override
    public void onBindViewHolder(final ItemHolder itemHolder, int i) {
        final Playlist localItem = arraylist.get(i);

        itemHolder.title.setText(localItem.name);

        String s = getAlbumArtUri(i, localItem.id);
        itemHolder.albumArt.setTag(firstAlbumID);
        ImageLoader.getInstance().displayImage(s, itemHolder.albumArt,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnFail(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true)
                        .build(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (isGrid) {
                            new Palette.Builder(loadedImage).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    Palette.Swatch swatch = palette.getVibrantSwatch();
                                    if (swatch != null) {
                                        int color = swatch.getRgb();
                                        itemHolder.footer.setBackgroundColor(color);
                                        int textColor = TimberUtils.getBlackWhiteColor(swatch.getTitleTextColor());
                                        itemHolder.title.setTextColor(textColor);
                                        itemHolder.artist.setTextColor(textColor);
                                    } else {
                                        Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                                        if (mutedSwatch != null) {
                                            int color = mutedSwatch.getRgb();
                                            itemHolder.footer.setBackgroundColor(color);
                                            int textColor = TimberUtils.getBlackWhiteColor(mutedSwatch.getTitleTextColor());
                                            itemHolder.title.setTextColor(textColor);
                                            itemHolder.artist.setTextColor(textColor);
                                        }
                                    }


                                }
                            });
                        }

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        if (isGrid) {
                            itemHolder.footer.setBackgroundColor(0);
                            if (mContext != null) {
                                int textColorPrimary = Config.textColorPrimary(mContext, Helpers.getATEKey(mContext));
                                itemHolder.title.setTextColor(textColorPrimary);
                                itemHolder.artist.setTextColor(textColorPrimary);
                            }
                        }
                    }
                });
        itemHolder.artist.setText(" " + String.valueOf(songCountInt) + " " + mContext.getString(R.string.songs) + " - " + TimberUtils.makeShortTimeString(mContext,totalRuntime));

        if (TimberUtils.isLollipop())
            itemHolder.albumArt.setTransitionName("transition_album_art" + i);

    }

    private String getAlbumArtUri(int position, long id) {
        if (mContext != null) {
            firstAlbumID = -1;
            if (showAuto) {
                switch (position) {
                    case 0:
                        List<Song> lastAddedSongs = LastAddedLoader.getLastAddedSongs(mContext);
                        songCountInt = lastAddedSongs.size();
                        totalRuntime = 0;
                        for(Song song : lastAddedSongs){
                                totalRuntime += song.duration / 1000; //for some reason default playlists have songs with durations 1000x larger than they should be
                        }

                        if (songCountInt != 0) {
                            firstAlbumID = lastAddedSongs.get(0).albumId;
                            return TimberUtils.getAlbumArtUri(firstAlbumID).toString();
                        } else return "nosongs";
                    case 1:
                        TopTracksLoader recentloader = new TopTracksLoader(mContext, TopTracksLoader.QueryType.RecentSongs);
                        List<Song> recentsongs = SongLoader.getSongsForCursor(TopTracksLoader.getCursor());
                        songCountInt = recentsongs.size();
                        totalRuntime = 0;
                        for(Song song : recentsongs){
                            totalRuntime += song.duration / 1000; //for some reason default playlists have songs with durations 1000x larger than they should be
                        }

                        if (songCountInt != 0) {
                            firstAlbumID = recentsongs.get(0).albumId;
                            return TimberUtils.getAlbumArtUri(firstAlbumID).toString();
                        } else return "nosongs";
                    case 2:
                        TopTracksLoader topTracksLoader = new TopTracksLoader(mContext, TopTracksLoader.QueryType.TopTracks);
                        List<Song> topsongs = SongLoader.getSongsForCursor(TopTracksLoader.getCursor());
                        songCountInt = topsongs.size();
                        totalRuntime = 0;
                        for(Song song : topsongs){
                            totalRuntime += song.duration / 1000; //for some reason default playlists have songs with durations 1000x larger than they should be
                        }

                        if (songCountInt != 0) {
                            firstAlbumID = topsongs.get(0).albumId;
                            return TimberUtils.getAlbumArtUri(firstAlbumID).toString();
                        } else return "nosongs";
                    default:
                        List<Song> playlistsongs = PlaylistSongLoader.getSongsInPlaylist(mContext, id);
                        songCountInt = playlistsongs.size();
                        totalRuntime = 0;
                        for(Song song : playlistsongs){
                            totalRuntime += song.duration;
                        }

                        if (songCountInt != 0) {
                            firstAlbumID = playlistsongs.get(0).albumId;
                            return TimberUtils.getAlbumArtUri(firstAlbumID).toString();
                        } else return "nosongs";

                }
            } else {
                List<Song> playlistsongs = PlaylistSongLoader.getSongsInPlaylist(mContext, id);
                songCountInt = playlistsongs.size();
                totalRuntime = 0;
                for(Song song : playlistsongs){
                    totalRuntime += song.duration;
                }

                if (songCountInt != 0) {
                    firstAlbumID = playlistsongs.get(0).albumId;
                    return TimberUtils.getAlbumArtUri(firstAlbumID).toString();
                } else return "nosongs";
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }

    public void updateDataSet(List<Playlist> arraylist) {
        this.arraylist.clear();
        this.arraylist.addAll(arraylist);
        notifyDataSetChanged();
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView title, artist;
        protected ImageView albumArt;
        protected View footer;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.album_title);
            this.artist = (TextView) view.findViewById(R.id.album_artist);
            this.albumArt = (ImageView) view.findViewById(R.id.album_art);
            this.footer = view.findViewById(R.id.footer);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            NavigationUtils.navigateToPlaylistDetail(mContext, getPlaylistType(getAdapterPosition()), (long) albumArt.getTag(), String.valueOf(title.getText()), foregroundColor, arraylist.get(getAdapterPosition()).id, null);

        }

    }

    private String getPlaylistType(int position) {
        if (showAuto) {
            switch (position) {
                case 0:
                    return Constants.NAVIGATE_PLAYLIST_LASTADDED;
                case 1:
                    return Constants.NAVIGATE_PLAYLIST_RECENT;
                case 2:
                    return Constants.NAVIGATE_PLAYLIST_TOPTRACKS;
                default:
                    return Constants.NAVIGATE_PLAYLIST_USERCREATED;
            }
        } else return Constants.NAVIGATE_PLAYLIST_USERCREATED;
    }


}




