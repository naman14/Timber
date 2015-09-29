package com.naman14.timber.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Pair;

import com.naman14.timber.activities.MainActivity;
import com.naman14.timber.activities.PlaylistDetailActivity;
import com.naman14.timber.activities.SearchActivity;
import com.naman14.timber.activities.SettingsActivity;
import com.naman14.timber.nowplaying.Timber1;
import com.naman14.timber.nowplaying.Timber2;
import com.naman14.timber.nowplaying.Timber3;
import com.naman14.timber.nowplaying.Timber4;

import java.util.ArrayList;

/**
 * Created by naman on 22/07/15.
 */
public class NavigationUtils {

    @TargetApi(21)
    public static void navigateToAlbum(Activity context,long albumID , ArrayList<Pair> transitionViews){
        final Intent intent=new Intent(context, MainActivity.class);
        if (!PreferencesUtility.getInstance(context).getSystemAnimations()){
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }
        intent.setAction(Constants.NAVIGATE_ALBUM);
        intent.putExtra(Constants.ALBUM_ID,albumID);

        if (TimberUtils.isLollipop() && transitionViews != null && PreferencesUtility.getInstance(context).getAnimations()) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(context, transitionViews.get(0));
            context.startActivity(intent, options.toBundle());
        } else {
            context.startActivity(intent);
        }

    }

    @TargetApi(21)
    public static void navigateToArtist(Activity context,long artistID,ArrayList<Pair> transitionViews){
        final Intent intent=new Intent(context, MainActivity.class);
        if (!PreferencesUtility.getInstance(context).getSystemAnimations()){
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }
        intent.setAction(Constants.NAVIGATE_ARTIST);
        intent.putExtra(Constants.ARTIST_ID,artistID);

        if (TimberUtils.isLollipop() && transitionViews != null  && PreferencesUtility.getInstance(context).getAnimations()) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(context, transitionViews.get(0));
            context.startActivity(intent, options.toBundle());
        } else {
            context.startActivity(intent);
        }
    }

    public static void navigateToNowplaying(Activity context,boolean withAnimations){

        final Intent intent=new Intent(context, MainActivity.class);
        if (!PreferencesUtility.getInstance(context).getSystemAnimations()){
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }
        intent.setAction(Constants.NAVIGATE_NOWPLAYING);
        intent.putExtra(Constants.WITH_ANIMATIONS,withAnimations);
        context.startActivity(intent);
    }

    public static Intent getNowPlayingIntent(Context context){

        final Intent intent=new Intent(context, MainActivity.class);
        intent.setAction(Constants.NAVIGATE_NOWPLAYING);
        return intent;
    }

    public static void navigateToSettings(Activity context) {
        final Intent intent=new Intent(context, SettingsActivity.class);
        if (!PreferencesUtility.getInstance(context).getSystemAnimations()){
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }
        intent.setAction(Constants.NAVIGATE_SETTINGS);
        context.startActivity(intent);
    }

    public static void navigateToSearch(Activity context){
        final Intent intent=new Intent(context, SearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setAction(Constants.NAVIGATE_SEARCH);
        context.startActivity(intent);
    }

    @TargetApi(21)
    public static void navigateToPlaylistDetail(Activity context,String action,long firstAlbumID,String playlistName,int foregroundcolor,long playlistID,ArrayList<Pair> transitionViews){
        final Intent intent=new Intent(context, PlaylistDetailActivity.class);
        if (!PreferencesUtility.getInstance(context).getSystemAnimations()){
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }
        intent.setAction(action);
        intent.putExtra(Constants.PLAYLIST_ID,playlistID);
        intent.putExtra(Constants.PLAYLIST_FOREGROUND_COLOR,foregroundcolor);
        intent.putExtra(Constants.ALBUM_ID,firstAlbumID);
        intent.putExtra(Constants.PLAYLIST_NAME,playlistName);

        if (TimberUtils.isLollipop() && PreferencesUtility.getInstance(context).getAnimations()) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.getInstance(), transitionViews.get(0),transitionViews.get(1),transitionViews.get(2));
            context.startActivity(intent, options.toBundle());
        } else {
            context.startActivity(intent);
        }
    }


    public static Intent getNavigateToStyleSelectorIntent(Activity context,String what){
        final Intent intent=new Intent(context, SettingsActivity.class);
        if (!PreferencesUtility.getInstance(context).getSystemAnimations()){
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }
        intent.setAction(Constants.SETTINGS_STYLE_SELECTOR);
        intent.putExtra(Constants.SETTINGS_STYLE_SELECTOR_WHAT,what);
        return intent;
    }

    public static Fragment  getFragmentForNowplayingID(String fragmentID){
        switch (fragmentID){
            case Constants.TIMBER1:return new Timber1();
            case Constants.TIMBER2:return new Timber2();
            case Constants.TIMBER3:return new Timber3();
            case Constants.TIMBER4:return new Timber4();
            default:return new Timber1();
        }

    }

}
