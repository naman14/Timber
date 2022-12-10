package com.naman14.timber.subfragments;

import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import me.wcy.lrcview.LrcView;

/**
 * Created by christoph on 10.12.16.
 */

public class LyricsFragment extends Fragment {

    private String lyrics = null;
    private Toolbar toolbar;
    private View rootView;
    private String syncLyrics = null;
    private LrcView syncLyricsView;
    private ActionBar actionBar;
    private long audioId = Long.MIN_VALUE;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_lyrics,container,false);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        syncLyricsView = (LrcView) rootView.findViewById(R.id.sync_lyrics);

        setupToolbar();

        loadLyrics();

        return rootView;
    }

    Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {

            long position = MusicPlayer.position();
            if (LyricsFragment.this.isResumed()) {
                long newAudioId = MusicPlayer.getCurrentAudioId();
                if (newAudioId != audioId) {
                    loadLyrics();
                }
                if (syncLyricsView.getVisibility() == View.VISIBLE) {
                    syncLyricsView.updateTime(position);
                }
                syncLyricsView.postDelayed(mUpdateProgress, 50);
            }
        }
    };

    private void loadLyrics() {

        final View lyricsView = rootView.findViewById(R.id.lyrics);
        final TextView poweredbyTextView = (TextView) lyricsView.findViewById(R.id.lyrics_makeitpersonal);
        final LrcView syncLyricsView = (LrcView) rootView.findViewById(R.id.sync_lyrics);
        poweredbyTextView.setVisibility(View.GONE);
        final TextView lyricsTextView = (TextView) lyricsView.findViewById(R.id.lyrics_text);
        lyricsTextView.setText(getString(R.string.lyrics_loading));

        if (MusicPlayer.getTrackName() != null) {
            actionBar.setTitle(MusicPlayer.getTrackName());
        } else {
            actionBar.setTitle(getString(R.string.app_name));
        }
        long newAudioId = MusicPlayer.getCurrentAudioId();
        if (newAudioId != audioId) {
            audioId = newAudioId;
            lyrics = null;
            syncLyrics = null;
        }
        String filename = getRealPathFromURI(Uri.parse(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/" + MusicPlayer.getCurrentAudioId()));
        if (filename != null && lyrics == null && syncLyrics == null) {
            File mediaFile = new File(filename);
            syncLyrics = LyricsExtractor.getSynchronizedLyrics(mediaFile);
            if (syncLyrics == null) {
                lyrics = LyricsExtractor.getLyrics(mediaFile);
            }
        }

        if (syncLyrics != null) {
            syncLyricsView.setVisibility(View.VISIBLE);
            lyricsView.setVisibility(View.GONE);

            syncLyricsView.loadLrc(syncLyrics);
        } else if (lyrics != null) {
            syncLyricsView.setVisibility(View.GONE);
            lyricsView.setVisibility(View.VISIBLE);

            lyricsTextView.setText(lyrics);
        } else {
            syncLyricsView.setVisibility(View.GONE);
            lyricsView.setVisibility(View.VISIBLE);

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

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        syncLyricsView.post(mUpdateProgress);
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
