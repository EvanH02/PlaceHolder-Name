/* package org.example.frontend;

import org.example.backend.Playlist;
import org.example.backend.Song;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistRemover {

    private final JFrame parentFrame;
    private final MusicTreeManager;

    public PlaylistRemover(JFrame parentFrame, MusicTreeManager treeManager) {

        this.parentFrame = parentFrame;
        this.treeManager = treeManager;
    }

    public void showstheplaylistremovedDialog() {

        Playlist selectedPlaylist = selectPlaylist();


        if (selectedPlaylist == null) {
            return;
        }
        showstheplaylistremovedDialog(selectedPlaylist);
    }

    private Playlist selectPlaylist() {

        List<Playlist> playlists = getAllPlaylists();

        if (playlists.isEmpty()) {
            JOptionPane.showMessageDialog((parentFrame,
                    "No playlist found.",
                    "Remove from playlist",
                    JOptionPane.INFORMATION_MESSAGE));
            return null;
        }

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

    if(chosen ==null)

    {
        return null;
    }

    //find matching (complete this later)
    return playlists.stream()

    private void showSongRemovalDialog(Playlist playlist) {
        List<Song> songs = getSongsInPlaylist(playlist);
        if (songs.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame,
                    "This playlist contains no songs.",
                    "Remove from Playlist",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    }

    //Jdialog
    JDialog dialog = new JDialog(parentFrame, "Remove songs " + playlist.getName(),true);
    dialog.setSize(300,400);
    dialog.setLocationRelativeTo(parentFrame);

    JPanel panel = new JPanel(new BorderLayout(10,10));
    panel.setBoarder(BorderFactory.createEmptyBorder(10,10,10,10));

    //explains
    JLabel instructions = new JLabel("Select songs to remove from ..." +playlistNames() + ) //not done at all
    instructions.setFont(new Font("Arial",Font.BOLD, 14 ));
    panel.add(instructions, BorderLayout.NORTH);

    //CHECKBOX
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

    // Button
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    JButton removeButton = new JButton("Remove Selected");
        removeButton.addActionListener(e -> {
        List<Song> songsToRemove = new ArrayList<>();
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                songsToRemove.add(songs.get(i));
            }
        } if (!songsToRemove.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Are you sure you want to remove " + songsToRemove.size() +
                            " song(s) from \"" + playlist.getName() + "\"?",
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
}


        */





