package com.naman14.timber.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

/**
 * Created by naman on 14/06/15.
 */
public class TimberUtils {

    @SuppressLint("NewApi")
    public static <T> void execute(final boolean forceSerial, final AsyncTask<T, ?, ?> task,
                                   final T... args) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.DONUT) {
            throw new UnsupportedOperationException(
                    "This class can only be used on API 4 and newer.");
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || forceSerial) {
            task.execute(args);
        } else {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, args);
        }
    }

    public static final ArtworkFetcher getImageFetcher(final Activity activity) {
        final ArtworkFetcher artworkFetcher = ArtworkFetcher.getInstance(activity);
        artworkFetcher.setImageCache(ImageCache.findOrCreateCache(activity));
        return artworkFetcher;
    }

    public static final boolean isOnline(final Context context) {

        if (context == null) {
            return false;
        }

        boolean state = false;
        //TODO make it better
        final boolean onlyOnWifi = true;

        /* Monitor network connections */
        final ConnectivityManager connectivityManager = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        /* Wi-Fi connection */
        final NetworkInfo wifiNetwork = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null) {
            state = wifiNetwork.isConnectedOrConnecting();
        }

        /* Mobile data connection */
        final NetworkInfo mbobileNetwork = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mbobileNetwork != null) {
            if (!onlyOnWifi) {
                state = mbobileNetwork.isConnectedOrConnecting();
            }
        }

        /* Other networks */
        final NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (!onlyOnWifi) {
                state = activeNetwork.isConnectedOrConnecting();
            }
        }

        return state;
    }
    public static String getTrimmedName(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }

        name = name.trim().toLowerCase();
        if (name.startsWith("the ")) {
            name = name.substring(4);
        }
        if (name.startsWith("an ")) {
            name = name.substring(3);
        }
        if (name.startsWith("a ")) {
            name = name.substring(2);
        }
        if (name.endsWith(", the") || name.endsWith(",the") ||
                name.endsWith(", an") || name.endsWith(",an") ||
                name.endsWith(", a") || name.endsWith(",a")) {
            name = name.substring(0, name.lastIndexOf(','));
        }
        name = name.replaceAll("[\\[\\]\\(\\)\"'.,?!]", "").trim();

        return name;
    }
    public static Uri getAlbumArtUri(long paramInt)
    {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), paramInt);
    }
}
