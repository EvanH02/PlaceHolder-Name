package org.example.frontend;

import org.example.backend.Playlist;
import org.example.backend.Song;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlaylistRemover {

    private final JFrame parentFrame;
    private final MusicTreeManager treeManager;
    private final List<Playlist> playlists;

    public PlaylistRemover(JFrame parentFrame, MusicTreeManager treeManager, List<Playlist> playlists) {
        this.parentFrame = parentFrame;
        this.treeManager = treeManager;
        this.playlists = playlists;
    }

    public void showstheplaylistremovedDialog() {
        Playlist selectedPlaylist = selectPlaylist();

        if (selectedPlaylist == null) {
            return;
        }
        showSongRemovalDialog(selectedPlaylist);
    }

    // dropdown available playlists for user to choose
    private Playlist selectPlaylist() {
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

        return playlists.stream()
                .filter(p -> p.getName().equals(chosen))
                .findFirst()
                .orElse(null);
    }

    // songs directly from playlist object
    private List<Song> getSongsInPlaylist(Playlist playlist) {
        return new ArrayList<>(playlist.getSongs());
    }
    //dialog checkbox for songs IN PLAYLIST
    private void showSongRemovalDialog(Playlist playlist) {
        List<Song> songs = getSongsInPlaylist(playlist);
        if (songs.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame,
                    "This playlist contains no songs.",
                    "Remove from Playlist",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // dialog with checkboxes for songs
        JDialog dialog = new JDialog(parentFrame, "Remove songs from " + playlist.getName(), true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        //instructions
        JLabel instructions = new JLabel("Select songs to remove from " + playlist.getName());
        instructions.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(instructions, BorderLayout.NORTH);



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


        //buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton removeButton = new JButton("Remove Selected");
        JButton cancelButton = new JButton("Cancel");

        removeButton.addActionListener(e -> {
            // Collect selected songs
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

    // removes songs from playlist object and saves to CS
    private void removeSongsFromPlaylist(Playlist playlist, List<Song> songsToRemove) {
        for (Song song : songsToRemove) {
            playlist.removeSong(song);
            System.out.println("Removed song: " + song.getTitle() + " from playlist: " + playlist.getName());
        }


        playlist.writeCSV();
    }
}