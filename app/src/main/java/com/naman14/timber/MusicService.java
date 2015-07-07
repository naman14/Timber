package com.naman14.timber;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;

/**
 * Created by naman on 27/06/15.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener {

    private static final String ACTION_PLAY = "PLAY";
    private static String mUrl;
    private static MusicService mInstance = null;

    private MediaPlayer mMediaPlayer = null;
    private int mBufferPosition;
    private static String mSongTitle;
    private static String mSongPicUrl;

    NotificationManager mNotificationManager;
    Notification mNotification = null;
    final int NOTIFICATION_ID = 1;


    // indicates the state our service:
    enum State {
        Retrieving,
        Stopped,
        Preparing,
        Playing,
        Paused

    };

    State mState = State.Retrieving;

    @Override
    public void onCreate() {
        mInstance = this;
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_PLAY)) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            initMediaPlayer();
        }
        return START_STICKY;
    }

    private void initMediaPlayer() {
        try {
            mMediaPlayer.setDataSource(mUrl);
        } catch (IllegalArgumentException e) {

        } catch (IllegalStateException e) {

        } catch (IOException e) {

        }

        try {
            mMediaPlayer.prepareAsync(); // prepare async to not block main thread
        } catch (IllegalStateException e) {

        }
        mState = State.Preparing;
    }

    public void restartMusic() {
        // Restart music
    }

    protected void setBufferPosition(int progress) {
        mBufferPosition = progress;
    }


    @Override
    public void onPrepared(MediaPlayer player) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        return false;
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        mState = State.Retrieving;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public void pauseMusic() {
        if (mState.equals(State.Playing)) {
            mMediaPlayer.pause();
            mState = State.Paused;
            updateNotification(mSongTitle + "(paused)");
        }
    }

    public void startMusic() {
        if (!mState.equals(State.Preparing) &&!mState.equals(State.Retrieving)) {
            mMediaPlayer.start();
            mState = State.Playing;
            updateNotification(mSongTitle + "(playing)");
        }
    }

    public boolean isPlaying() {
        if (mState.equals(State.Playing)) {
            return true;
        }
        return false;
    }

//    public int getMusicDuration() {
//        // Return current music duration
//    }
//
//    public int getCurrentPosition() {
//        // Return current position
//    }

    public int getBufferPercentage() {
        return mBufferPosition;
    }

    public void seekMusicTo(int pos) {
        // Seek music to pos
    }

    public static MusicService getInstance() {
        return mInstance;
    }

    public static void setSong(String url, String title, String songPicUrl) {
        mUrl = url;
        mSongTitle = title;
        mSongPicUrl = songPicUrl;
    }

    public String getSongTitle() {
        return mSongTitle;
    }

    public String getSongPicUrl() {
        return mSongPicUrl;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//        setBufferPosition(percent * getMusicDuration() / 100);
    }

    /** Updates the notification. */
    void updateNotification(String text) {
        // Notify NotificationManager of new intent
    }

    /**
     * Configures service as a foreground service. A foreground service is a service that's doing something the user is
     * actively aware of (such as playing music), and must appear to the user as a notification. That's why we create
     * the notification here.
     */
//    void setUpAsForeground(String text) {
//        PendingIntent pi =
//                PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MusicActivity.class),
//                        PendingIntent.FLAG_UPDATE_CURRENT);
//        mNotification = new Notification();
//        mNotification.tickerText = text;
//        mNotification.icon = R.drawable.ic_mshuffle_icon;
//        mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
//        mNotification.setLatestEventInfo(getApplicationContext(), getResources().getString(R.string.app_name), text, pi);
//        startForeground(NOTIFICATION_ID, mNotification);
//    }
}