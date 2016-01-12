package com.naman14.timber.timely.model.number;

import com.naman14.timber.timely.model.core.Figure;

public class Five extends Figure {
    private static final float[][] POINTS = {
            {0.806629834254144f, 0.110497237569061f}, {0.502762430939227f, 0.110497237569061f}, {0.502762430939227f, 0.110497237569061f},
            {0.502762430939227f, 0.110497237569061f}, {0.397790055248619f, 0.430939226519337f}, {0.397790055248619f, 0.430939226519337f},
            {0.397790055248619f, 0.430939226519337f}, {0.535911602209945f, 0.364640883977901f}, {0.801104972375691f, 0.469613259668508f},
            {0.801104972375691f, 0.712707182320442f}, {0.773480662983425f, 1.01104972375691f}, {0.375690607734807f, 1.0939226519337f},
            {0.248618784530387f, 0.850828729281768f}
    };

    private static Five INSTANCE = new Five();

    protected Five() {
        super(POINTS);
    }

    public static Five getInstance() {
        return INSTANCE;
    }
}