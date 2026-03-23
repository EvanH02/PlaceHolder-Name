// Handles creating new playlist folders
package org.example.frontend;

import org.example.backend.CsvStore;
import org.example.backend.Playlist;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class PlaylistManager {

    private final MusicTreeManager treeManager;

    public PlaylistManager(MusicTreeManager treeManager) {
        this.treeManager = treeManager;
    }

    public Playlist createPlaylist () {
        String name = JOptionPane.showInputDialog("Enter playlist name:");
        if (name == null || name.trim().isEmpty()) return null;
        Playlist savePlaylist = new Playlist(name);
        String filename = CsvStore.DATA_DIR + name + ".csv";
        File newplaylist = new File(filename);
        try {
            newplaylist.getParentFile().mkdirs();
            boolean created = newplaylist.createNewFile();
            if (created) {
                CsvStore.writeSongsToCsv(filename, new ArrayList<>());
                System.out.println("Created: " + newplaylist.getAbsolutePath());
                treeManager.refreshTree();
            } else {
                System.out.println("File already exists or failed to create.");
            }
        } catch (Exception e) {
            System.out.println("Failed to create playlist file: " + e.getMessage());
        }
        return savePlaylist;

    }
}
