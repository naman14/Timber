package com.naman14.timber.timely.animation;


import android.animation.TypeEvaluator;

public class TimelyEvaluator implements TypeEvaluator<float[][]> {
    private float[][] _cachedPoints = null;

    @Override
    public float[][] evaluate(float fraction, float[][] startValue, float[][] endValue) {
        int pointsCount = startValue.length;
        initCache(pointsCount);

        for(int i = 0; i < pointsCount; i++) {
            _cachedPoints[i][0] = startValue[i][0] + fraction * (endValue[i][0] - startValue[i][0]);
            _cachedPoints[i][1] = startValue[i][1] + fraction * (endValue[i][1] - startValue[i][1]);
        }

        return _cachedPoints;
    }

    private void initCache(int pointsCount) {
        if(_cachedPoints == null || _cachedPoints.length != pointsCount) {
            _cachedPoints = new float[pointsCount][2];
        }
    }

}
