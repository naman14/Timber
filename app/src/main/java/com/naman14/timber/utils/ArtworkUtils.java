package com.naman14.timber.utils;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;

/**
 * Created by naman on 14/06/15.
 */
public class ArtworkUtils {

    private static final Uri mArtworkUri;

    static {
        mArtworkUri = Uri.parse("content://media/external/audio/albumart");
    }

    public static final Bitmap getArtworkFromFile(final Context context, final long albumId,int reqWidth,int reqHeight) {
        if (albumId < 0) {
            return null;
        }
        Bitmap artwork = null;

        try {
            final Uri uri = ContentUris.withAppendedId(mArtworkUri, albumId);
            final ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver()
                    .openFileDescriptor(uri, "r");
            if (parcelFileDescriptor != null) {
                final FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                artwork = decodeSampledBitmapFromDescriptor(fileDescriptor,reqWidth,reqHeight);
            }
        } catch (final IllegalStateException e) {
            // Log.e(TAG, "IllegalStateExcetpion - getArtworkFromFile - ", e);
        } catch (final FileNotFoundException e) {
            // Log.e(TAG, "FileNotFoundException - getArtworkFromFile - ", e);
        } catch (final OutOfMemoryError evict) {
             Log.e("lol", "OutOfMemoryError - getArtworkFromFile - ", evict);

        }
        return artwork;
    }
    public static final int calculateInSampleSize(final BitmapFactory.Options options,
                                                  final int reqWidth, final int reqHeight) {
        /* Raw height and width of image */
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
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

    public static Bitmap decodeSampledBitmapFromDescriptor(FileDescriptor fileDescriptor,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    }



}
