// Handles the search
package org.example.frontend;

import org.example.backend.CsvStore;
import org.example.backend.Playlist;
import org.example.backend.Song;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SearchHandler {

    private final JFrame parentFrame;
    private final MusicTreeManager treeManager;
    private JTextField searchField;
    private DefaultListModel<String> searchResultsModel;
    private JList<String> searchResultsList;
    private final List<Song> foundSongs = new ArrayList<>();

    public SearchHandler(JFrame parentFrame, MusicTreeManager treeManager) {
        this.parentFrame = parentFrame;
        this.treeManager = treeManager;
    }

    public void showSearchDialog() {
        JDialog searchDialog = new JDialog(parentFrame, "Search Songs", true);
        searchDialog.setSize(350, 400);
        searchDialog.setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //the search input
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        searchField = new JTextField();
        JButton searchBtn = new JButton("Search");
        inputPanel.add(searchField, BorderLayout.CENTER);
        inputPanel.add(searchBtn, BorderLayout.EAST);

        //the results list
        searchResultsModel = new DefaultListModel<>();
        searchResultsList = new JList<>(searchResultsModel);
        searchResultsList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(searchResultsList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Results"));

        //close button
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> searchDialog.dispose());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(closeBtn);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        searchDialog.add(panel);

        //the searchh action
        searchBtn.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());

        //double click
        searchResultsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int idx = searchResultsList.getSelectedIndex();
                    if (idx < 0 || idx >= foundSongs.size()) return;
                    Song song = foundSongs.get(idx);
                    openEditDialog(searchDialog, song);
                }
            }
        });

        searchDialog.setVisible(true);
    }

    // opens the edit dialog for a song
    private void openEditDialog(JDialog owner, Song song) {
        boolean isAdmin = false;
        if (parentFrame instanceof PlaceHolderName) {
            isAdmin = ((PlaceHolderName) parentFrame).isAdmin();
        }

        JTextField titleField = new JTextField(song.getTitle());
        JTextField genreField = new JTextField(song.getGenre());
        JTextField artistField = new JTextField(song.getArtist());
        JTextArea editLyrics = new JTextArea(10, 30);
        editLyrics.setText(song.getLyrics());

        if (!isAdmin) {
            titleField.setEditable(false);
            genreField.setEditable(false);
            artistField.setEditable(false);
            editLyrics.setEditable(false);
        }

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JPanel fields = new JPanel(new GridLayout(3, 2, 5, 5));
        fields.add(new JLabel("Title:")); fields.add(titleField);
        fields.add(new JLabel("Genre:")); fields.add(genreField);
        fields.add(new JLabel("Artist:")); fields.add(artistField);
        panel.add(fields, BorderLayout.NORTH);
        panel.add(new JScrollPane(editLyrics), BorderLayout.CENTER);

        String dialogTitle = isAdmin ? "Edit Song" : "Song Info";
        int res = JOptionPane.showConfirmDialog(owner, panel, dialogTitle, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION || !isAdmin) return;

        String newTitle = titleField.getText().trim();
        String newGenre = genreField.getText().trim();
        String newArtist = artistField.getText().trim();
        String newLyrics = editLyrics.getText();

        // update root CSV
        List<Song> rootSongs = CsvStore.readSongsFromCsv(CsvStore.ROOT_CSV);
        for (Song s : rootSongs) {
            if (s.getTitle().equals(song.getTitle())) {
                s.setTitle(newTitle);
                s.setGenre(newGenre);
                s.setArtist(newArtist);
                s.setLyrics(newLyrics);
                break;
            }
        }
        CsvStore.writeSongsToCsv(CsvStore.ROOT_CSV, rootSongs);

        // update lyrics map
        Map<String, String> lyricsMap = CsvStore.readLyricsMap(CsvStore.LYRICS_CSV);
        if (!song.getTitle().equals(newTitle)) {
            lyricsMap.remove(song.getTitle());
        }
        lyricsMap.put(newTitle, newLyrics);
        CsvStore.writeLyricsMap(CsvStore.LYRICS_CSV, lyricsMap);

        treeManager.refreshTree();
        if (parentFrame instanceof PlaceHolderName) {
            ((PlaceHolderName) parentFrame).saveAllFromTree();
        }

        // re-run search to refresh results
        performSearch();
    }

    //perform search method
    private void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            return;
        }

        searchResultsModel.clear();
        foundSongs.clear();

        DefaultMutableTreeNode root = treeManager.buildResourceTree();
        Set<String> seenSongs = new HashSet<>();
        searchInNode(root, searchText, seenSongs);

        if (foundSongs.isEmpty()) {
            searchResultsModel.addElement("No results found");
        }
    }

    //recursive method - songs only, no playlists in results
    private void searchInNode(DefaultMutableTreeNode node, String searchText, Set<String> seenSongs) {
        Object userObject = node.getUserObject();

        if (userObject instanceof Song) {
            Song song = (Song) userObject;
            String songKey = song.getTitle() == null ? "" : song.getTitle().toLowerCase();
            if (song.getTitle().toLowerCase().contains(searchText)) {
                if (!seenSongs.contains(songKey)) {
                    searchResultsModel.addElement("\ud83c\udfb5 " + song.getTitle());
                    foundSongs.add(song);
                    seenSongs.add(songKey);
                }
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            searchInNode((DefaultMutableTreeNode) node.getChildAt(i), searchText, seenSongs);
        }
    }
}
