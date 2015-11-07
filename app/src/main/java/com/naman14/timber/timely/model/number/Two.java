package com.naman14.timber.timely.model.number;

import com.naman14.timber.timely.model.core.Figure;

public class Two extends Figure {
    private static final float[][] POINTS = {
            {0.30939226519337f, 0.331491712707182f}, {0.325966850828729f, 0.0110497237569061f}, {0.790055248618785f, 0.0220994475138122f},
            {0.798342541436464f, 0.337016574585635f}, {0.798342541436464f, 0.430939226519337f}, {0.718232044198895f, 0.541436464088398f},
            {0.596685082872928f, 0.674033149171271f}, {0.519337016574586f, 0.762430939226519f}, {0.408839779005525f, 0.856353591160221f},
            {0.314917127071823f, 0.977900552486188f}, {0.314917127071823f, 0.977900552486188f}, {0.812154696132597f, 0.977900552486188f},
            {0.812154696132597f, 0.977900552486188f}
    };

    private static Two INSTANCE = new Two();

    protected Two() {
        super(POINTS);
    }

    public static Two getInstance() {
        return INSTANCE;
    }
}