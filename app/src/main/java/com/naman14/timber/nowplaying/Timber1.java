package com.naman14.timber.nowplaying;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naman14.timber.R;
import com.naman14.timber.dataloaders.QueueLoader;
import com.naman14.timber.models.Song;

import java.util.ArrayList;

/**
 * Created by naman on 26/07/15.
 */
public class Timber1 extends BaseNowplayingFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_timber1, container, false);

        ArrayList<Song> arrayList= QueueLoader.getQueueSongsList(getActivity());

        for (int i=0;i<arrayList.size();i++){
            Song song=arrayList.get(i);
            Log.d("lol", song.title);
        }

        setSongDetails(rootView);



        return rootView;
    }
}
