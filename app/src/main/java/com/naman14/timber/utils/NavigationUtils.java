package com.naman14.timber.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.View;

import com.naman14.timber.activities.MainActivity;
import com.naman14.timber.nowplaying.Timber1;

import java.util.ArrayList;

/**
 * Created by naman on 22/07/15.
 */
public class NavigationUtils {

    public static void navigateToAlbum(Activity context,long albumID , ArrayList<Pair> transitionViews){
        final Intent intent=new Intent(context, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setAction(Constants.NAVIGATE_ALBUM);
        intent.putExtra(Constants.ALBUM_ID,albumID);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.getInstance(), transitionViews.get(0));
        context.startActivity(intent,options.toBundle());
    }

    public static void navigateToArtist(Activity context,long artistID){
        final Intent intent=new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setAction(Constants.NAVIGATE_ARTIST);
        intent.putExtra(Constants.ARTIST_ID,artistID);
        context.startActivity(intent);
    }

    public static void navigateToNowplaying(Activity context,boolean withAnimations){
        String fragmentID=Constants.TIMBER1;
        final Intent intent=new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setAction(Constants.NAVIGATE_NOWPLAYING);
        intent.putExtra(Constants.NOWPLAYING_FRAGMENT_ID,fragmentID);
        intent.putExtra(Constants.WITH_ANIMATIONS,withAnimations);
        context.startActivity(intent);
    }

    public static Fragment  getFragmentForNowplayingID(String fragmentID){
        //TODO implement shared prefernces here to get fragmentid
        return new Timber1();
    }
}
