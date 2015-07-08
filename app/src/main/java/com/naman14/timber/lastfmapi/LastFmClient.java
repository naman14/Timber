package com.naman14.timber.lastfmapi;

import android.content.Context;

/**
 * Created by naman on 08/07/15.
 */
public class LastFmClient {

    public static final String BASE_API_URL = "http://ws.audioscrobbler.com/2.0";

    private static LastFmClient sInstance;
    private LastFmRestService mRestService;

    private static final Object sLock = new Object();

    public static LastFmClient getInstance(Context context) {
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new LastFmClient();
                sInstance.mRestService = RestServiceFactory.create(context, BASE_API_URL, LastFmRestService.class);
            }
            return sInstance;
        }
    }
}
