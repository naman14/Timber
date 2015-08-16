package com.naman14.timber.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.naman14.timber.R;
import com.naman14.timber.utils.Constants;

/**
 * Created by naman on 16/08/15.
 */
public class PlaylistDetailActivity extends AppCompatActivity {

    String action;
    long playlistID;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);

        action=getIntent().getAction();

        playlistID=getIntent().getExtras().getLong(Constants.PLAYLIST_ID);

    }
}
