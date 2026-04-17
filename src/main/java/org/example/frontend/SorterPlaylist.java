package org.example.frontend;

import org.example.backend.CsvStore;
import org.example.backend.Playlist;
import org.example.backend.Song;
import org.example.backend.songSorter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SorterPlaylist {

    private final JFrame parentFrame;
    private final MusicTreeManager treeManager;

    public SorterPlaylist(JFrame parentFrame, MusicTreeManager treeManager) {
        this.parentFrame = parentFrame;
        this.treeManager = treeManager;
    }

    public void showSortDialog() {
        // pick playlist
        List<PlaylistEntry> entries = loadAllPlaylists();
        if (entries.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame,
                    "No playlists found.",
                    "Sort Playlist",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] names = entries.stream()
                .map(PlaylistEntry::getDisplayName)
                .toArray(String[]::new);

        String chosenName = (String) JOptionPane.showInputDialog(
                parentFrame,
                "Select a playlist to sort:",
                "Sort Playlist",
                JOptionPane.QUESTION_MESSAGE,
                null,
                names,
                names[0]);

        if (chosenName == null) return;

        PlaylistEntry chosen = entries.stream()
                .filter(e -> e.getDisplayName().equals(chosenName))
                .findFirst()
                .orElse(null);

        if (chosen == null) return;

        List<Song> songs = CsvStore.readSongsFromCsv(chosen.getCsvPath());

        if (songs.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame,
                    "\"" + chosenName + "\" has no songs to sort.",
                    "Sort Playlist",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // pick a sort criterion
        showSortCriteriaDialog(chosen, songs);
    }

    private void showSortCriteriaDialog(PlaylistEntry entry, List<Song> songs) {
        JDialog dialog = new JDialog(parentFrame, "Sort \"" + entry.getDisplayName() + "\"", true);
        dialog.setSize(420, 520);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // title
        JLabel titleLabel = new JLabel("Sort \"" + entry.getDisplayName() + "\"", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // sort options
        JPanel optionsPanel = new JPanel(new GridLayout(6, 1, 5, 8));
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Sort by"));

        ButtonGroup group = new ButtonGroup();
        JRadioButton byTitle     = new JRadioButton("Title (A → Z)");
        JRadioButton byTitleDesc = new JRadioButton("Title (Z → A)");
        JRadioButton byGenre     = new JRadioButton("Genre (A → Z)");
        JRadioButton byGenreDesc = new JRadioButton("Genre (Z → A)");
        JRadioButton byArtist    = new JRadioButton("Artist (A → Z)");
        JRadioButton byArtistDesc= new JRadioButton("Artist (Z → A)");

        byTitle.setSelected(true);
        for (JRadioButton rb : new JRadioButton[]{byTitle, byTitleDesc, byGenre, byGenreDesc, byArtist, byArtistDesc}) {
            rb.setFont(new Font("Arial", Font.PLAIN, 13));
            group.add(rb);
            optionsPanel.add(rb);
        }
        mainPanel.add(optionsPanel, BorderLayout.CENTER);

        // preview
        JTextArea preview = new JTextArea(8, 30);
        preview.setEditable(false);
        preview.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane previewScroll = new JScrollPane(preview);
        previewScroll.setBorder(BorderFactory.createTitledBorder("Preview (current order)"));
        updatePreview(preview, songs);

        // update preview live when selection changes
        for (JRadioButton rb : new JRadioButton[]{byTitle, byTitleDesc, byGenre, byGenreDesc, byArtist, byArtistDesc}) {
            rb.addActionListener(e -> {
                List<Song> sorted = buildSortedCopy(songs,
                        byTitle.isSelected(), byTitleDesc.isSelected(),
                        byGenre.isSelected(), byGenreDesc.isSelected(),
                        byArtist.isSelected(), byArtistDesc.isSelected());
                updatePreview(preview, sorted);
            });
        }

        JPanel centerSection = new JPanel(new BorderLayout(5, 5));
        centerSection.add(optionsPanel, BorderLayout.NORTH);
        centerSection.add(previewScroll, BorderLayout.CENTER);
        mainPanel.add(centerSection, BorderLayout.CENTER);

        // buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton applyBtn  = new JButton("Apply & Save");
        JButton cancelBtn = new JButton("Cancel");

        applyBtn.addActionListener(e -> {
            List<Song> sorted = buildSortedCopy(songs,
                    byTitle.isSelected(), byTitleDesc.isSelected(),
                    byGenre.isSelected(), byGenreDesc.isSelected(),
                    byArtist.isSelected(), byArtistDesc.isSelected());

            boolean ok = CsvStore.writeSongsToCsv(entry.getCsvPath(), sorted);
            if (ok) {
                treeManager.refreshTree();
                dialog.dispose();
                JOptionPane.showMessageDialog(parentFrame,
                        "\"" + entry.getDisplayName() + "\" has been sorted and saved.",
                        "Sort Complete",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parentFrame,
                        "Failed to save sorted playlist.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(applyBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    // returns sorted but leaves og until confirmed
    private List<Song> buildSortedCopy(List<Song> songs,
                                       boolean byTitle, boolean byTitleDesc,
                                       boolean byGenre, boolean byGenreDesc,
                                       boolean byArtist, boolean byArtistDesc) {
        List<Song> copy = new ArrayList<>(songs);

        if (byTitle) {
            songSorter.sortAlphabetically(copy);
        } else if (byTitleDesc) {
            songSorter.sortAlphabetically(copy);
            Collections.reverse(copy);
        } else if (byGenre) {
            sortByField(copy, "genre", false);
        } else if (byGenreDesc) {
            sortByField(copy, "genre", true);
        } else if (byArtist) {
            sortByField(copy, "artist", false);
        } else if (byArtistDesc) {
            sortByField(copy, "artist", true);
        }

        return copy;
    }

    private void sortByField(List<Song> songs, String field, boolean descending) {
        songs.sort((a, b) -> {
            String va = field.equals("genre") ? nullSafe(a.getGenre()) : nullSafe(a.getArtist());
            String vb = field.equals("genre") ? nullSafe(b.getGenre()) : nullSafe(b.getArtist());
            int cmp = va.compareToIgnoreCase(vb);
            return descending ? -cmp : cmp;
        });
    }

    private String nullSafe(String s) {
        return s == null ? "" : s;
    }

    private void updatePreview(JTextArea area, List<Song> songs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < songs.size(); i++) {
            Song s = songs.get(i);
            sb.append(String.format("%2d. %-30s %-15s %s%n",
                    i + 1,
                    truncate(s.getTitle(), 28),
                    truncate(nullSafe(s.getArtist()), 13),
                    truncate(nullSafe(s.getGenre()), 13)));
        }
        area.setText(sb.toString());
        area.setCaretPosition(0);
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    // loads root and all playlist CSVs as PlaylistEntry objects
    private List<PlaylistEntry> loadAllPlaylists() {
        List<PlaylistEntry> entries = new ArrayList<>();

        // root
        File rootFile = new File(CsvStore.ROOT_CSV);
        if (rootFile.exists()) {
            entries.add(new PlaylistEntry("Root", CsvStore.ROOT_CSV));
        }

        // Playlist CSVs
        File dataDir = new File(CsvStore.DATA_DIR);
        String lyricsFileName = new File(CsvStore.LYRICS_CSV).getName();
        if (dataDir.exists() && dataDir.isDirectory()) {
            File[] files = dataDir.listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".csv")
                            && !name.equalsIgnoreCase("RootSongs.csv")
                            && !name.equalsIgnoreCase("users.csv")
                            && !name.equalsIgnoreCase(lyricsFileName));
            if (files != null) {
                for (File f : files) {
                    String displayName = f.getName().replaceFirst("\\.csv$", "");
                    entries.add(new PlaylistEntry(displayName, f.getAbsolutePath()));
                }
            }
        }
        return entries;
    }

    // Simple value holder,  keeps display name and CSV path together
    private static class PlaylistEntry {
        private final String displayName;
        private final String csvPath;

        PlaylistEntry(String displayName, String csvPath) {
            this.displayName = displayName;
            this.csvPath = csvPath;
        }

        String getDisplayName() { return displayName; }
        String getCsvPath()     { return csvPath; }
    }
}
