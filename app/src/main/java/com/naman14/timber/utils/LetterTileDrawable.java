/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.naman14.timber.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.naman14.timber.R;
import com.naman14.timber.utils.ImageWorker.ImageType;

import junit.framework.Assert;

/**
 * A drawable that encapsulates all the functionality needed to display a letter tile to
 * represent a artist/album/playlist image.
 */
public class LetterTileDrawable extends Drawable {

    private final String TAG = LetterTileDrawable.class.getSimpleName();

    private final Paint mPaint;

    /** Letter tile */
    private static TypedArray sColors;
    private static TypedArray sVibrantDarkColors;
    private static int sDefaultColor;
    private static int sTileFontColor;
    private static float sLetterToTileRatio;
    private static Bitmap DEFAULT_ARTIST;
    private static Bitmap DEFAULT_ARTIST_LARGE;
    private static Bitmap DEFAULT_ALBUM;
    private static Bitmap DEFAULT_ALBUM_LARGE;
    private static Bitmap DEFAULT_PLAYLIST;
    private static Bitmap DEFAULT_PLAYLIST_LARGE;

    /** Reusable components to avoid new allocations */
    private static final Paint sPaint = new Paint();
    private static final Rect sRect = new Rect();
    private static final char[] sChars = new char[2];

    private String mDisplayName;
    private String mIdentifier;
    private float mScale = 1.0f;
    private float mOffset = 0.0f;
    private Resources res;
    private boolean mIsCircle = false;

    private ImageType mImageType;

    private static synchronized void initializeStaticVariables(final Resources res) {
        if (sColors == null) {
            sColors = res.obtainTypedArray(R.array.letter_tile_colors);
            sVibrantDarkColors = res.obtainTypedArray(R.array.letter_tile_vibrant_dark_colors);
            sDefaultColor = res.getColor(R.color.letter_tile_default_color);
            sTileFontColor = res.getColor(R.color.letter_tile_font_color);
            sLetterToTileRatio = res.getFraction(R.dimen.letter_to_tile_ratio, 1, 1);
            DEFAULT_ARTIST = BitmapFactory.decodeResource(res, R.drawable.ic_artist);
            DEFAULT_ARTIST_LARGE = BitmapFactory.decodeResource(res, R.drawable.ic_artist_lg);
            DEFAULT_ALBUM = BitmapFactory.decodeResource(res, R.drawable.ic_album);
            DEFAULT_ALBUM_LARGE = BitmapFactory.decodeResource(res, R.drawable.ic_album_lg);
            DEFAULT_PLAYLIST = BitmapFactory.decodeResource(res, R.drawable.ic_playlist);
            DEFAULT_PLAYLIST_LARGE = BitmapFactory.decodeResource(res, R.drawable.ic_playlist_lg);

            sPaint.setTypeface(Typeface.create(
                    res.getString(R.string.letter_tile_letter_font_family), Typeface.NORMAL));
            sPaint.setTextAlign(Align.CENTER);
            sPaint.setAntiAlias(true);
        }
    }

    public LetterTileDrawable(final Context context) {
        mPaint = new Paint();
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);
        res = context.getResources();

        initializeStaticVariables(res);
    }

    @Override
    public void draw(final Canvas canvas) {
        //setBounds(0, 0, 120, 120);
        final Rect bounds = getBounds();
        if (!isVisible() || bounds.isEmpty()) {
            return;
        }
        // Draw letter tile.
        drawLetterTile(canvas);
    }

    @Override
    public void setBounds(Rect bounds) {
        super.setBounds(bounds);
    }

    private void drawLetterTile(final Canvas canvas) {
        // Draw background color.
        sPaint.setColor(pickColor(mIdentifier));

        sPaint.setAlpha(mPaint.getAlpha());
        final Rect bounds = getBounds();
        final int minDimension = Math.min(bounds.width(), bounds.height());

        if (mIsCircle) {
            canvas.drawCircle(bounds.centerX(), bounds.centerY(), minDimension / 2, sPaint);
        } else {
            canvas.drawRect(bounds, sPaint);
        }

        // Draw letter/digit only if the first character is an english letter
        if (mDisplayName != null && !mDisplayName.isEmpty()
                && isEnglishLetter(mDisplayName.charAt(0))) {
            int numChars = 1;

            // Draw letter or digit.
            sChars[0] = Character.toUpperCase(mDisplayName.charAt(0));

            if (mDisplayName.length() > 1 && isEnglishLetter(mDisplayName.charAt(1))) {
                sChars[1] = Character.toLowerCase(mDisplayName.charAt(1));
                numChars = 2;
            }

            // Scale text by canvas bounds and user selected scaling factor
            sPaint.setTextSize(mScale * sLetterToTileRatio * minDimension);
            //sPaint.setTextSize(sTileLetterFontSize);
            sPaint.getTextBounds(sChars, 0, numChars, sRect);
            sPaint.setColor(sTileFontColor);

            // Draw the letter in the canvas, vertically shifted up or down by the user-defined
            // offset
            canvas.drawText(sChars, 0, numChars, bounds.centerX(),
                    bounds.centerY() + mOffset * bounds.height() + sRect.height() / 2,
                    sPaint);
        } else {
            // Draw the default image if there is no letter/digit to be drawn
            final Bitmap bitmap = getDefaultBitmapForImageType(mImageType, bounds);

            // The bitmap should be drawn in the middle of the canvas without changing its width to
            // height ratio.
            final Rect destRect = copyBounds();

            drawBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), canvas, destRect, mScale,
                    mOffset, mPaint);
        }
    }

    public int getColor() {
        return pickColor(mIdentifier);
    }

    /**
     * @return the corresponding index in the color palette based on the identifier
     */
    private static int getColorIndex(final String identifier) {
        if (TextUtils.isEmpty(identifier)) {
            return -1;
        }

        return Math.abs(identifier.hashCode()) % sColors.length();
    }

    /**
     * Returns a deterministic color based on the provided contact identifier string.
     */
    private static int pickColor(final String identifier) {
        final int idx = getColorIndex(identifier);
        if (idx == -1) {
            return sDefaultColor;
        }

        return sColors.getColor(idx, sDefaultColor);
    }

    /**
     * Returns the vibrant matching color based on the provided contact identifier string.
     */
    private static int pickVibrantDarkColor(final String identifier) {
        final int idx = getColorIndex(identifier);
        if (idx == -1) {
            return sDefaultColor;
        }

        return sVibrantDarkColors.getColor(idx, sDefaultColor);
    }

    /**
     * Gets the default image to show for the image type.  If the bounds are large,
     * it will use the large default bitmap
     */
    private static Bitmap getDefaultBitmapForImageType(ImageType type, Rect bounds) {
        Bitmap ret = getDefaultBitmap(type, true);
        if (Math.max(bounds.width(), bounds.height()) > Math.max(ret.getWidth(), ret.getHeight())) {
            ret = getDefaultBitmap(type, false);
        }

        return ret;
    }

    private static Bitmap getDefaultBitmap(ImageType type, boolean small) {
        switch (type) {
            case ARTIST:
                return small ? DEFAULT_ARTIST : DEFAULT_ARTIST_LARGE;
            case ALBUM:
                return small ? DEFAULT_ALBUM : DEFAULT_ALBUM_LARGE;
             case PLAYLIST:
                 return small ? DEFAULT_PLAYLIST : DEFAULT_PLAYLIST_LARGE;
            default:
                throw new IllegalArgumentException("Unrecognized image type");
        }
    }

    private static boolean isEnglishLetter(final char c) {
        return ('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z') || ('0' <= c && c <= '9');
    }

    @Override
    public void setAlpha(final int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(final ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return android.graphics.PixelFormat.OPAQUE;
    }

    /**
     * Scale the drawn letter tile to a ratio of its default size
     *
     * @param scale The ratio the letter tile should be scaled to as a percentage of its default
     * size, from a scale of 0 to 2.0f. The default is 1.0f.
     */
    public void setScale(float scale) {
        mScale = scale;
    }

    /**
     * Assigns the vertical offset of the position of the letter tile to the ContactDrawable
     *
     * @param offset The provided offset must be within the range of -0.5f to 0.5f.
     * If set to -0.5f, the letter will be shifted upwards by 0.5 times the height of the canvas
     * it is being drawn on, which means it will be drawn with the center of the letter starting
     * at the top edge of the canvas.
     * If set to 0.5f, the letter will be shifted downwards by 0.5 times the height of the canvas
     * it is being drawn on, which means it will be drawn with the center of the letter starting
     * at the bottom edge of the canvas.
     * The default is 0.0f.
     */
    public void setOffset(float offset) {
        Assert.assertTrue(offset >= -0.5f && offset <= 0.5f);
        mOffset = offset;
    }

    /**
     * Sets the tile data used to determine the display text and color
     * @param displayName the name to display - Some logic will be applied to do some trimming
     *                    and up to the first two letters will be displayed
     * @param identifier the identifier used to determine the color of the background.  For
     *                   album, use albumId, for artist use artistName and for playlist use
     *                   playlistId
     * @param type the type of item that this tile drawable corresponds to
     */
    public void setTileDetails(final String displayName, final String identifier,
                               final ImageType type) {
        mDisplayName = TimberUtils.getTrimmedName(displayName);
        mIdentifier = TimberUtils.getTrimmedName(identifier);
        mImageType = type;
        invalidateSelf();
    }

    public void setIsCircular(boolean isCircle) {
        mIsCircle = isCircle;
    }

    /**
     * Draw the bitmap onto the canvas at the current bounds taking into account the current scale.
     */
    private static void drawBitmap(final Bitmap bitmap, final int width, final int height,
                                   final Canvas canvas, final Rect destRect, final float scale,
                                   final float offset, final Paint paint) {
        // Crop the destination bounds into a square, scaled and offset as appropriate
        final int halfLength = (int) (scale * Math.min(destRect.width(), destRect.height()) / 2);

        destRect.set(destRect.centerX() - halfLength,
                (int) (destRect.centerY() - halfLength + offset * destRect.height()),
                destRect.centerX() + halfLength,
                (int) (destRect.centerY() + halfLength + offset * destRect.height()));

        // Source rectangle remains the entire bounds of the source bitmap.
        sRect.set(0, 0, width, height);

        canvas.drawBitmap(bitmap, sRect, destRect, paint);
    }

    /**
     * Draws the default letter tile drawable for the image type to a bitmap
     * @param identifier the identifier used to determine the color of the background.  For
     *                   album, use albumId, for artist use artistName and for playlist use
     *                   playlistId
     * @param type the type of item that this tile drawable corresponds to
     * @param isCircle whether to draw a circle or a square
     * @param smallArtwork true if you want to draw a smaller version of the default bitmap for
     *                     perf/memory reasons
     */
    public static BitmapWithColors createDefaultBitmap(Context context, String identifier,
            ImageType type, boolean isCircle, boolean smallArtwork) {
        initializeStaticVariables(context.getResources());
        
        identifier = TimberUtils.getTrimmedName(identifier);

        // get the default bitmap to determine what to draw to
        Bitmap defaultBitmap = getDefaultBitmap(type, smallArtwork);
        final Rect bounds = new Rect(0, 0, defaultBitmap.getWidth(), defaultBitmap.getHeight());

        // create a bitmap and canvas for drawing
        Bitmap createdBitmap = Bitmap.createBitmap(defaultBitmap.getWidth(),
                defaultBitmap.getHeight(), defaultBitmap.getConfig());
        Canvas canvas = new Canvas(createdBitmap);

        int color = pickColor(identifier);
        int vibrantDarkColor = pickVibrantDarkColor(identifier);

        Paint paint = new Paint();
        paint.setColor(color);

        final int minDimension = Math.min(bounds.width(), bounds.height());

        if (isCircle) {
            canvas.drawCircle(bounds.centerX(), bounds.centerY(), minDimension / 2, paint);
        } else {
            canvas.drawRect(bounds, paint);
        }

        // draw to the bitmap
        drawBitmap(defaultBitmap, defaultBitmap.getWidth(), defaultBitmap.getHeight(), canvas,
                bounds, 1, 0, paint);

        return new BitmapWithColors(createdBitmap, identifier.hashCode(), color, vibrantDarkColor);
    }
}
