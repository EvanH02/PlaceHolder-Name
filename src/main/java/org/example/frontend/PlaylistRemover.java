package org.example.frontend;

import org.example.backend.CsvStore;
import org.example.backend.Playlist;
import org.example.backend.Song;

import javax.swing.*;
import java.awt.*;
import java.io.File;
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

    // playlists from the filesystem (csv files)

    private List<Playlist> getAllPlaylists() {
        List<Playlist> playlists = new ArrayList<>();
        File dataDir = new File(CsvStore.DATA_DIR);

        String lyricsFileName = new File(CsvStore.LYRICS_CSV).getName();

        if (dataDir.exists() && dataDir.isDirectory()) {
            File[] files = dataDir.listFiles((dir, name) -> {
                String lower = name.toLowerCase();
                return lower.endsWith(".csv") && !name.equalsIgnoreCase("RootSongs.csv") && !name.equalsIgnoreCase("users.csv") && !name.equalsIgnoreCase(lyricsFileName);
            });
            if (files != null) {
                for (File file : files) {
                    Playlist playlist = new Playlist(file.getName().replaceFirst("\\.csv$", ""));
                    // loading songs from csv
                    List<Song> songs = CsvStore.readSongsFromCsv(file.getAbsolutePath());
                    for (Song song : songs) {
                        playlist.addSong(song);
                    }
                    playlists.add(playlist);
                }
            }
        }

        return playlists;
    }

    // get all songs in a specific playlist

    private List<Song> getSongsInPlaylist(Playlist playlist) {
        String path = CsvStore.DATA_DIR + playlist.getName() + ".csv";
        return CsvStore.readSongsFromCsv(path);
    }
    // show dialog with checkboxes
    private void showSongRemovalDialog(Playlist playlist) {
        List<Song> songs = getSongsInPlaylist(playlist);
        if (songs.isEmpty()) {
            int choice = JOptionPane.showConfirmDialog(parentFrame,
                    "This playlist contains no songs. Do you want to delete the playlist?",
                    "Empty Playlist",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                File file = new File(CsvStore.DATA_DIR + playlist.getName() + ".csv");
                boolean deleted = false;
                try {
                    deleted = file.delete();
                } catch (Exception ex) {
                    deleted = false;
                }
                if (deleted) {
                    treeManager.refreshTree();
                    JOptionPane.showMessageDialog(parentFrame,
                            "Playlist deleted.",
                            "Deleted",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Failed to delete playlist file.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
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
                    // remove and write back csv
                    songs.removeAll(songsToRemove);
                    String path = CsvStore.DATA_DIR + playlist.getName() + ".csv";
                    CsvStore.writeSongsToCsv(path, songs);

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

}