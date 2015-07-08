package com.naman14.timber.lastfmapi.callbacks;

import com.naman14.timber.lastfmapi.models.LastfmAlbum;

/**
 * Created by naman on 08/07/15.
 */
public interface AlbuminfoListener {

    public void albumInfoSucess(LastfmAlbum album);
    public void albumInfoFailed();

}
