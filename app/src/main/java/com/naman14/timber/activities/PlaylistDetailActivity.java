package com.naman14.timber.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.naman14.timber.R;
import com.naman14.timber.dataloaders.SongLoader;
import com.naman14.timber.dataloaders.TopTracksLoader;
import com.naman14.timber.models.Song;
import com.naman14.timber.utils.TimberUtils;

import java.util.List;

/**
 * Created by naman on 16/08/15.
 */
public class PlaylistDetailActivity extends AppCompatActivity {

    String action;

    protected TimberUtils.PlaylistType getSmartPlaylistType() {
        return TimberUtils.PlaylistType.TopTracks;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);

        action=getIntent().getAction();

        TopTracksLoader loader=new TopTracksLoader(this, TopTracksLoader.QueryType.TopTracks);
        List<Song> songList= SongLoader.getSongsForCursor(loader.getCursor());
        for (int i=0;i<songList.size();i++){
            Log.d("playlist",songList.get(i).title);
        }

    }
}
