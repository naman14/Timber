package com.naman14.timber.utils;


import java.util.Timer;
import java.util.TimerTask;

public class DelaySearchUtils {
    private Timer timer;
    private CallbackDelay callbackDelayListener;

    public DelaySearchUtils(CallbackDelay callbackDelayListener) {
        this.callbackDelayListener = callbackDelayListener;
        initTimer();
    }

    public interface CallbackDelay {
        void callbackDelayListener(String searchText);
        void callbackClearFieldIfEmpty();
    }

    public void delay(final String searchText) {
        final int DELAY = 200;
        timer.cancel();
        initTimer();
        scheduleTimer(new TimerTask() {
            @Override
            public void run() {
                if(!searchText.trim().isEmpty())
                    callbackDelayListener.callbackDelayListener(searchText);
                else
                    callbackDelayListener.callbackClearFieldIfEmpty();
            }
        }, DELAY);
    }

    private void scheduleTimer(TimerTask timerTask, int delay) {
        timer.schedule(timerTask, delay);
    }

    private void initTimer() {
        timer = new Timer();
    }

    public void cancelTimerOnDestroyActivity() {
        if (timer != null) timer.cancel();
    }
}
