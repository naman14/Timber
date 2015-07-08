package com.naman14.timber.lastfmapi;

import android.content.Context;
import android.util.Log;

import com.naman14.timber.lastfmapi.callbacks.ArtistInfoListener;
import com.naman14.timber.lastfmapi.models.AlbumInfo;
import com.naman14.timber.lastfmapi.models.AlbumQuery;
import com.naman14.timber.lastfmapi.models.ArtistInfo;
import com.naman14.timber.lastfmapi.models.ArtistQuery;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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

    public void getAlbumInfo(AlbumQuery albumQuery) {
        mRestService.getAlbumInfo(albumQuery.mArtist, albumQuery.mALbum,new Callback<AlbumInfo>() {
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
                Log.d("lol","failed");
                error.printStackTrace();
            }
        });
    }
}
