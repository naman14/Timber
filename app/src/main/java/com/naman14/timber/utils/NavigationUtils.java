package com.naman14.timber.utils;

import android.content.Context;
import android.content.Intent;

import com.naman14.timber.activities.MainActivity;

/**
 * Created by naman on 22/07/15.
 */
public class NavigationUtils {

    public static void navigateToAlbum(Context context){
        Intent intent=new Intent(context, MainActivity.class);
        intent.setAction(Constants.NAVIGATE_ALBUM);
        context.startActivity(intent);
    }
}
