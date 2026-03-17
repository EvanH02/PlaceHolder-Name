package org.example.backend;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class songSorterTest {
    @Test
    void testSortSongsAlphabetically() {
        List<Song> songs = new ArrayList<>();

        songs.add(new Song("C Song", "path"));
        songs.add(new Song("A Song", "path"));
        songs.add(new Song("B Song", "path"));

        List<Song> sorted = songSorter.sortAlphabetically(songs);

        assertEquals("A Song", sorted.get(0).getTitle());
        assertEquals("B Song", sorted.get(1).getTitle());
        assertEquals("C Song", sorted.get(2).getTitle());
    }
    @Test
    void testSortSongsGenre() {
        List<Song> songs = new ArrayList<>();

        songs.add(new Song("C Song", "path","Hip-Hop"));
        songs.add(new Song("A Song", "path","Blues"));
        songs.add(new Song("B Song", "path","Rap"));

        List<Song> sorted = songSorter.sortGenre(songs);

        assertEquals("A Song", sorted.get(0).getTitle());
        assertEquals("C Song", sorted.get(1).getTitle());
        assertEquals("B Song", sorted.get(2).getTitle());
    }
}
