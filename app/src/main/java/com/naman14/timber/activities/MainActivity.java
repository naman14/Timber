package com.naman14.timber.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import com.naman14.timber.fragments.PlaylistFragment;
import com.naman14.timber.nowplaying.NowPlayingFragment;
import com.naman14.timber.slidinguppanel.SlidingUpPanelLayout;
import com.naman14.timber.subfragments.QuickControlsFragment;
import com.naman14.timber.utils.Constants;
import com.naman14.timber.utils.NavigationUtils;
import com.naman14.timber.utils.TimberUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {


    private static MainActivity sMainActivity;

    private DrawerLayout mDrawerLayout;
    QuickControlsFragment mControlsFragment;
    NowPlayingFragment mNowPlayingFragment;
    SlidingUpPanelLayout panelLayout;

    TextView songtitle, songartist;
    ImageView albumart;

    String action;

    Map<String,Runnable> navigationMap = new HashMap<String,Runnable>();

    public static MainActivity getInstance() {
        return sMainActivity;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        sMainActivity=this;
        action = getIntent().getAction();

        if (action.equals(Constants.NAVIGATE_ALBUM) || action.equals(Constants.NAVIGATE_ARTIST) || action.equals(Constants.NAVIGATE_NOWPLAYING)) {
            setTheme(R.style.AppTheme_FullScreen);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main_fullscreen);
        } else {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        }

        navigationMap.put(Constants.NAVIGATE_LIBRARY, navigateLibrary);
        navigationMap.put(Constants.NAVIGATE_ALBUM, navigateAlbum);
        navigationMap.put(Constants.NAVIGATE_ARTIST, navigateArtist);
        navigationMap.put(Constants.NAVIGATE_NOWPLAYING, navigateNowplaying);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        panelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        Runnable navigation = navigationMap.get(action);
        if (navigation!= null) {
            navigation.run();
        } else {
            navigateLibrary.run();
        }


        setPanelSlideListeners();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        setupDrawerContent(navigationView);
        setupNavigationIcons(navigationView);
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
            case R.id.action_settings:
                NavigationUtils.navigateToSettings(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updatePosition(menuItem);
                            }
                        },300);

                        return true;

                    }
                });
    }
    private void setupNavigationIcons(NavigationView navigationView){
        MaterialDrawableBuilder drawable = MaterialDrawableBuilder.with(this)
                .setColor(Color.BLACK);

        navigationView.getMenu().findItem(R.id.nav_library).setIcon(drawable.setIcon(MaterialDrawableBuilder.IconValue.LIBRARY_MUSIC).build());
        navigationView.getMenu().findItem(R.id.nav_library).setChecked(true);

        navigationView.getMenu().findItem(R.id.nav_playlists).setIcon(drawable.setIcon(MaterialDrawableBuilder.IconValue.PLAYLIST_PLUS).build());
        navigationView.getMenu().findItem(R.id.nav_nowplaying).setIcon(drawable.setIcon(MaterialDrawableBuilder.IconValue.MUSIC_CIRCLE).build());
        navigationView.getMenu().findItem(R.id.nav_artist).setIcon(drawable.setIcon(MaterialDrawableBuilder.IconValue.NAVIGATION).build());
        navigationView.getMenu().findItem(R.id.nav_album).setIcon(drawable.setIcon(MaterialDrawableBuilder.IconValue.NAVIGATION).build());
        navigationView.getMenu().findItem(R.id.nav_settings).setIcon(drawable.setIcon(MaterialDrawableBuilder.IconValue.SETTINGS).build());
        navigationView.getMenu().findItem(R.id.nav_help).setIcon(drawable.setIcon(MaterialDrawableBuilder.IconValue.HELP).build());
    }

    private void updatePosition(MenuItem menuItem){
        Fragment fragment = null;

        switch (menuItem.getItemId()){
            case R.id.nav_library:
                fragment=new MainFragment();
                break;
            case R.id.nav_playlists:
                fragment=new PlaylistFragment();
                break;
            case R.id.nav_nowplaying:
                NavigationUtils.navigateToNowplaying(MainActivity.this,false);
                menuItem.setChecked(false);
                break;
            case R.id.nav_album:
                break;
            case R.id.nav_artist:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out)
                    .replace(R.id.fragment_container, fragment).commit();

        }
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
            View nowPlayingCard =QuickControlsFragment.topContainer;
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

    @Override
    public void onResume(){
        super.onResume();
        sMainActivity=this;
    }

    Runnable navigateAlbum = new Runnable() {
        public void run() {
            long albumID = getIntent().getExtras().getLong(Constants.ALBUM_ID);
            Fragment fragment = new AlbumDetailFragment().newInstance(albumID);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();}
    };

    Runnable navigateArtist =new Runnable() {
        public void run() {
            long artistID = getIntent().getExtras().getLong(Constants.ARTIST_ID);
            Fragment fragment = new ArtistDetailFragment().newInstance(artistID);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();}
    };

    Runnable navigateNowplaying =new Runnable() {
        public void run() {
            String fragmentID = getIntent().getExtras().getString(Constants.NOWPLAYING_FRAGMENT_ID);
            boolean withAnimations =getIntent().getExtras().getBoolean(Constants.WITH_ANIMATIONS);

            Fragment fragment = NavigationUtils.getFragmentForNowplayingID(fragmentID);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
            panelLayout.setPanelHeight(0);}
    };

    Runnable navigateLibrary =new Runnable() {
        public void run() {
            Fragment fragment = new MainFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();}
    };



}
