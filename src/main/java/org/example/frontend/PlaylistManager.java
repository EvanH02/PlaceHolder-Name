// Handles creating new playlist folders
package org.example.frontend;

import org.example.backend.Playlist;

import javax.swing.*;
import java.io.File;

public class PlaylistManager {

    private final MusicTreeManager treeManager;

    public PlaylistManager(MusicTreeManager treeManager) {
        this.treeManager = treeManager;
    }

    public Playlist createPlaylist () {
        String name = JOptionPane.showInputDialog("Enter playlist name:");
        //change this later to its own folder
        Playlist savePlaylist = new Playlist(name);
        File newplaylist = new File(
                System.getProperty("user.dir") + "/src/main/resources/PlaceHolder Name Songs/" + name
        );
        if (newplaylist.mkdirs()) {
            System.out.println("Created: " + newplaylist.getAbsolutePath());
            treeManager.refreshTree();
        } else {
            System.out.println("Folder already exists or failed.");
        }
        System.out.println(System.getProperty("user.dir") + "src/main/resources/PlaceHolder Name Songs/" + name);
        return savePlaylist;

    }
}
