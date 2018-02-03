package com.naman14.timber.subfragments;

import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.utils.LyricsExtractor;
import com.naman14.timber.utils.LyricsLoader;

import java.io.File;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by christoph on 10.12.16.
 */

public class LyricsFragment extends Fragment {

    private String lyrics = null;
    private Toolbar toolbar;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_lyrics,container,false);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        setupToolbar();

        loadLyrics();

        return rootView;
    }

    private void loadLyrics() {

        final View lyricsView = rootView.findViewById(R.id.lyrics);
        final TextView poweredbyTextView = (TextView) lyricsView.findViewById(R.id.lyrics_makeitpersonal);
        poweredbyTextView.setVisibility(View.GONE);
        final TextView lyricsTextView = (TextView) lyricsView.findViewById(R.id.lyrics_text);
        lyricsTextView.setText(getString(R.string.lyrics_loading));
        String filename = getRealPathFromURI(Uri.parse(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/" + MusicPlayer.getCurrentAudioId()));
        if (filename != null && lyrics == null) {
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

                LyricsLoader.getInstance(this.getContext()).getLyrics(artist, MusicPlayer.getTrackName(), new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        lyrics = s;
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

    private void setupToolbar() {

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        if (MusicPlayer.getTrackName() != null) {
            ab.setTitle(MusicPlayer.getTrackName());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setBackgroundColor(Color.TRANSPARENT);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        CursorLoader loader = new CursorLoader(this.getContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
}
