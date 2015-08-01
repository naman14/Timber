package com.naman14.timber.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.fragments.AlbumDetailFragment;
import com.naman14.timber.fragments.ArtistDetailFragment;
import com.naman14.timber.fragments.MainFragment;
import com.naman14.timber.fragments.QuickControlsFragment;
import com.naman14.timber.nowplaying.NowPlayingFragment;
import com.naman14.timber.slidinguppanel.SlidingUpPanelLayout;
import com.naman14.timber.utils.Constants;
import com.naman14.timber.utils.NavigationUtils;
import com.naman14.timber.utils.TimberUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MainActivity extends BaseActivity {


    private static MainActivity sMainActivity;

    private DrawerLayout mDrawerLayout;
    QuickControlsFragment mControlsFragment;
    NowPlayingFragment mNowPlayingFragment;
    CardView nowPlayingCard;
    SlidingUpPanelLayout panelLayout;

    TextView songtitle, songartist;
    ImageView albumart;

    String action;

    public static MainActivity getInstance() {
        return sMainActivity;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        sMainActivity=this;
        action = getIntent().getAction();

        if (action.equals(Constants.NAVIGATE_ALBUM) || action.equals(Constants.NAVIGATE_ARTIST) || action.equals(Constants.NAVIGATE_NOWPLAYING)) {
            setTheme(R.style.AppTheme_FullScreen);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (action.equals(Constants.NAVIGATE_ALBUM)) {

            long albumID = getIntent().getExtras().getLong(Constants.ALBUM_ID);
            Fragment fragment = new AlbumDetailFragment().newInstance(albumID);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();

        } else if (action.equals(Constants.NAVIGATE_ARTIST)) {

            long artistID = getIntent().getExtras().getLong(Constants.ARTIST_ID);
            Fragment fragment = new ArtistDetailFragment().newInstance(artistID);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();

        } else if (action.equals(Constants.NAVIGATE_NOWPLAYING)) {

            String fragmentID = getIntent().getExtras().getString(Constants.NOWPLAYING_FRAGMENT_ID);
            boolean withAnimations =getIntent().getExtras().getBoolean(Constants.WITH_ANIMATIONS);

            Fragment fragment = NavigationUtils.getFragmentForNowplayingID(fragmentID);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment).commit();

        } else {

            Fragment fragment = new MainFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();

        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        panelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        nowPlayingCard = (CardView) findViewById(R.id.controls_container);


        setPanelSlideListeners();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        setupDrawerContent(navigationView);
        View header = navigationView.inflateHeaderView(R.layout.nav_header);

        albumart = (ImageView) header.findViewById(R.id.album_art);
        songtitle = (TextView) header.findViewById(R.id.song_title);
        songartist = (TextView) header.findViewById(R.id.song_artist);
        setDetailsToHeader();


        mControlsFragment = (QuickControlsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_playback_controls);
        if (mControlsFragment == null) {
            throw new IllegalStateException("Mising fragment with id 'controls'. Cannot continue.");
        }
        mNowPlayingFragment = (NowPlayingFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_now_playing);
        if (mNowPlayingFragment == null) {
            throw new IllegalStateException("Mising fragment with id 'nowplaying'. Cannot continue.");
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!(action.equals(Constants.NAVIGATE_ALBUM) || action.equals(Constants.NAVIGATE_ARTIST)))
                mDrawerLayout.openDrawer(GravityCompat.START);
                else super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    public void setDetailsToHeader() {

        songtitle.setText(MusicPlayer.getTrackName());
        songartist.setText(MusicPlayer.getArtistName());
        ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(MusicPlayer.getCurrentAlbumId()).toString(), albumart,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnFail(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true)
                        .build());
    }
    @Override
    public void onMetaChanged() {
        super.onMetaChanged();
        setDetailsToHeader();
    }


    private void setPanelSlideListeners() {
        panelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                nowPlayingCard.setAlpha(1 - slideOffset);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                nowPlayingCard.setAlpha(1);
            }

            @Override
            public void onPanelExpanded(View panel) {
                nowPlayingCard.setAlpha(0);
            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });
    }


}
