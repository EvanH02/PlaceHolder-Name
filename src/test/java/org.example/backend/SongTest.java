package org.example.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SongTest {

    private Song song;

    @BeforeEach
    void setUp() {
        song = new Song("TestSong", "path/to/song.mp3");
    }

    @Test
    void testGetTitle() {
        assertEquals("TestSong", song.getTitle());
    }

    @Test
    void testSetTitle() {
        song.setTitle("NewTitle");
        assertEquals("NewTitle", song.getTitle());
    }

    @Test
    void testGetFilePath() {
        assertEquals("path/to/song.mp3", song.getFilePath());
    }

    @Test
    void testSetFilePath() {
        song.setFilePath("new/path/song.mp3");
        assertEquals("new/path/song.mp3", song.getFilePath());
    }

    @Test
    void testToString() {
        assertEquals("TestSong", song.toString());
    }

    @Test
    void testToCSVString() {
        String expected = "TestSong,null,null";
        assertEquals(expected, song.toCSVString());
    }
}