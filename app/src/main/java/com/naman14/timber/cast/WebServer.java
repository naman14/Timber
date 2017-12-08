package com.naman14.timber.cast;

import android.content.Context;
import android.net.Uri;

import com.naman14.timber.utils.TimberUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class WebServer extends NanoHTTPD {

    private Context context;
    private Uri songUri, albumArtUri;

    public WebServer(Context context) {
        super(8080);
        this.context = context;
    }

    @Override
    public Response serve(String uri, Method method,
                          Map<String, String> header,
                          Map<String, String> parameters,
                          Map<String, String> files) {
        if (uri.contains("albumart")) {
            //serve the picture

            Uri url = Uri.parse(uri);
            String albumId = url.getQueryParameter("id");
            this.albumArtUri = TimberUtils.getAlbumArtUri(Long.parseLong(albumId));

            if (albumArtUri != null) {
                String mediasend = "image/jpg";
                FileInputStream fisAlbumArt = null;
                File albumArt = new File(albumArtUri.getPath());
                try {
                    fisAlbumArt = new FileInputStream(albumArt);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Response.Status st = Response.Status.OK;

                //serve the song
                return newFixedLengthResponse(st, mediasend, fisAlbumArt, albumArt.length());
            }

        } else if (uri.contains("song")) {

            Uri url = Uri.parse(uri);
            String songId = url.getQueryParameter("id");
            this.songUri = TimberUtils.getSongUri(context, Long.parseLong(songId));

            if (songUri != null) {
                String mediasend = "audio/mp3";
                FileInputStream fisSong = null;
                File song = new File(songUri.getPath());
                try {
                    fisSong = new FileInputStream(song);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Response.Status st = Response.Status.OK;

                //serve the song
                return newFixedLengthResponse(st, mediasend, fisSong, song.length());
            }

        }
        return newFixedLengthResponse("Error");
    }

}