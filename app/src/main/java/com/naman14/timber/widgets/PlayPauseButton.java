package com.naman14.timber.widgets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

public class PlayPauseButton extends View {

    /**
     * √3
     */
    private final static double SQRT_3 = Math.sqrt(3);

    /**
     * Animationの速度の倍率
     */
    private final static int SPEED = 1;
    /**
     * 座標の基準を
     * ・中心を(0,0)
     * ・右上(1,1) 右下(1,ー1) 左上(ー1,1) 左下(ー1,ー1)
     * の座標時期に変換するためのClass
     */
    private final Point mPoint;
    /**
     * 描画するPaint
     */
    private Paint mPaint;
    /**
     * 左半分のPath
     */
    private Path mLeftPath;
    /**
     * 右半分のPath
     */
    private Path mRightPath;
    /**
     * 真ん中のAnimator
     */
    private ValueAnimator mCenterEdgeAnimator;
    /**
     * 一番左側のAnimator
     */
    private ValueAnimator mLeftEdgeAnimator;
    /**
     * 一番右側のAnimator
     */
    private ValueAnimator mRightEdgeAnimator;
    /**
     * ボタンの状況
     */
    private boolean mPlayed;

    /**
     * ボタンの色
     */
    private int mBackgroundColor = Color.BLACK;

    /**
     * AnimatorのUpdateListener
     */
    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener =
            new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    invalidate();
                }
            };

    private OnControlStatusChangeListener mListener;

    /**
     * コンストラクタ
     * {@inheritDoc}
     */
    public PlayPauseButton(Context context) {
        this(context, null, 0);
    }

    /**
     * コンストラクタ
     * {@inheritDoc}
     */
    public PlayPauseButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * コンストラクタ
     * android:backgroundのデータを取得しボタンの色とする
     * {@inheritDoc}
     */
    public PlayPauseButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPoint = new Point();
        initView();
    }

    /**
     * Viewの初期化
     */
    private void initView() {
        setUpPaint();
        setUpPath();
        setUpAnimator();
    }

    /**
     * Animatorの初期化
     * それぞれのAnimatorに初期値をセットし､startさせている
     */
    private void setUpAnimator() {
        if (mPlayed) {
            mCenterEdgeAnimator = ValueAnimator.ofFloat(1.f, 1.f);
            mLeftEdgeAnimator = ValueAnimator.ofFloat((float) (-0.2f * SQRT_3), (float) (-0.2f * SQRT_3));
            mRightEdgeAnimator = ValueAnimator.ofFloat(1.f, 1.f);
        } else {
            mCenterEdgeAnimator = ValueAnimator.ofFloat(0.5f, 0.5f);
            mLeftEdgeAnimator = ValueAnimator.ofFloat(0.f, 0.f);
            mRightEdgeAnimator = ValueAnimator.ofFloat(0.f, 0.f);
        }

        mCenterEdgeAnimator.start();
        mLeftEdgeAnimator.start();
        mRightEdgeAnimator.start();
    }

    /**
     * Paintの初期化
     */
    private void setUpPaint() {
        mPaint = new Paint();
        mPaint.setColor(mBackgroundColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * Pathの初期化
     */
    private void setUpPath() {
        mLeftPath = new Path();
        mRightPath = new Path();
    }

    /**
     * 描画処理
     * {@inheritDoc}
     */
    @Override
    protected void onDraw(Canvas canvas) {
        mPoint.setHeight(canvas.getHeight());
        mPoint.setWidth(canvas.getWidth());

        mLeftPath.reset();
        mRightPath.reset();

        //左半分Pathの設定
        mLeftPath.moveTo(mPoint.getX(-0.5 * SQRT_3), mPoint.getY(1.f));
        mLeftPath.lineTo(mPoint.getY((Float) mLeftEdgeAnimator.getAnimatedValue()) + 0.7f,
                mPoint.getY((Float) mCenterEdgeAnimator.getAnimatedValue()));
        mLeftPath.lineTo(mPoint.getY((Float) mLeftEdgeAnimator.getAnimatedValue()) + 0.7f,
                mPoint.getY(-1 * (Float) mCenterEdgeAnimator.getAnimatedValue()));
        mLeftPath.lineTo(mPoint.getX(-0.5 * SQRT_3), mPoint.getY(-1.f));

        //右半分Pathの設定
        mRightPath.moveTo(mPoint.getY(-1 * (Float) mLeftEdgeAnimator.getAnimatedValue()),
                mPoint.getY((Float) mCenterEdgeAnimator.getAnimatedValue()));
        mRightPath.lineTo(mPoint.getX(0.5 * SQRT_3),
                mPoint.getY((Float) mRightEdgeAnimator.getAnimatedValue()));
        mRightPath.lineTo(mPoint.getX(0.5 * SQRT_3),
                mPoint.getY(-1 * (Float) mRightEdgeAnimator.getAnimatedValue()));
        mRightPath.lineTo(mPoint.getY(-1 * (Float) mLeftEdgeAnimator.getAnimatedValue()),
                mPoint.getY(-1 * (Float) mCenterEdgeAnimator.getAnimatedValue()));

        canvas.drawPath(mLeftPath, mPaint);
        canvas.drawPath(mRightPath, mPaint);
    }

    /**
     * アニメーションを開始する
     */
    public void startAnimation() {
        mCenterEdgeAnimator = ValueAnimator.ofFloat(1.f, 0.5f);
        mCenterEdgeAnimator.setDuration(100 * SPEED);
        mCenterEdgeAnimator.addUpdateListener(mAnimatorUpdateListener);

        mLeftEdgeAnimator = ValueAnimator.ofFloat((float) (-0.2 * SQRT_3), 0.f);
        mLeftEdgeAnimator.setDuration(100 * SPEED);
        mLeftEdgeAnimator.addUpdateListener(mAnimatorUpdateListener);

        mRightEdgeAnimator = ValueAnimator.ofFloat(1.f, 0.f);
        mRightEdgeAnimator.setDuration(150 * SPEED);
        mRightEdgeAnimator.addUpdateListener(mAnimatorUpdateListener);

        if (!mPlayed) {
            mCenterEdgeAnimator.start();
            mLeftEdgeAnimator.start();
            mRightEdgeAnimator.start();
        } else {
            mCenterEdgeAnimator.reverse();
            mLeftEdgeAnimator.reverse();
            mRightEdgeAnimator.reverse();
        }
    }

    public void setOnControlStatusChangeListener(OnControlStatusChangeListener listener) {
        mListener = listener;
    }

//  /**
//   * ACTION_DOWNされたらアニメーションのスタートとか
//   * {@inheritDoc}
//   */
//  @Override public boolean onTouchEvent(@NonNull MotionEvent event) {
//    switch (event.getAction()) {
//      case MotionEvent.ACTION_DOWN:
//        setPlayed(!mPlayed);
//        startAnimation();
//        if (mListener != null) {
//          mListener.onStatusChange(this, mPlayed);
//        }
//        break;
//    }
//    return false;
//  }

    /**
     * 回転状況を {@link PlayPauseButton.SavedState} に保存する
     * {@inheritDoc}
     */
    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.played = isPlayed();
        return savedState;
    }

    /**
     * Restoreしてきたやつから {@link PlayPauseButton.SavedState}
     * を取得し状況をセットする
     * {@inheritDoc}
     */
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        setPlayed(savedState.played);
        setUpAnimator();
        invalidate();
    }

    /**
     * android:background で親の {@link View} が背景をセットするので上書き
     * {@inheritDoc}
     */
    @Override
    public void setBackground(Drawable background) {
    }

    /**
     * {@link PlayPauseButton#mPlayed} を返す
     *
     * @return {@link PlayPauseButton#mPlayed}
     */
    public boolean isPlayed() {
        return mPlayed;
    }

    /**
     * {@link PlayPauseButton#mPlayed} をセットする
     *
     * @param played 状況
     */
    public void setPlayed(boolean played) {
        if (mPlayed != played) {
            mPlayed = played;
            invalidate();
        }
    }

    /**
     * {@link PlayPauseButton#mBackgroundColor} をセットして再描画する
     *
     * @param color セットする色
     */
    public void setColor(int color) {
        mBackgroundColor = color;
        mPaint.setColor(mBackgroundColor);
        invalidate();
    }

    public interface OnControlStatusChangeListener {
        void onStatusChange(View view, boolean state);
    }

    /**
     * 画面開店時の状態を保存しておくやつ
     */
    static class SavedState extends BaseSavedState {

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
        boolean played;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            played = (Boolean) in.readValue(null);
        }

        @Override
        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeValue(played);
        }
    }

    /**
     * 座標の基準を
     * ・中心を(0,0)
     * ・右上(1,1) 右下(1,ー1) 左上(ー1,1) 左下(ー1,ー1)
     * の座標時期に変換するためのClass
     */
    static class Point {

        private int width;

        private int height;

        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public float getX(float x) {
            return (width / 2) * (x + 1);
        }

        public float getY(float y) {
            return (height / 2) * (y + 1);
        }

        public float getX(double x) {
            return getX((float) x);
        }

        public float getY(double y) {
            return getY((float) y);
        }
    }
}