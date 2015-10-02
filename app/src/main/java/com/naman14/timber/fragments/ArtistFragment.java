package com.naman14.timber.fragments;

import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naman14.timber.R;
import com.naman14.timber.adapters.ArtistAdapter;
import com.naman14.timber.dataloaders.ArtistLoader;
import com.naman14.timber.utils.PreferencesUtility;
import com.naman14.timber.widgets.DividerItemDecoration;
import com.naman14.timber.widgets.FastScroller;

/**
 * Created by naman on 08/07/15.
 */
public class ArtistFragment extends Fragment {

    ArtistAdapter mAdapter;
    RecyclerView recyclerView;
    private boolean isGrid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(
                R.layout.fragment_recyclerview, container, false);

        recyclerView=(RecyclerView) rootView.findViewById(R.id.recyclerview);
        FastScroller fastScroller = (FastScroller) rootView.findViewById(R.id.fastscroller);

        isGrid=PreferencesUtility.getInstance(getActivity()).isArtistsInGrid();

        if (isGrid) {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
            fastScroller.setVisibility(View.GONE);
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            fastScroller.setRecyclerView(recyclerView);
        }

        new loadArtists().execute("");
        return rootView;
    }


    private class loadArtists extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            mAdapter = new ArtistAdapter(getActivity(), ArtistLoader.getAllArtists(getActivity()));
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            recyclerView.setAdapter(mAdapter);
            if (getActivity() != null) {
                if (isGrid) {
                    int spacingInPixels = getActivity().getResources().getDimensionPixelSize(R.dimen.spacing_card_album_grid);
                    recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
                } else
                    recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
            }
        }

        @Override
        protected void onPreExecute() {}
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {


            outRect.left = space;
            outRect.top=space;
            outRect.right=space;
            outRect.bottom=space;

        }
    }



}
