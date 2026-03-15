// Handles adding and removing song files from playlist folders
package org.example;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class SongManager {

    private final JFrame parentFrame;
    private final MusicTreeManager treeManager;

    public SongManager(JFrame parentFrame, MusicTreeManager treeManager) {
        this.parentFrame = parentFrame;
        this.treeManager = treeManager;
    }

    public void addSong () {

                JFileChooser addSongFile = new JFileChooser();

                int result = addSongFile.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {

                    File selectedFile = addSongFile.getSelectedFile();

                    // Build list of available playlists
                    File rootFolder = new File("src/main/resources/PlaceHolder Name Songs");
                    List<File> playlistFolders = new ArrayList<>();
                    playlistFolders.add(rootFolder);
                    collectSubfolders(rootFolder, playlistFolders);

                    String[] playlistNames = playlistFolders.stream()
                            .map(File::getName)
                            .toArray(String[]::new);

                    String chosen = (String) JOptionPane.showInputDialog(
                            parentFrame,
                            "Select a playlist to add the song to:",
                            "Choose Playlist",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            playlistNames,
                            playlistNames[0]
                    );

                    if (chosen == null) {
                        return; // user cancelled
                    }

                    // Find the matching folder
                    File destinationFolder = playlistFolders.stream()
                            .filter(f -> f.getName().equals(chosen))
                            .findFirst()
                            .orElse(rootFolder);

                    Path destinationFile = destinationFolder.toPath().resolve(selectedFile.getName());

                    try {
                        Files.copy(
                                selectedFile.toPath(),
                                destinationFile,
                                StandardCopyOption.REPLACE_EXISTING
                        );

                        System.out.println("song added to " + chosen);
                        treeManager.refreshTree();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            public void removeSong () {

                JFileChooser removeSongFile = new JFileChooser();
                JOptionPane removeConfirm = new JOptionPane();
                JOptionPane.showMessageDialog(parentFrame, "This action will DELETE song", "Dialog Title", JOptionPane.WARNING_MESSAGE);

                // Set the chooser to open in the playlist folder
                removeSongFile.setCurrentDirectory(new File("src/main/resources/PlaceHolder Name Songs"));

                int result = removeSongFile.showDialog(parentFrame, "DELETE");


                if (result == JFileChooser.APPROVE_OPTION) {


                    File selectedFile = removeSongFile.getSelectedFile();

                    try {
                        Files.deleteIfExists(selectedFile.toPath());
                        System.out.println("song removed");
                        treeManager.refreshTree();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void collectSubfolders(File folder, List<File> result) {
                File[] children = folder.listFiles();
                if (children != null) {
                    for (File child : children) {
                        if (child.isDirectory()) {
                            result.add(child);
                            collectSubfolders(child, result);
                        }
                    }
                }
            }
}
