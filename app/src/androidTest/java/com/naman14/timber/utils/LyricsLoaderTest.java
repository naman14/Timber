package com.naman14.timber.utils;

import android.view.View;
import android.widget.TextView;

import com.naman14.timber.R;
import com.naman14.timber.subfragments.LyricsFragment;

import org.junit.Test;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LyricsLoaderTest {
    String lyrics = null;
    private View rootView;

    @Test
    public void getLyrics() {
        //Load lyrics from web (https://makeitpersonal.co)
        String[] authors = {"a-ha", "The Beatles", "Michael Jackson", "Journey", "Rick Astley"};
        String[] titles = {"Take on me", "A Hard Day's Night", "Beat It", "Don't Stop Believin'",
                "Never Gonna Give You Up"};

        for (int i = 0; i < 5; i++) {
            LyricsFragment frag = new LyricsFragment();
            final View lyricsView = rootView.findViewById(R.id.lyrics);
            final TextView poweredbyTextView = (TextView) lyricsView.findViewById(R.id.lyrics_makeitpersonal);
            final TextView lyricsTextView = (TextView) lyricsView.findViewById(R.id.lyrics_text);
            LyricsLoader.getInstance(frag.getContext()).getLyrics(authors[i], titles[i], new Callback<String>(){
                @Override
                public void success(String s, Response response) {
                    lyrics = s;
                    //If not found will display message
                    if (s.equals("Sorry, We don't have lyrics for this song yet.\n")) {
                        lyricsTextView.setText(R.string.no_lyrics);
                    } else {
                        //Lyrics found set to view
                        lyricsTextView.setText(s);
                        poweredbyTextView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    lyricsTextView.setText(R.string.no_lyrics);
                }


            });

        }
    }
}