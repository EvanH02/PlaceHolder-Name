package org.example.backend;

public class Song {
    private String title;
    private String genre;
    private String artist;
    private String lyrics;
    private String filePath;

    public Song(String title, String filePath) {
        this.title = title;
        this.filePath = filePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return title;
    }
    public String toCSVString() {
        return title+","+genre+","+artist+","+lyrics+","+filePath;
    }
}
