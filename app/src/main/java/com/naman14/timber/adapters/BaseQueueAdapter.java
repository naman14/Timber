package com.naman14.timber.adapters;

import android.app.Activity;
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
 * Created by naman on 27/07/15.
 */
public class BaseQueueAdapter extends RecyclerView.Adapter<BaseQueueAdapter.ItemHolder> {

    private List<Song> arraylist;
    private Activity mContext;
    public static int currentlyPlayingPosition;

    public BaseQueueAdapter(Activity context, List<Song> arraylist) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.currentlyPlayingPosition=MusicPlayer.getQueuePosition();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song_timber1, null);
        ItemHolder ml = new ItemHolder(v);
        return ml;
    }

    @Override
    public void onBindViewHolder(ItemHolder itemHolder, int i) {
        Song localItem = arraylist.get(i);

        itemHolder.title.setText(localItem.title);
        itemHolder.artist.setText(localItem.artistName);

        if (i==currentlyPlayingPosition){
            if (MusicPlayer.isPlaying()){
                itemHolder.playSong.setVisibility(View.VISIBLE);
                itemHolder.playSong.setIcon(MaterialDrawableBuilder.IconValue.MUSIC_NOTE);
            } else {
                itemHolder.playSong.setVisibility(View.VISIBLE);
                itemHolder.playSong.setIcon(MaterialDrawableBuilder.IconValue.PLAY);
            }
        }else {
            itemHolder.playSong.setVisibility(View.INVISIBLE);
        }
        ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(localItem.albumId).toString(), itemHolder.albumArt, new DisplayImageOptions.Builder().cacheInMemory(true).showImageOnFail(R.drawable.ic_empty_music2).resetViewBeforeLoading(true).build());

    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }


    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView title,artist;
        protected ImageView albumArt;
        protected MaterialIconView playSong;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.song_title);
            this.artist = (TextView) view.findViewById(R.id.song_artist);
            this.albumArt=(ImageView) view.findViewById(R.id.albumArt);
            this.playSong=(MaterialIconView) view.findViewById(R.id.playSong);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            notifyItemChanged(currentlyPlayingPosition);
            currentlyPlayingPosition=getAdapterPosition();
            playSong.setVisibility(View.VISIBLE);
            playSong.setIcon(MaterialDrawableBuilder.IconValue.MUSIC_NOTE);
            playSong.setColorResource(R.color.colorAccent);
            try {
                MusicPlayer.setQueuePosition(getAdapterPosition());
            } catch (IllegalStateException e){
                e.printStackTrace();
            }

//            BaseNowplayingFragment.updateSongDetails();

        }

    }

    public long[] getSongIds() {
        long[] ret = new long[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            ret[i] = arraylist.get(i).id;
        }

        return ret;
    }

}



