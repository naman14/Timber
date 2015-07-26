package com.naman14.timber.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.naman14.timber.R;
import com.naman14.timber.dataloaders.ArtistLoader;
import com.naman14.timber.lastfmapi.LastFmClient;
import com.naman14.timber.lastfmapi.callbacks.ArtistInfoListener;
import com.naman14.timber.lastfmapi.models.ArtistQuery;
import com.naman14.timber.lastfmapi.models.LastfmArtist;
import com.naman14.timber.models.Artist;
import com.naman14.timber.utils.Constants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by naman on 23/07/15.
 */
public class ArtistDetailFragment extends Fragment  {

    long artistID = -1;

    ImageView artistArt;
    TextView artistName;

    public static View mHeader;
    Toolbar toolbar;
    TabLayout tabLayout;

    public static int mActionBarHeight;
    public static int mMinHeaderHeight;
    public static int mHeaderHeight;
    public static int mMinHeaderTranslation;

    private TypedValue mTypedValue = new TypedValue();

    public static ArtistDetailFragment newInstance(long id) {
        ArtistDetailFragment fragment = new ArtistDetailFragment();
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
                R.layout.fragment_artist_detail, container, false);

        mHeader=rootView.findViewById(R.id.header);
        artistArt=(ImageView) rootView.findViewById(R.id.artist_art);
        artistName=(TextView) rootView.findViewById(R.id.artist);

        mMinHeaderHeight = getResources().getDimensionPixelSize(R.dimen.min_header_height);
        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
        mMinHeaderTranslation = -mMinHeaderHeight + getActionBarHeight();



        toolbar=(Toolbar) rootView.findViewById(R.id.toolbar);
        setupToolbar();
        setUpArtistDetails();

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
            viewPager.setOffscreenPageLimit(0);
        }

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabTextColors(Color.parseColor("#ffffff"),getActivity().getResources().getColor(R.color.colorAccent));
        tabLayout.setupWithViewPager(viewPager);


        return rootView;
    }

    private void setupToolbar(){

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setUpArtistDetails(){

        Artist artist= ArtistLoader.getArtist(getActivity(),artistID);

        artistName.setText(artist.name);

        LastFmClient.getInstance(getActivity()).getArtistInfo(new ArtistQuery(artist.name),new ArtistInfoListener() {
            @Override
            public void artistInfoSucess(LastfmArtist artist) {
                ImageLoader.getInstance().displayImage(artist.mArtwork.get(4).mUrl, artistArt,
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

    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new ArtistMusicFragment().newInstance(artistID), "Music");
        adapter.addFragment(new ArtistBioFragment(), "Artist");
        adapter.addFragment(new ArtistBioFragment(), "Related Music");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }


    public static void adjustHeader(int scrollY){
        mHeader.setTranslationY(Math.max(-scrollY, mMinHeaderTranslation));
    }


    public int getActionBarHeight() {
        if (mActionBarHeight != 0) {
            return mActionBarHeight;
        }
            getActivity().getTheme().resolveAttribute(R.attr.actionBarSize, mTypedValue, true);

        mActionBarHeight = TypedValue.complexToDimensionPixelSize(mTypedValue.data, getResources().getDisplayMetrics());

        return mActionBarHeight;
    }

    public static float clamp(float value, float max, float min) {
        return Math.max(Math.min(value, min), max);
    }



}

