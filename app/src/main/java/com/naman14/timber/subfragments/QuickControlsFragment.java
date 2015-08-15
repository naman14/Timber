package com.naman14.timber.subfragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.activities.BaseActivity;
import com.naman14.timber.listeners.MusicStateListener;
import com.naman14.timber.nowplaying.BaseNowplayingFragment;
import com.naman14.timber.utils.ImageUtils;
import com.naman14.timber.utils.TimberUtils;
import com.naman14.timber.widgets.PlayPauseButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;


/**
 * Created by naman on 13/06/15.
 */
public class QuickControlsFragment extends BaseNowplayingFragment implements MusicStateListener {


    private PlayPauseButton mPlayPause;
    private TextView mTitle;
    private TextView mArtist;
    private TextView mExtraInfo;
    private ImageView mAlbumArt,mBlurredArt;
    private String mArtUrl;
    private static ProgressBar mProgress;
    public static View topContainer;
    private View rootView;
    private View playPauseWrapper;

    private boolean duetoplaypause=false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playback_controls, container, false);
        this.rootView=rootView;

        mPlayPause = (PlayPauseButton) rootView.findViewById(R.id.play_pause);
        playPauseWrapper=rootView.findViewById(R.id.play_pause_wrapper);
        mPlayPause.setEnabled(true);
        playPauseWrapper.setOnClickListener(mButtonListener);
        mProgress=(ProgressBar) rootView.findViewById(R.id.song_progress_normal);
        mTitle=(TextView) rootView.findViewById(R.id.title);
        mArtist=(TextView) rootView.findViewById(R.id.artist);
        mAlbumArt = (ImageView) rootView.findViewById(R.id.album_art_nowplayingcard);
        mBlurredArt=(ImageView) rootView.findViewById(R.id.blurredAlbumart);
        topContainer=rootView.findViewById(R.id.topContainer);

        LinearLayout.LayoutParams layoutParams=(LinearLayout.LayoutParams)mProgress.getLayoutParams();
        mProgress.measure(0,0);
        layoutParams.setMargins(0,-(mProgress.getMeasuredHeight()/2),0,0);
        mProgress.setLayoutParams(layoutParams);
        mProgress.setScaleY(0.5f);

        mPlayPause.setColor(getActivity().getResources().getColor(R.color.colorPrimary));

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ((BaseActivity)getActivity()).setMusicStateListenerListener(this);


        return rootView;
    }

    public void updateControlsFragment() {
        //let basenowplayingfragment take care of this
        setSongDetails(rootView);

    }

    //to update the permanent now playing card at the bottom
    public void updateNowplayingCard(){
        mTitle.setText(MusicPlayer.getTrackName());
        mArtist.setText(MusicPlayer.getArtistName());
        if (!duetoplaypause) {
            ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(MusicPlayer.getCurrentAlbumId()).toString(), mAlbumArt,
                    new DisplayImageOptions.Builder().cacheInMemory(true)
                            .showImageOnFail(R.drawable.ic_empty_music2)
                            .resetViewBeforeLoading(true)
                            .build(), new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            if (getActivity() != null) {
                                mBlurredArt.setImageDrawable(ImageUtils.createBlurredImageFromBitmap(loadedImage, getActivity(), 6));
                            }
                        }
                    });
        }
        duetoplaypause=false;
    }



    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onResume() {
        super.onResume();
        topContainer=rootView.findViewById(R.id.topContainer);

    }


    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            duetoplaypause=true;;
            if (!mPlayPause.isPlayed()) {
                mPlayPause.setPlayed(true);
                mPlayPause.startAnimation();
            }
            else {
                mPlayPause.setPlayed(false);
                mPlayPause.startAnimation();
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.playOrPause();
                }
            },150);

        }
    };

    public void updateState() {
        if (MusicPlayer.isPlaying()) {
            if (!mPlayPause.isPlayed()) {
                mPlayPause.setPlayed(true);
                mPlayPause.startAnimation();
            }
//            mPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.btn_playback_pause));
        } else {
            if (mPlayPause.isPlayed()) {
                mPlayPause.setPlayed(false);
                mPlayPause.startAnimation();
            }
//            mPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.btn_playback_play));
        }
    }

    public void restartLoader(){

    }

    public void onPlaylistChanged(){

    }

    public void onMetaChanged(){
        //only update nowplayingcard,quick controls will be updated by basenowplayingfragment's onMetaChanged
            updateNowplayingCard();
            updateState();
            //TODO
            updateControlsFragment();
    }


}
