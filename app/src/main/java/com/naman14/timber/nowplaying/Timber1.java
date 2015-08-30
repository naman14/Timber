package com.naman14.timber.nowplaying;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

/**
 * Created by naman on 26/07/15.
 */
public class Timber1 extends BaseNowplayingFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_timber1, container, false);

        setMusicStateListener();
        setSongDetails(rootView);

        return rootView;
    }

    @Override
    public void updateShuffleState(){
            if (shuffle!=null && getActivity()!=null) {
                MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(getActivity())
                        .setIcon(MaterialDrawableBuilder.IconValue.SHUFFLE)
                        .setSizeDp(30);

                int color,color2;
                if (isThemeIsLight()) {
                    color = Color.parseColor("#ffffff");
                    color2=Color.parseColor("#000000");
                }
                else  {
                    color=Color.parseColor("#000000");
                    if (isThemeIsBlack())
                    color2=Color.parseColor("#ffb701");
                    else color2= Color.parseColor("#ffffff");
                };

                if (MusicPlayer.getShuffleMode()==0){
                    builder.setColor(color);
                } else builder.setColor(color2);

                shuffle.setImageDrawable(builder.build());
                shuffle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MusicPlayer.cycleShuffle();
                        updateShuffleState();
                    }
                });
            }
    }
}
