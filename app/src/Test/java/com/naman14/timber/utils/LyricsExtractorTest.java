package com.naman14.timber.utils;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class LyricsExtractorTest {

    @Test
    public void getLyrics() {
        LyricsExtractor le = new LyricsExtractor();
        //MP3 file containing lyrics in ID3 tag
        String File_1 = "Sample1.mp3";
        //AM4A file containing lyrics
        String File_2 = "Sample2.m4a";
        //OGG file contains lyrics in Vorbis tag
        String File_3 = "Sample3.ogg";

        assertEquals(le.getLyrics(new File("SONG.fake")), null);
        assertNotEquals(le.getLyrics(new File(File_1)), "This is not sample 1");
        assertEquals(le.getLyrics(new File(File_1)), "This is sample 1 now it's time to run lalala");
        assertEquals(le.getLyrics(new File(File_2)), "This is sample 2 beepboop beepboop");
        assertEquals(le.getLyrics(new File(File_3)), "This is sample 3 [Chorus] yeah its me!");

    }
}