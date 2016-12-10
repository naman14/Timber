package com.naman14.timber.subfragments;

import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
    View view;
    Drawable bg;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lyrics,container,false);
        final View lyricsView = root.findViewById(R.id.lyrics);
        lyricsView.setActivated(true);
        lyricsView.setVisibility(View.VISIBLE);

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
                LyricsLoader.getInstance(this.getContext()).getLyrics(artist, MusicPlayer.getTrackName(), new Callback<String>() {
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
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.view = view;
        if(bg!=null)
        view.setBackground(bg);
    }


    public void setBackground(Drawable drawable){
        if(view!=null)
            view.setBackground(drawable);
        else
            bg = drawable;
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
