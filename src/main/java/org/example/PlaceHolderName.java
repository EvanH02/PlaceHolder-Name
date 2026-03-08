package org.example;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class PlaceHolderName extends JFrame {
    ArrayList<Playlist> playlists= new ArrayList();

    // the search components
    private JTextField searchField;
    private DefaultListModel<String> searchResultsModel;
    private JList<String> searchResultsList;

    public PlaceHolderName() {
        //on start loads the playlists
        // window settings
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("PlaceHolder Name");
        this.setSize(500, 400);

        //menubar containing the Add song/playlisy
        JMenuBar menuBar= new JMenuBar();
        JMenuItem addSong= new JMenuItem("Add Song");
        addSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSong();
            }
        });

        menuBar.add(addSong);
        JMenuItem newPlaylist= new JMenuItem("Add Playlist");
        newPlaylist.addActionListener(new ActionListener() {
                                          @Override
                                          public void actionPerformed(ActionEvent e) {
                                              createPlaylist();
                                          }
                                      }

        );
        menuBar.add(addSong);
        menuBar.add(newPlaylist);

        // Add Search menu
        JMenu searchMenu = new JMenu("Search");
        JMenuItem searchItem = new JMenuItem("Find Song");
        searchItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSearchDialog();
            }
        });
        searchMenu.add(searchItem);
        menuBar.add(searchMenu);

        this.add(menuBar, BorderLayout.PAGE_START);
        //Admin can remove songs
        JMenuItem removeSong= new JMenuItem("Remove Song");
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
        DefaultMutableTreeNode root = buildResourceTree("Playlist 1");
        JTree tree = new JTree(root);
        JScrollPane treeScrollPane = new JScrollPane(tree);
        treePanel.add(treeScrollPane, BorderLayout.CENTER);
        this.add(treePanel, BorderLayout.CENTER);

    }

    // the search dialog method
    private void showSearchDialog() {
        JDialog searchDialog = new JDialog(this, "Search Songs", true);
        searchDialog.setSize(250, 300);
        searchDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search input
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        searchField = new JTextField();
        JButton searchBtn = new JButton("Search");
        inputPanel.add(searchField, BorderLayout.CENTER);
        inputPanel.add(searchBtn, BorderLayout.EAST);

        // Results list
        searchResultsModel = new DefaultListModel<>();
        searchResultsList = new JList<>(searchResultsModel);
        searchResultsList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(searchResultsList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Results"));

        // Close button
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> searchDialog.dispose());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(closeBtn);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        searchDialog.add(panel);

        // Search action
        searchBtn.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());

        // Double click on result
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

    // this performs search method
    private void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            return;
        }

        searchResultsModel.clear();

        DefaultMutableTreeNode root = buildResourceTree("Playlist 1");
        List<String> results = new ArrayList<>();
        searchInNode(root, searchText, results);

        for (String result : results) {
            searchResultsModel.addElement(result);
        }

        if (results.isEmpty()) {
            searchResultsModel.addElement("No results found");
        }
    }

    // this is a recursive search method
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


    public DefaultMutableTreeNode buildResourceTree(String folderName) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource(folderName);

        if (resource == null) {
            throw new IllegalArgumentException("Folder not found: " + folderName);
        }

        File folder;
        try {
            folder = new File(resource.toURI());
        } catch (Exception e) {
            throw new RuntimeException("Error accessing folder: " + folderName, e);
        }

        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("Invalid folder: " + folderName);
        }

        return buildTreeNode(folder);
    }

    private DefaultMutableTreeNode buildTreeNode(File folder) {
        Playlist playlist = new Playlist(folder.getName());
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(playlist);
        File[] files = folder.listFiles();
        if(files!= null){
            for(File file: files){
                if(file.isDirectory()){
                    node.add(buildTreeNode(file));
                } else {
                    Song song = new Song(file.getName(), file.getAbsolutePath());
                    playlist.addSong(song);
                    node.add(new DefaultMutableTreeNode(song));
                }
            }
        }

        return node;
    }
    private void addSong() {

        JFileChooser addSongFile = new JFileChooser();

        int result = addSongFile.showOpenDialog(null);
        //System.out.println(currentPlaylist.getAbsolutePath());
        if (result == JFileChooser.APPROVE_OPTION) {

            File selectedFile = addSongFile.getSelectedFile();

            // Destination folder
            Path destinationFolder = Path.of("src/main/resources/Playlist 1");

            // Create the final path (folder + filename)
            Path destinationFile =  destinationFolder.resolve(selectedFile.getName());

            try {
                Files.copy(
                        selectedFile.toPath(),
                        destinationFile,
                        StandardCopyOption.REPLACE_EXISTING
                );

                System.out.println("song added");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void removeSong() {

        JFileChooser removeSongFile = new JFileChooser();
        JOptionPane removeConfirm = new JOptionPane();

        JOptionPane.showMessageDialog(getParent(), "This action will DELETE song", "Dialog Title", JOptionPane.WARNING_MESSAGE);

        // Set the chooser to open in the playlist folder
        removeSongFile.setCurrentDirectory(new File("src/main/resources/Playlist 1"));

        int result = removeSongFile.showDialog(getParent(),"DELETE");


        if (result == JFileChooser.APPROVE_OPTION) {


            File selectedFile = removeSongFile.getSelectedFile();

            try {
                Files.deleteIfExists(selectedFile.toPath());
                System.out.println("song removed");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void createPlaylist(){
        String name = JOptionPane.showInputDialog("Enter playlist name:");
        //change this later to its own folder
        File newplaylist = new File(
                System.getProperty("user.dir") + "/src/main/resources/Playlist 1/" + name
        );
        if (newplaylist.mkdirs()) {
            System.out.println("Created: " + newplaylist.getAbsolutePath());
        } else {
            System.out.println("Folder already exists or failed.");
        }
        System.out.println(System.getProperty("user.dir")+"src/main/resources/Playlist 1/"+name);



    }
    public static void main(String[] args) {
        PlaceHolderName placeHolderName = new PlaceHolderName();
        placeHolderName.setVisible(true);
    }
}