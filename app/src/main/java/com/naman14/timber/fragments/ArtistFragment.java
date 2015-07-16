package com.naman14.timber.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naman14.timber.R;
import com.naman14.timber.adapters.ArtistAdapter;
import com.naman14.timber.dataloaders.ArtistLoader;

/**
 * Created by naman on 08/07/15.
 */
public class ArtistFragment extends Fragment {

    ArtistAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.fragment_recyclerview, container, false);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        mAdapter = new ArtistAdapter(getActivity(), ArtistLoader.getAllArtists(getActivity()));
        recyclerView.setAdapter(mAdapter);

        return recyclerView;
    }
}
