package com.naman14.timber.timely.model.number;

import com.naman14.timber.timely.model.core.Figure;

public class Nine extends Figure {
    private static final float[][] POINTS = {
            {0.80939226519337f, 0.552486187845304f}, {0.685082872928177f, 0.751381215469613f}, {0.298342541436464f, 0.740331491712707f},
            {0.259668508287293f, 0.408839779005525f}, {0.232044198895028f, 0.0441988950276243f}, {0.81767955801105f, -0.0441988950276243f},
            {0.850828729281768f, 0.408839779005525f}, {0.839779005524862f, 0.596685082872928f}, {0.712707182320442f, 0.668508287292818f},
            {0.497237569060773f, 0.994475138121547f}, {0.497237569060773f, 0.994475138121547f}, {0.497237569060773f, 0.994475138121547f},
            {0.497237569060773f, 0.994475138121547f}
    };

    private static Nine INSTANCE = new Nine();

    protected Nine() {
        super(POINTS);
    }

    public static Nine getInstance() {
        return INSTANCE;
    }
}