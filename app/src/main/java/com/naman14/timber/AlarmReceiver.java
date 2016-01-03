package com.naman14.timber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "闹钟时间到", Toast.LENGTH_SHORT).show();
        Log.d("AlarmReceiver" ,  "闹钟时间到" + new Date());
         intent = new Intent(context , MusicService.class);
        //启动Service播放音乐
       // context.startService(intent);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (MusicPlayer.isPlaying() == false){
                    MusicPlayer.playOrPause();
                }
            }
        }, 5);
    }
}
