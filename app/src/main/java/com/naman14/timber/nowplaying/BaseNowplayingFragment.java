package com.naman14.timber.nowplaying;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.adapters.BaseQueueAdapter;
import com.naman14.timber.dataloaders.QueueLoader;
import com.naman14.timber.utils.TimberUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

/**
 * Created by naman on 26/07/15.
 */
public class BaseNowplayingFragment extends Fragment {

    ImageView albumart;
    ImageView shuffle;
    MaterialIconView previous,next,playpause;
    TextView songtitle,songalbum,songartist,songduration,elapsedtime;
    SeekBar mProgress;

    RecyclerView recyclerView;
    BaseQueueAdapter mAdapter;

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

        recyclerView=(RecyclerView) view.findViewById(R.id.queue_recyclerview);

        setSongDetails();

    }

    private void setSongDetails(){
        ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(MusicPlayer.getCurrentAlbumId()).toString(), albumart,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnFail(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true)
                        .build());

        updatePlayPauseButton();

        songtitle.setText(MusicPlayer.getTrackName());

        if (songalbum!=null)
            songalbum.setText(MusicPlayer.getAlbumName());

        if (songartist!=null)
            songartist.setText(MusicPlayer.getArtistName());

        if (songduration!=null)
            songduration.setText(String.valueOf(MusicPlayer.duration()/1000));

        mProgress.setMax((int)MusicPlayer.duration());
        if (mUpdateProgress!=null){
            mProgress.removeCallbacks(mUpdateProgress);
        }
        mProgress.postDelayed(mUpdateProgress, 10);

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
                setSongDetails();
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicPlayer.previous(getActivity(),false);
                setSongDetails();
            }
        });
        playpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicPlayer.playOrPause();
                updatePlayPauseButton();
            }
        });

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            MusicPlayer.cycleShuffle();
            }
        });
    }

    public void setQueueSongs(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new BaseQueueAdapter(getActivity(), QueueLoader.getQueueSongsList(getActivity()));
        recyclerView.setAdapter(mAdapter);

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
}
