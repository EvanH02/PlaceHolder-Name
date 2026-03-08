package org.example;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;


public class PlaceHolderName extends JFrame {
    ArrayList<Playlist> playlists= new ArrayList();

    public PlaceHolderName() {
        //on start loads the playlists
        // window settings
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("PlaceHolder Name");
        this.setSize(400, 300);
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
