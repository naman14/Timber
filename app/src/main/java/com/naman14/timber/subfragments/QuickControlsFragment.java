package com.naman14.timber.subfragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.activities.BaseActivity;
import com.naman14.timber.listeners.MusicStateListener;
import com.naman14.timber.utils.TimberUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


/**
 * Created by naman on 13/06/15.
 */
public class QuickControlsFragment extends Fragment implements MusicStateListener {


    private ImageButton mPlayPause;
    private TextView mTitle;
    private TextView mArtist;
    private TextView mExtraInfo;
    private ImageView mAlbumArt;
    private String mArtUrl;
    private static ProgressBar mProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playback_controls, container, false);

        mPlayPause = (ImageButton) rootView.findViewById(R.id.play_pause);
        mPlayPause.setEnabled(true);
        mPlayPause.setOnClickListener(mButtonListener);
        mProgress=(ProgressBar) rootView.findViewById(R.id.song_progress);
        mTitle=(TextView) rootView.findViewById(R.id.title);
        mArtist=(TextView) rootView.findViewById(R.id.artist);
        mAlbumArt = (ImageView) rootView.findViewById(R.id.album_art);


//        int color = 0xFF00FF00;
//        mProgress.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
//        mProgress.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);


        LinearLayout.LayoutParams layoutParams=(LinearLayout.LayoutParams)mProgress.getLayoutParams();
        mProgress.measure(0,0);
        layoutParams.setMargins(0,-(mProgress.getMeasuredHeight()/2),0,0);
        mProgress.setLayoutParams(layoutParams);
        mProgress.setScaleY(0.5f);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ((BaseActivity)getActivity()).setMusicStateListenerListener(this);

        return rootView;
    }

    public void updateControlsFragment() {
        mTitle.setText(MusicPlayer.getTrackName());
        mArtist.setText(MusicPlayer.getArtistName());
        ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(MusicPlayer.getCurrentAlbumId()).toString(), mAlbumArt,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .showImageOnFail(R.drawable.ic_empty_music2)
                        .resetViewBeforeLoading(true)
                        .build());
       updateProgressbar();

    }
    public static void updateProgressbar(){
        mProgress.setMax((int)MusicPlayer.duration());
//        ObjectAnimator animation = ObjectAnimator.ofInt(mProgress, "progress", (int)MusicPlayer.position());
//        animation.setDuration(500);
//        animation.setInterpolator(new LinearInterpolator());
//        animation.start();
        if (mUpdateProgress!=null){
            mProgress.removeCallbacks(mUpdateProgress);
        }
        mProgress.postDelayed(mUpdateProgress, 10);
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }


    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MusicPlayer.playOrPause();
            updateState();
        }
    };

    public void updateState() {
        if (MusicPlayer.isPlaying()) {
            mPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.btn_playback_pause));
        } else {
            mPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.btn_playback_play));
        }
    }

    public void restartLoader(){

    }

    public void onPlaylistChanged(){

    }

    public void onMetaChanged(){
        updateControlsFragment();
        updateState();
    }

    public static Runnable mUpdateProgress=new Runnable() {

        @Override
        public void run() {

            if(mProgress != null) {
                mProgress.setProgress((int)MusicPlayer.position());
            }

            if(MusicPlayer.isPlaying()) {
                mProgress.postDelayed(mUpdateProgress, 50);
            }

        }
    };


}
