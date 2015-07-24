package com.naman14.timber.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naman14.timber.R;
import com.naman14.timber.adapters.ArtistAlbumAdapter;
import com.naman14.timber.adapters.ArtistSongAdapter;
import com.naman14.timber.dataloaders.ArtistAlbumLoader;
import com.naman14.timber.utils.Constants;

/**
 * Created by naman on 23/07/15.
 */
public class ArtistMusicFragment extends Fragment {

    long artistID = -1;

    RecyclerView albumsRecyclerview, songsRecyclerview;

    ArtistAlbumAdapter mAlbumAdapter;
    ArtistSongAdapter mSongAdapter;

    public static ArtistMusicFragment newInstance(long id) {
        ArtistMusicFragment fragment = new ArtistMusicFragment();
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
                R.layout.fragment_artist_music, container, false);

        albumsRecyclerview=(RecyclerView) rootView.findViewById(R.id.recycler_view_album);
        songsRecyclerview=(RecyclerView) rootView.findViewById(R.id.recycler_view_songs);

        setUpAlbums();
        setUpSongs();

        return rootView;
    }

    private void setUpAlbums(){
        albumsRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        albumsRecyclerview.setHasFixedSize(true);

        //to add spacing between cards
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing_card);
        albumsRecyclerview.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        
        mAlbumAdapter=new ArtistAlbumAdapter(getActivity(), ArtistAlbumLoader.getAlbumsForArtist(getActivity(),artistID));
        albumsRecyclerview.setAdapter(mAlbumAdapter);
    }

    private void setUpSongs(){
        songsRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
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
}
