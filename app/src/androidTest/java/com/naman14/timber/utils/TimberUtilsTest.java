package com.naman14.timber.utils;

import android.graphics.Color;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TimberUtilsTest {

    public TimberUtils utils = new TimberUtils();


    @org.junit.Test
    public void getBlackWhiteColor() {
        //will return Black or White for contrast depending on the light intensity
        //Really Dark blue
        int DarkColor = Color.parseColor("#403f4d");
        //Really Light blue
        int LightColor = Color.parseColor("#d9d6fc");

        assertEquals(utils.getBlackWhiteColor(LightColor), Color.BLACK);
        assertEquals(utils.getBlackWhiteColor(DarkColor), Color.WHITE);

        //Black & White for boundries
        LightColor = Color.parseColor("#ffffff");
        DarkColor = Color.parseColor("#000000");

        assertEquals(utils.getBlackWhiteColor(LightColor), Color.BLACK);
        assertEquals(utils.getBlackWhiteColor(DarkColor), Color.WHITE);

        //For = case
        int Gray = Color.parseColor("#777777");
        assertEquals(utils.getBlackWhiteColor(Gray), Color.BLACK);
        assertEquals(Gray, Color.WHITE);
    }


    @org.junit.Test
    public void getIPAddress() {
        //Addresses to be spoofed on device and compared to
        String IPv4_Address = "3ffe:1900:4545:3:200:f8ff:fe21:67cf";
        String Non_IPv4_Address = "1.160.10.240";

        assertEquals(utils.getIPAddress(true), IPv4_Address);
        assertEquals(utils.getIPAddress(false), Non_IPv4_Address);
        //Should give an exeption
        assertNotEquals(utils.getIPAddress(true), Non_IPv4_Address);
    }
}