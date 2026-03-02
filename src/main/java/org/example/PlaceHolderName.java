package org.example;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.File;
import java.net.URL;


public class PlaceHolderName extends JFrame {

    public PlaceHolderName() {
        // window settings
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("PlaceHolder Name");
        this.setSize(400, 300);

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


    public static void main(String[] args) {
        PlaceHolderName placeHolderName = new PlaceHolderName();
        placeHolderName.setVisible(true);
    }
}