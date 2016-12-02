package com.naman14.timber.musicplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import com.naman14.timber.utils.FileCrypto;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import static com.naman14.timber.musicplayer.MusicService.RELEASE_WAKELOCK;
import static com.naman14.timber.musicplayer.MusicService.SERVER_DIED;

public final class MultiPlayer2 implements MediaPlayer.OnErrorListener {

    private static final String TAG = "MultiPlayer";
    private final WeakReference<MusicService> mService;

    private MediaPlayer mCurrentMediaPlayer;
    private MediaPlayer mMediaPlayer1;
    private MediaPlayer mMediaPlayer2;

    private StandbyPreparedListner standbyPreparedListner;
    private CurrentPlayerPreparedListner currentPlayerPreparedListner;

    private Handler mHandler;

    private boolean mIsInitialized = false;

    public MultiPlayer2(final MusicService service) {
        mService = new WeakReference<MusicService>(service);
        mMediaPlayer1 = new MediaPlayer();
        mMediaPlayer2 = new MediaPlayer();
        setCurrentMediaPlayer(mMediaPlayer1);
    }

    private void setCurrentMediaPlayer(MediaPlayer player) {
        mCurrentMediaPlayer = player;
        Log.d(TAG, "setCurrentMediaPlayer: " + player.toString() + "---" + mCurrentMediaPlayer.toString());
        mCurrentMediaPlayer.setOnPreparedListener(new CurrentPlayerPreparedListner());
        mCurrentMediaPlayer.setOnCompletionListener(new CurrentPlayerCompleteListner());
        if (isCurrentPlayerMediaPlayer1()) {
            mMediaPlayer2.setOnPreparedListener(standbyPreparedListner = new StandbyPreparedListner());
        } else {
            mMediaPlayer1.setOnPreparedListener(standbyPreparedListner = new StandbyPreparedListner());
        }
        mCurrentMediaPlayer.setOnErrorListener(this);
        mCurrentMediaPlayer.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);
    }

    private boolean isCurrentPlayerMediaPlayer1() {
        return mCurrentMediaPlayer == mMediaPlayer1;
    }

    public void setDataSource(final String path) {
        try {
            mIsInitialized = setDataSourceImpl(mCurrentMediaPlayer, path, false);
            if (mIsInitialized) {
                setNextDataSource(null);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private boolean setDataSourceImpl(final MediaPlayer player, final String path, boolean isForNextPlayer) {
        try {
            player.reset();
            if (path.startsWith("content://")) {
                player.setDataSource(mService.get(), Uri.parse(path));
            } else if (path.startsWith("http://")) {
                player.setDataSource(mService.get(), Uri.parse(path));
            } else {
                FileDescriptor fileDescriptor = decryptAndGiveFileDescriptor(path);
                player.setDataSource(fileDescriptor);
            }
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);

            if (isForNextPlayer) {
                player.setOnPreparedListener(standbyPreparedListner = new StandbyPreparedListner());
                player.setOnCompletionListener(null);
                player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        return true;
                    }
                });
                player.prepareAsync();
            } else {
                player.setOnPreparedListener(new CurrentPlayerPreparedListner());
                player.setOnErrorListener(this);
                player.setOnCompletionListener(new CurrentPlayerCompleteListner());
                player.prepareAsync();
            }

        } catch (final IOException todo) {
            return false;
        } catch (final IllegalArgumentException todo) {
            return false;
        }

        return true;
    }

    public void setNextDataSource(final String path) {
        if (path == null) {
            return;
        }
        if (isCurrentPlayerMediaPlayer1()) {
            mMediaPlayer2.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);
            setDataSourceImpl(mMediaPlayer2, path, true);
        } else {
            mMediaPlayer1.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);
            setDataSourceImpl(mMediaPlayer1, path, true);
        }
    }


    public void setHandler(final Handler handler) {
        mHandler = handler;
    }


    public boolean isInitialized() {
        return mIsInitialized;
    }


    public void start() {
        mCurrentMediaPlayer.start();
    }


    public void stop() {
        mCurrentMediaPlayer.reset();
        mIsInitialized = false;
    }


    public void release() {
        mCurrentMediaPlayer.release();
    }


    public void pause() {
        mCurrentMediaPlayer.pause();
    }


    public long duration() {
        return mCurrentMediaPlayer.getDuration();
    }


    public long position() {
        return mCurrentMediaPlayer.getCurrentPosition();
    }


    public long seek(final long whereto) {
        mCurrentMediaPlayer.seekTo((int) whereto);
        return whereto;
    }


    public void setVolume(final float vol) {
        try {
            mCurrentMediaPlayer.setVolume(vol, vol);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public int getAudioSessionId() {
        return mCurrentMediaPlayer.getAudioSessionId();
    }

    public void setAudioSessionId(final int sessionId) {
        mCurrentMediaPlayer.setAudioSessionId(sessionId);
    }

    @Override
    public boolean onError(final MediaPlayer mp, final int what, final int extra) {
        Log.w(TAG, "Music Server Error what: " + what + " extra: " + extra);
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                final MusicService service = mService.get();
                final TrackErrorInfo errorInfo = new TrackErrorInfo(service.getAudioId(),
                        service.getTrackName());

                mIsInitialized = false;
                mCurrentMediaPlayer.release();
                mCurrentMediaPlayer = new MediaPlayer();
                mCurrentMediaPlayer.setWakeMode(service, PowerManager.PARTIAL_WAKE_LOCK);
                Message msg = mHandler.obtainMessage(SERVER_DIED, errorInfo);
                mHandler.sendMessageDelayed(msg, 2000);
                return true;
            default:
                break;
        }
        return true;
    }

    private FileDescriptor getFileDescriptor(byte[] decryptedFileData, String fileName) {

        try {

            // create temp file that will hold byte array

            File tempMp3 = File.createTempFile("temp_" + fileName, "mp3", mService.get().getApplicationContext().getCacheDir());

            tempMp3.deleteOnExit();

            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(decryptedFileData);
            fos.close();

            FileInputStream fis = new FileInputStream(tempMp3);
            return fis.getFD();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public FileDescriptor decryptAndGiveFileDescriptor(String fileName) {
        try {

            // decryptAndGiveFileDescriptor the file
            byte[] decrypt = FileCrypto.decrypt(getAudioFileFromFileSystem(fileName));

            return getFileDescriptor(decrypt, fileName);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] getAudioFileFromFileSystem(String fileName) throws FileNotFoundException {

        byte[] inarry = null;

        try {

            File file = new File(mService.get().getApplicationContext().getExternalFilesDir(null), fileName);

            byte[] bFile = new byte[(int) file.length()];

            FileInputStream fileInputStream = new FileInputStream(file);

            fileInputStream.read(bFile);

            fileInputStream.close();

            inarry = bFile;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return inarry;
    }


    private static class CurrentPlayerPreparedListner implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            Log.d(TAG, "onPrepared: current player");
            mp.start();
        }
    }

    private static class StandbyPreparedListner implements MediaPlayer.OnPreparedListener {

        private boolean isPrepared = false;

        @Override
        public void onPrepared(MediaPlayer mp) {
            Log.d(TAG, "onPrepared: StandBy player");
            isPrepared = true;
        }

        public boolean isPrepared() {
            return isPrepared;
        }
    }

    private class CurrentPlayerCompleteListner implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.d(TAG, "onCompletion: Called");
            if (mp != mCurrentMediaPlayer || mCurrentMediaPlayer == null) {
                mService.get().mWakeLock.acquire(30000);
                mHandler.sendEmptyMessage(MusicService.TRACK_ENDED);
                mHandler.sendEmptyMessage(RELEASE_WAKELOCK);
                return;
            }
            boolean prepared = standbyPreparedListner.isPrepared();
            if (isCurrentPlayerMediaPlayer1()) {
                setCurrentMediaPlayer(mMediaPlayer2);
            } else {
                setCurrentMediaPlayer(mMediaPlayer1);
            }
            mHandler.sendEmptyMessage(MusicService.TRACK_WENT_TO_NEXT);
            if (prepared) {
                mCurrentMediaPlayer.start();
            }
        }
    }

    private class StandByErrorListner implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return true;
        }
    }


}