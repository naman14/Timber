package com.naman14.timber.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naman14.timber.R;
import com.naman14.timber.adapters.SongsListAdapter;
import com.naman14.timber.dataloaders.SongLoader;
import com.naman14.timber.widgets.DividerItemDecoration;

/**
 * Created by naman on 12/06/15.
 */
public class SongsFragment extends Fragment {

    private SongsListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.fragment_recyclerview, container, false);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        mAdapter = new SongsListAdapter(getActivity(), SongLoader.getAllSongs(getActivity()));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST));

        return recyclerView;
    }

}
