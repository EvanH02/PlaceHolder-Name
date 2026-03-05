package org.example;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class Playlist {
    private String name;
    private List<Song> songs;

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
    public void WriteCSV(){} //converts song list to csv so each song is an entry
    //writes to csv

    public void readCSV(File csv){
        //reads playlist csv into list


    }
}
