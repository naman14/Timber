/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.naman14.timber.lastfmapi;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.naman14.timber.lastfmapi.callbacks.ArtistInfoListener;
import com.naman14.timber.lastfmapi.callbacks.UserListener;
import com.naman14.timber.lastfmapi.models.AlbumInfo;
import com.naman14.timber.lastfmapi.models.AlbumQuery;
import com.naman14.timber.lastfmapi.models.ArtistInfo;
import com.naman14.timber.lastfmapi.models.ArtistQuery;
import com.naman14.timber.lastfmapi.models.LastfmUserSession;
import com.naman14.timber.lastfmapi.models.ScrobbleInfo;
import com.naman14.timber.lastfmapi.models.ScrobbleQuery;
import com.naman14.timber.lastfmapi.models.UserLoginInfo;
import com.naman14.timber.lastfmapi.models.UserLoginQuery;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LastFmClient {

    //TODO update the api keys
    public static final String API_KEY = "62ac1851456e4558bef1c41747b1aec2";
    public static final String API_SECRET = "b4ae8965723d67fb18e35d207014d6f3";

    public static final String JSON = "json";

    public static final String BASE_API_URL = "http://ws.audioscrobbler.com/2.0";
    public static final String BASE_SECURE_API_URL = "https://ws.audioscrobbler.com/2.0";

    public static final String PREFERENCES_NAME = "Lastfm";
    static final String PREFERENCE_CACHE_NAME = "Cache";

    private static LastFmClient sInstance;
    private LastFmRestService mRestService;
    private LastFmUserRestService mUserRestService;

    private Context context;

    private LastfmUserSession mUserSession;
    private static final Object sLock = new Object();

    public static LastFmClient getInstance(Context context) {
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new LastFmClient();
                sInstance.context = context;
                sInstance.mRestService = RestServiceFactory.createStatic(context, BASE_API_URL, LastFmRestService.class);
                sInstance.mUserRestService = RestServiceFactory.create(context, BASE_SECURE_API_URL, LastFmUserRestService.class);
                sInstance.mUserSession = LastfmUserSession.getSession(context);

            }
            return sInstance;
        }
    }

    private static String generateMD5(String in) {
        byte[] bytesOfMessage = new byte[0];
        try {
            bytesOfMessage = in.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(bytesOfMessage);
            String out = "";
            for (byte symbol : digest) {
                out += String.format("%02X", symbol);
            }
            return out;
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ignored) {
            return null;
        }


    }

    public void getAlbumInfo(AlbumQuery albumQuery) {
        mRestService.getAlbumInfo(albumQuery.mArtist, albumQuery.mALbum, new Callback<AlbumInfo>() {
            @Override
            public void success(AlbumInfo albumInfo, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

                error.printStackTrace();
            }
        });
    }

    public void getArtistInfo(ArtistQuery artistQuery, final ArtistInfoListener listener) {
        mRestService.getArtistInfo(artistQuery.mArtist, new Callback<ArtistInfo>() {
            @Override
            public void success(ArtistInfo artistInfo, Response response) {
                listener.artistInfoSucess(artistInfo.mArtist);
            }

            @Override
            public void failure(RetrofitError error) {
                listener.artistInfoFailed();
                error.printStackTrace();
            }
        });
    }

    public void getUserLoginInfo(UserLoginQuery userLoginQuery, final UserListener listener) {
        mUserRestService.getUserLoginInfo(UserLoginQuery.Method, JSON, API_KEY, generateMD5(userLoginQuery.getSignature()), userLoginQuery.mUsername, userLoginQuery.mPassword, new Callback<UserLoginInfo>() {
            @Override
            public void success(UserLoginInfo userLoginInfo, Response response) {
                Log.d("Logedin", userLoginInfo.mSession.mToken + " " + userLoginInfo.mSession.mUsername);
                mUserSession = userLoginInfo.mSession;
                mUserSession.update(context);
                listener.userSuccess();
            }

            @Override
            public void failure(RetrofitError error) {
                listener.userInfoFailed();
            }
        });
    }

    public void Scrobble(final ScrobbleQuery scrobbleQuery) {
        try {
            TreeMap<String,String> fields = new TreeMap<>();
            fields.put("method",ScrobbleQuery.Method);
            fields.put("api_key", API_KEY);
            fields.put("sk", mUserSession.mToken);
            fields.put("artist[0]",scrobbleQuery.mArtist);
            fields.put("track[0]",scrobbleQuery.mTrack);
            fields.put("timestamp[0]",Long.toString(scrobbleQuery.mTimestamp));
            final ScrobbleQuery[] queries = getScrobbleCache();
            for(int i = 0; i<queries.length;i++){
                fields.put("artist["+(i+1)+']',queries[i].mArtist);
                fields.put("track["+(i+1)+']',queries[i].mTrack);
                fields.put("timestamp["+(i+1)+']',Long.toString(queries[i].mTimestamp));
            }
            String sig = "";
            for(Map.Entry<String,String> ent: fields.entrySet()){
                sig += ent.getKey()+ent.getValue();
            }
            sig += API_SECRET;
            mUserRestService.getScrobbleInfo(generateMD5(sig), fields, new Callback<ScrobbleInfo>() {
                @Override
                public void success(ScrobbleInfo scrobbleInfo, Response response) {
                    if(queries.length>0){
                        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putStringSet(PREFERENCE_CACHE_NAME,null);
                        editor.commit();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    putScrobbleCache(scrobbleQuery);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        getScrobbleCache();
    }

    private void putScrobbleCache(ScrobbleQuery query){
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Set<String>  cache = preferences.getStringSet(PREFERENCE_CACHE_NAME,new HashSet<String>());
        SharedPreferences.Editor editor = preferences.edit();
        try {
            cache.add(URLEncoder.encode(query.mArtist,"UTF-8")+','+URLEncoder.encode(query.mTrack,"UTF-8")+','+URLEncoder.encode(Long.toHexString(query.mTimestamp),"UTF-8"));
        } catch (UnsupportedEncodingException ignored) { }
        editor.putStringSet(PREFERENCE_CACHE_NAME,cache);
        editor.commit();
    }

    private ScrobbleQuery[] getScrobbleCache(){
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Set<String> cache = preferences.getStringSet(PREFERENCE_CACHE_NAME,new HashSet<String>());
        ScrobbleQuery[] queries = new ScrobbleQuery[cache.size()];
        int i = 0;
        for(String str: cache){
            String[] arr = str.split(",");
            try {
                queries[i++] = new ScrobbleQuery(URLDecoder.decode(arr[0],"UTF-8"),URLDecoder.decode(arr[1],"UTF-8"),Long.parseLong(URLDecoder.decode(arr[2],"UTF-8"),16));
            } catch (UnsupportedEncodingException ignored) { }
        }
        return queries;
    }

    public void logout() {
        this.mUserSession.mToken = null;
        this.mUserSession.mUsername = null;
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public String getUsername() {
        if (mUserSession != null) return mUserSession.mUsername;
        return null;
    }
}
