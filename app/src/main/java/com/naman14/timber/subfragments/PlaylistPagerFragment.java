package com.naman14.timber.subfragments;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.naman14.timber.R;
import com.naman14.timber.dataloaders.LastAddedLoader;
import com.naman14.timber.dataloaders.PlaylistLoader;
import com.naman14.timber.dataloaders.PlaylistSongLoader;
import com.naman14.timber.dataloaders.SongLoader;
import com.naman14.timber.dataloaders.TopTracksLoader;
import com.naman14.timber.models.Playlist;
import com.naman14.timber.models.Song;
import com.naman14.timber.utils.Constants;
import com.naman14.timber.utils.NavigationUtils;
import com.naman14.timber.utils.TimberUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by naman on 14/08/15.
 */
public class PlaylistPagerFragment extends Fragment {

    private static final String ARG_PAGE_NUMBER = "pageNumber";
    private int pageNumber,songCountInt;
    private int foregroundColor;
    private long firstAlbumID;

    private Playlist playlist;
    private TextView playlistame,songcount, playlistnumber,playlisttype;
    private ImageView playlistImage;
    private View foreground;

    int[] foregroundColors = {R.color.pink_transparent, R.color.green_transparent, R.color.blue_transparent, R.color.red_transparent, R.color.purple_transparent};

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

        final List<Playlist> playlists = PlaylistLoader.getPlaylists(getActivity());

        pageNumber=getArguments().getInt(ARG_PAGE_NUMBER);
        playlist = playlists.get(pageNumber);

        playlistame = (TextView) rootView.findViewById(R.id.name);
        playlistnumber = (TextView) rootView.findViewById(R.id.number);
        songcount=(TextView) rootView.findViewById(R.id.songcount);
        playlisttype = (TextView) rootView.findViewById(R.id.playlisttype);
        playlistImage = (ImageView) rootView.findViewById(R.id.playlist_image);
        foreground = rootView.findViewById(R.id.foreground);

        playlistImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Pair> tranitionViews=new ArrayList<>();
                tranitionViews.add(0, Pair.create((View)playlistame,"transition_playlist_name"));
                NavigationUtils.navigateToPlaylistDetail(getActivity(), getPlaylistType(),firstAlbumID,String.valueOf(playlistame.getText()),foregroundColor, playlist.id,tranitionViews);
            }
        });

        setUpPlaylistDetails();
        return rootView;
    }


    @Override
    public void onViewCreated(View view,Bundle savedinstancestate){
        new loadPlaylistImage().execute("");
    }

    private void setUpPlaylistDetails() {
        playlistame.setText(playlist.name);

        int number = getArguments().getInt(ARG_PAGE_NUMBER) + 1;
        String playlistnumberstring;

        if (number > 9) {
            playlistnumberstring = String.valueOf(number);
        } else {
            playlistnumberstring = "0" + String.valueOf(number);
        }
        playlistnumber.setText(playlistnumberstring);

        Random random = new Random();
        int rndInt = random.nextInt(foregroundColors.length);

        foregroundColor=foregroundColors[rndInt];
        foreground.setBackgroundColor(foregroundColor);

        if (pageNumber>2){
            playlisttype.setVisibility(View.GONE);
        }

    }

    private String getPlaylistType(){
        switch (pageNumber){
            case 0:
                return Constants.NAVIGATE_PLAYLIST_LASTADDED;
            case 1:
                return Constants.NAVIGATE_PLAYLIST_RECENT;
            case 2:
                return Constants.NAVIGATE_PLAYLIST_TOPTRACKS;
            default:
                return Constants.NAVIGATE_PLAYLIST_USERCREATED;
        }
    }


    private class loadPlaylistImage extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (getActivity()!=null) {
                switch (pageNumber) {
                    case 0:
                        List<Song> lastAddedSongs = LastAddedLoader.getLastAddedSongs(getActivity());
                        songCountInt=lastAddedSongs.size();
                        firstAlbumID=lastAddedSongs.get(0).albumId;
                        if (songCountInt != 0)
                            return TimberUtils.getAlbumArtUri(firstAlbumID).toString();
                        else return "nosongs";
                    case 1:
                        TopTracksLoader recentloader = new TopTracksLoader(getActivity(), TopTracksLoader.QueryType.RecentSongs);
                        List<Song> recentsongs = SongLoader.getSongsForCursor(recentloader.getCursor());
                        songCountInt=recentsongs.size();
                        firstAlbumID=recentsongs.get(0).albumId;
                        if (songCountInt != 0)
                            return TimberUtils.getAlbumArtUri(firstAlbumID).toString();
                        else return "nosongs";
                    case 2:
                        TopTracksLoader topTracksLoader = new TopTracksLoader(getActivity(), TopTracksLoader.QueryType.TopTracks);
                        List<Song> topsongs = SongLoader.getSongsForCursor(topTracksLoader.getCursor());
                        songCountInt=topsongs.size();
                        firstAlbumID=topsongs.get(0).albumId;
                        if (songCountInt != 0)
                            return TimberUtils.getAlbumArtUri(firstAlbumID).toString();
                        else return "nosongs";
                    default:
                        List<Song> playlistsongs = PlaylistSongLoader.getSongsInPlaylist(getActivity(), playlist.id);
                        songCountInt=playlistsongs.size();
                        firstAlbumID=playlistsongs.get(0).albumId;
                        if (songCountInt != 0)
                            return TimberUtils.getAlbumArtUri(firstAlbumID).toString();
                        else return "nosongs";

                }
            } else return "context is null";

        }

        @Override
        protected void onPostExecute(String uri) {
            ImageLoader.getInstance().displayImage(uri,playlistImage,
                    new DisplayImageOptions.Builder().cacheInMemory(true)
                            .showImageOnFail(R.drawable.ic_empty_music2)
                            .resetViewBeforeLoading(true)
                            .build(), new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        }
                    });
            songcount.setText(" "+String.valueOf(songCountInt)+" Songs");
        }

        @Override
        protected void onPreExecute() {}
    }


}

