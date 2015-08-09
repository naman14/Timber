package com.naman14.timber.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
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
public class AlbumDetailFragment extends Fragment implements ObservableScrollViewCallbacks {

    long albumID=-1;

    ImageView albumArt,artistArt;
    TextView albumTitle,albumDetails;
    FrameLayout header;
    View paletteView;

    ObservableRecyclerView recyclerView;
    AlbumSongsAdapter mAdapter;

    Toolbar toolbar;

    public int mActionBarSize;
    public int mMinHeaderHeight;
    public int mHeaderHeight;
    public int mMinHeaderTranslation;
    float maxTitleTranslationX;

    private int paletteColor;

    private static final float MAX_TEXT_SCALE_DELTA = 255f;
    private static final float MAX_TEXT_ALPHA = 255f;

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

        paletteView=rootView.findViewById(R.id.paletteView);

        toolbar=(Toolbar) rootView.findViewById(R.id.toolbar);

         maxTitleTranslationX = getResources().getDimensionPixelSize(R.dimen.title_translation_x);
        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_height_album);
        mMinHeaderHeight = mHeaderHeight-(TimberUtils.getActionBarHeight(getActivity())-getResources().getDimensionPixelSize(R.dimen.statusBarHeight));
//        mMinHeaderHeight = mHeaderHeight-(TimberUtils.getActionBarHeight(getActivity()));
        mMinHeaderTranslation = -mMinHeaderHeight + TimberUtils.getActionBarHeight(getActivity());

        recyclerView=(ObservableRecyclerView) rootView.findViewById(R.id.recyclerview);


        setupToolbar();
        setAlbumDetails();
        setUpAlbumSongs();

//        getActivity().setEnterSharedElementCallback(new SharedElementCallback() {
//            @Override
//            public View onCreateSnapshotView(Context context, Parcelable snapshot) {
//                View view = new View(context);
//                view.setBackground(new BitmapDrawable((Bitmap) snapshot));
//                return view;
//            }
//
//            @Override
//            public void onSharedElementStart(List<String> sharedElementNames,
//                                             List<View> sharedElements,
//                                             List<View> sharedElementSnapshots) {
//                ImageView sharedElement = (ImageView) rootView.findViewById(R.id.album_art);
//                for (int i = 0; i < sharedElements.size(); i++) {
//                    if (sharedElements.get(i) == sharedElement) {
//                        View snapshot = sharedElementSnapshots.get(i);
//                        Drawable snapshotDrawable = snapshot.getBackground();
//                        sharedElement.setBackground(snapshotDrawable);
//                        sharedElement.setImageAlpha(0);
//                        forceSharedElementLayout();
//                        break;
//                    }
//                }
//            }
//
//            private void forceSharedElementLayout() {
//                ImageView sharedElement = (ImageView) rootView.findViewById(R.id.album_art);
//                int widthSpec = View.MeasureSpec.makeMeasureSpec(sharedElement.getWidth(),
//                        View.MeasureSpec.EXACTLY);
//                int heightSpec = View.MeasureSpec.makeMeasureSpec(sharedElement.getHeight(),
//                        View.MeasureSpec.EXACTLY);
//                int left = sharedElement.getLeft();
//                int top = sharedElement.getTop();
//                int right = sharedElement.getRight();
//                int bottom = sharedElement.getBottom();
//                sharedElement.measure(widthSpec, heightSpec);
//                sharedElement.layout(left, top, right, bottom);
//            }
//
//            @Override
//            public void onSharedElementEnd(List<String> sharedElementNames,
//                                           List<View> sharedElements,
//                                           List<View> sharedElementSnapshots) {
//                ImageView sharedElement = (ImageView) rootView.findViewById(R.id.album_art);
//                sharedElement.setBackground(null);
//                sharedElement.setImageAlpha(255);
//            }
//        });

        mActionBarSize=TimberUtils.getActionBarHeight(getActivity());
        recyclerView.setScrollViewCallbacks(this);

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
                        .build(), new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Palette palette=Palette.generate(loadedImage);
                paletteColor=palette.getDarkVibrantColor(Color.parseColor("#66000000"));
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


    }

    private void setUpAlbumSongs(){

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Song> songList=AlbumSongLoader.getSongsForAlbum(getActivity(),albumID);
        songList.add(0,new Song(-1,-1,-1,"dummy","dummy","dummy",-1,-1));
        mAdapter = new AlbumSongsAdapter(getActivity(), songList,albumID);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST,R.drawable.item_divider_black));
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll,
                                boolean dragging) {
        adjustHeader(scrollY,dragging);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    private void adjustHeader(int scrollY,boolean dragging) {

        Log.d("lol", "here21");
        Log.d("lol","scrolly"+ String.valueOf(scrollY));
        float flexibleRange = mMinHeaderHeight;
        header.setTranslationY(Math.max(-scrollY, mMinHeaderTranslation));
        if (scrollY>300) {
            float scale = ScrollUtils.getFloat((flexibleRange - (scrollY-300)) / flexibleRange, 0.7f, MAX_TEXT_SCALE_DELTA);
            float alpha = ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0.0f, MAX_TEXT_ALPHA);
            float translation = ScrollUtils.getFloat(((scrollY-300))/4, 0.0f, maxTitleTranslationX);
            setPivotXToTitle();
            Log.d("lol", "flexiblerange" + flexibleRange);
            Log.d("lol", "divided" + ((flexibleRange - scrollY) / flexibleRange));
            Log.d("lol", "scale" + String.valueOf(scale));
            Log.d("lol", "alpha" + alpha);
            Log.d("lol", "maxtranslation" + String.valueOf(maxTitleTranslationX));
            Log.d("lol", "translation" + String.valueOf(translation));
            albumTitle.setPivotY(0);
            albumTitle.setScaleX(scale);
            albumTitle.setScaleY(scale);
            albumDetails.setPivotY(0);
            albumDetails.setScaleX(scale);
            albumDetails.setScaleY(scale);

            albumTitle.setTranslationX(translation);
            albumDetails.setTranslationX(translation);
            paletteView.setBackgroundColor(ColorUtils.setAlphaComponent(paletteColor,(int)alpha*100));
        }

    }
    private void setPivotXToTitle() {
//
            albumTitle.setPivotX(0);
        albumDetails.setPivotX(0);

    }

}
