package com.naman14.timber.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.naman14.timber.R;
import com.naman14.timber.adapters.AlbumSongsAdapter;
import com.naman14.timber.dataloaders.AlbumLoader;
import com.naman14.timber.dataloaders.AlbumSongLoader;
import com.naman14.timber.lastfmapi.LastFmClient;
import com.naman14.timber.lastfmapi.callbacks.ArtistInfoListener;
import com.naman14.timber.lastfmapi.models.ArtistQuery;
import com.naman14.timber.lastfmapi.models.LastfmArtist;
import com.naman14.timber.models.Album;
import com.naman14.timber.models.Song;
import com.naman14.timber.utils.Constants;
import com.naman14.timber.utils.TimberUtils;
import com.naman14.timber.widgets.DividerItemDecoration;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

/**
 * Created by naman on 22/07/15.
 */
public class AlbumDetailFragment extends Fragment  {

    long albumID=-1;

    ImageView albumArt,artistArt;
    TextView albumTitle,albumDetails;
    FrameLayout header;

    RecyclerView recyclerView;
    AlbumSongsAdapter mAdapter;

    Toolbar toolbar;
    CoordinatorLayout coordinatorLayout;


    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;

    public static AlbumDetailFragment newInstance(long id) {
        AlbumDetailFragment fragment = new AlbumDetailFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.ALBUM_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumID = getArguments().getLong(Constants.ALBUM_ID);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(
                R.layout.fragment_album_detail, container, false);

        header=(FrameLayout) rootView.findViewById(R.id.header);
        albumArt=(ImageView) rootView.findViewById(R.id.album_art);
        artistArt=(ImageView) rootView.findViewById(R.id.artist_art);
        albumTitle=(TextView) rootView.findViewById(R.id.album_title);
        albumDetails=(TextView) rootView.findViewById(R.id.album_details);

        toolbar=(Toolbar) rootView.findViewById(R.id.toolbar);


        recyclerView=(RecyclerView) rootView.findViewById(R.id.recyclerview);
        collapsingToolbarLayout=(CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        appBarLayout=(AppBarLayout) rootView.findViewById(R.id.app_bar);

        setupToolbar();
        setAlbumDetails();
        setUpAlbumSongs();

        return rootView;
    }

    private void setupToolbar(){

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

    }

    private void setAlbumDetails(){

        Album album=AlbumLoader.getAlbum(getActivity(),albumID);

        ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(albumID).toString(), albumArt,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnFail(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true)
                        .displayer(new FadeInBitmapDisplayer(100))
                        .build(), new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Palette palette=Palette.generate(loadedImage);
                collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(Color.parseColor("#66000000")));
                collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(Color.parseColor("#66000000")));
            }
        });
        LastFmClient.getInstance(getActivity()).getArtistInfo(new ArtistQuery(album.artistName), new ArtistInfoListener() {
            @Override
            public void artistInfoSucess(LastfmArtist artist) {
                ImageLoader.getInstance().displayImage(artist.mArtwork.get(1).mUrl, artistArt,
                        new DisplayImageOptions.Builder().cacheInMemory(true)
                                .cacheOnDisk(true)
                                .showImageOnFail(R.drawable.ic_empty_music2)
                                .resetViewBeforeLoading(true)
                                .displayer(new FadeInBitmapDisplayer(400))
                                .build());
            }

            @Override
            public void artistInfoFailed() {

            }
        });
        String songCount= TimberUtils.makeLabel(getActivity(),R.plurals.Nsongs,album.songCount);

        String year= (album.year!=0) ? (" - " + String.valueOf(album.year)) : "";

        albumTitle.setText(album.title);
        albumDetails.setText(album.artistName+" - " + songCount + year);
        collapsingToolbarLayout.setTitle(album.title);


    }

    private void setUpAlbumSongs(){

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Song> songList=AlbumSongLoader.getSongsForAlbum(getActivity(),albumID);
        mAdapter = new AlbumSongsAdapter(getActivity(), songList,albumID);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST,R.drawable.item_divider_black));
        recyclerView.setAdapter(mAdapter);

    }



}
