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

package com.naman14.timber.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import com.naman14.timber.R;

import java.util.ArrayList;

@TargetApi(21)
public class PlayTransition extends Transition {
    private static final String PROPERTY_BOUNDS = "circleTransition:bounds";
    private static final String PROPERTY_POSITION = "circleTransition:position";
    private static final String PROPERTY_IMAGE = "circleTransition:image";
    private static final String[] TRANSITION_PROPERTIES = {
            PROPERTY_BOUNDS,
            PROPERTY_POSITION,
    };

    private int mColor = Color.parseColor("#6c1622");

    public PlayTransition() {
    }

    public PlayTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PlayTransition);
        setColor(a.getColor(R.styleable.PlayTransition_colorCT, getColor()));
        a.recycle();
    }

    public void setColor(int color) {
        mColor = color;
    }

    public int getColor() {
        return mColor;
    }

    @Override
    public String[] getTransitionProperties() {
        return TRANSITION_PROPERTIES;
    }

    private void captureValues(TransitionValues transitionValues) {
        final View view = transitionValues.view;
        transitionValues.values.put(PROPERTY_BOUNDS, new Rect(
                view.getLeft(), view.getTop(), view.getRight(), view.getBottom()
        ));
        int[] position = new int[2];
        transitionValues.view.getLocationInWindow(position);
        transitionValues.values.put(PROPERTY_POSITION, position);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        final View view = transitionValues.view;
        if (view.getWidth() <= 0 || view.getHeight() <= 0) {
            return;
        }
        captureValues(transitionValues);
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        final View view = transitionValues.view;
        if (view.getWidth() <= 0 || view.getHeight() <= 0) {
            return;
        }
        captureValues(transitionValues);
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        transitionValues.values.put(PROPERTY_IMAGE, bitmap);
    }

    @Override
    public Animator createAnimator(final ViewGroup sceneRoot, TransitionValues startValues,
                                   final TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }
        Rect startBounds = (Rect) startValues.values.get(PROPERTY_BOUNDS);
        Rect endBounds = (Rect) endValues.values.get(PROPERTY_BOUNDS);
        if (startBounds == null || endBounds == null || startBounds.equals(endBounds)) {
            return null;
        }
        Bitmap startImage = (Bitmap) startValues.values.get(PROPERTY_IMAGE);
        Drawable startBackground = new BitmapDrawable(sceneRoot.getContext().getResources(), startImage);
        final View startView = addViewToOverlay(sceneRoot, startImage.getWidth(),
                startImage.getHeight(), startBackground);
        Drawable shrinkingBackground = new ColorDrawable(mColor);
        final View shrinkingView = addViewToOverlay(sceneRoot, startImage.getWidth(),
                startImage.getHeight(), shrinkingBackground);

        int[] sceneRootLoc = new int[2];
        sceneRoot.getLocationInWindow(sceneRootLoc);
        int[] startLoc = (int[]) startValues.values.get(PROPERTY_POSITION);
        int startTranslationX = startLoc[0] - sceneRootLoc[0];
        int startTranslationY = startLoc[1] - sceneRootLoc[1];

        startView.setTranslationX(startTranslationX);
        startView.setTranslationY(startTranslationY);
        shrinkingView.setTranslationX(startTranslationX);
        shrinkingView.setTranslationY(startTranslationY);

        final View endView = endValues.view;
        float startRadius = calculateMaxRadius(shrinkingView);
        int minRadius = Math.min(calculateMinRadius(shrinkingView), calculateMinRadius(endView));

        ShapeDrawable circleBackground = new ShapeDrawable(new OvalShape());
        circleBackground.getPaint().setColor(mColor);
        final View circleView = addViewToOverlay(sceneRoot, minRadius * 2, minRadius * 2,
                circleBackground);
        float circleStartX = startLoc[0] - sceneRootLoc[0] +
                ((startView.getWidth() - circleView.getWidth()) / 2);
        float circleStartY = startLoc[1] - sceneRootLoc[1] +
                ((startView.getHeight() - circleView.getHeight()) / 2);
        circleView.setTranslationX(circleStartX);
        circleView.setTranslationY(circleStartY);

        circleView.setVisibility(View.INVISIBLE);
        shrinkingView.setAlpha(0f);
        endView.setAlpha(0f);

        Animator shrinkingAnimator = createCircularReveal(shrinkingView, startRadius, minRadius);
        shrinkingAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                shrinkingView.setVisibility(View.INVISIBLE);
                startView.setVisibility(View.INVISIBLE);
                circleView.setVisibility(View.VISIBLE);
            }
        });

        Animator startAnimator = createCircularReveal(startView, startRadius, minRadius);
        Animator fadeInAnimator = ObjectAnimator.ofFloat(shrinkingView, View.ALPHA, 0, 1);

        AnimatorSet shrinkFadeSet = new AnimatorSet();
        shrinkFadeSet.playTogether(shrinkingAnimator, startAnimator,
                fadeInAnimator);

        int[] endLoc = (int[]) endValues.values.get(PROPERTY_POSITION);
        float circleEndX = endLoc[0] - sceneRootLoc[0] +
                ((endView.getWidth() - circleView.getWidth()) / 2);
        float circleEndY = endLoc[1] - sceneRootLoc[1] +
                ((endView.getHeight() - circleView.getHeight()) / 2);
        Path circlePath = getPathMotion().getPath(circleStartX, circleStartY, circleEndX,
                circleEndY);
        Animator circleAnimator = ObjectAnimator.ofFloat(circleView, View.TRANSLATION_X,
                View.TRANSLATION_Y, circlePath);

        final View growingView = addViewToOverlay(sceneRoot, endView.getWidth(),
                endView.getHeight(), shrinkingBackground);
        growingView.setVisibility(View.INVISIBLE);
        float endTranslationX = endLoc[0] - sceneRootLoc[0];
        float endTranslationY = endLoc[1] - sceneRootLoc[1];
        growingView.setTranslationX(endTranslationX);
        growingView.setTranslationY(endTranslationY);

        float endRadius = calculateMaxRadius(endView);

        circleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                circleView.setVisibility(View.INVISIBLE);
                growingView.setVisibility(View.VISIBLE);
                endView.setAlpha(1f);
            }
        });

        Animator fadeOutAnimator = ObjectAnimator.ofFloat(growingView, View.ALPHA, 1, 0);
        Animator endAnimator = createCircularReveal(endView, minRadius, endRadius);
        Animator growingAnimator = createCircularReveal(growingView, minRadius, endRadius);

        growingAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                sceneRoot.getOverlay().remove(startView);
                sceneRoot.getOverlay().remove(shrinkingView);
                sceneRoot.getOverlay().remove(circleView);
                sceneRoot.getOverlay().remove(growingView);
            }
        });
        AnimatorSet growingFadeSet = new AnimatorSet();
        growingFadeSet.playTogether(fadeOutAnimator, endAnimator, growingAnimator);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(shrinkFadeSet, circleAnimator, growingFadeSet);
        return animatorSet;
    }

    private View addViewToOverlay(ViewGroup sceneRoot, int width, int height, Drawable background) {
        View view = new NoOverlapView(sceneRoot.getContext());
        view.setBackground(background);
        int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        view.measure(widthSpec, heightSpec);
        view.layout(0, 0, width, height);
        sceneRoot.getOverlay().add(view);
        return view;
    }

    private Animator createCircularReveal(View view, float startRadius, float endRadius) {
        int centerX = view.getWidth() / 2;
        int centerY = view.getHeight() / 2;

        Animator reveal = ViewAnimationUtils.createCircularReveal(view, centerX, centerY,
                startRadius, endRadius);
        return new NoPauseAnimator(reveal);
    }

    static float calculateMaxRadius(View view) {
        float widthSquared = view.getWidth() * view.getWidth();
        float heightSquared = view.getHeight() * view.getHeight();
        float radius = (float) Math.sqrt(widthSquared + heightSquared) / 2;
        return radius;
    }

    static int calculateMinRadius(View view) {
        return Math.min(view.getWidth() / 2, view.getHeight() / 2);
    }

    private static class NoPauseAnimator extends Animator {
        private final Animator mAnimator;
        private final ArrayMap<AnimatorListener, AnimatorListener> mListeners =
                new ArrayMap<AnimatorListener, AnimatorListener>();

        public NoPauseAnimator(Animator animator) {
            mAnimator = animator;
        }

        @Override
        public void addListener(AnimatorListener listener) {
            AnimatorListener wrapper = new AnimatorListenerWrapper(this, listener);
            if (!mListeners.containsKey(listener)) {
                mListeners.put(listener, wrapper);
                mAnimator.addListener(wrapper);
            }
        }

        @Override
        public void cancel() {
            mAnimator.cancel();
        }

        @Override
        public void end() {
            mAnimator.end();
        }

        @Override
        public long getDuration() {
            return mAnimator.getDuration();
        }

        @Override
        public TimeInterpolator getInterpolator() {
            return mAnimator.getInterpolator();
        }

        @Override
        public ArrayList<AnimatorListener> getListeners() {
            return new ArrayList<AnimatorListener>(mListeners.keySet());
        }

        @Override
        public long getStartDelay() {
            return mAnimator.getStartDelay();
        }

        @Override
        public boolean isPaused() {
            return mAnimator.isPaused();
        }

        @Override
        public boolean isRunning() {
            return mAnimator.isRunning();
        }

        @Override
        public boolean isStarted() {
            return mAnimator.isStarted();
        }

        @Override
        public void removeAllListeners() {
            mListeners.clear();
            mAnimator.removeAllListeners();
        }

        @Override
        public void removeListener(AnimatorListener listener) {
            AnimatorListener wrapper = mListeners.get(listener);
            if (wrapper != null) {
                mListeners.remove(listener);
                mAnimator.removeListener(wrapper);
            }
        }

        @Override
        public Animator setDuration(long durationMS) {
            mAnimator.setDuration(durationMS);
            return this;
        }

        @Override
        public void setInterpolator(TimeInterpolator timeInterpolator) {
            mAnimator.setInterpolator(timeInterpolator);
        }

        @Override
        public void setStartDelay(long delayMS) {
            mAnimator.setStartDelay(delayMS);
        }

        @Override
        public void setTarget(Object target) {
            mAnimator.setTarget(target);
        }

        @Override
        public void setupEndValues() {
            mAnimator.setupEndValues();
        }

        @Override
        public void setupStartValues() {
            mAnimator.setupStartValues();
        }

        @Override
        public void start() {
            mAnimator.start();
        }
    }

    private static class AnimatorListenerWrapper implements Animator.AnimatorListener {
        private final Animator mAnimator;
        private final Animator.AnimatorListener mListener;

        public AnimatorListenerWrapper(Animator animator, Animator.AnimatorListener listener) {
            mAnimator = animator;
            mListener = listener;
        }

        @Override
        public void onAnimationStart(Animator animator) {
            mListener.onAnimationStart(mAnimator);
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            mListener.onAnimationEnd(mAnimator);
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            mListener.onAnimationCancel(mAnimator);
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
            mListener.onAnimationRepeat(mAnimator);
        }
    }

    private static class NoOverlapView extends View {
        public NoOverlapView(Context context) {
            super(context);
        }

        @Override
        public boolean hasOverlappingRendering() {
            return false;
        }
    }
}