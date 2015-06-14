package com.naman14.timber.utils;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.widget.ImageView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;

/**
 * Created by naman on 14/06/15.
 */
public class ArtworkUtils {

    private static final Uri mArtworkUri;

    static {
        mArtworkUri = Uri.parse("content://media/external/audio/albumart");
    }

    public static final FileDescriptor getArtworkFromFile(final Context context, final long albumId) {
        if (albumId < 0) {
            return null;
        }
        FileDescriptor fileDescriptor=null;

        try {
            final Uri uri = ContentUris.withAppendedId(mArtworkUri, albumId);
            final ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver()
                    .openFileDescriptor(uri, "r");
            if (parcelFileDescriptor != null) {
                 fileDescriptor = parcelFileDescriptor.getFileDescriptor();

            }
        } catch (final IllegalStateException e) {
            // Log.e(TAG, "IllegalStateExcetpion - getArtworkFromFile - ", e);
        } catch (final FileNotFoundException e) {
            // Log.e(TAG, "FileNotFoundException - getArtworkFromFile - ", e);
        }
        return fileDescriptor;
    }

    public static class BitmapWorkerTask extends AsyncTask<BitmapTaskParams, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(BitmapTaskParams... params) {
            FileDescriptor fileDescriptor=params[0].fileDescriptor;
            int width = params[0].reqWidth;
            int height = params[0].reqHeight;
            return BitmapUtils.decodeSampledBitmapFromDescriptor(fileDescriptor,width,height);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
    private static class BitmapTaskParams {
        FileDescriptor fileDescriptor;
        int reqWidth,reqHeight;

        BitmapTaskParams(FileDescriptor filedesc,int width, int height) {
            this.fileDescriptor=filedesc;
            this.reqWidth = width;
            this.reqHeight = height;
        }
    }

    public static void loadBitmap( Context context,ImageView imageView, long albumId,int reqWidth,int reqHeight) {

        FileDescriptor fileDescriptor=getArtworkFromFile(context,albumId);

        if (fileDescriptor!=null) {
            BitmapTaskParams params = new BitmapTaskParams(fileDescriptor, reqWidth, reqHeight);
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            task.execute(params);
        }
    }

}
