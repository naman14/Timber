package com.naman14.timber.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.util.Pair;

import com.naman14.timber.activities.MainActivity;
import com.naman14.timber.activities.PlaylistDetailActivity;
import com.naman14.timber.activities.SettingsActivity;
import com.naman14.timber.nowplaying.Timber1;
import com.naman14.timber.nowplaying.Timber2;
import com.naman14.timber.nowplaying.Timber3;

import java.util.ArrayList;

/**
 * Created by naman on 22/07/15.
 */
public class NavigationUtils {

    @TargetApi(21)
    public static void navigateToAlbum(Activity context,long albumID , ArrayList<Pair> transitionViews){
        final Intent intent=new Intent(context, MainActivity.class);
        intent.setAction(Constants.NAVIGATE_ALBUM);
        intent.putExtra(Constants.ALBUM_ID,albumID);

        if (TimberUtils.isLollipop()) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.getInstance(), transitionViews.get(0));
            context.startActivity(intent, options.toBundle());
        } else {
            context.startActivity(intent);
        }

    }

    public static void navigateToArtist(Activity context,long artistID,ArrayList<Pair> transitionViews){
        final Intent intent=new Intent(context, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setAction(Constants.NAVIGATE_ARTIST);
        intent.putExtra(Constants.ARTIST_ID,artistID);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.getInstance(), transitionViews.get(0));
        context.startActivity(intent,options.toBundle());
    }

    public static void navigateToNowplaying(Activity context,boolean withAnimations){
        SharedPreferences prefs = context.getSharedPreferences(Constants.FRAGMENT_ID, Context.MODE_PRIVATE);
        String fragmentID= prefs.getString(Constants.NOWPLAYING_FRAGMENT_ID, Constants.TIMBER1);

        final Intent intent=new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setAction(Constants.NAVIGATE_NOWPLAYING);
        intent.putExtra(Constants.NOWPLAYING_FRAGMENT_ID,fragmentID);
        intent.putExtra(Constants.WITH_ANIMATIONS,withAnimations);
        context.startActivity(intent);
    }

    public static void navigateToSettings(Activity context){
        final Intent intent=new Intent(context, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setAction(Constants.NAVIGATE_SETTINGS);
        context.startActivity(intent);
    }

    public static void navigateToPlaylistDetail(Activity context,String action){
        final Intent intent=new Intent(context, PlaylistDetailActivity.class);
        intent.setAction(action);
        context.startActivity(intent);
    }


    public static Intent getNavigateToStyleSelectorIntent(Activity context,String what){
        final Intent intent=new Intent(context, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setAction(Constants.SETTINGS_STYLE_SELECTOR);
        intent.putExtra(Constants.SETTINGS_STYLE_SELECTOR_WHAT,what);
        return intent;
    }

    public static Fragment  getFragmentForNowplayingID(String fragmentID){
        switch (fragmentID){
            case Constants.TIMBER1:return new Timber1();
            case Constants.TIMBER2:return new Timber2();
            case Constants.TIMBER3:return new Timber3();
            default:return new Timber1();
        }

    }

}
