package com.naman14.timber.subfragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naman14.timber.R;
import com.naman14.timber.dataloaders.PlaylistLoader;
import com.naman14.timber.models.Playlist;

import java.util.List;

/**
 * Created by naman on 14/08/15.
 */
public class PlaylistPagerFragment extends Fragment {

    private static final String ARG_PAGE_NUMBER = "pageNumber";

    public static PlaylistPagerFragment newInstance(int pageNumber) {
        PlaylistPagerFragment fragment = new PlaylistPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PAGE_NUMBER, pageNumber);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playlist_pager, container, false);

        final List<Playlist> playlists= PlaylistLoader.getPlaylists(getActivity());
        Playlist playlist=playlists.get(getArguments().getInt(ARG_PAGE_NUMBER));

        TextView playlistame=(TextView) rootView.findViewById(R.id.name);
        playlistame.setText(playlist.name);
        return rootView;
    }

}

