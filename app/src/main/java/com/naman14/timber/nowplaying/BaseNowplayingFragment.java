package com.naman14.timber.nowplaying;

import android.graphics.PorterDuff;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.activities.BaseActivity;
import com.naman14.timber.adapters.BaseQueueAdapter;
import com.naman14.timber.dataloaders.QueueLoader;
import com.naman14.timber.listeners.MusicStateListener;
import com.naman14.timber.utils.TimberUtils;
import com.naman14.timber.widgets.DividerItemDecoration;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

/**
 * Created by naman on 26/07/15.
 */
public class BaseNowplayingFragment extends Fragment implements MusicStateListener {

    ImageView albumart;
    ImageView shuffle;
    MaterialIconView previous,next,playpause;
    TextView songtitle,songalbum,songartist,songduration,elapsedtime;
    SeekBar mProgress;
    ProgressBar mProgressNormal;

    RecyclerView recyclerView;
    BaseQueueAdapter mAdapter;

    private boolean duetoplaypause=false;

    public void setSongDetails(View view){

        albumart=(ImageView) view.findViewById(R.id.album_art);
        shuffle=(ImageView) view.findViewById(R.id.shuffle);
        next=(MaterialIconView) view.findViewById(R.id.next);
        previous=(MaterialIconView) view.findViewById(R.id.previous);
        playpause=(MaterialIconView) view.findViewById(R.id.playpause);

        songtitle=(TextView) view.findViewById(R.id.song_title);
        songalbum=(TextView) view.findViewById(R.id.song_album);
        songartist=(TextView) view.findViewById(R.id.song_artist);
        songduration=(TextView) view.findViewById(R.id.song_duration);
        elapsedtime=(TextView) view.findViewById(R.id.song_elapsed_time);

        mProgress=(SeekBar) view.findViewById(R.id.song_progress);
        mProgressNormal=(ProgressBar) view.findViewById(R.id.song_progress_normal);

        recyclerView=(RecyclerView) view.findViewById(R.id.queue_recyclerview);

        Toolbar toolbar=(Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar!=null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("");
        }
        setSongDetails();

    }

    private void setSongDetails(){
        updateSongDetails();
        mProgress.getThumb().setColorFilter(getActivity().getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);

        if (recyclerView!=null)
        setQueueSongs(recyclerView);

        mProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b){
                    MusicPlayer.seek((long)i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicPlayer.next();
//                updateSongDetails();
                notifyPlayingDrawableChange();
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicPlayer.previous(getActivity(),false);
//                updateSongDetails();
                notifyPlayingDrawableChange();
            }
        });
        playpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                duetoplaypause=true;
                MusicPlayer.playOrPause();
//                updatePlayPauseButton();
                if (recyclerView!=null)
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
        if (shuffle!=null) {
            shuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MusicPlayer.cycleShuffle();
                }
            });
        }
    }

    public void updateSongDetails(){
        //do not reload image if it was a play/pause change
        if (!duetoplaypause) {
            ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(MusicPlayer.getCurrentAlbumId()).toString(), albumart,
                    new DisplayImageOptions.Builder().cacheInMemory(true)
                            .showImageOnFail(R.drawable.ic_empty_music2)
                            .resetViewBeforeLoading(true)
                            .displayer(new FadeInBitmapDisplayer(400))
                            .build());
        }
        duetoplaypause=false;
        updatePlayPauseButton();

        songtitle.setText(MusicPlayer.getTrackName());

        if (songalbum!=null)
            songalbum.setText(MusicPlayer.getAlbumName());

        if (songartist!=null)
            songartist.setText(MusicPlayer.getArtistName());

        if (songduration!=null)
            songduration.setText(String.valueOf(MusicPlayer.duration()/1000));

        if (mProgress!=null) {
            mProgress.setMax((int) MusicPlayer.duration());
            if (mUpdateProgress != null) {
                mProgress.removeCallbacks(mUpdateProgress);
            }
            mProgress.postDelayed(mUpdateProgress, 10);
        }

        if (mProgressNormal!=null) {
            mProgressNormal.setMax((int) MusicPlayer.duration());
            if (mUpdateProgressNormal != null) {
                mProgressNormal.removeCallbacks(mUpdateProgressNormal);
            }
            mProgressNormal.postDelayed(mUpdateProgressNormal, 10);
        }
    }

    public void setQueueSongs(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new BaseQueueAdapter(getActivity(), QueueLoader.getQueueSongsList(getActivity()));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST,R.drawable.item_divider_white));
        recyclerView.scrollToPosition(MusicPlayer.getQueuePosition());

    }

    public void updatePlayPauseButton(){
        if (MusicPlayer.isPlaying())
            playpause.setIcon(MaterialDrawableBuilder.IconValue.PAUSE);
        else playpause.setIcon(MaterialDrawableBuilder.IconValue.PLAY);
    }

    public Runnable mUpdateProgress=new Runnable() {

        @Override
        public void run() {

            if(mProgress != null) {
                long position=MusicPlayer.position();
                mProgress.setProgress((int)position);
                if (elapsedtime!=null)
                elapsedtime.setText(String.valueOf(position/1000));
            }

            if(MusicPlayer.isPlaying()) {
                mProgress.postDelayed(mUpdateProgress, 50);
            }

        }
    };
    public Runnable mUpdateProgressNormal=new Runnable() {

        @Override
        public void run() {

            if(mProgressNormal != null) {
                long position=MusicPlayer.position();
                mProgressNormal.setProgress((int)position);
            }

            if(MusicPlayer.isPlaying()) {
                mProgressNormal.postDelayed(mUpdateProgressNormal, 50);
            }

        }
    };

    public void notifyPlayingDrawableChange(){
        int position =MusicPlayer.getQueuePosition();
        BaseQueueAdapter.currentlyPlayingPosition=position;
//        recyclerView.smoothScrollToPosition(MusicPlayer.getQueuePosition());
    }

    public void restartLoader(){

    }

    public void onPlaylistChanged(){

    }

    public void onMetaChanged(){
        updateSongDetails();
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public void setMusicStateListener(){
        ((BaseActivity) getActivity()).setMusicStateListenerListener(this);
    }
}
