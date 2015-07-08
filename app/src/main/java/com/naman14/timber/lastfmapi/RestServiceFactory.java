package com.naman14.timber.lastfmapi;

import android.content.Context;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class RestServiceFactory {
    private static final String TAG_OK_HTTP = "OkHttp";
    private static final long CACHE_SIZE = 1024 * 1024;

    public static <T> T create(final Context context, String baseUrl, Class<T> clazz) {
        final OkHttpClient okHttpClient = new OkHttpClient();

        okHttpClient.setCache(new Cache(context.getApplicationContext().getCacheDir(),
                CACHE_SIZE));
        okHttpClient.setConnectTimeout(40, TimeUnit.SECONDS);

        RequestInterceptor interceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                //7-days cache
                request.addHeader("Cache-Control", String.format("max-age=%d,max-stale=%d", 60*60*24*7, 0));
                request.addHeader("Connection", "keep-alive");
            }
        };

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(baseUrl)
                .setRequestInterceptor(interceptor)
                .setClient(new OkClient(okHttpClient));

        return builder
                .build()
                .create(clazz);

    }


}
