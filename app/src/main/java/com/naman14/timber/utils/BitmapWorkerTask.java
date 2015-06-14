package com.naman14.timber.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.naman14.timber.utils.ImageWorker.ImageType;

import java.lang.ref.WeakReference;

/**
 * Created by naman on 14/06/15.
 */

public abstract class BitmapWorkerTask<Params, Progress, Result>
        extends AsyncTask<Params, Progress, Result> {

    protected final WeakReference<ImageView> mImageReference;

    protected final ImageWorker.ImageType mImageType;

    protected Drawable mFromDrawable;

    protected final Context mContext;

    protected final ImageCache mImageCache;

    protected final Resources mResources;

    protected boolean mScaleImgToView;

    public String mKey;


    public BitmapWorkerTask(final String key, final ImageView imageView, final ImageType imageType,
                            final Drawable fromDrawable, final Context context) {
        this(key, imageView, imageType, fromDrawable, context, false);
    }


    public BitmapWorkerTask(final String key, final ImageView imageView, final ImageType imageType,
                            final Drawable fromDrawable, final Context context, final boolean scaleImgToView) {
        mKey = key;

        mContext = context;
        mImageCache = ImageCache.getInstance(mContext);
        mResources = mContext.getResources();

        mImageReference = new WeakReference<ImageView>(imageView);
        mImageType = imageType;

        // A transparent image (layer 0) and the new result (layer 1)
        mFromDrawable = fromDrawable;

        mScaleImgToView = scaleImgToView;
    }


    protected ImageView getAttachedImageView() {
        final ImageView imageView = mImageReference.get();
        if (imageView != null) {
            final BitmapWorkerTask bitmapWorkerTask = ImageWorker.getBitmapWorkerTask(imageView);
            if (this == bitmapWorkerTask) {
                return imageView;
            }
        }

        return null;
    }


    protected Bitmap getBitmapInBackground(final String... params) {
        return ImageWorker.getBitmapInBackground(mContext, mImageCache, mKey,
                params[1], params[0], Long.valueOf(params[2]), mImageType);
    }


    protected TransitionDrawable createImageTransitionDrawable(final Bitmap bitmap) {
        return createImageTransitionDrawable(bitmap, ImageWorker.FADE_IN_TIME, false, false);
    }

    protected TransitionDrawable createImageTransitionDrawable(final Bitmap bitmap,
                                                               final int fadeTime, final boolean dither, final boolean force) {
        return ImageWorker.createImageTransitionDrawable(mResources, mFromDrawable, bitmap,
                fadeTime, dither, force);
    }
}
