package com.naman14.timber.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.naman14.timber.R;
import com.naman14.timber.fragments.AlbumFragment;
import com.naman14.timber.fragments.ArtistFragment;
import com.naman14.timber.fragments.MainFragment;
import com.naman14.timber.fragments.PlaybackControlsFragment;
import com.naman14.timber.fragments.SongsFragment;
import com.naman14.timber.nowplaying.NowPlayingFragment;
import com.naman14.timber.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {


    private DrawerLayout mDrawerLayout;
    PlaybackControlsFragment mControlsFragment;
    NowPlayingFragment mNowPlayingFragment;
    CardView nowPlayingCard;
    SlidingUpPanelLayout panelLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Fragment fragment=new MainFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();



        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        panelLayout=(SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        nowPlayingCard=(CardView) findViewById(R.id.controls_container);

        setPanelSlideListeners();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }



         mControlsFragment = (PlaybackControlsFragment) getSupportFragmentManager()
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
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new SongsFragment(), "Category 1");
        adapter.addFragment(new AlbumFragment(), "Category 2");
        adapter.addFragment(new ArtistFragment(), "Category 3");
        viewPager.setAdapter(adapter);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
//                        FragmentManager fragmentManager = getSupportFragmentManager();
//                        fragmentManager.beginTransaction()
//                                .replace(R.id.fragment_container, fragment).commit();
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
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

    public void showPlaybackControls() {

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom,
                        R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom)
                .show(mControlsFragment)
                .commit();

    }

    public void hidePlaybackControls() {

        getSupportFragmentManager().beginTransaction()
                .hide(mControlsFragment)
                .commit();
    }

    private void setPanelSlideListeners(){
        panelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                nowPlayingCard.setAlpha(1-slideOffset);
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
