package com.naman14.timber.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v8.renderscript.RenderScript;
import android.view.View;
import android.widget.ImageView;

import com.naman14.timber.R;
import com.naman14.timber.dataloaders.AlbumLoader;
import com.naman14.timber.lastfmapi.LastFmClient;
import com.naman14.timber.lastfmapi.callbacks.AlbuminfoListener;
import com.naman14.timber.lastfmapi.models.AlbumQuery;
import com.naman14.timber.lastfmapi.models.LastfmAlbum;
import com.naman14.timber.models.Album;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ImageUtils {
    private static final DisplayImageOptions lastfmDisplayImageOptions =
            new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .showImageOnFail(R.drawable.ic_empty_music2)
                    .build();

    private static final DisplayImageOptions diskDisplayImageOptions =
            new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .build();

    public static void loadAlbumArtIntoView(final long albumId, final ImageView view) {
        loadAlbumArtIntoView(albumId, view, new SimpleImageLoadingListener());
    }

    public static void loadAlbumArtIntoView(final long albumId, final ImageView view,
                                            final ImageLoadingListener listener) {
        ImageLoader.getInstance()
                .displayImage(TimberUtils.getAlbumArtUri(albumId).toString(),
                        view,
                        diskDisplayImageOptions,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingFailed(String imageUri, View view,
                                                        FailReason failReason) {
                                loadAlbumArtFromLastfm(albumId, (ImageView) view);
                                listener.onLoadingFailed(imageUri, view, failReason);
                            }
                        });
    }

    private static void loadAlbumArtFromLastfm(long albumId, final ImageView albumArt) {
        Album album = AlbumLoader.getAlbum(albumArt.getContext(), albumId);
        LastFmClient.getInstance(albumArt.getContext())
                .getAlbumInfo(new AlbumQuery(album.title, album.artistName),
                        new AlbuminfoListener() {
                            @Override
                            public void albumInfoSucess(final LastfmAlbum album) {
                                if (album != null) {
                                    ImageLoader.getInstance()
                                            .displayImage(album.mArtwork.get(4).mUrl,
                                                    albumArt,
                                                    lastfmDisplayImageOptions);
                                }
                            }

                            @Override
                            public void albumInfoFailed() { }
                        });
    }

    public static Drawable createBlurredImageFromBitmap(Bitmap bitmap, Context context, int inSampleSize) {

        RenderScript rs = RenderScript.create(context);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
        Bitmap blurTemplate = BitmapFactory.decodeStream(bis, null, options);

        final android.support.v8.renderscript.Allocation input = android.support.v8.renderscript.Allocation.createFromBitmap(rs, blurTemplate);
        final android.support.v8.renderscript.Allocation output = android.support.v8.renderscript.Allocation.createTyped(rs, input.getType());
        final android.support.v8.renderscript.ScriptIntrinsicBlur script = android.support.v8.renderscript.ScriptIntrinsicBlur.create(rs, android.support.v8.renderscript.Element.U8_4(rs));
        script.setRadius(8f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(blurTemplate);

        return new BitmapDrawable(context.getResources(), blurTemplate);
    }
}
