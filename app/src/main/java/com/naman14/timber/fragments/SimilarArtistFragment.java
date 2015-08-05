package com.naman14.timber.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naman14.timber.R;
import com.naman14.timber.dataloaders.ArtistLoader;
import com.naman14.timber.lastfmapi.LastFmClient;
import com.naman14.timber.lastfmapi.callbacks.ArtistInfoListener;
import com.naman14.timber.lastfmapi.models.ArtistQuery;
import com.naman14.timber.lastfmapi.models.LastfmArtist;
import com.naman14.timber.models.Artist;
import com.naman14.timber.utils.Constants;

/**
 * Created by naman on 05/08/15.
 */
public class SimilarArtistFragment extends Fragment {

    long artistID=-1;

    public static SimilarArtistFragment newInstance(long id) {
        SimilarArtistFragment fragment = new SimilarArtistFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.ARTIST_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artistID = getArguments().getLong(Constants.ARTIST_ID);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_similar_artists, container, false);

        Artist artist= ArtistLoader.getArtist(getActivity(), artistID);

        LastFmClient.getInstance(getActivity()).getArtistInfo(new ArtistQuery(artist.name),new ArtistInfoListener() {
            @Override
            public void artistInfoSucess(LastfmArtist artist) {
                Log.d("lol10",artist.mSimilarArtist.size()+"   "+artist.mSimilarArtist.get(0).mName);
            }

            @Override
            public void artistInfoFailed() {
                Log.d("lol10","failed");
            }
        });

        return rootView;

    }

}
