package com.naman14.timber.musicplayer;

import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

import static com.naman14.timber.musicplayer.MusicService.FADEDOWN;
import static com.naman14.timber.musicplayer.MusicService.FADEUP;
import static com.naman14.timber.musicplayer.MusicService.FOCUSCHANGE;
import static com.naman14.timber.musicplayer.MusicService.META_CHANGED;
import static com.naman14.timber.musicplayer.MusicService.RELEASE_WAKELOCK;
import static com.naman14.timber.musicplayer.MusicService.REPEAT_CURRENT;
import static com.naman14.timber.musicplayer.MusicService.SERVER_DIED;
import static com.naman14.timber.musicplayer.MusicService.TRACK_ENDED;
import static com.naman14.timber.musicplayer.MusicService.TRACK_WENT_TO_NEXT;

public final class MusicPlayerHandler extends Handler {
        private final WeakReference<MusicService> mService;
        private float mCurrentVolume = 1.0f;


        public MusicPlayerHandler(final MusicService service, final Looper looper) {
            super(looper);
            mService = new WeakReference<MusicService>(service);
        }


        @Override
        public void handleMessage(final Message msg) {
            final MusicService service = mService.get();
            if (service == null) {
                return;
            }

            synchronized (service) {
                switch (msg.what) {
                    case FADEDOWN:
                        mCurrentVolume -= .05f;
                        if (mCurrentVolume > .2f) {
                            sendEmptyMessageDelayed(FADEDOWN, 10);
                        } else {
                            mCurrentVolume = .2f;
                        }
                        service.mPlayer.setVolume(mCurrentVolume);
                        break;
                    case FADEUP:
                        mCurrentVolume += .01f;
                        if (mCurrentVolume < 1.0f) {
                            sendEmptyMessageDelayed(FADEUP, 10);
                        } else {
                            mCurrentVolume = 1.0f;
                        }
                        service.mPlayer.setVolume(mCurrentVolume);
                        break;
                    case SERVER_DIED:
                        if (service.isPlaying()) {
                            final TrackErrorInfo info = (TrackErrorInfo) msg.obj;
                            service.sendErrorMessage(info.mTrackName);


                            service.removeTrack(info.mId);
                        } else {
                            service.openCurrentAndNext();
                        }
                        break;
                    case TRACK_WENT_TO_NEXT:
                        mService.get().scrobble();
                        service.setAndRecordPlayPos(service.mNextPlayPos);
                        service.setNextTrack();
                        /*if (service.mCursor != null) {
                            service.mCursor.close();
                            service.mCursor = null;
                        }*/
                        service.updateCursor(service.mPlaylist.get(service.mPlayPos).mId);
                        service.notifyChange(META_CHANGED);
                        service.updateNotification();
                        break;
                    case TRACK_ENDED:
                        mService.get().scrobble();
                        if (service.mRepeatMode == REPEAT_CURRENT) {
                            service.seek(0);
                            service.play();
                        } else {
                            service.gotoNext(false);
                        }
                        break;
                    case RELEASE_WAKELOCK:
                        service.mWakeLock.release();
                        break;
                    case FOCUSCHANGE:
                        switch (msg.arg1) {
                            case AudioManager.AUDIOFOCUS_LOSS:
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                                if (service.isPlaying()) {
                                    service.mPausedByTransientLossOfFocus =
                                            msg.arg1 == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
                                }
                                service.pause();
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                                removeMessages(FADEUP);
                                sendEmptyMessage(FADEDOWN);
                                break;
                            case AudioManager.AUDIOFOCUS_GAIN:
                                if (!service.isPlaying()
                                        && service.mPausedByTransientLossOfFocus) {
                                    service.mPausedByTransientLossOfFocus = false;
                                    mCurrentVolume = 0f;
                                    service.mPlayer.setVolume(mCurrentVolume);
                                    service.play();
                                } else {
                                    removeMessages(FADEDOWN);
                                    sendEmptyMessage(FADEUP);
                                }
                                break;
                            default:
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }