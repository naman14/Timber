package com.naman14.timber.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.naman14.timber.R;
import com.naman14.timber.adapters.SongsListAdapter;
import com.naman14.timber.dataloaders.LastAddedLoader;
import com.naman14.timber.dataloaders.PlaylistSongLoader;
import com.naman14.timber.dataloaders.SongLoader;
import com.naman14.timber.dataloaders.TopTracksLoader;
import com.naman14.timber.utils.Constants;
import com.naman14.timber.widgets.DividerItemDecoration;
import com.naman14.timber.widgets.FastScroller;

import java.util.HashMap;

/**
 * Created by naman on 16/08/15.
 */
public class PlaylistDetailActivity extends AppCompatActivity {

    private Activity mContext = PlaylistDetailActivity.this;
    String action;
    long playlistID;

    private SongsListAdapter mAdapter;
    private RecyclerView recyclerView;

    HashMap<String,Runnable> playlistsMap =new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);

        action=getIntent().getAction();

        playlistsMap.put(Constants.NAVIGATE_PLAYLIST_LASTADDED, playlistLastAdded);
        playlistsMap.put(Constants.NAVIGATE_PLAYLIST_RECENT, playlistRecents);
        playlistsMap.put(Constants.NAVIGATE_PLAYLIST_TOPTRACKS, playlistToptracks);
        playlistsMap.put(Constants.NAVIGATE_PLAYLIST_USERCREATED, playlistUsercreated);

        recyclerView=(RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FastScroller fastScroller=(FastScroller) findViewById(R.id.fastscroller);
        fastScroller.setRecyclerView(recyclerView);

        Runnable navigation = playlistsMap.get(action);
        if (navigation != null) {
            navigation.run();
        } else {
            Log.d("PlaylistDetail","mo action specified");
        }


    }

    Runnable playlistLastAdded = new Runnable() {
        public void run() {
            mAdapter = new SongsListAdapter(mContext, LastAddedLoader.getLastAddedSongs(mContext));
            recyclerView.setAdapter(mAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL_LIST,R.drawable.item_divider_black));
        }
    };

    Runnable playlistRecents =new Runnable() {
        @Override
        public void run() {
            TopTracksLoader loader=new TopTracksLoader(mContext, TopTracksLoader.QueryType.RecentSongs);
            mAdapter = new SongsListAdapter(mContext, SongLoader.getSongsForCursor(loader.getCursor()));
            recyclerView.setAdapter(mAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL_LIST,R.drawable.item_divider_black));
        }
    };
    Runnable playlistToptracks =new Runnable() {
        @Override
        public void run() {
            TopTracksLoader loader=new TopTracksLoader(mContext, TopTracksLoader.QueryType.TopTracks);
            mAdapter = new SongsListAdapter(mContext, SongLoader.getSongsForCursor(loader.getCursor()));
            recyclerView.setAdapter(mAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL_LIST,R.drawable.item_divider_black));
        }
    };
    Runnable playlistUsercreated =new Runnable() {
        @Override
        public void run() {
            playlistID=getIntent().getExtras().getLong(Constants.PLAYLIST_ID);
            mAdapter = new SongsListAdapter(mContext, PlaylistSongLoader.getSongsInPlaylist(mContext,playlistID));
            recyclerView.setAdapter(mAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL_LIST,R.drawable.item_divider_black));
        }
    };
}
