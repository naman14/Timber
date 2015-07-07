package com.naman14.timber;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by naman on 14/06/15.
 */
public class TimberApp extends Application {


    private static TimberApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        ImageLoaderConfiguration localImageLoaderConfiguration = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(localImageLoaderConfiguration);

    }

    public static synchronized TimberApp getInstance() {
        return mInstance;
    }





}
