package com.naman14.timber.lastfmapi.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.annotations.SerializedName;

/**
 * Created by christoph on 17.07.16.
 */
public class LastfmUserSession {
    private static final String USERNAME = "name";
    private static final String TOKEN = "key";

    private static final String PREFERENCES_NAME = "Lastfm";

    public static LastfmUserSession getSession(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        LastfmUserSession session = new LastfmUserSession();
        session.mToken = preferences.getString(TOKEN, null);
        session.mUsername = preferences.getString(USERNAME, null);
        if (session.mToken == null || session.mUsername == null) return null;
        return session;
    }

    public void update(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (this.mToken == null || this.mUsername == null) {
            editor.clear();
        } else {
            editor.putString(TOKEN, this.mToken);
            editor.putString(USERNAME, this.mUsername);
        }
        editor.apply();
    }

    @SerializedName(USERNAME)
    public String mUsername;

    @SerializedName(TOKEN)
    public String mToken;
}
