package com.naman14.timber.fragments;

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

import com.naman14.timber.R;


/**
 * Created by naman on 13/06/15.
 */
public class PlaybackControlsFragment extends Fragment {


    private ImageButton mPlayPause;
    private TextView mTitle;
    private TextView mSubtitle;
    private TextView mExtraInfo;
    private ImageView mAlbumArt;
    private String mArtUrl;
    private ProgressBar mProgress;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playback_controls, container, false);

        mPlayPause = (ImageButton) rootView.findViewById(R.id.play_pause);
        mPlayPause.setEnabled(true);
        mPlayPause.setOnClickListener(mButtonListener);
        mProgress=(ProgressBar) rootView.findViewById(R.id.song_progress);


//        int color = 0xFF00FF00;
//        mProgress.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
//        mProgress.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);


        LinearLayout.LayoutParams layoutParams=(LinearLayout.LayoutParams)mProgress.getLayoutParams();
        mProgress.measure(0,0);
        layoutParams.setMargins(0,-(mProgress.getMeasuredHeight()/2),0,0);
        mProgress.setLayoutParams(layoutParams);
        mProgress.setScaleY(0.5f);

        mTitle = (TextView) rootView.findViewById(R.id.title);
        mSubtitle = (TextView) rootView.findViewById(R.id.artist);
        mAlbumArt = (ImageView) rootView.findViewById(R.id.album_art);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return rootView;
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

        }
    };


}
