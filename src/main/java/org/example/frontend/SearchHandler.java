// Handles the search
package org.example.frontend;

import org.example.backend.Playlist;
import org.example.backend.Song;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchHandler {

    private final JFrame parentFrame;
    private final MusicTreeManager treeManager;
    private JTextField searchField;
    private DefaultListModel<String> searchResultsModel;
    private JList<String> searchResultsList;

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
                    String selected = searchResultsList.getSelectedValue();
                    if (selected != null && !selected.equals("No results found")) {
                        JOptionPane.showMessageDialog(searchDialog, "Selected: " + selected);
                    }
                }
            }
        });

        searchDialog.setVisible(true);
    }

    //perform search method
    private void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            return;
        }

        searchResultsModel.clear();

        DefaultMutableTreeNode root = treeManager.buildResourceTree();
        List<String> results = new ArrayList<>();
        Set<String> seenSongs = new HashSet<>();
        searchInNode(root, searchText, results, seenSongs);
        for (String result : results) {
            searchResultsModel.addElement(result);
        }

        if (results.isEmpty()) {
            searchResultsModel.addElement("No results found");
        }
    }

    //recursive method - modified to avoid duplicate songs from multiple playlists in result
    private void searchInNode(DefaultMutableTreeNode node, String searchText, List<String> results, Set<String> seenSongs) {
        Object userObject = node.getUserObject();

        if (userObject instanceof Song) {
            Song song = (Song) userObject;
            String songKey = song.getTitle() == null ? "" : song.getTitle().toLowerCase();
            if (song.getTitle().toLowerCase().contains(searchText)) {
                if (!seenSongs.contains(songKey)) {
                    results.add("\ud83c\udfb5 " + song.getTitle());
                    seenSongs.add(songKey);
                }
            }
        } else if (userObject instanceof Playlist) {
            Playlist playlist = (Playlist) userObject;
            if (playlist.getName().toLowerCase().contains(searchText)) {
                results.add("\ud83d\udcc1 " + playlist.getName());
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            searchInNode((DefaultMutableTreeNode) node.getChildAt(i), searchText, results, seenSongs);
        }
    }
}
