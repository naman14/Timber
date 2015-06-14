package com.naman14.timber;

import android.app.Application;

/**
 * Created by naman on 14/06/15.
 */
public class TimberApp extends Application {


    private static TimberApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;


    }

    public static synchronized TimberApp getInstance() {
        return mInstance;
    }





}
