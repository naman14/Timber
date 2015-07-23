package com.naman14.timber.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.naman14.timber.R;
import com.naman14.timber.dataloaders.AlbumLoader;
import com.naman14.timber.lastfmapi.LastFmClient;
import com.naman14.timber.lastfmapi.callbacks.ArtistInfoListener;
import com.naman14.timber.lastfmapi.models.ArtistQuery;
import com.naman14.timber.lastfmapi.models.LastfmArtist;
import com.naman14.timber.models.Album;
import com.naman14.timber.utils.Constants;
import com.naman14.timber.utils.TimberUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by naman on 22/07/15.
 */
public class AlbumDetailFragment extends Fragment {

    long albumID=-1;

    ImageView albumArt,artistArt;
    TextView albumTitle,albumDetails;

    Toolbar toolbar;

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
        View rootView = inflater.inflate(
                R.layout.fragment_album_detail, container, false);

        albumArt=(ImageView) rootView.findViewById(R.id.album_art);
        artistArt=(ImageView) rootView.findViewById(R.id.artist_art);
        albumTitle=(TextView) rootView.findViewById(R.id.album_title);
        albumDetails=(TextView) rootView.findViewById(R.id.album_details);

        toolbar=(Toolbar) rootView.findViewById(R.id.toolbar);

        setupToolbar();
        setAlbumDetails();

        return rootView;
    }

    private void setupToolbar(){

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setAlbumDetails(){

        Album album=AlbumLoader.getAlbum(getActivity(),albumID);

        ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(albumID).toString(), albumArt,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnFail(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true)
                        .displayer(new FadeInBitmapDisplayer(100))
                        .build());
        LastFmClient.getInstance(getActivity()).getArtistInfo(new ArtistQuery(album.artistName),new ArtistInfoListener() {
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

    }

}
