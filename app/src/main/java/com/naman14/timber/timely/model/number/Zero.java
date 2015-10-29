package com.naman14.timber.timely.model.number;

import com.naman14.timber.timely.model.core.Figure;

public class Zero extends Figure {
    private static final float[][] POINTS = {
            {0.24585635359116f, 0.552486187845304f}, {0.24585635359116f, 0.331491712707182f}, {0.370165745856354f, 0.0994475138121547f},
            {0.552486187845304f, 0.0994475138121547f}, {0.734806629834254f, 0.0994475138121547f}, {0.861878453038674f, 0.331491712707182f},
            {0.861878453038674f, 0.552486187845304f}, {0.861878453038674f, 0.773480662983425f}, {0.734806629834254f, 0.994475138121547f},
            {0.552486187845304f, 0.994475138121547f}, {0.370165745856354f, 0.994475138121547f}, {0.24585635359116f, 0.773480662983425f},
            {0.24585635359116f, 0.552486187845304f}
    };

    private static Zero INSTANCE = new Zero();

    protected Zero() {
        super(POINTS);
    }

    public static Zero getInstance() {
        return INSTANCE;
    }
}