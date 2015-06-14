package com.naman14.timber.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;

import android.view.View;
import android.widget.ImageView;



import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;

public abstract class ImageWorker {


    public static Set<String> sKeys = Collections.synchronizedSet(new HashSet<String>());

    public static final int FADE_IN_TIME = 200;

    public static final int FADE_IN_TIME_SLOW = 1000;

    private final Resources mResources;

    private final ColorDrawable mTransparentDrawable;

    protected Context mContext;

    protected ImageCache mImageCache;

    protected ImageWorker(final Context context) {
        mContext = context.getApplicationContext();
        mResources = mContext.getResources();
        // Create the transparent layer for the transition drawable
        mTransparentDrawable = new ColorDrawable(Color.TRANSPARENT);
    }

    public void setImageCache(final ImageCache cacheCallback) {
        mImageCache = cacheCallback;
    }


    public void close() {
        if (mImageCache != null) {
            mImageCache.close();
        }
    }


    public void flush() {
        if (mImageCache != null) {
            mImageCache.flush();
        }
    }

    public void addBitmapToCache(final String key, final Bitmap bitmap) {
        if (mImageCache != null) {
            mImageCache.addBitmapToCache(key, bitmap);
        }
    }


    public Drawable getNewDrawable(ImageType imageType, String name,
                                                String identifier) {
        LetterTileDrawable letterTileDrawable = new LetterTileDrawable(mContext);
        letterTileDrawable.setTileDetails(name, identifier, imageType);
        letterTileDrawable.setIsCircular(false);
        return letterTileDrawable;
    }

    public static Bitmap getBitmapInBackground(final Context context, final ImageCache imageCache,
                                   final String key, final String albumName, final String artistName,
                                   final long albumId, final ImageType imageType) {
        // The result
        Bitmap bitmap = null;

        // First, check the disk cache for the image
        if (key != null && imageCache != null) {
            bitmap = imageCache.getCachedBitmap(key);
        }

        // Second, if we're fetching artwork, check the device for the image
        if (bitmap == null && imageType.equals(ImageType.ALBUM) && albumId >= 0
                && key != null && imageCache != null) {
            bitmap = imageCache.getCachedArtwork(context, key, albumId);
        }

        // Third, by now we need to download the image
        if (bitmap == null && TimberUtils.isOnline(context) && !sKeys.contains(key)) {
            // Now define what the artist name, album name, and url are.
            String url = ImageUtils.processImageUrl(context, artistName, albumName, imageType);
            if (url != null) {
                bitmap = ImageUtils.processBitmap(context, url);
            }
        }

        // Fourth, add the new image to the cache
        if (bitmap != null && key != null && imageCache != null) {
            imageCache.addBitmapToCache(key, bitmap);
        }

        sKeys.add(key);

        return bitmap;
    }

    public static Drawable getTopDrawable(final Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        Drawable retDrawable = drawable;
        while (retDrawable instanceof TransitionDrawable) {
            TransitionDrawable transition = (TransitionDrawable) retDrawable;
            retDrawable = transition.getDrawable(transition.getNumberOfLayers() - 1);
        }

        return retDrawable;
    }

    public static TransitionDrawable createImageTransitionDrawable(final Resources resources,
               final Drawable fromDrawable, final Bitmap bitmap, final int fadeTime,
               final boolean dither, final boolean force) {
        if (bitmap != null || force) {
            final Drawable[] arrayDrawable = new Drawable[2];
            arrayDrawable[0] = getTopDrawable(fromDrawable);

            // Add the transition to drawable
            Drawable layerTwo;
            if (bitmap != null) {
                layerTwo = new BitmapDrawable(resources, bitmap);
                layerTwo.setFilterBitmap(false);
                layerTwo.setDither(dither);
            } else {
                // if no bitmap (forced) then transition to transparent
                layerTwo = new ColorDrawable(Color.TRANSPARENT);
            }

            arrayDrawable[1] = layerTwo;

            // Finally, return the image
            final TransitionDrawable result = new TransitionDrawable(arrayDrawable);
            result.setCrossFadeEnabled(true);
            result.startTransition(fadeTime);
            return result;
        }

        return null;
    }


    public static final void cancelWork(final View image) {
        Object tag = image.getTag();
        if (tag != null && tag instanceof AsyncTaskContainer) {
            AsyncTaskContainer asyncTaskContainer = (AsyncTaskContainer)tag;
            BitmapWorkerTask bitmapWorkerTask = asyncTaskContainer.getBitmapWorkerTask();
            if (bitmapWorkerTask != null) {
                bitmapWorkerTask.cancel(false);
            }

            // clear out the tag
            image.setTag(null);
        }
    }

    public static final boolean executePotentialWork(final String key, final View view) {
        final AsyncTaskContainer asyncTaskContainer = getAsyncTaskContainer(view);
        if (asyncTaskContainer != null) {
            // we are trying to reload the same image, return false to indicate no work is needed
            if (asyncTaskContainer.getKey().equals(key)) {
                return false;
            }

            // since we don't match, cancel the work and switch to the new worker task
            cancelWork(view);
        }

        return true;
    }


    public static final AsyncTaskContainer getAsyncTaskContainer(final View view) {
        if (view != null) {
            if (view.getTag() instanceof AsyncTaskContainer) {
                return (AsyncTaskContainer) view.getTag();
            }
        }

        return null;
    }


    public static final BitmapWorkerTask getBitmapWorkerTask(final View view) {
        AsyncTaskContainer asyncTask = getAsyncTaskContainer(view);
        if (asyncTask != null) {
            return asyncTask.getBitmapWorkerTask();
        }

        return null;
    }


    public static final class AsyncTaskContainer {

        private final WeakReference<BitmapWorkerTask> mBitmapWorkerTaskReference;
        // keep a copy of the key in case the worker task mBitmapWorkerTaskReference is released
        // after completion
        private String mKey;

        /**
         * Constructor of <code>AsyncDrawable</code>
         */
        public AsyncTaskContainer(final BitmapWorkerTask bitmapWorkerTask) {
            mBitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
            mKey = bitmapWorkerTask.mKey;
        }

        /**
         * @return The {@link BitmapWorkerTask} associated with this drawable
         */
        public BitmapWorkerTask getBitmapWorkerTask() {
            return mBitmapWorkerTaskReference.get();
        }

        public String getKey() {
            return mKey;
        }
    }


    public void loadDefaultImage(final ImageView imageView, final ImageType imageType,
                                    final String name, final String identifier) {
        if (imageView != null) {
            // if an existing letter drawable exists, re-use it
            Drawable existingDrawable = imageView.getDrawable();
            if (existingDrawable != null && existingDrawable instanceof LetterTileDrawable) {
                ((LetterTileDrawable)existingDrawable).setTileDetails(name, identifier, imageType);
            } else {
                imageView.setImageDrawable(getNewDrawable(imageType, name,
                        identifier));
            }
        }
    }


    protected void loadImage(final String key, final String artistName, final String albumName,
            final long albumId, final ImageView imageView, final ImageType imageType) {

        loadImage(key, artistName, albumName, albumId, imageView, imageType, false);
    }


    protected void loadImage(final String key, final String artistName, final String albumName,
                             final long albumId, final ImageView imageView,
                             final ImageType imageType, final boolean scaleImgToView) {

        if (key == null || mImageCache == null || imageView == null) {
            return;
        }

        // First, check the memory for the image
        final Bitmap lruBitmap = mImageCache.getBitmapFromMemCache(key);
        if (lruBitmap != null) {   // Bitmap found in memory cache
            // scale image if necessary
            if (scaleImgToView) {
                imageView.setImageBitmap(ImageUtils.scaleBitmapForImageView(lruBitmap, imageView));
            } else {
                imageView.setImageBitmap(lruBitmap);
            }
        } else {
            // load the default image
            if (imageType == ImageType.ARTIST) {
                loadDefaultImage(imageView, imageType, artistName, key);
            } else if (imageType == ImageType.ALBUM) {
                // don't show letters for albums so pass in null as the display string
                // because an album could have multiple artists, use the album id as the key here
                loadDefaultImage(imageView, imageType, null, String.valueOf(albumId));
            } else {
                // don't show letters for playlists so pass in null as the display string
                loadDefaultImage(imageView, imageType, null, key);
            }

            if (executePotentialWork(key, imageView)
                    && imageView != null && !mImageCache.isDiskCachePaused()) {
                Drawable fromDrawable = imageView.getDrawable();
                if (fromDrawable == null) {
                    fromDrawable = mTransparentDrawable;
                }

                // Otherwise run the worker task
                final SimpleBitmapWorkerTask bitmapWorkerTask = new SimpleBitmapWorkerTask(key,
                            imageView, imageType, fromDrawable, mContext, scaleImgToView);

                final AsyncTaskContainer asyncTaskContainer = new AsyncTaskContainer(bitmapWorkerTask);
                imageView.setTag(asyncTaskContainer);
                try {
                    TimberUtils.execute(false, bitmapWorkerTask,
                            artistName, albumName, String.valueOf(albumId));
                } catch (RejectedExecutionException e) {
                    // Executor has exhausted queue space
                }
            }
        }
    }

    public enum ImageType {
        ARTIST, ALBUM, PLAYLIST;
    }
}
