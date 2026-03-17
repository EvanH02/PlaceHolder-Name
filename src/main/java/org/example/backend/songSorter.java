package org.example.backend;

import java.util.*;

public class songSorter {

    public static List<Song> sortAlphabetically(List<Song> songs) {
        songs.sort(Comparator.comparing(Song::getTitle));
        return songs;
    }

    public static List<Song> sortGenre(List<Song> songs) {
        songs.sort(Comparator.comparing(Song::getGenre));
        return songs;
    }
}