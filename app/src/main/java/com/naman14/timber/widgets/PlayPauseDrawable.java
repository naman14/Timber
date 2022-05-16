/**
 * This code was modified by me, Paul Woitaschek. All these changes are licensed under GPLv3. The
 * original source can be found here: {@link https://github.com/alexjlockwood/material-pause-play-
 * animation/blob/master/app/src/main/java/com/alexjlockwood/example/playpauseanimation/
 * PlayPauseView.java}
 * <p/>
 * The original licensing is as follows:
 * <p/>
 * <p/>
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Alex Lockwood
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.naman14.timber.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.util.Log;
import android.util.Property;
import android.view.animation.DecelerateInterpolator;


public class PlayPauseDrawable extends Drawable {


    private static final String TAG = PlayPauseDrawable.class.getSimpleName();
    private final Path leftPauseBar = new Path();
    private final Path rightPauseBar = new Path();
    private final Paint paint = new Paint();
    private float progress;
    private static final Property<PlayPauseDrawable, Float> PROGRESS =
            new Property<PlayPauseDrawable, Float>(Float.class, "progress") {
                @Override
                public Float get(PlayPauseDrawable d) {
                    return d.getProgress();
                }

                @Override
                public void set(PlayPauseDrawable d, Float value) {
                    d.setProgress(value);
                }
            };
    private boolean isPlay;
    @Nullable
    private Animator animator;

    public PlayPauseDrawable() {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
    }

    /**
     * Linear interpolate between a and b with parameter t.
     */
    private static float interpolate(float a, float b, float t) {
        return a + (b - a) * t;
    }

    @Override
    public void draw(Canvas canvas) {
        long startDraw = System.currentTimeMillis();

        leftPauseBar.rewind();
        rightPauseBar.rewind();

        // move to center of canvas
        canvas.translate(getBounds().left, getBounds().top);

        float pauseBarHeight = 7.0F / 12.0F * ((float) getBounds().height());
        float pauseBarWidth = pauseBarHeight / 3.0F;
        float pauseBarDistance = pauseBarHeight / 3.6F;

        // The current distance between the two pause bars.
        final float barDist = interpolate(pauseBarDistance, 0.0F, progress);
        // The current width of each pause bar.
        final float barWidth = interpolate(pauseBarWidth, pauseBarHeight / 1.75F, progress);
        // The current position of the left pause bar's top left coordinate.
        final float firstBarTopLeft = interpolate(0.0F, barWidth, progress);
        // The current position of the right pause bar's top right coordinate.
        final float secondBarTopRight = interpolate(2.0F * barWidth + barDist, barWidth + barDist, progress);

        // Draw the left pause bar. The left pause bar transforms into the
        // top half of the play button triangle by animating the position of the
        // rectangle's top left coordinate and expanding its bottom width.
        leftPauseBar.moveTo(0.0F, 0.0F);
        leftPauseBar.lineTo(firstBarTopLeft, -pauseBarHeight);
        leftPauseBar.lineTo(barWidth, -pauseBarHeight);
        leftPauseBar.lineTo(barWidth, 0.0F);
        leftPauseBar.close();

        // Draw the right pause bar. The right pause bar transforms into the
        // bottom half of the play button triangle by animating the position of the
        // rectangle's top right coordinate and expanding its bottom width.
        rightPauseBar.moveTo(barWidth + barDist, 0.0F);
        rightPauseBar.lineTo(barWidth + barDist, -pauseBarHeight);
        rightPauseBar.lineTo(secondBarTopRight, -pauseBarHeight);
        rightPauseBar.lineTo(2.0F * barWidth + barDist, 0.0F);
        rightPauseBar.close();

        canvas.save();

        // Translate the play button a tiny bit to the right so it looks more centered.
        canvas.translate(interpolate(0.0F, pauseBarHeight / 8.0F, progress), 0.0F);

        // (1) Pause --> Play: rotate 0 to 90 degrees clockwise.
        // (2) Play --> Pause: rotate 90 to 180 degrees clockwise.
        final float rotationProgress = isPlay ? 1.0F - progress : progress;
        final float startingRotation = isPlay ? 90.0F : 0.0F;
        canvas.rotate(interpolate(startingRotation, startingRotation + 90.0F, rotationProgress), getBounds().width() / 2.0F, getBounds().height() / 2.0F);

        // Position the pause/play button in the center of the drawable's bounds.
        canvas.translate(getBounds().width() / 2.0F - ((2.0F * barWidth + barDist) / 2.0F), getBounds().height() / 2.0F + (pauseBarHeight / 2.0F));

        // Draw the two bars that form the animated pause/play button.
        canvas.drawPath(leftPauseBar, paint);
        canvas.drawPath(rightPauseBar, paint);

        canvas.restore();

        long timeElapsed = System.currentTimeMillis() - startDraw;
        if (timeElapsed > 16) {
            Log.e(TAG, "Drawing took too long=" + timeElapsed);
        }
    }

    public void transformToPause(boolean animated) {
        if (isPlay) {
            if (animated) {
                toggle();
            } else {
                isPlay = false;
                setProgress(0.0F);
            }
        }
    }

    @Override
    public void jumpToCurrentState() {
        Log.v(TAG, "jumpToCurrentState()");
        if (animator != null) {
            animator.cancel();
        }
        setProgress(isPlay ? 1.0F : 0.0F);
    }

    public void transformToPlay(boolean animated) {
        if (!isPlay) {
            if (animated) {
                toggle();
            } else {
                isPlay = true;
                setProgress(1.0F);
            }
        }
    }

    private void toggle() {
        if (animator != null) {
            animator.cancel();
        }

        animator = ObjectAnimator.ofFloat(this, PROGRESS, isPlay ? 1.0F : 0.0F, isPlay ? 0.0F : 1.0F);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isPlay = !isPlay;
            }
        });

        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(200);
        animator.start();
    }

    private float getProgress() {
        return progress;
    }

    private void setProgress(float progress) {
        this.progress = progress;
        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
        invalidateSelf();
    }


    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}