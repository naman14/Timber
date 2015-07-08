package com.naman14.timber.lastfmapi;

import com.naman14.timber.lastfmapi.models.AlbumInfo;
import com.naman14.timber.lastfmapi.models.ArtistInfo;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;

/**
 * Created by naman on 08/07/15.
 */

public interface LastFmRestService {

    public static final String BASE_PARAMETERS_ALBUM = "/?method=album.getinfo&api_key=fdb3a51437d4281d4d64964d333531d4&format=json";
    public static final String BASE_PARAMETERS_ARTIST = "/?method=artist.getinfo&api_key=fdb3a51437d4281d4d64964d333531d4&format=json";

    @Headers("Cache-Control: public")
    @GET(BASE_PARAMETERS_ALBUM)
    void getAlbumInfo(@Query("artist") String artist, @Query("album") String album, Callback<AlbumInfo> callback);

    @Headers("Cache-Control: public")
    @GET(BASE_PARAMETERS_ARTIST)
    void getArtistInfo(@Query("artist") String artist, Callback<ArtistInfo> callback);

}
