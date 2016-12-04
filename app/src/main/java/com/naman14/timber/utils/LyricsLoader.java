package com.naman14.timber.utils;

import android.content.Context;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

/**
 * Created by Christoph Walcher on 03.12.16.
 */

public class LyricsLoader {
    private static LyricsLoader instance = null;
    private static final String BASE_API_URL = "https://makeitpersonal.co";
    private static final long CACHE_SIZE = 1024 * 1024;
    private LyricsRestService service;

    public static LyricsLoader getInstance(Context con) {
        if(instance==null)instance = new LyricsLoader(con);
        return instance;
    }

    private LyricsLoader(Context con){
        final OkHttpClient okHttpClient = new OkHttpClient();

        okHttpClient.setCache(new Cache(con.getApplicationContext().getCacheDir(),
                CACHE_SIZE));
        okHttpClient.setConnectTimeout(20, TimeUnit.SECONDS);

        RequestInterceptor interceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                //7-days cache
                request.addHeader("Cache-Control", String.format("max-age=%d,max-stale=%d", Integer.valueOf(60 * 60 * 24 * 7), Integer.valueOf(31536000)));
            }
        };

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(BASE_API_URL)
                .setRequestInterceptor(interceptor)
                .setConverter(new Converter() {
                    @Override
                    public Object fromBody(TypedInput arg0, Type arg1)
                            throws ConversionException {

                        try {
                            BufferedReader br = null;
                            StringBuilder sb = new StringBuilder();

                            String line;

                            br = new BufferedReader(new InputStreamReader(arg0.in()));
                            while ((line = br.readLine()) != null) {
                                sb.append(line);
                                sb.append('\n');
                            }
                            return sb.toString();

                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    public TypedOutput toBody(Object arg0) {
                        return null;
                    }
                })
                .setClient(new OkClient(okHttpClient));

         service = builder
                .build()
                .create(LyricsRestService.class);
    }

    public void getLyrics(String artist, String title, Callback<String> callback){
        service.getLyrics(artist,title,callback);
    }

    private interface LyricsRestService {
        @Headers("Cache-Control: public")
        @GET("/lyrics")
        void getLyrics(@Query("artist") String artist, @Query("title") String title, Callback<String> callback);
    }

}
