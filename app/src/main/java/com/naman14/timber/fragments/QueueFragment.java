package com.naman14.timber.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.activities.BaseActivity;
import com.naman14.timber.adapters.PlayingQueueAdapter;
import com.naman14.timber.dataloaders.QueueLoader;
import com.naman14.timber.listeners.MusicStateListener;
import com.naman14.timber.models.Song;
import com.naman14.timber.widgets.DragSortRecycler;

/**
 * Created by naman on 25/08/15.
 */
public class QueueFragment extends Fragment implements MusicStateListener {

    private PlayingQueueAdapter mAdapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(
                R.layout.fragment_queue, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Playing Queue");

        recyclerView=(RecyclerView) rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(null);

        new loadQueueSongs().execute("");
        ((BaseActivity)getActivity()).setMusicStateListenerListener(this);

        return rootView;
    }

    public void restartLoader(){

    }

    public void onPlaylistChanged(){

    }

    public void onMetaChanged(){
        if (mAdapter!=null)
            mAdapter.notifyDataSetChanged();
    }

    private class loadQueueSongs extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            mAdapter = new PlayingQueueAdapter(getActivity(), QueueLoader.getQueueSongsList(getActivity()));
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            recyclerView.setAdapter(mAdapter);
            DragSortRecycler dragSortRecycler = new DragSortRecycler();
            dragSortRecycler.setViewHandleId(R.id.albumArt);

            dragSortRecycler.setOnItemMovedListener(new DragSortRecycler.OnItemMovedListener() {
                @Override
                public void onItemMoved(int from, int to) {
                    Log.d("queue", "onItemMoved " + from + " to " + to);
                    Song song= mAdapter.getSongAt(from);
                    mAdapter.removeSongAt(from);
                    mAdapter.addSongTo(to,song);
                    mAdapter.notifyDataSetChanged();
                    MusicPlayer.moveQueueItem(from, to);
                }
            });

            recyclerView.addItemDecoration(dragSortRecycler);
            recyclerView.addOnItemTouchListener(dragSortRecycler);
            recyclerView.setOnScrollListener(dragSortRecycler.getScrollListener());

        }

        @Override
        protected void onPreExecute() {}
    }

}

