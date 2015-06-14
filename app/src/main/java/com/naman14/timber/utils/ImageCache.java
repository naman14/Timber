package com.naman14.timber.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentCallbacks2;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;


import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;

public final class ImageCache {

    private static final String TAG = ImageCache.class.getSimpleName();

    private static final Uri mArtworkUri;

    private static final float MEM_CACHE_DIVIDER = 0.25f;

    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10;

    private static final CompressFormat COMPRESS_FORMAT = CompressFormat.JPEG;

    private static final int DISK_CACHE_INDEX = 0;

    private static final int COMPRESS_QUALITY = 98;

    private MemoryCache mLruCache;

    private DiskLruCache mDiskCache;

    private HashSet<ICacheListener> mListeners = new HashSet<ICacheListener>();

    private static ImageCache sInstance;

    public boolean mPauseDiskAccess = false;
    private Object mPauseLock = new Object();

    static {
        mArtworkUri = Uri.parse("content://media/external/audio/albumart");
    }

    public ImageCache(final Context context) {
        init(context);
    }

    public final static ImageCache getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new ImageCache(context.getApplicationContext());
        }
        return sInstance;
    }
    private void init(final Context context) {
        TimberUtils.execute(false, new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(final Void... unused) {
                // Initialize the disk cahe in a background thread
                initDiskCache(context);
                return null;
            }
        }, (Void[])null);
        // Set up the memory cache
        initLruCache(context);
    }

    private synchronized void initDiskCache(final Context context) {
        // Set up disk cache
        if (mDiskCache == null || mDiskCache.isClosed()) {
            File diskCacheDir = getDiskCacheDir(context, TAG);
            if (diskCacheDir != null) {
                if (!diskCacheDir.exists()) {
                    diskCacheDir.mkdirs();
                }
                if (getUsableSpace(diskCacheDir) > DISK_CACHE_SIZE) {
                    try {
                        mDiskCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE);
                    } catch (final IOException e) {
                        diskCacheDir = null;
                    }
                }
            }
        }
    }

    @SuppressLint("NewApi")
    public void initLruCache(final Context context) {
        final ActivityManager activityManager = (ActivityManager)context
                .getSystemService(Context.ACTIVITY_SERVICE);
        final int lruCacheSize = Math.round(MEM_CACHE_DIVIDER * activityManager.getMemoryClass()
                * 1024 * 1024);
        mLruCache = new MemoryCache(lruCacheSize);

        // Release some memory as needed
        context.registerComponentCallbacks(new ComponentCallbacks2() {


            @Override
            public void onTrimMemory(final int level) {
                if (level >= TRIM_MEMORY_MODERATE) {
                    evictAll();
                } else if (level >= TRIM_MEMORY_BACKGROUND) {
                    mLruCache.trimToSize(mLruCache.size() / 2);
                }
            }

            @Override
            public void onLowMemory() {
                // Nothing to do
            }


            @Override
            public void onConfigurationChanged(final Configuration newConfig) {
                // Nothing to do
            }
        });
    }


    public static final ImageCache findOrCreateCache(final Activity activity) {

        // Search for, or create an instance of the non-UI RetainFragment
        final RetainFragment retainFragment = findOrCreateRetainFragment(
                activity.getFragmentManager());

        // See if we already have an ImageCache stored in RetainFragment
        ImageCache cache = (ImageCache)retainFragment.getObject();

        // No existing ImageCache, create one and store it in RetainFragment
        if (cache == null) {
            cache = getInstance(activity);
            retainFragment.setObject(cache);
        }
        return cache;
    }


    public static final RetainFragment findOrCreateRetainFragment(final FragmentManager fm) {
        // Check to see if we have retained the worker fragment
        RetainFragment retainFragment = (RetainFragment)fm.findFragmentByTag(TAG);

        // If not retained, we need to create and add it
        if (retainFragment == null) {
            retainFragment = new RetainFragment();
            fm.beginTransaction().add(retainFragment, TAG).commit();
        }
        return retainFragment;
    }

    public void addBitmapToCache(final String data, final Bitmap bitmap) {
        addBitmapToCache(data, bitmap, false);
    }


    public void addBitmapToCache(final String data, final Bitmap bitmap, final boolean replace) {
        if (data == null || bitmap == null) {
            return;
        }

        // Add to memory cache
        addBitmapToMemCache(data, bitmap, replace);

        // Add to disk cache
        if (mDiskCache != null && !mDiskCache.isClosed()) {
            final String key = hashKeyForDisk(data);
            OutputStream out = null;
            try {
                final DiskLruCache.Snapshot snapshot = mDiskCache.get(key);
                if (snapshot != null) {
                    snapshot.getInputStream(DISK_CACHE_INDEX).close();
                }

                if (snapshot == null || replace) {
                    final DiskLruCache.Editor editor = mDiskCache.edit(key);
                    if (editor != null) {
                        out = editor.newOutputStream(DISK_CACHE_INDEX);
                        bitmap.compress(COMPRESS_FORMAT, COMPRESS_QUALITY, out);
                        editor.commit();
                        out.close();
                        flush();
                    }
                }
            } catch (final IOException e) {
                Log.e(TAG, "addBitmapToCache - " + e);
            } catch (final IllegalStateException e) {
                // if the user clears the cache while we have an async task going we could try
                // writing to the disk cache while it isn't ready. Catching here will silently
                // fail instead
                Log.e(TAG, "addBitmapToCache - " + e);
            } finally {
                try {
                    if (out != null) {
                        out.close();
                        out = null;
                    }
                } catch (final IOException e) {
                    Log.e(TAG, "addBitmapToCache - " + e);
                } catch (final IllegalStateException e) {
                    Log.e(TAG, "addBitmapToCache - " + e);
                }
            }
        }
    }


    public void addBitmapToMemCache(final String data, final Bitmap bitmap) {
        addBitmapToMemCache(data, bitmap, false);
    }

    public void addBitmapToMemCache(final String data, final Bitmap bitmap, final boolean replace) {
        if (data == null || bitmap == null) {
            return;
        }
        // Add to memory cache
        if (replace || getBitmapFromMemCache(data) == null) {
            mLruCache.put(data, bitmap);
        }
    }

    public final Bitmap getBitmapFromMemCache(final String data) {
        if (data == null) {
            return null;
        }
        if (mLruCache != null) {
            final Bitmap lruBitmap = mLruCache.get(data);
            if (lruBitmap != null) {
                return lruBitmap;
            }
        }
        return null;
    }


    public final Bitmap getBitmapFromDiskCache(final String data) {
        if (data == null) {
            return null;
        }

        // Check in the memory cache here to avoid going to the disk cache less
        // often
        if (getBitmapFromMemCache(data) != null) {
            return getBitmapFromMemCache(data);
        }

        waitUntilUnpaused();
        final String key = hashKeyForDisk(data);
        if (mDiskCache != null) {
            InputStream inputStream = null;
            try {
                final DiskLruCache.Snapshot snapshot = mDiskCache.get(key);
                if (snapshot != null) {
                    inputStream = snapshot.getInputStream(DISK_CACHE_INDEX);
                    if (inputStream != null) {
                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        if (bitmap != null) {
                            return bitmap;
                        }
                    }
                }
            } catch (final IOException e) {
                Log.e(TAG, "getBitmapFromDiskCache - " + e);
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (final IOException e) {
                }
            }
        }
        return null;
    }


    public final Bitmap getCachedBitmap(final String data) {
        if (data == null) {
            return null;
        }
        Bitmap cachedImage = getBitmapFromMemCache(data);
        if (cachedImage == null) {
            cachedImage = getBitmapFromDiskCache(data);
        }
        if (cachedImage != null) {
            addBitmapToMemCache(data, cachedImage);
            return cachedImage;
        }
        return null;
    }

    public final Bitmap getCachedArtwork(final Context context, final String data, final long id) {
        if (context == null || data == null) {
            return null;
        }
        Bitmap cachedImage = getCachedBitmap(data);
        if (cachedImage == null && id >= 0) {
            cachedImage = getArtworkFromFile(context, id);
        }
        if (cachedImage != null) {
            addBitmapToMemCache(data, cachedImage);
            return cachedImage;
        }
        return null;
    }

    public final Bitmap getArtworkFromFile(final Context context, final long albumId) {
        if (albumId < 0) {
            return null;
        }
        Bitmap artwork = null;
        waitUntilUnpaused();
        try {
            final Uri uri = ContentUris.withAppendedId(mArtworkUri, albumId);
            final ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver()
                    .openFileDescriptor(uri, "r");
            if (parcelFileDescriptor != null) {
                final FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                artwork = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            }
        } catch (final IllegalStateException e) {
            // Log.e(TAG, "IllegalStateExcetpion - getArtworkFromFile - ", e);
        } catch (final FileNotFoundException e) {
            // Log.e(TAG, "FileNotFoundException - getArtworkFromFile - ", e);
        } catch (final OutOfMemoryError evict) {
            // Log.e(TAG, "OutOfMemoryError - getArtworkFromFile - ", evict);
            evictAll();
        }
        return artwork;
    }


    public void flush() {
        TimberUtils.execute(false, new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(final Void... unused) {
                if (mDiskCache != null) {
                    try {
                        if (!mDiskCache.isClosed()) {
                            mDiskCache.flush();
                        }
                    } catch (final IOException e) {
                        Log.e(TAG, "flush - " + e);
                    }
                }
                return null;
            }
        }, (Void[])null);
    }


    public void clearCaches() {
        TimberUtils.execute(false, new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(final Void... unused) {
                // Clear the disk cache
                try {
                    if (mDiskCache != null) {
                        mDiskCache.delete();
                        mDiskCache = null;
                    }
                } catch (final IOException e) {
                    Log.e(TAG, "clearCaches - " + e);
                }
                // Clear the memory cache
                evictAll();
                return null;
            }
        }, (Void[])null);
    }


    public void close() {
        TimberUtils.execute(false, new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(final Void... unused) {
                if (mDiskCache != null) {
                    try {
                        if (!mDiskCache.isClosed()) {
                            mDiskCache.close();
                            mDiskCache = null;
                        }
                    } catch (final IOException e) {
                        Log.e(TAG, "close - " + e);
                    }
                }
                return null;
            }
        }, (Void[]) null);
    }


    public void evictAll() {
        if (mLruCache != null) {
            mLruCache.evictAll();
        }
        System.gc();
    }


    public void removeFromCache(final String key) {
        if (key == null) {
            return;
        }
        // Remove the Lru entry
        if (mLruCache != null) {
            mLruCache.remove(key);
        }

        try {
            // Remove the disk entry
            if (mDiskCache != null) {
                mDiskCache.remove(hashKeyForDisk(key));
            }
        } catch (final IOException e) {
            Log.e(TAG, "remove - " + e);
        }
        flush();
    }


    public void setPauseDiskCache(final boolean pause) {
        synchronized (mPauseLock) {
            if (mPauseDiskAccess != pause) {
                mPauseDiskAccess = pause;
                if (!pause) {
                    mPauseLock.notify();

                    for (ICacheListener listener : mListeners) {
                        listener.onCacheUnpaused();
                    }
                }
            }
        }
    }

    private void waitUntilUnpaused() {
        synchronized (mPauseLock) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                while (mPauseDiskAccess) {
                    try {
                        mPauseLock.wait();
                    } catch (InterruptedException e) {
                        // ignored, we'll start waiting again
                    }
                }
            }
        }
    }


    public boolean isDiskCachePaused() {
        return mPauseDiskAccess;
    }

    public void addCacheListener(ICacheListener listener) {
        mListeners.add(listener);
    }

    public void removeCacheListener(ICacheListener listener) {
        mListeners.remove(listener);
    }


    public static final File getDiskCacheDir(final Context context, final String uniqueName) {
        // getExternalCacheDir(context) returns null if external storage is not ready
        final String cachePath = getExternalCacheDir(context) != null
                                    ? getExternalCacheDir(context).getPath()
                                    : context.getCacheDir().getPath();
        return new File(cachePath, uniqueName);
    }

    public static final boolean isExternalStorageRemovable() {
        return Environment.isExternalStorageRemovable();
    }


    public static final File getExternalCacheDir(final Context context) {
        return context.getExternalCacheDir();
    }


    public static final long getUsableSpace(final File path) {
        return path.getUsableSpace();
    }


    public static final String hashKeyForDisk(final String key) {
        String cacheKey;
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(key.getBytes());
            cacheKey = bytesToHexString(digest.digest());
        } catch (final NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static final String bytesToHexString(final byte[] bytes) {
        final StringBuilder builder = new StringBuilder();
        for (final byte b : bytes) {
            final String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                builder.append('0');
            }
            builder.append(hex);
        }
        return builder.toString();
    }


    public static final class RetainFragment extends Fragment {


        private Object mObject;


        public RetainFragment() {
        }


        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Make sure this Fragment is retained over a configuration change
            setRetainInstance(true);
        }


        public void setObject(final Object object) {
            mObject = object;
        }


        public Object getObject() {
            return mObject;
        }
    }


    public static final class MemoryCache extends LruCache<String, Bitmap> {


        public MemoryCache(final int maxSize) {
            super(maxSize);
        }


        public static final int getBitmapSize(final Bitmap bitmap) {
            return bitmap.getByteCount();
        }


        @Override
        protected int sizeOf(final String paramString, final Bitmap paramBitmap) {
            return getBitmapSize(paramBitmap);
        }

    }

}
