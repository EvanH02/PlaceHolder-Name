package org.example.frontend;

import org.example.backend.Playlist;
import org.example.backend.Song;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class PlaylistRemover {

    private final JFrame parentFrame;
    private final MusicTreeManager treeManager;

    public PlaylistRemover(JFrame parentFrame, MusicTreeManager treeManager) {
        this.parentFrame = parentFrame;
        this.treeManager = treeManager;
    }

    public void showstheplaylistremovedDialog() {
        Playlist selectedPlaylist = selectPlaylist();

        if (selectedPlaylist == null) {
            return;
        }
        showSongRemovalDialog(selectedPlaylist);
    }
    //User choose which playlist to remove songs from
    private Playlist selectPlaylist() {
        List<Playlist> playlists = getAllPlaylists();

        if (playlists.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame,
                    "No playlist found",
                    "Remove from playlist",
                    JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        String[] playlistNames = playlists.stream()
                .map(Playlist::getName)
                .toArray(String[]::new);

        String chosen = (String) JOptionPane.showInputDialog(
                parentFrame,
                "Select a Playlist to remove from:",
                "Choose the playlist",
                JOptionPane.QUESTION_MESSAGE,
                null,
                playlistNames,
                playlistNames[0]
        );

        if (chosen == null) {
            return null;
        }

        // finding the matching playlist
        return playlists.stream()
                .filter(p -> p.getName().equals(chosen))
                .findFirst()
                .orElse(null);
    }

    // playlists from the filesystem

    private List<Playlist> getAllPlaylists() {
        List<Playlist> playlists = new ArrayList<>();
        File rootFolder = new File("src/main/resources/PlaceHolder Name Songs");

        if (rootFolder.exists() && rootFolder.isDirectory()) {
            File[] folders = rootFolder.listFiles(File::isDirectory);
            if (folders != null) {
                for (File folder : folders) {
                    Playlist playlist = new Playlist(folder.getName());
                    // loading songs from folder
                    File[] songFiles = folder.listFiles((dir, name) ->
                            name.toLowerCase().endsWith(".mp3") ||
                                    name.toLowerCase().endsWith(".wav") ||
                                    name.toLowerCase().endsWith(".m4a"));

                    if (songFiles != null) {
                        for (File songFile : songFiles) {
                            Song song = new Song(songFile.getName(), songFile.getAbsolutePath());
                            playlist.addSong(song);
                        }
                    }
                    playlists.add(playlist);
                }
            }
        }

        return playlists;
    }

    // get all songs in a specific playlist

    private List<Song> getSongsInPlaylist(Playlist playlist) {
        List<Song> songs = new ArrayList<>();
        File playlistFolder = new File("src/main/resources/PlaceHolder Name Songs/" + playlist.getName());

        if (playlistFolder.exists() && playlistFolder.isDirectory()) {
            File[] songFiles = playlistFolder.listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".mp3") ||
                            name.toLowerCase().endsWith(".wav") ||
                            name.toLowerCase().endsWith(".m4a"));

            if (songFiles != null) {
                for (File songFile : songFiles) {
                    Song song = new Song(songFile.getName(), songFile.getAbsolutePath());
                    songs.add(song);
                }
            }
        }

        return songs;
    }
    // show dialog with checkboxes
    private void showSongRemovalDialog(Playlist playlist) {
        List<Song> songs = getSongsInPlaylist(playlist);
        if (songs.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame,
                    "This playlist contains no songs.",
                    "Remove from Playlist",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // creat the dialog
        JDialog dialog = new JDialog(parentFrame, "Remove songs from " + playlist.getName(), true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //the instructions
        JLabel instructions = new JLabel("Select songs to remove from " + playlist.getName());
        instructions.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(instructions, BorderLayout.NORTH);

        //checkbox panel
        JPanel songsPanel = new JPanel();
        songsPanel.setLayout(new BoxLayout(songsPanel, BoxLayout.Y_AXIS));
        List<JCheckBox> checkBoxes = new ArrayList<>();

        for (Song song : songs) {
            JCheckBox checkBox = new JCheckBox(song.getTitle());
            checkBox.setFont(new Font("Monospaced", Font.PLAIN, 12));
            checkBoxes.add(checkBox);
            songsPanel.add(checkBox);
        }

        JScrollPane scrollPane = new JScrollPane(songsPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Songs"));
        panel.add(scrollPane, BorderLayout.CENTER);

        //button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton removeButton = new JButton("Remove Selected");
        JButton cancelButton = new JButton("Cancel");

        removeButton.addActionListener(e -> {
            List<Song> songsToRemove = new ArrayList<>();
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    songsToRemove.add(songs.get(i));
                }
            }

            if (!songsToRemove.isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(dialog,
                        "Are you sure you want to remove " + songsToRemove.size() +
                                " song(s) from \"" + playlist.getName() + "\"?",
                        "Confirm Removal",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    removeSongsFromPlaylist(playlist, songsToRemove);
                    dialog.dispose();
                    treeManager.refreshTree();
                    JOptionPane.showMessageDialog(parentFrame,
                            "Successfully removed " + songsToRemove.size() + " song(s).",
                            "Removal Complete",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Please select at least one song to remove.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(removeButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    //  deletes selected song files

    private void removeSongsFromPlaylist(Playlist playlist, List<Song> songsToRemove) {
        File playlistFolder = new File("src/main/resources/PlaceHolder Name Songs/" + playlist.getName());

        for (Song song : songsToRemove) {
            File songFile = new File(song.getFilePath());
            try {
                if (songFile.exists() && songFile.getParentFile().equals(playlistFolder)) {
                    Files.deleteIfExists(songFile.toPath());
                    System.out.println("Removed song: " + song.getTitle() + " from playlist: " + playlist.getName());
                }
            } catch (IOException e) {
                System.err.println("Error removing song: " + song.getTitle());
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame,
                        "Error removing song: " + song.getTitle(),
                        "Removal Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}





