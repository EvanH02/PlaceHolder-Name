// Handles adding and removing song files from playlist folders
package org.example.frontend;

import org.example.backend.CsvStore;
import org.example.backend.Song;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SongManager {

    private final JFrame parentFrame;
    private final MusicTreeManager treeManager;

    public SongManager(JFrame parentFrame, MusicTreeManager treeManager) {
        this.parentFrame = parentFrame;
        this.treeManager = treeManager;
    }

    // admin-only: adds song to root CSV
    public void addSong() {
        JTextField titleField = new JTextField();
        JTextField genreField = new JTextField();
        JTextField artistField = new JTextField();
        JTextArea lyricsArea = new JTextArea(6, 30);

        JPanel panel = new JPanel(new BorderLayout(5,5));
        JPanel fields = new JPanel(new GridLayout(3,2,5,5));
        fields.add(new JLabel("Title:")); fields.add(titleField);
        fields.add(new JLabel("Genre:")); fields.add(genreField);
        fields.add(new JLabel("Artist:")); fields.add(artistField);
        panel.add(fields, BorderLayout.NORTH);
        panel.add(new JScrollPane(lyricsArea), BorderLayout.CENTER);

        int res = JOptionPane.showConfirmDialog(parentFrame, panel, "Add Song to Root (admin)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "Title is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Song s = new Song(title, CsvStore.ROOT_CSV);
        s.setGenre(genreField.getText().trim());
        s.setArtist(artistField.getText().trim());
        s.setLyrics(lyricsArea.getText());

        List<Song> toAppend = new ArrayList<>();
        toAppend.add(s);
        boolean okSongs = CsvStore.appendSongsToCsv(CsvStore.ROOT_CSV, toAppend);
        boolean okLyrics = CsvStore.appendLyric(CsvStore.LYRICS_CSV, s);
        if (okSongs && okLyrics) {
            treeManager.refreshTree();
            // ensure save
            if (parentFrame instanceof PlaceHolderName) ((PlaceHolderName) parentFrame).saveAllFromTree();
            JOptionPane.showMessageDialog(parentFrame, "Song added to root.", "Done", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(parentFrame, "Failed to write song.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // user feature: select songs from root CSV, append them to playlist CSV
    public void addToPlaylist() {
        List<Song> rootSongs = CsvStore.readSongsFromCsv(CsvStore.ROOT_CSV);
        if (rootSongs.isEmpty()) {
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
        for (Song s : rootSongs) {
            JCheckBox cb = new JCheckBox(s.getTitle());
            cb.setFont(new Font("Monospaced", Font.PLAIN, 12));
            checkBoxes.add(cb);
            songsPanel.add(cb);
        }
        JScrollPane songScroll = new JScrollPane(songsPanel);
        songScroll.setBorder(BorderFactory.createTitledBorder("Select Songs"));
        mainPanel.add(songScroll, BorderLayout.CENTER);

        // playlist picker panel
        JPanel bottomSection = new JPanel(new BorderLayout(5, 5));

        // gather playlist CSVs
        File dataDir = new File(CsvStore.DATA_DIR);
        List<File> playlistFiles = new ArrayList<>();
        String lyricsFileName = new File(CsvStore.LYRICS_CSV).getName();
        if (dataDir.exists() && dataDir.isDirectory()) {
            File[] files = dataDir.listFiles((d, name) -> {
                return name.toLowerCase().endsWith(".csv") && !name.equalsIgnoreCase("RootSongs.csv") && !name.equalsIgnoreCase("users.csv") && !name.equalsIgnoreCase(lyricsFileName);
            });
            if (files != null) {
                for (File f : files) playlistFiles.add(f);
            }
        }

        DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<>();
        for (File pf : playlistFiles) comboModel.addElement(pf.getName().replaceFirst("\\.csv$", ""));
        JComboBox<String> playlistCombo = new JComboBox<>(comboModel);

        JPanel pickerRow = new JPanel(new BorderLayout(5, 5));
        pickerRow.add(new JLabel("Playlist:"), BorderLayout.WEST);
        pickerRow.add(playlistCombo, BorderLayout.CENTER);

        // create new playlist inline (creates csv file)
        JButton createBtn = new JButton("New Playlist");
        createBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(dialog, "Enter playlist name:");
            if (name != null && !name.trim().isEmpty()) {
                String filename = CsvStore.DATA_DIR + name.trim() + ".csv";
                File newFile = new File(filename);
                try {
                    newFile.getParentFile().mkdirs();
                    boolean created = newFile.createNewFile();
                    // write header
                    if (created) CsvStore.writeSongsToCsv(filename, new ArrayList<>());
                    comboModel.addElement(name.trim());
                    playlistCombo.setSelectedItem(name.trim());
                    playlistFiles.add(newFile);
                    treeManager.refreshTree();
                } catch (Exception ex) {
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
            List<Song> selected = new ArrayList<>();
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    selected.add(rootSongs.get(i));
                }
            }
            if (selected.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Select at least one song.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // find destination file
            String chosenName = (String) playlistCombo.getSelectedItem();
            String destPath = CsvStore.DATA_DIR + chosenName + ".csv";

            if (CsvStore.appendSongsToCsv(destPath, selected)) {
                treeManager.refreshTree();
                if (parentFrame instanceof PlaceHolderName) ((PlaceHolderName) parentFrame).saveAllFromTree();
                dialog.dispose();
                JOptionPane.showMessageDialog(parentFrame, "Added " + selected.size() + " song(s) to \"" + chosenName + "\".", "Done", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parentFrame, "Failed to write to playlist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
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
        // allow admin to remove from root or playlists by choosing which playlist (or root)
        String[] choices = {"Root", "Playlist"};
        String choice = (String) JOptionPane.showInputDialog(parentFrame, "Remove from:", "Remove Song", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
        if (choice == null) return;

        if (choice.equals("Root")) {
            // select title from root
            List<Song> rootSongs = CsvStore.readSongsFromCsv(CsvStore.ROOT_CSV);
            if (rootSongs.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, "No root songs.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String[] titles = rootSongs.stream().map(Song::getTitle).toArray(String[]::new);
            String sel = (String) JOptionPane.showInputDialog(parentFrame, "Select song to delete:", "Delete", JOptionPane.QUESTION_MESSAGE, null, titles, titles[0]);
            if (sel == null) return;
            rootSongs.removeIf(s -> s.getTitle().equals(sel));
            CsvStore.writeSongsToCsv(CsvStore.ROOT_CSV, rootSongs);
            treeManager.refreshTree();
            if (parentFrame instanceof PlaceHolderName) ((PlaceHolderName) parentFrame).saveAllFromTree();
            JOptionPane.showMessageDialog(parentFrame, "Removed from root.", "Done", JOptionPane.INFORMATION_MESSAGE);
            return;
        } else {
            // choose playlist
            File dataDir = new File(CsvStore.DATA_DIR);
            List<File> playlistFiles = new ArrayList<>();
            String lyricsFileName = new File(CsvStore.LYRICS_CSV).getName();
            if (dataDir.exists() && dataDir.isDirectory()) {
                File[] files = dataDir.listFiles((d, name) -> {
                    return name.toLowerCase().endsWith(".csv") && !name.equalsIgnoreCase("RootSongs.csv") && !name.equalsIgnoreCase("users.csv") && !name.equalsIgnoreCase(lyricsFileName);
                });
                if (files != null) for (File f : files) playlistFiles.add(f);
            }
            if (playlistFiles.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, "No playlists.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String[] names = playlistFiles.stream().map(f -> f.getName().replaceFirst("\\.csv$", "")).toArray(String[]::new);
            String chosen = (String) JOptionPane.showInputDialog(parentFrame, "Select playlist:", "Choose", JOptionPane.QUESTION_MESSAGE, null, names, names[0]);
            if (chosen == null) return;
            String path = CsvStore.DATA_DIR + chosen + ".csv";
            List<Song> songs = CsvStore.readSongsFromCsv(path);
            if (songs.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, "Playlist empty.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String[] titles = songs.stream().map(Song::getTitle).toArray(String[]::new);
            String sel = (String) JOptionPane.showInputDialog(parentFrame, "Select song to delete:", "Delete", JOptionPane.QUESTION_MESSAGE, null, titles, titles[0]);
            if (sel == null) return;
            songs.removeIf(s -> s.getTitle().equals(sel));
            CsvStore.writeSongsToCsv(path, songs);
            treeManager.refreshTree();
            if (parentFrame instanceof PlaceHolderName) ((PlaceHolderName) parentFrame).saveAllFromTree();
            JOptionPane.showMessageDialog(parentFrame, "Removed from playlist.", "Done", JOptionPane.INFORMATION_MESSAGE);
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
