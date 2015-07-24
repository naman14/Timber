package com.naman14.timber.adapters;

import android.app.Activity;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.dataloaders.ArtistAlbumLoader;
import com.naman14.timber.models.Song;
import com.naman14.timber.utils.TimberUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by naman on 24/07/15.
 */
public class ArtistSongAdapter extends RecyclerView.Adapter<ArtistSongAdapter.ItemHolder> {

    private List<Song> arraylist;
    private Activity mContext;
    private long artistID;

    public ArtistSongAdapter(Activity context, List<Song> arraylist,long artistID) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.artistID=artistID;

    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType==0) {
            View v1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.artist_detail_albums_header, null);
            ItemHolder ml = new ItemHolder(v1);
            return ml;
        } else {
            View v2 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_artist_song, null);
            ItemHolder ml = new ItemHolder(v2);
            return ml;
        }
    }

    @Override
    public void onBindViewHolder(ItemHolder itemHolder, int i) {

        if (getItemViewType(i)==0){
            setUpAlbums(itemHolder.albumsRecyclerView);

        } else {
            Song localItem = arraylist.get(i);

            itemHolder.title.setText(localItem.title);
            itemHolder.artist.setText(localItem.artistName);

            ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(localItem.albumId).toString(), itemHolder.albumArt, new DisplayImageOptions.Builder().cacheInMemory(true).showImageOnFail(R.drawable.ic_empty_music2).resetViewBeforeLoading(true).build());

        }

    }

    @Override
    public void onViewRecycled(ItemHolder itemHolder) {

        if (itemHolder.getItemViewType()==0)
            clearExtraSpacingBetweenCards(itemHolder.albumsRecyclerView);

    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }


    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView title,artist;
        protected ImageView albumArt;
        protected RecyclerView albumsRecyclerView;

        public ItemHolder(View view) {
            super(view);

                    this.albumsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_album);

                    this.title = (TextView) view.findViewById(R.id.song_title);
                    this.artist = (TextView) view.findViewById(R.id.song_artist);
                    this.albumArt = (ImageView) view.findViewById(R.id.albumArt);


            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            MusicPlayer.playAll(mContext, getSongIds(), getAdapterPosition(), -1, TimberUtils.IdType.NA, false);
        }

    }

    private void setUpAlbums(RecyclerView albumsRecyclerview){

        albumsRecyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        albumsRecyclerview.setHasFixedSize(true);

        //to add spacing between cards
        int spacingInPixels = mContext.getResources().getDimensionPixelSize(R.dimen.spacing_card);
        albumsRecyclerview.addItemDecoration(new SpacesItemDecoration(spacingInPixels));


        ArtistAlbumAdapter mAlbumAdapter=new ArtistAlbumAdapter(mContext, ArtistAlbumLoader.getAlbumsForArtist(mContext, artistID));
        albumsRecyclerview.setAdapter(mAlbumAdapter);
    }

    private void clearExtraSpacingBetweenCards(RecyclerView albumsRecyclerview){
        //to clear any extra spacing between cards
        int spacingInPixelstoClear = -(mContext.getResources().getDimensionPixelSize(R.dimen.spacing_card));
        albumsRecyclerview.addItemDecoration(new SpacesItemDecoration(spacingInPixelstoClear));
    }


    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {

            //the padding from left
            outRect.left = space;



        }
    }

    public long[] getSongIds() {
        long[] ret = new long[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            ret[i] = arraylist.get(i).id;
        }

        return ret;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;
        if (position == 0) {
            viewType = 0;
        } else {
            viewType = 1;
        }
        return viewType;
    }
}




