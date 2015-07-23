package com.naman14.timber.utils;

import android.app.Activity;
import android.content.Intent;

import com.naman14.timber.activities.MainActivity;

/**
 * Created by naman on 22/07/15.
 */
public class NavigationUtils {

    public static void navigateToAlbum(Activity context,long albumID){
        final Intent intent=new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setAction(Constants.NAVIGATE_ALBUM);
        intent.putExtra(Constants.ALBUM_ID,albumID);
        context.startActivity(intent);
    }

    public static void navigateToArtist(Activity context,long artistID){
        final Intent intent=new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setAction(Constants.NAVIGATE_ARTIST);
        intent.putExtra(Constants.ARTIST_ID,artistID);
        context.startActivity(intent);
    }
}
