package org.example.backend;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.example.backend.CsvStore;

public class Playlist {
    private String name;
    private List<Song> songs;
    public static final String COMMA_DELIMITER = ",";//"\t";

    public Playlist(String name) {
        this.name = name;
        this.songs = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void addSong(Song song) {
        this.songs.add(song);
    }

    public void removeSong(Song song) {
        this.songs.remove(song);
    }

    @Override
    public String toString() {
        return name;
    }

    public void writeCSV() {
        String fileName = CsvStore.DATA_DIR + name + ".csv";
        CsvStore.writeSongsToCsv(fileName, songs);
    }

    public void readCSV(File csv) {
        List<Song> loaded = CsvStore.readSongsFromCsv(csv.getAbsolutePath());
        this.songs.clear();
        this.songs.addAll(loaded);
    }


    //songs.remove(0);


    public static void main(String[] args) {
        File csv = new File("src/main/resources/PlaceHolder Name Songs/test.csv");
        System.out.println(System.getProperty("user.dir"));
//find out how to change directory
        Playlist test = new Playlist("PLaylistTest");


        //test.readCSV(csv);
        System.out.println(test.songs.toString());
        test.writeCSV();
    }

}



