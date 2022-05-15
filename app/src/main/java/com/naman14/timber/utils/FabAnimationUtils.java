/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.naman14.timber.utils;

import android.os.Build;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import com.naman14.timber.R;

public class FabAnimationUtils {

    private static final long DEFAULT_DURATION = 200L;
    private static final Interpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new FastOutSlowInInterpolator();

    public static void scaleIn(final View fab) {
        scaleIn(fab, DEFAULT_DURATION, null);
    }

    public static void scaleIn(final View fab, long duration, final ScaleCallback callback) {
        fab.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ViewCompat.animate(fab)
                    .scaleX(1.0F)
                    .scaleY(1.0F)
                    .alpha(1.0F)
                    .setDuration(duration)
                    .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                    .withLayer()
                    .setListener(new ViewPropertyAnimatorListener() {
                        public void onAnimationStart(View view) {
                            if (callback != null) callback.onAnimationStart();
                        }

                        public void onAnimationCancel(View view) {
                        }

                        public void onAnimationEnd(View view) {
                            view.setVisibility(View.VISIBLE);
                            if (callback != null) callback.onAnimationEnd();
                        }
                    }).start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(fab.getContext(), R.anim.design_fab_out);
            anim.setDuration(duration);
            anim.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
            anim.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    if (callback != null) callback.onAnimationStart();
                }

                public void onAnimationEnd(Animation animation) {
                    fab.setVisibility(View.VISIBLE);
                    if (callback != null) callback.onAnimationEnd();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    //
                }
            });
            fab.startAnimation(anim);
        }
    }

    public static void scaleOut(final View fab) {
        scaleOut(fab, DEFAULT_DURATION, null);
    }

    public static void scaleOut(final View fab, final ScaleCallback callback) {
        scaleOut(fab, DEFAULT_DURATION, callback);
    }

    public static void scaleOut(final View fab, long duration, final ScaleCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ViewCompat.animate(fab)
                    .scaleX(0.0F)
                    .scaleY(0.0F).alpha(0.0F)
                    .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                    .setDuration(duration)
                    .withLayer()
                    .setListener(new ViewPropertyAnimatorListener() {
                        public void onAnimationStart(View view) {
                            if (callback != null) callback.onAnimationStart();
                        }

                        public void onAnimationCancel(View view) {
                        }

                        public void onAnimationEnd(View view) {
                            view.setVisibility(View.INVISIBLE);
                            if (callback != null) callback.onAnimationEnd();
                        }
                    }).start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(fab.getContext(), R.anim.design_fab_out);
            anim.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
            anim.setDuration(duration);
            anim.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    if (callback != null) callback.onAnimationStart();
                }

                public void onAnimationEnd(Animation animation) {
                    fab.setVisibility(View.INVISIBLE);
                    if (callback != null) callback.onAnimationEnd();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    //
                }
            });
            fab.startAnimation(anim);
        }
    }

    public interface ScaleCallback {
        void onAnimationStart();

        void onAnimationEnd();
    }


}
