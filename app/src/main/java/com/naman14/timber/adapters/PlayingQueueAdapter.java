package com.naman14.timber.adapters;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.models.Song;
import com.naman14.timber.utils.TimberUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.List;

/**
 * Created by naman on 26/08/15.
 */
public class PlayingQueueAdapter extends RecyclerView.Adapter<PlayingQueueAdapter.ItemHolder> {

    private List<Song> arraylist;
    private Activity mContext;
    public int currentlyPlayingPosition;

    public PlayingQueueAdapter(Activity context, List<Song> arraylist) {
        this.arraylist = arraylist;
        this.mContext = context;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song, null);
        ItemHolder ml = new ItemHolder(v);
        return ml;
    }

    @Override
    public void onBindViewHolder(ItemHolder itemHolder, int i) {
        Song localItem = arraylist.get(i);

        itemHolder.title.setText(localItem.title);
        itemHolder.artist.setText(localItem.artistName);

        if (MusicPlayer.getCurrentAudioId()==localItem.id){
            currentlyPlayingPosition=i;
            if (MusicPlayer.isPlaying()){
                itemHolder.playingIndicator.setVisibility(View.VISIBLE);
                itemHolder.playingIndicator.setIcon(MaterialDrawableBuilder.IconValue.MUSIC_NOTE);
            } else {
                itemHolder.playingIndicator.setVisibility(View.VISIBLE);
                itemHolder.playingIndicator.setIcon(MaterialDrawableBuilder.IconValue.PLAY);
            }
        } else itemHolder.playingIndicator.setVisibility(View.INVISIBLE);
        ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(localItem.albumId).toString(), itemHolder.albumArt, new DisplayImageOptions.Builder().cacheInMemory(true).showImageOnFail(R.drawable.ic_empty_music2).resetViewBeforeLoading(true).build());

    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }


    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView title,artist;
        protected ImageView albumArt;
        protected MaterialIconView playingIndicator;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.song_title);
            this.artist = (TextView) view.findViewById(R.id.song_artist);
            this.albumArt=(ImageView) view.findViewById(R.id.albumArt);
            this.playingIndicator=(MaterialIconView) view.findViewById(R.id.currentlyPlayingIndicator);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.setQueuePosition(getAdapterPosition());
                    Handler handler1=new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notifyItemChanged(currentlyPlayingPosition);
                            notifyItemChanged(getAdapterPosition());
                            Handler handler2=new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {
//                                    NavigationUtils.navigateToNowplaying(mContext, true);
                                }
                            },50);
                        }
                    },50);
                }
            },100);

        }

    }

    public long[] getSongIds() {
        long[] ret = new long[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            ret[i] = arraylist.get(i).id;
        }

        return ret;
    }

    public Song getSongAt(int i){
        return arraylist.get(i);
    }

    public void addSongTo(int i,Song song){
        arraylist.add(i,song);
    }

    public void removeSongAt(int i){
        arraylist.remove(i);
    }

}




