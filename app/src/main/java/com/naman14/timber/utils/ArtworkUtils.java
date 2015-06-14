package com.naman14.timber.utils;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.widget.ImageView;

import com.naman14.timber.R;

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

    public static Uri getUri(final Context context,long albumId){
        Uri uri = ContentUris.withAppendedId(mArtworkUri, albumId);
        return uri;

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
            if (isCancelled()) {
                bitmap = null;
            }


            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
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

        if (cancelPotentialWork(albumId, imageView)) {

            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            BitmapTaskParams params = new BitmapTaskParams(fileDescriptor, reqWidth, reqHeight);
            Bitmap mPlaceHolderBitmap= BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(albumId, mPlaceHolderBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            if (fileDescriptor!=null) {
                task.execute(params);
            }
        }
    }
    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(long id, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
           // super(id, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    public static boolean cancelPotentialWork(long data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == 0 || bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

}
