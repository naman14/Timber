/*
* Copyright 2014 Adnan A M.

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at

*   http://www.apache.org/licenses/LICENSE-2.0

* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.naman14.timber.timely.animation;

import android.animation.TypeEvaluator;

public class TimelyEvaluator implements TypeEvaluator<float[][]> {
    private float[][] _cachedPoints = null;

    @Override
    public float[][] evaluate(float fraction, float[][] startValue, float[][] endValue) {
        int pointsCount = startValue.length;
        initCache(pointsCount);

        for (int i = 0; i < pointsCount; i++) {
            _cachedPoints[i][0] = startValue[i][0] + fraction * (endValue[i][0] - startValue[i][0]);
            _cachedPoints[i][1] = startValue[i][1] + fraction * (endValue[i][1] - startValue[i][1]);
        }

        return _cachedPoints;
    }

    private void initCache(int pointsCount) {
        if (_cachedPoints == null || _cachedPoints.length != pointsCount) {
            _cachedPoints = new float[pointsCount][2];
        }
    }

}
