package org.example.backend;

import org.junit.jupiter.api.*;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlaylistTest {

    private Playlist playlist;
    private Song song1;
    private Song song2;

    @BeforeEach
    void setUp() {
        playlist = new Playlist("TestPlaylist");
        song1 = new Song("Song1", "path/to/song1.mp3");
        song2 = new Song("Song2", "path/to/song2.mp3");
    }

    @Test
    void testAddSong() {
        playlist.addSong(song1);
        List<Song> songs = playlist.getSongs();
        assertEquals(1, songs.size());
        assertTrue(songs.contains(song1));
    }

    @Test
    void testRemoveSong() {
        playlist.addSong(song1);
        playlist.addSong(song2);
        playlist.removeSong(song1);
        List<Song> songs = playlist.getSongs();
        assertEquals(1, songs.size());
        assertFalse(songs.contains(song1));
    }

    @Test
    void testSetNameAndGetName() {
        playlist.setName("NewName");
        assertEquals("NewName", playlist.getName());
    }

    @Test
    void testToString() {
        assertEquals("TestPlaylist", playlist.toString());
    }

    @Test
    void testWriteAndReadCSV() {
        playlist.addSong(song1);
        playlist.addSong(song2);

        // Write CSV
        playlist.writeCSV();
        File file = new File("src/main/resources/data/TestPlaylist.csv");
        assertTrue(file.exists(), "CSV file should be created");

        // Read CSV into new playlist
        Playlist newPlaylist = new Playlist("TestPlaylist");
        newPlaylist.readCSV(file);
        List<Song> songsFromCSV = newPlaylist.getSongs();

        assertEquals(2, songsFromCSV.size());
        assertEquals("Song1", songsFromCSV.get(0).getTitle());
        assertEquals("Song2", songsFromCSV.get(1).getTitle());
        file.delete();
    }
}