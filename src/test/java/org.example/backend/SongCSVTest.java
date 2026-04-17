package org.example.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SongCSVTest {

    private static final String TEST_FILE =
            "src/test/resources/RootSongsTest.csv";

    @BeforeEach
    void setUp() {
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testAddSongToCSV() {
        List<Song> songs = new ArrayList<>();

        Song song = new Song("TestSong", TEST_FILE);
        song.setGenre("Pop");
        song.setArtist("TestArtist");

        songs.add(song);

        boolean result = CsvStore.appendSongsToCsv(TEST_FILE, songs);

        assertTrue(result);

        List<Song> loaded = CsvStore.readSongsFromCsv(TEST_FILE);

        assertEquals(1, loaded.size());
        assertEquals("TestSong", loaded.get(0).getTitle());
    }

    @Test
    void testRemoveSongFromCSV() {
        List<Song> songs = new ArrayList<>();
        songs.add(new Song("TestSong", TEST_FILE));

        CsvStore.writeSongsToCsv(TEST_FILE, songs);

        songs.clear();

        boolean result = CsvStore.writeSongsToCsv(TEST_FILE, songs);

        assertTrue(result);

        List<Song> loaded = CsvStore.readSongsFromCsv(TEST_FILE);

        assertEquals(0, loaded.size());
    }
}