package org.example.backend;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Playlist {
    private String name;
    private List<Song> songs;
    private String csvHeader= "Name,Genre,Artist,Lyrics,FilePath";
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
    public void writeCSV(){
        String csvpath="src/main/resources/data/";
        String fileName= csvpath+name+".csv";
         try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
             writer.write(csvHeader+"\n");
            for (Song s: songs) {
                writer.write(s.toCSVString());
                writer.newLine();
                System.out.println("Successfully wrote to the file.");

            }
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }

    }
        //writes to csv

    public void readCSV(File csv){
        //reads  csv into list
        int cursong=0;
        try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
            String line;

            String header=br.readLine();//header check here

            while ((line = br.readLine()) != null) {
               // System.out.println(csvHeader+"oe");
              //System.out.println("式"+line);
                String[] values = line.split(COMMA_DELIMITER);


                songs.add(cursong,new Song(values[0],values[4]));
                cursong++;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    //songs.remove(0);
    }
    public static void main(String[] args) {
    File csv = new File("src/main/resources/PlaceHolder Name Songs/test.csv");
        System.out.println(System.getProperty("user.dir"));
//find out how to change directory
    Playlist test= new Playlist("PLaylistTest");


    test.readCSV(csv);
         System.out.println(test.songs.toString());
         test.writeCSV();
    }


}
