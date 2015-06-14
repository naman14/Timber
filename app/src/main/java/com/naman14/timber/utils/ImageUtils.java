package com.naman14.timber.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageUtils {
    private static final String DEFAULT_HTTP_CACHE_DIR = "http"; //$NON-NLS-1$

    public static final int IO_BUFFER_SIZE_BYTES = 1024;

    private static final int DEFAULT_MAX_IMAGE_HEIGHT = 1024;

    private static final int DEFAULT_MAX_IMAGE_WIDTH = 1024;

    private static AtomicInteger sInteger = new AtomicInteger(0);

    public static String processImageUrl(final Context context, final String artistName,
                                         final String albumName, final ImageWorker.ImageType imageType) {
        switch (imageType) {
            case ARTIST:

                break;
            case ALBUM:

                break;
            default:
                break;
        }
        return null;
    }


    public static Bitmap processBitmap(final Context context, final String url) {
        if (url == null) {
            return null;
        }
        final File file = downloadBitmapToFile(context, url, DEFAULT_HTTP_CACHE_DIR);
        if (file != null) {
            // Return a sampled down version
            final Bitmap bitmap = decodeSampledBitmapFromFile(file.toString());
            file.delete();
            if (bitmap != null) {
                return bitmap;
            }
        }
        return null;
    }


    public static Bitmap decodeSampledBitmapFromFile(final String filename) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, DEFAULT_MAX_IMAGE_WIDTH,
                DEFAULT_MAX_IMAGE_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filename, options);
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

    public static final File downloadBitmapToFile(final Context context, final String urlString,
                                                  final String uniqueName) {
        final File cacheDir = ImageCache.getDiskCacheDir(context, uniqueName);

        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }

        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;

        try {
            // increment the number to not collisions on the temp file name.  A collision can
            // potentially cause up to 50s on the first creation of the temp file but not on
            // subsequent ones for some reason.
            int number = sInteger.getAndIncrement() % 10;
            final File tempFile = File.createTempFile("bitmap" + number, null, cacheDir); //$NON-NLS-1$

            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection)url.openConnection();
            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            int contentLength = urlConnection.getContentLength();
            final InputStream in = new BufferedInputStream(urlConnection.getInputStream(),
                    IO_BUFFER_SIZE_BYTES);
            out = new BufferedOutputStream(new FileOutputStream(tempFile), IO_BUFFER_SIZE_BYTES);

            final byte[] buffer = new byte[IO_BUFFER_SIZE_BYTES];
            int numBytes;
            while ((numBytes = in.read(buffer)) != -1) {
                out.write(buffer, 0, numBytes);
                contentLength -= numBytes;
            }

            // valid values for contentLength are either -ve (meaning it wasn't set) or 0
            // if it is  > 0 that means we got a value but didn't fully download the content
            if (contentLength > 0) {
                return null;
            }

            return tempFile;
        } catch (final IOException ignored) {
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (out != null) {
                try {
                    out.close();
                } catch (final IOException ignored) {
                }
            }
        }
        return null;
    }


    public static Bitmap scaleBitmapForImageView(Bitmap src, ImageView imageView) {
        if (src == null || imageView == null) {
            return src;
        }
        // get bitmap properties
        int srcHeight = src.getHeight();
        int srcWidth = src.getWidth();

        // get image view bounds
        int viewHeight = imageView.getHeight();
        int viewWidth = imageView.getWidth();

        int deltaWidth = viewWidth - srcWidth;
        int deltaHeight = viewHeight - srcHeight;

        if (deltaWidth <= 0 && deltaWidth <= 0)     // nothing to do if src bitmap is bigger than image-view
            return src;

        // scale bitmap along the dimension that is lacking the greatest
        float scale = Math.max(((float) viewWidth) / srcWidth, ((float) viewHeight) / srcHeight);

        // calculate the new bitmap dimensions
        int dstHeight = (int) Math.ceil(srcHeight * scale);
        int dstWidth = (int) Math.ceil(srcWidth * scale);
        Bitmap scaledBitmap =  Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        Bitmap croppedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, viewWidth, viewHeight);

        return croppedBitmap;

    }
}
