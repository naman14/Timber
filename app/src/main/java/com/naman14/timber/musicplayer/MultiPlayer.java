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

public final class MultiPlayer implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private static final String TAG = "MultiPlayer";
    private final WeakReference<MusicService> mService;

    private MediaPlayer mCurrentMediaPlayer = new MediaPlayer();

    private MediaPlayer mNextMediaPlayer;

    private Handler mHandler;

    private boolean mIsInitialized = false;

    private String mNextMediaPath;

    public MultiPlayer(final MusicService service) {
        mService = new WeakReference<MusicService>(service);
        mCurrentMediaPlayer.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);

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
            }
            else if (path.startsWith("http://")) {
                player.setDataSource(mService.get(), Uri.parse(path));
            }
            else {
                FileDescriptor fileDescriptor = decryptAndGiveFileDescriptor(path);
                player.setDataSource(fileDescriptor);
            }
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);

            if (isForNextPlayer) {
                player.setOnPreparedListener(null);
                player.prepare();
            } else {
                player.setOnPreparedListener(this);
                player.prepareAsync();
            }

        } catch (final IOException todo) {
            return false;
        } catch (final IllegalArgumentException todo) {
            return false;
        }
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        return true;
    }

    public void setNextDataSource(final String path) {
        mNextMediaPath = null;
        try {
            mCurrentMediaPlayer.setNextMediaPlayer(null);
        } catch (IllegalArgumentException e) {
            Log.i(TAG, "Next media player is current one, continuing");
        } catch (IllegalStateException e) {
            Log.e(TAG, "Media player not initialized!");
            return;
        }
        if (mNextMediaPlayer != null) {
            mNextMediaPlayer.release();
            mNextMediaPlayer = null;
        }
        if (path == null) {
            return;
        }
        mNextMediaPlayer = new MediaPlayer();
        mNextMediaPlayer.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);
        mNextMediaPlayer.setAudioSessionId(getAudioSessionId());
        try {
            if (setDataSourceImpl(mNextMediaPlayer, path, true)) {
                mNextMediaPath = path;
                mCurrentMediaPlayer.setNextMediaPlayer(mNextMediaPlayer);
            } else {
                if (mNextMediaPlayer != null) {
                    mNextMediaPlayer.release();
                    mNextMediaPlayer = null;
                }
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
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
        return false;
    }

    @Override
    public void onCompletion(final MediaPlayer mp) {
        if (mp == mCurrentMediaPlayer && mNextMediaPlayer != null) {
            mCurrentMediaPlayer.release();
            mCurrentMediaPlayer = mNextMediaPlayer;
            mNextMediaPath = null;
            mNextMediaPlayer = null;
            mHandler.sendEmptyMessage(MusicService.TRACK_WENT_TO_NEXT);
        } else {
            mService.get().mWakeLock.acquire(30000);
            mHandler.sendEmptyMessage(MusicService.TRACK_ENDED);
            mHandler.sendEmptyMessage(RELEASE_WAKELOCK);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
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

        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public FileDescriptor decryptAndGiveFileDescriptor(String fileName) {
        try {

            // decryptAndGiveFileDescriptor the file
            byte[] decrypt = FileCrypto.decrypt(getAudioFileFromFileSystem(fileName));

            return getFileDescriptor(decrypt, fileName);

        }
        catch (Exception e) {
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

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return inarry;
    }
}