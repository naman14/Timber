package com.naman14.timber.lastfmapi.models;

import com.google.gson.annotations.SerializedName;
import com.naman14.timber.lastfmapi.LastFmClient;

/**
 * Created by christoph on 17.07.16.
 */
public class UserLoginQuery {
    private static final String USERNAME_NAME = "username";
    private static final String PASSWORD_NAME = "password";

    @SerializedName(USERNAME_NAME)
    public String mUsername;

    @SerializedName(PASSWORD_NAME)
    public String mPassword;

    public static final String Method = "auth.getMobileSession";

    public UserLoginQuery(String username, String password) {
        this.mUsername = username;
        this.mPassword = password;
    }

    public String getSignature() {
        return "api_key" + LastFmClient.API_KEY  + "method" + Method + "password" + mPassword + "username" + mUsername + LastFmClient.API_SECRET;
    }
}
