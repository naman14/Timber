package com.naman14.timber.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by naman on 14/06/15.
 */

public class ArtworkFetcher extends ImageWorker {

    private static final int DEFAULT_MAX_IMAGE_HEIGHT = 1024;

    private static final int DEFAULT_MAX_IMAGE_WIDTH = 1024;

    private static ArtworkFetcher sInstance = null;

    public static final String ALBUM_ART_SUFFIX = "album";


    public ArtworkFetcher(final Context context) {
        super(context);
    }


    public static final ArtworkFetcher getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new ArtworkFetcher(context.getApplicationContext());
        }
        return sInstance;
    }


    public void loadAlbumImage(final String artistName, final String albumName, final long albumId,
                               final ImageView imageView) {
        loadImage(generateAlbumCacheKey(albumName, artistName), artistName, albumName, albumId, imageView,
                ImageType.ALBUM);
    }

    public void loadArtistImage(final String key, final ImageView imageView) {
        loadImage(key, key, null, -1, imageView, ImageType.ARTIST);
    }

    public void loadArtistImage(final String key, final ImageView imageView, boolean scaleImgToView) {
        loadImage(key, key, null, -1, imageView, ImageType.ARTIST, scaleImgToView);
    }


    public void setPauseDiskCache(final boolean pause) {
        if (mImageCache != null) {
            mImageCache.setPauseDiskCache(pause);
        }
    }

    public void clearCaches() {
        if (mImageCache != null) {
            mImageCache.clearCaches();
        }

        // clear the keys of images we've already downloaded
        sKeys.clear();
    }

    public void addCacheListener(ICacheListener listener) {
        if (mImageCache != null) {
            mImageCache.addCacheListener(listener);
        }
    }

    public void removeCacheListener(ICacheListener listener) {
        if (mImageCache != null) {
            mImageCache.removeCacheListener(listener);
        }
    }

    public void removeFromCache(final String key) {
        if (mImageCache != null) {
            mImageCache.removeFromCache(key);
        }
    }


    public BitmapWithColors getArtwork(final String albumName, final long albumId,
            final String artistName, boolean smallArtwork) {
        // Check the disk cache
        Bitmap artwork = null;
        String key = String.valueOf(albumId);

        if (artwork == null && albumName != null && mImageCache != null) {
            artwork = mImageCache.getBitmapFromDiskCache(key);
        }
        if (artwork == null && albumId >= 0 && mImageCache != null) {
            // Check for local artwork
            artwork = mImageCache.getArtworkFromFile(mContext, albumId);
        }
        if (artwork != null) {
            return new BitmapWithColors(artwork, key.hashCode());
        }

        return LetterTileDrawable.createDefaultBitmap(mContext, key, ImageType.ALBUM, false,
                smallArtwork);
    }

    public static String generateAlbumCacheKey(final String albumName, final String artistName) {
        if (albumName == null || artistName == null) {
            return null;
        }
        return new StringBuilder(albumName)
                .append("_")
                .append(artistName)
                .append("_")
                .append(ALBUM_ART_SUFFIX)
                .toString();
    }

    public static Bitmap decodeSampledBitmapFromUri(ContentResolver cr, final Uri selectedImage) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        try {
            InputStream input = cr.openInputStream(selectedImage);
            BitmapFactory.decodeStream(input, null, options);
            input.close();

            if (options.outHeight == -1 || options.outWidth == -1) {
                return null;
            }

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, DEFAULT_MAX_IMAGE_WIDTH,
                    DEFAULT_MAX_IMAGE_HEIGHT);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            input = cr.openInputStream(selectedImage);
            return BitmapFactory.decodeStream(input, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static final int calculateInSampleSize(final BitmapFactory.Options options,
                                                  final int reqWidth, final int reqHeight) {
        /* Raw height and width of image */
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }

            final float totalPixels = width * height;

            /* More than 2x the requested pixels we'll sample down further */
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }
}
