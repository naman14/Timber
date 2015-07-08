package com.naman14.timber.lastfmapi.callbacks;

import com.naman14.timber.lastfmapi.models.LastfmArtist;

/**
 * Created by naman on 08/07/15.
 */
public interface ArtistInfoListener {

    public void artistInfoSucess(LastfmArtist artist);
    public void artistInfoFailed();

}
