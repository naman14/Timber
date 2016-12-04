package com.naman14.timber.activities;

import android.content.Context;
import android.content.CursorLoader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.customizers.ATEActivityThemeCustomizer;
import com.afollestad.appthemeengine.customizers.ATEToolbarCustomizer;
import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.utils.Constants;
import com.naman14.timber.utils.ImageUtils;
import com.naman14.timber.utils.LyricsExtractor;
import com.naman14.timber.utils.LyricsLoader;
import com.naman14.timber.utils.NavigationUtils;
import com.naman14.timber.utils.PreferencesUtility;

import java.io.File;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by naman on 01/01/16.
 */
public class NowPlayingActivity extends BaseActivity implements ATEActivityThemeCustomizer, ATEToolbarCustomizer {

    boolean lyricsopened = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nowplaying);
        SharedPreferences prefs = getSharedPreferences(Constants.FRAGMENT_ID, Context.MODE_PRIVATE);
        String fragmentID = prefs.getString(Constants.NOWPLAYING_FRAGMENT_ID, Constants.TIMBER3);

        Fragment fragment = NavigationUtils.getFragmentForNowplayingID(fragmentID);
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment).commit();

    }

    private void displayLyrics() {
        lyricsopened = true;
        final View bg = findViewById(R.id.container);
        final View lyricsView = findViewById(R.id.lyrics);
        lyricsView.setActivated(true);
        lyricsView.setVisibility(View.VISIBLE);
        bg.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                bg.getViewTreeObserver().removeOnPreDrawListener(this);
                bg.buildDrawingCache();
                lyricsView.setBackground(ImageUtils.createBlurredImageFromBitmap(bg.getDrawingCache(), NowPlayingActivity.this, 6));
                return true;
            }
        });
        final TextView poweredbyTextView = (TextView) lyricsView.findViewById(R.id.lyrics_makeitpersonal);
        poweredbyTextView.setVisibility(View.GONE);
        final TextView lyricsTextView = (TextView) lyricsView.findViewById(R.id.lyrics_text);
        lyricsTextView.setText("");
        String lyrics = null;
        String filename = getRealPathFromURI(Uri.parse(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/" + MusicPlayer.getCurrentAudioId()));
        if (filename != null) {
            lyrics = LyricsExtractor.getLyrics(new File(filename));
        }
        if (lyrics != null) {
            lyricsTextView.setText(lyrics);
        } else {
            String artist = MusicPlayer.getArtistName();
            if (artist != null) {
                int i = artist.lastIndexOf(" feat");
                if (i != -1) {
                    artist = artist.substring(0, i);
                }
                LyricsLoader.getInstance(this).getLyrics(artist, MusicPlayer.getTrackName(), new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        if (s.equals("Sorry, We don't have lyrics for this song yet.\n")) {
                            lyricsTextView.setText(R.string.no_lyrics);
                        } else {
                            lyricsTextView.setText(s);
                            poweredbyTextView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        lyricsTextView.setText(R.string.no_lyrics);
                    }
                });
            } else {
                lyricsTextView.setText(R.string.no_lyrics);
            }
        }
    }


    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void closeLyrics() {
        lyricsopened = false;
        final View lyricsView = findViewById(R.id.lyrics);
        lyricsView.setActivated(false);
        lyricsView.setVisibility(View.GONE);
    }

    @StyleRes
    @Override
    public int getActivityTheme() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false) ? R.style.AppTheme_FullScreen_Dark : R.style.AppTheme_FullScreen_Light;
    }

    @Override
    public int getLightToolbarMode() {
        return Config.LIGHT_TOOLBAR_AUTO;
    }

    @Override
    public int getToolbarColor() {
        return Color.TRANSPARENT;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PreferencesUtility.getInstance(this).didNowplayingThemeChanged()) {
            PreferencesUtility.getInstance(this).setNowPlayingThemeChanged(false);
            recreate();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_lyrics) {
            displayLyrics();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (lyricsopened)
            closeLyrics();
        else
            super.onBackPressed();
    }
}
