// Handles adding and removing song files from playlist folders
package org.example.frontend;

import javax.swing.*;
import java.awt.*;
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

    // admin-only: adds song to root folder
    public void addSong() {
        JFileChooser addSongFile = new JFileChooser();
        int result = addSongFile.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = addSongFile.getSelectedFile();
            File rootFolder = new File("src/main/resources/PlaceHolder Name Songs");
            Path destinationFile = rootFolder.toPath().resolve(selectedFile.getName());

            try {
                Files.copy(
                        selectedFile.toPath(),
                        destinationFile,
                        StandardCopyOption.REPLACE_EXISTING
                );
                System.out.println("song added to root");
                treeManager.refreshTree();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // user feature: select songs from root, copy to playlist
    public void addToPlaylist() {
        File rootFolder = new File("src/main/resources/PlaceHolder Name Songs");

        // get songs in root only
        File[] rootFiles = rootFolder.listFiles(f -> f.isFile());
        if (rootFiles == null || rootFiles.length == 0) {
            JOptionPane.showMessageDialog(parentFrame, "No songs in root to add.", "Add to Playlist", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // build dialog
        JDialog dialog = new JDialog(parentFrame, "Add to Playlist", true);
        dialog.setSize(450, 550);
        dialog.setLocationRelativeTo(parentFrame);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // song checkboxes
        JPanel songsPanel = new JPanel();
        songsPanel.setLayout(new BoxLayout(songsPanel, BoxLayout.Y_AXIS));
        List<JCheckBox> checkBoxes = new ArrayList<>();
        for (File f : rootFiles) {
            JCheckBox cb = new JCheckBox(f.getName());
            cb.setFont(new Font("Monospaced", Font.PLAIN, 12));
            checkBoxes.add(cb);
            songsPanel.add(cb);
        }
        JScrollPane songScroll = new JScrollPane(songsPanel);
        songScroll.setBorder(BorderFactory.createTitledBorder("Select Songs"));
        mainPanel.add(songScroll, BorderLayout.CENTER);

        // playlist picker panel
        JPanel bottomSection = new JPanel(new BorderLayout(5, 5));

        // dropdown of existing playlists
        List<File> playlistFolders = new ArrayList<>();
        collectSubfolders(rootFolder, playlistFolders);
        DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<>();
        for (File pf : playlistFolders) {
            comboModel.addElement(pf.getName());
        }
        JComboBox<String> playlistCombo = new JComboBox<>(comboModel);

        JPanel pickerRow = new JPanel(new BorderLayout(5, 5));
        pickerRow.add(new JLabel("Playlist:"), BorderLayout.WEST);
        pickerRow.add(playlistCombo, BorderLayout.CENTER);

        // create new playlist inline
        JButton createBtn = new JButton("New Playlist");
        createBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(dialog, "Enter playlist name:");
            if (name != null && !name.trim().isEmpty()) {
                File newDir = new File(rootFolder, name.trim());
                if (newDir.mkdirs()) {
                    comboModel.addElement(name.trim());
                    playlistCombo.setSelectedItem(name.trim());
                    playlistFolders.add(newDir);
                    treeManager.refreshTree();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Folder already exists or failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        pickerRow.add(createBtn, BorderLayout.EAST);
        bottomSection.add(pickerRow, BorderLayout.NORTH);

        // action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addBtn = new JButton("Add Selected");
        JButton cancelBtn = new JButton("Cancel");

        addBtn.addActionListener(e -> {
            if (playlistCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(dialog, "Select or create a playlist first.", "No Playlist", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // collect selected songs
            List<File> selected = new ArrayList<>();
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    selected.add(rootFiles[i]);
                }
            }
            if (selected.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Select at least one song.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // find destination folder
            String chosenName = (String) playlistCombo.getSelectedItem();
            File destFolder = playlistFolders.stream()
                    .filter(f -> f.getName().equals(chosenName))
                    .findFirst()
                    .orElse(null);
            if (destFolder == null) {
                return;
            }

            // copy files to playlist, keep originals
            int copied = 0;
            for (File src : selected) {
                try {
                    Files.copy(src.toPath(), destFolder.toPath().resolve(src.getName()), StandardCopyOption.REPLACE_EXISTING);
                    copied++;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            treeManager.refreshTree();
            dialog.dispose();
            JOptionPane.showMessageDialog(parentFrame, "Added " + copied + " song(s) to \"" + chosenName + "\".", "Done", JOptionPane.INFORMATION_MESSAGE);
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(addBtn);
        buttonPanel.add(cancelBtn);
        bottomSection.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomSection, BorderLayout.SOUTH);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    public void removeSong() {
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
