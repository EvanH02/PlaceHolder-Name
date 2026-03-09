package org.example;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;




public class PlaceHolderName extends JFrame {
    ArrayList<Playlist> playlists = new ArrayList();
    private JTextField searchField;
    private DefaultListModel<String> searchResultsModel;
    private JList<String> searchResultsList;
    private JTree tree;
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;



    public PlaceHolderName() {
        //on start loads the playlists
        // window settings
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("PlaceHolderName");
        this.setSize(500, 400);
        //menubar containing the Add song/playlisy
        JMenuBar menuBar = new JMenuBar();
        JMenu searchMenu = new JMenu("Search");
        JMenuItem searchItem = new JMenuItem("Find Song:");
        searchItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSearchDialog();
            }
        });
        searchMenu.add(searchItem);
        menuBar.add(searchMenu);








        JMenuItem addSong = new JMenuItem("Add Song");
        addSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSong();
            }
        });

        menuBar.add(addSong);
        JMenuItem newPlaylist = new JMenuItem("Add Playlist");
        newPlaylist.addActionListener(new ActionListener() {
                                          @Override
                                          public void actionPerformed(ActionEvent e) {
                                              createPlaylist();
                                          }
                                      }

        );
        menuBar.add(addSong);
        menuBar.add(newPlaylist);
        this.add(menuBar, BorderLayout.PAGE_START);
        //Admin can remove songs
        JMenuItem removeSong = new JMenuItem("Remove Song");
        removeSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("aaaaaaa");
                removeSong();
            }
        });

        menuBar.add(removeSong);


        // JPanel containing the JTree
        JPanel treePanel = new JPanel();
        treePanel.setLayout(new BorderLayout());

        // create JTree from resource folder
        rootNode = buildResourceTree("PlaceHolder Name Songs");
        treeModel = new DefaultTreeModel(rootNode, true);
        tree = new JTree(treeModel);

        // playlists get folder icon, songs get file icon
        tree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value,
                    boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                if (value instanceof DefaultMutableTreeNode) {
                    Object userObj = ((DefaultMutableTreeNode) value).getUserObject();
                    if (userObj instanceof Playlist) {
                        setIcon(expanded ? getOpenIcon() : getClosedIcon());
                    } else if (userObj instanceof Song) {
                        setIcon(getLeafIcon());
                    }
                }
                return this;
            }
        });

        JScrollPane treeScrollPane = new JScrollPane(tree);
        treePanel.add(treeScrollPane, BorderLayout.CENTER);
        this.add(treePanel, BorderLayout.CENTER);

    }

    private void showSearchDialog() {
        JDialog searchDialog = new JDialog(this, "Search Songs", true);
        searchDialog.setSize(350, 400);
        searchDialog.setLocationRelativeTo(this);

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

        DefaultMutableTreeNode root = buildResourceTree("PlaceHolder Name Songs");
        List<String> results = new ArrayList<>();
        searchInNode(root, searchText, results);
        for (String result : results) {
            searchResultsModel.addElement(result);
        }

        if (results.isEmpty()) {
            searchResultsModel.addElement("No results found");
        }
    }

    //recursive method
    private void searchInNode(DefaultMutableTreeNode node, String searchText, List<String> results) {
        Object userObject = node.getUserObject();

        if (userObject instanceof Song) {
            Song song = (Song) userObject;
            if (song.getTitle().toLowerCase().contains(searchText)) {
                results.add("🎵 " + song.getTitle());
            }
        } else if (userObject instanceof Playlist) {
            Playlist playlist = (Playlist) userObject;
            if (playlist.getName().toLowerCase().contains(searchText)) {
                results.add("📁 " + playlist.getName());
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            searchInNode((DefaultMutableTreeNode) node.getChildAt(i), searchText, results);
        }
    }





    private void refreshTree() {
        rootNode = buildResourceTree("PlaceHolder Name Songs");
        treeModel.setRoot(rootNode);
        treeModel.reload();
        // expand all nodes
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    public DefaultMutableTreeNode buildResourceTree (String folderName){
                File folder = new File("src/main/resources/" + folderName);

                if (!folder.exists() || !folder.isDirectory()) {
                    throw new IllegalArgumentException("Folder not found: " + folderName);
                }

                return buildTreeNode(folder);
            }

            private DefaultMutableTreeNode buildTreeNode (File folder){
                Playlist playlist = new Playlist(folder.getName());
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(playlist);
                node.setAllowsChildren(true);
                File[] files = folder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            node.add(buildTreeNode(file));
                        } else {
                            Song song = new Song(file.getName(), file.getAbsolutePath());
                            playlist.addSong(song);
                            DefaultMutableTreeNode songNode = new DefaultMutableTreeNode(song);
                            songNode.setAllowsChildren(false);
                            node.add(songNode);
                        }
                    }
                }

                return node;
            }
            private void addSong () {

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
                            this,
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
                        refreshTree();

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
            private void removeSong () {

                JFileChooser removeSongFile = new JFileChooser();
                JOptionPane removeConfirm = new JOptionPane();
                JOptionPane.showMessageDialog(getParent(), "This action will DELETE song", "Dialog Title", JOptionPane.WARNING_MESSAGE);

                // Set the chooser to open in the playlist folder
                removeSongFile.setCurrentDirectory(new File("src/main/resources/PlaceHolder Name Songs"));

                int result = removeSongFile.showDialog(getParent(), "DELETE");


                if (result == JFileChooser.APPROVE_OPTION) {


                    File selectedFile = removeSongFile.getSelectedFile();

                    try {
                        Files.deleteIfExists(selectedFile.toPath());
                        System.out.println("song removed");
                        refreshTree();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            private void createPlaylist () {
                String name = JOptionPane.showInputDialog("Enter playlist name:");
                //change this later to its own folder
                File newplaylist = new File(
                        System.getProperty("user.dir") + "/src/main/resources/PlaceHolder Name Songs/" + name
                );
                if (newplaylist.mkdirs()) {
                    System.out.println("Created: " + newplaylist.getAbsolutePath());
                    refreshTree();
                } else {
                    System.out.println("Folder already exists or failed.");
                }
                System.out.println(System.getProperty("user.dir") + "src/main/resources/PlaceHolder Name Songs/" + name);


            }
            public static void main(String[] args) {
                PlaceHolderName placeHolderName = new PlaceHolderName();
                placeHolderName.setVisible(true);
            }
        }


