package com.naman14.timber.activities;

import android.os.AsyncTask;
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
import com.naman14.timber.fragments.QueueFragment;
import com.naman14.timber.slidinguppanel.SlidingUpPanelLayout;
import com.naman14.timber.subfragments.QuickControlsFragment;
import com.naman14.timber.utils.Constants;
import com.naman14.timber.utils.NavigationUtils;
import com.naman14.timber.utils.PreferencesUtility;
import com.naman14.timber.utils.TimberUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {


    private static MainActivity sMainActivity;

    private DrawerLayout mDrawerLayout;
    SlidingUpPanelLayout panelLayout;
    NavigationView navigationView;

    TextView songtitle, songartist;
    ImageView albumart;

    String action;

    Map<String, Runnable> navigationMap = new HashMap<String, Runnable>();
    Handler navDrawerRunnable = new Handler();

    private boolean isLightTheme;
    private boolean isDarkTheme;

    public static MainActivity getInstance() {
        return sMainActivity;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        sMainActivity = this;
        action = getIntent().getAction();

        isLightTheme = PreferencesUtility.getInstance(this).getTheme().equals("light");
        isDarkTheme = PreferencesUtility.getInstance(this).getTheme().equals("dark");

        if (action.equals(Constants.NAVIGATE_ALBUM) || action.equals(Constants.NAVIGATE_ARTIST) || action.equals(Constants.NAVIGATE_NOWPLAYING)) {
            if (isLightTheme)
                setTheme(R.style.AppTheme_FullScreen_Light);
            else if (isDarkTheme) setTheme(R.style.AppTheme_FullScreen_Dark);
            else setTheme(R.style.AppTheme_FullScreen_Black);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main_fullscreen);
        } else {
            if (isLightTheme)
                setTheme(R.style.AppThemeLight);
            else if (isDarkTheme) setTheme(R.style.AppThemeDark);
            else setTheme(R.style.AppThemeBlack);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        }


        navigationMap.put(Constants.NAVIGATE_LIBRARY, navigateLibrary);
        navigationMap.put(Constants.NAVIGATE_ALBUM, navigateAlbum);
        navigationMap.put(Constants.NAVIGATE_ARTIST, navigateArtist);
        navigationMap.put(Constants.NAVIGATE_NOWPLAYING, navigateNowplaying);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        panelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.inflateHeaderView(R.layout.nav_header);

        albumart = (ImageView) header.findViewById(R.id.album_art);
        songtitle = (TextView) header.findViewById(R.id.song_title);
        songartist = (TextView) header.findViewById(R.id.song_artist);

        Runnable navigation = navigationMap.get(action);
        if (navigation != null) {
            navigation.run();
        } else {
            navigateLibrary.run();
        }

        setPanelSlideListeners();


        navDrawerRunnable.postDelayed(new Runnable() {
            @Override
            public void run() {
                setupDrawerContent(navigationView);
                setupNavigationIcons(navigationView);
            }
        }, 700);


        new initQuickControls().execute("");

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
                if (!(action.equals(Constants.NAVIGATE_ALBUM) || action.equals(Constants.NAVIGATE_ARTIST) || action.equals(Constants.NAVIGATE_NOWPLAYING)))
                    mDrawerLayout.openDrawer(GravityCompat.START);
                else super.onBackPressed();
                return true;
            case R.id.action_settings:
                NavigationUtils.navigateToSettings(this);
                return true;
            case R.id.action_shuffle:
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayer.shuffleAll(MainActivity.this);
                    }
                }, 80);

                return true;
            case R.id.action_search:
                NavigationUtils.navigateToSearch(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (panelLayout.isPanelExpanded())
            panelLayout.collapsePanel();
        else
            super.onBackPressed();
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
                        updatePosition(menuItem);
                        return true;

                    }
                });
    }

    private void setupNavigationIcons(NavigationView navigationView) {

        //material-icon-lib currently doesn't work with navigationview of design support library 22.2.0+
        //set icons manually for now
        //https://github.com/code-mc/material-icon-lib/issues/15

        if (isLightTheme) {
            navigationView.getMenu().findItem(R.id.nav_library).setIcon(R.drawable.library_music);
            navigationView.getMenu().findItem(R.id.nav_playlists).setIcon(R.drawable.playlist_play);
            navigationView.getMenu().findItem(R.id.nav_queue).setIcon(R.drawable.music_note);
            navigationView.getMenu().findItem(R.id.nav_nowplaying).setIcon(R.drawable.bookmark_music);
            navigationView.getMenu().findItem(R.id.nav_settings).setIcon(R.drawable.settings);
            navigationView.getMenu().findItem(R.id.nav_help).setIcon(R.drawable.help_circle);
        } else {
            navigationView.getMenu().findItem(R.id.nav_library).setIcon(R.drawable.library_music_white);
            navigationView.getMenu().findItem(R.id.nav_playlists).setIcon(R.drawable.playlist_play_white);
            navigationView.getMenu().findItem(R.id.nav_queue).setIcon(R.drawable.music_note_white);
            navigationView.getMenu().findItem(R.id.nav_nowplaying).setIcon(R.drawable.bookmark_music_white);
            navigationView.getMenu().findItem(R.id.nav_settings).setIcon(R.drawable.settings_white);
            navigationView.getMenu().findItem(R.id.nav_help).setIcon(R.drawable.help_circle_white);
        }

    }

    private void updatePosition(final MenuItem menuItem) {
        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_library:
                fragment = new MainFragment();
                break;
            case R.id.nav_playlists:
                fragment = new PlaylistFragment();
                break;
            case R.id.nav_nowplaying:
                NavigationUtils.navigateToNowplaying(MainActivity.this, false);
                break;
            case R.id.nav_queue:
                fragment = new QueueFragment();
                break;
            case R.id.nav_settings:
                NavigationUtils.navigateToSettings(MainActivity.this);
                break;
        }

        if (fragment != null) {
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();
            FragmentManager fragmentManager = getSupportFragmentManager();
            final android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    transaction.commit();
                }
            }, 350);
        }
    }

    public void setDetailsToHeader() {
        String name = MusicPlayer.getTrackName();
        String artist = MusicPlayer.getArtistName();

        if (name != null && artist != null) {
            songtitle.setText(name);
            songartist.setText(artist);
        }
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
                View nowPlayingCard = QuickControlsFragment.topContainer;
                nowPlayingCard.setAlpha(1 - slideOffset);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                View nowPlayingCard = QuickControlsFragment.topContainer;
                nowPlayingCard.setAlpha(1);
            }

            @Override
            public void onPanelExpanded(View panel) {
                View nowPlayingCard = QuickControlsFragment.topContainer;
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
    public void onResume() {
        super.onResume();
        sMainActivity = this;
    }

    Runnable navigateAlbum = new Runnable() {
        public void run() {
            long albumID = getIntent().getExtras().getLong(Constants.ALBUM_ID);
            Fragment fragment = new AlbumDetailFragment().newInstance(albumID);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }
    };

    Runnable navigateArtist = new Runnable() {
        public void run() {
            long artistID = getIntent().getExtras().getLong(Constants.ARTIST_ID);
            Fragment fragment = new ArtistDetailFragment().newInstance(artistID);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }
    };

    Runnable navigateNowplaying = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_nowplaying).setCheckable(false);
            String fragmentID = getIntent().getExtras().getString(Constants.NOWPLAYING_FRAGMENT_ID);
            boolean withAnimations = getIntent().getExtras().getBoolean(Constants.WITH_ANIMATIONS);

            Fragment fragment = NavigationUtils.getFragmentForNowplayingID(fragmentID);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
            panelLayout.setPanelHeight(0);
        }
    };

    Runnable navigateLibrary = new Runnable() {
        public void run() {
            navigationView.getMenu().findItem(R.id.nav_library).setChecked(true);
            Fragment fragment = new MainFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();

        }
    };

    private class initQuickControls extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            QuickControlsFragment fragment1 = new QuickControlsFragment();
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .replace(R.id.quickcontrols_container, fragment1).commit();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {
        }
    }

}


