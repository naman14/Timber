package com.naman14.timber;

import org.junit.Test;

import static org.junit.Assert.*;

public class MusicPlayerTest {

    MusicPlayer player = new MusicPlayer();


    @Test
    public void getTrack() {
        player.getTrack(1);
    }

    @Test
    public void openFile() {
    }
}