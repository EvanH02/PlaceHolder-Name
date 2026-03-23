// CSV utility to read/write songs for the app
package org.example.backend;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CsvStore {
    public static final String ROOT_CSV = "src/main/resources/data/RootSongs.csv";
    public static final String DATA_DIR = "src/main/resources/data/";
    public static final String HEADER = "Name,Genre,Artist";

    public static List<Song> readSongsFromCsv(String path) {
        List<Song> songs = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) return songs;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                String[] parts = splitCsvLine(line);
                if (parts.length >= 1) {
                    String name = parts.length > 0 ? parts[0] : "";
                    String genre = parts.length > 1 ? parts[1] : "";
                    String artist = parts.length > 2 ? parts[2] : "";
                    Song s = new Song(name, path);
                    s.setGenre(genre);
                    s.setArtist(artist);
                    songs.add(s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return songs;
    }

    public static synchronized boolean appendSongsToCsv(String path, List<Song> songs) {
        File file = new File(path);
        try {
            file.getParentFile().mkdirs();
            boolean writeHeader = !file.exists();
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
                if (writeHeader) {
                    bw.write(HEADER);
                    bw.newLine();
                }
                for (Song s : songs) {
                    bw.write(escapeCsv(s.getTitle()) + "," + escapeCsv(nullToEmpty(s.getGenre())) + "," + escapeCsv(nullToEmpty(s.getArtist())));
                    bw.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static synchronized boolean writeSongsToCsv(String path, List<Song> songs) {
        File file = new File(path);
        try {
            file.getParentFile().mkdirs();
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {
                bw.write(HEADER);
                bw.newLine();
                for (Song s : songs) {
                    bw.write(escapeCsv(s.getTitle()) + "," + escapeCsv(nullToEmpty(s.getGenre())) + "," + escapeCsv(nullToEmpty(s.getArtist())));
                    bw.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    // very small CSV escape
    private static String escapeCsv(String s) {
        if (s == null) return "";
        boolean needQuotes = s.contains(",") || s.contains("\n") || s.contains("\r") || s.contains("\"");
        String out = s.replace("\"", "\"\"");
        if (needQuotes) {
            out = "\"" + out + "\"";
        }
        return out;
    }

    private static String[] splitCsvLine(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cur.append('"');
                    i++; // skip escaped quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                parts.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        parts.add(cur.toString());
        return parts.toArray(new String[0]);
    }
}
