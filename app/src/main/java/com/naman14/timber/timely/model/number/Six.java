package com.naman14.timber.timely.model.number;

import com.naman14.timber.timely.model.core.Figure;

public class Six extends Figure {
    private static final float[][] POINTS = {
            {0.607734806629834f, 0.110497237569061f}, {0.607734806629834f, 0.110497237569061f}, {0.607734806629834f, 0.110497237569061f},
            {0.607734806629834f, 0.110497237569061f}, {0.392265193370166f, 0.43646408839779f}, {0.265193370165746f, 0.50828729281768f},
            {0.25414364640884f, 0.696132596685083f}, {0.287292817679558f, 1.13017127071823f}, {0.87292817679558f, 1.06077348066298f},
            {0.845303867403315f, 0.696132596685083f}, {0.806629834254144f, 0.364640883977901f}, {0.419889502762431f, 0.353591160220994f},
            {0.295580110497238f, 0.552486187845304f}
    };

    private static Six INSTANCE = new Six();

    protected Six() {
        super(POINTS);
    }

    public static Six getInstance() {
        return INSTANCE;
    }
}