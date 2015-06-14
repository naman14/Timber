package com.naman14.timber.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

import com.naman14.timber.utils.ImageWorker.ImageType;


public class SimpleBitmapWorkerTask extends BitmapWorkerTask<String, Void, TransitionDrawable> {


    public SimpleBitmapWorkerTask(final String key, final ImageView imageView, final ImageWorker.ImageType imageType,
                                  final Drawable fromDrawable, final Context context) {
        super(key, imageView, imageType, fromDrawable, context);
    }


    public SimpleBitmapWorkerTask(final String key, final ImageView imageView, final ImageType imageType,
                                  final Drawable fromDrawable, final Context context, final boolean scaleImgToView) {
        super(key, imageView, imageType, fromDrawable, context, scaleImgToView);
    }


    @Override
    protected TransitionDrawable doInBackground(final String... params) {
        if (isCancelled()) {
            return null;
        }

        final Bitmap bitmap = getBitmapInBackground(params);
        if (mScaleImgToView) {
            Bitmap scaledBitmap = ImageUtils.scaleBitmapForImageView(bitmap, getAttachedImageView());
            return createImageTransitionDrawable(scaledBitmap);
        }
        else
            return createImageTransitionDrawable(bitmap);
    }


    @Override
    protected void onPostExecute(TransitionDrawable transitionDrawable) {
        final ImageView imageView = getAttachedImageView();
        if (transitionDrawable != null && imageView != null) {
            imageView.setImageDrawable(transitionDrawable);
        } else if (imageView != null) {
            imageView.setImageDrawable(mFromDrawable);
        }
    }
}
