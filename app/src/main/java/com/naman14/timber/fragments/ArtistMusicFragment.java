package com.naman14.timber.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.naman14.timber.R;
import com.naman14.timber.adapters.ArtistSongAdapter;
import com.naman14.timber.dataloaders.ArtistSongLoader;
import com.naman14.timber.models.Song;
import com.naman14.timber.utils.Constants;
import com.naman14.timber.widgets.DividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by naman on 23/07/15.
 */
public class ArtistMusicFragment extends Fragment implements ObservableScrollViewCallbacks {

    long artistID = -1;

    public static ObservableRecyclerView songsRecyclerview;

    ArtistSongAdapter mSongAdapter;
    private static int mHeaderheight;

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

        songsRecyclerview=(ObservableRecyclerView) rootView.findViewById(R.id.recycler_view_songs);
        mHeaderheight=getResources().getDimensionPixelSize(R.dimen.header_height);

        setUpSongs();
        songsRecyclerview.setScrollViewCallbacks(this);


//        getActivity().setExitSharedElementCallback(new SharedElementCallback() {
//            @Override
//            public Parcelable onCaptureSharedElementSnapshot(View sharedElement, Matrix viewToGlobalMatrix, RectF screenBounds) {
//                int bitmapWidth = Math.round(screenBounds.width());
//                int bitmapHeight = Math.round(screenBounds.height());
//                Bitmap bitmap = null;
//                if (bitmapWidth > 0 && bitmapHeight > 0) {
//                    Matrix matrix = new Matrix();
//                    matrix.set(viewToGlobalMatrix);
//                    matrix.postTranslate(-screenBounds.left, -screenBounds.top);
//                    bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
//                    Canvas canvas = new Canvas(bitmap);
//                    canvas.concat(matrix);
//                    sharedElement.draw(canvas);
//                }
//                return bitmap;
//            }
//        });

        return rootView;
    }



    private void setUpSongs() {
        songsRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        ArrayList<Song> songList;
        songList=ArtistSongLoader.getSongsForArtist(getActivity(), artistID);

       // adding two dummy songs to top of arraylist
        //there will be  dummy header and albums header respectively in theses two positions in recyclerview
        songList.add(0,new Song(-1,-1,-1,"dummy","dummy","dummy",-1,-1));
        songList.add(0,new Song(-1,-1,-1,"dummy","dummy","dummy",-1,-1));

        mSongAdapter = new ArtistSongAdapter(getActivity(), songList, artistID);
        songsRecyclerview.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST,R.drawable.item_divider_black));
        songsRecyclerview.setAdapter(mSongAdapter);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll,
                                boolean dragging) {
        ArtistDetailFragment.adjustHeader(scrollY);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

}
