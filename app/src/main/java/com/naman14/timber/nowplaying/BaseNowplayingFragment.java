package com.naman14.timber.nowplaying;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.adnansm.timelytextview.TimelyView;
import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.activities.BaseActivity;
import com.naman14.timber.adapters.BaseQueueAdapter;
import com.naman14.timber.dataloaders.QueueLoader;
import com.naman14.timber.listeners.MusicStateListener;
import com.naman14.timber.utils.PreferencesUtility;
import com.naman14.timber.utils.TimberUtils;
import com.naman14.timber.widgets.CircularSeekBar;
import com.naman14.timber.widgets.DividerItemDecoration;
import com.naman14.timber.widgets.PlayPauseButton;
import com.naman14.timber.widgets.PlayPauseDrawable;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

import java.security.InvalidParameterException;

/**
 * Created by naman on 26/07/15.
 */
public class BaseNowplayingFragment extends Fragment implements MusicStateListener {

    ImageView albumart;
    ImageView shuffle;
    ImageView repeat;
    MaterialIconView previous, next;
    PlayPauseButton mPlayPause;
    PlayPauseDrawable playPauseDrawable = new PlayPauseDrawable();
    FloatingActionButton playPauseFloating;
    View playPauseWrapper;

    TextView songtitle, songalbum, songartist, songduration, elapsedtime;
    SeekBar mProgress;
    CircularSeekBar mCircularProgress;
    ProgressBar mProgressNormal;

    RecyclerView recyclerView;
    BaseQueueAdapter mAdapter;

    TimelyView timelyView11, timelyView12, timelyView13, timelyView14;
    int[] timeArr = new int[]{0, 0, 0, 0};
    Handler mElapsedTimeHandler;

    private boolean duetoplaypause = false;

    public void setSongDetails(View view) {

        albumart = (ImageView) view.findViewById(R.id.album_art);
        shuffle = (ImageView) view.findViewById(R.id.shuffle);
        repeat = (ImageView) view.findViewById(R.id.repeat);
        next = (MaterialIconView) view.findViewById(R.id.next);
        previous = (MaterialIconView) view.findViewById(R.id.previous);
        mPlayPause = (PlayPauseButton) view.findViewById(R.id.playpause);
        playPauseFloating = (FloatingActionButton) view.findViewById(R.id.playpausefloating);
        playPauseWrapper = view.findViewById(R.id.playpausewrapper);

        songtitle = (TextView) view.findViewById(R.id.song_title);
        songalbum = (TextView) view.findViewById(R.id.song_album);
        songartist = (TextView) view.findViewById(R.id.song_artist);
        songduration = (TextView) view.findViewById(R.id.song_duration);
        elapsedtime = (TextView) view.findViewById(R.id.song_elapsed_time);

        timelyView11 = (TimelyView) view.findViewById(R.id.timelyView11);
        timelyView12 = (TimelyView) view.findViewById(R.id.timelyView12);
        timelyView13 = (TimelyView) view.findViewById(R.id.timelyView13);
        timelyView14 = (TimelyView) view.findViewById(R.id.timelyView14);

        mProgress = (SeekBar) view.findViewById(R.id.song_progress);
        mCircularProgress = (CircularSeekBar) view.findViewById(R.id.song_progress_circular);
        mProgressNormal = (ProgressBar) view.findViewById(R.id.song_progress_normal);

        recyclerView = (RecyclerView) view.findViewById(R.id.queue_recyclerview);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("");
        }
        if (mPlayPause != null && getActivity() != null)
            mPlayPause.setColor(getActivity().getResources().getColor(android.R.color.white));

        if (playPauseFloating != null) {
            playPauseFloating.setImageDrawable(playPauseDrawable);
            if (MusicPlayer.isPlaying())
                playPauseDrawable.transformToPause(false);
            else playPauseDrawable.transformToPlay(false);
        }

        if (timelyView11!=null) {
            String time = TimberUtils.makeShortTimeString(getActivity(), MusicPlayer.position() / 1000);
            if (time.length() > 4) {
                changeDigit(timelyView11, time.charAt(0) - '0');
                changeDigit(timelyView12, time.charAt(1) - '0');
                changeDigit(timelyView13, time.charAt(3) - '0');
                changeDigit(timelyView14, time.charAt(4) - '0');
            } else {
                timelyView11.setVisibility(View.GONE);
                changeDigit(timelyView12, time.charAt(0) - '0');
                changeDigit(timelyView13, time.charAt(2) - '0');
                changeDigit(timelyView14, time.charAt(3) - '0');
            }
        }

        setSongDetails();

    }

    private void setSongDetails() {
        updateSongDetails();

        if (mProgress != null && getActivity() != null) {
            if (isThemeIsLight()) {
                mProgress.getThumb().setColorFilter(getActivity().getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
            } else {
                mProgress.getThumb().setColorFilter(getActivity().getResources().getColor(R.color.colorAccentDarkTheme), PorterDuff.Mode.SRC_IN);
            }
        }
        if (recyclerView != null)
            setQueueSongs();

        setSeekBarListener();

        if (next != null) {
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MusicPlayer.next();
//                updateSongDetails();
                            notifyPlayingDrawableChange();
                        }
                    }, 200);

                }
            });
        }
        if (previous != null) {
            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MusicPlayer.previous(getActivity(), false);
//                updateSongDetails();
                            notifyPlayingDrawableChange();
                        }
                    }, 200);

                }
            });
        }

        if (playPauseWrapper != null)
            playPauseWrapper.setOnClickListener(mButtonListener);

        if (playPauseFloating != null)
            playPauseFloating.setOnClickListener(mFLoatingButtonListener);

        updateShuffleState();
        updateRepeatState();

    }

    public void updateShuffleState() {
        if (shuffle != null && getActivity() != null) {
            MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(getActivity())
                    .setIcon(MaterialDrawableBuilder.IconValue.SHUFFLE)
                    .setSizeDp(30);
            TypedValue typeValue = new TypedValue();

            getActivity().getTheme().resolveAttribute(R.attr.iconColor, typeValue, true);
            int color = typeValue.data;

            getActivity().getTheme().resolveAttribute(R.attr.accentColor, typeValue, true);
            int color2 = typeValue.data;

            if (MusicPlayer.getShuffleMode() == 0) {
                builder.setColor(color);
            } else builder.setColor(color2);

            shuffle.setImageDrawable(builder.build());
            shuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MusicPlayer.cycleShuffle();
                    updateShuffleState();
                    updateRepeatState();
                }
            });
        }
    }

    private void updateRepeatState() {
        if (repeat != null && getActivity() != null) {
            MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(getActivity())
                    .setIcon(MaterialDrawableBuilder.IconValue.REPEAT)
                    .setSizeDp(30);
            TypedValue typeValue = new TypedValue();

            getActivity().getTheme().resolveAttribute(R.attr.iconColor, typeValue, true);
            int color = typeValue.data;

            getActivity().getTheme().resolveAttribute(R.attr.accentColor, typeValue, true);
            int color2 = typeValue.data;

            if (MusicPlayer.getRepeatMode() == 0) {
                builder.setColor(color);
            } else builder.setColor(color2);

            repeat.setImageDrawable(builder.build());
            repeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MusicPlayer.cycleRepeat();
                    updateRepeatState();
                    updateShuffleState();
                }
            });
        }
    }

    private void setSeekBarListener() {
        if (mProgress != null)
            mProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (b) {
                        MusicPlayer.seek((long) i);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        if (mCircularProgress != null) {
            mCircularProgress.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
                @Override
                public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        MusicPlayer.seek((long) progress);
                    }
                }

                @Override
                public void onStopTrackingTouch(CircularSeekBar seekBar) {

                }

                @Override
                public void onStartTrackingTouch(CircularSeekBar seekBar) {

                }
            });
        }
    }

    public void updateSongDetails() {
        //do not reload image if it was a play/pause change
        if (!duetoplaypause) {
            if (albumart != null) {
                ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(MusicPlayer.getCurrentAlbumId()).toString(), albumart,
                        new DisplayImageOptions.Builder().cacheInMemory(true)
                                .showImageOnFail(R.drawable.ic_empty_music2)
                                .build(), new SimpleImageLoadingListener() {

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                doAlbumArtStuff(loadedImage);
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                            }

                        });
            }
        }
        duetoplaypause = false;

        if (mPlayPause != null)
            updatePlayPauseButton();

        if (playPauseFloating != null)
            updatePlayPauseFloatingButton();


        if (songtitle != null)
            songtitle.setText(MusicPlayer.getTrackName());

        if (songalbum != null)
            songalbum.setText(MusicPlayer.getAlbumName());

        if (songartist != null)
            songartist.setText(MusicPlayer.getArtistName());

        if (songduration != null)
            songduration.setText(String.valueOf(MusicPlayer.duration() / 1000));

        if (mProgress != null) {
            mProgress.setMax((int) MusicPlayer.duration());
            if (mUpdateProgress != null) {
                mProgress.removeCallbacks(mUpdateProgress);
            }
            mProgress.postDelayed(mUpdateProgress, 10);
        }
        if (mCircularProgress != null) {
            mCircularProgress.setMax((int) MusicPlayer.duration());
            if (mUpdateCircularProgress != null) {
                mCircularProgress.removeCallbacks(mUpdateCircularProgress);
            }
            mCircularProgress.postDelayed(mUpdateCircularProgress, 10);
        }

        if (mProgressNormal != null) {
            mProgressNormal.setMax((int) MusicPlayer.duration());
            if (mUpdateProgressNormal != null) {
                mProgressNormal.removeCallbacks(mUpdateProgressNormal);
            }
            mProgressNormal.postDelayed(mUpdateProgressNormal, 10);
        }
        if (timelyView11 != null) {
            mElapsedTimeHandler = new Handler();
            mElapsedTimeHandler.postDelayed(mUpdateElapsedTime, 600);
        }
    }

    public void setQueueSongs() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //load queue songs in asynctask
        new loadQueueSongs().execute("");

    }

    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            duetoplaypause = true;
            ;
            if (!mPlayPause.isPlayed()) {
                mPlayPause.setPlayed(true);
                mPlayPause.startAnimation();
            } else {
                mPlayPause.setPlayed(false);
                mPlayPause.startAnimation();
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.playOrPause();
                    if (recyclerView != null && recyclerView.getAdapter() != null)
                        recyclerView.getAdapter().notifyDataSetChanged();
                }
            }, 200);


        }
    };
    private final View.OnClickListener mFLoatingButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            duetoplaypause = true;
            playPauseDrawable.transformToPlay(true);
            playPauseDrawable.transformToPause(true);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.playOrPause();
                    if (recyclerView != null && recyclerView.getAdapter() != null)
                        recyclerView.getAdapter().notifyDataSetChanged();
                }
            }, 250);


        }
    };

    public void updatePlayPauseButton() {
        if (MusicPlayer.isPlaying()) {
            if (!mPlayPause.isPlayed()) {
                mPlayPause.setPlayed(true);
                mPlayPause.startAnimation();
            }
        } else {
            if (mPlayPause.isPlayed()) {
                mPlayPause.setPlayed(false);
                mPlayPause.startAnimation();
            }
        }
    }

    public void updatePlayPauseFloatingButton() {
        if (MusicPlayer.isPlaying()) {
            playPauseDrawable.transformToPause(false);
        } else {
            playPauseDrawable.transformToPlay(false);
        }
    }

    //seekbar
    public Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {

            if (mProgress != null) {
                long position = MusicPlayer.position();
                mProgress.setProgress((int) position);
                if (elapsedtime != null && getActivity()!=null)
                    elapsedtime.setText(TimberUtils.makeShortTimeString(getActivity(), position / 1000));
            }

            if (MusicPlayer.isPlaying()) {
                mProgress.postDelayed(mUpdateProgress, 50);
            }

        }
    };
    //circular seekbar
    public Runnable mUpdateCircularProgress = new Runnable() {

        @Override
        public void run() {

            if (mCircularProgress != null) {
                long position = MusicPlayer.position();
                mCircularProgress.setProgress((int) position);
                if (elapsedtime != null && getActivity()!=null)
                    elapsedtime.setText(TimberUtils.makeShortTimeString(getActivity(), position / 1000));

            }

            if (MusicPlayer.isPlaying()) {
                mCircularProgress.postDelayed(mUpdateCircularProgress, 50);
            }

        }
    };
    //normal progressbar
    public Runnable mUpdateProgressNormal = new Runnable() {

        @Override
        public void run() {

            if (mProgressNormal != null) {
                long position = MusicPlayer.position();
                mProgressNormal.setProgress((int) position);
            }

            if (MusicPlayer.isPlaying()) {
                mProgressNormal.postDelayed(mUpdateProgressNormal, 50);
            }

        }
    };

    public Runnable mUpdateElapsedTime = new Runnable() {
        @Override
        public void run() {
            if (getActivity()!=null) {
                String time = TimberUtils.makeShortTimeString(getActivity(), MusicPlayer.position() / 1000);
                if (time.length() > 4) {
                    tv11(time.charAt(0) - '0');
                    tv12(time.charAt(1) - '0');
                    tv13(time.charAt(3) - '0');
                    tv14(time.charAt(4) - '0');
                } else {
                    tv12(time.charAt(0) - '0');
                    tv13(time.charAt(2) - '0');
                    tv14(time.charAt(3) - '0');
                }
                mElapsedTimeHandler.postDelayed(this, 600);
            }

        }
    };

    public void notifyPlayingDrawableChange() {
        int position = MusicPlayer.getQueuePosition();
        BaseQueueAdapter.currentlyPlayingPosition = position;
    }

    private class loadQueueSongs extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            mAdapter = new BaseQueueAdapter(getActivity(), QueueLoader.getQueueSongsList(getActivity()));
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            recyclerView.setAdapter(mAdapter);
            if (getActivity() != null)
                recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
            recyclerView.scrollToPosition(MusicPlayer.getQueuePosition());

        }

        @Override
        protected void onPreExecute() {
        }
    }


    public void restartLoader() {

    }

    public void onPlaylistChanged() {

    }

    public void onMetaChanged() {
        updateSongDetails();

        if (recyclerView != null && recyclerView.getAdapter() != null)
            recyclerView.getAdapter().notifyDataSetChanged();
    }

    public void setMusicStateListener() {
        ((BaseActivity) getActivity()).setMusicStateListenerListener(this);
    }


    public void doAlbumArtStuff(Bitmap loadedImage) {

    }

    public boolean isThemeIsLight() {
        if (getActivity() != null)
            return PreferencesUtility.getInstance(getActivity()).getTheme().equals("light");
        else return true;
    }

    public void changeDigit(TimelyView tv, int end) {
        ObjectAnimator obja = tv.animate(end);
        obja.setDuration(400);
        obja.start();
    }

    public void changeDigit(TimelyView tv, int start, int end) {
        try {
            ObjectAnimator obja = tv.animate(start, end);
            obja.setDuration(400);
            obja.start();
        } catch (InvalidParameterException e) {
            e.printStackTrace();
        }
    }

    public void tv11(int a) {
        if (a != timeArr[0]) {
            changeDigit(timelyView11, timeArr[0], a);
            timeArr[0] = a;
        }
    }

    public void tv12(int a) {
        if (a != timeArr[1]) {
            changeDigit(timelyView12, timeArr[1], a);
            timeArr[1] = a;
        }
    }

    public void tv13(int a) {
        if (a != timeArr[2]) {
            changeDigit(timelyView13, timeArr[2], a);
            timeArr[2] = a;
        }
    }

    public void tv14(int a) {
        if (a != timeArr[3]) {
            changeDigit(timelyView14, timeArr[3], a);
            timeArr[3] = a;
        }
    }
}
