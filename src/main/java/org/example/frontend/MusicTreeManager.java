// Manages the JTree
package org.example.frontend;

import org.example.backend.Playlist;
import org.example.backend.Song;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;

public class MusicTreeManager {

    private JTree tree;
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;

    public MusicTreeManager() {
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
    }

    public JTree getTree() {
        return tree;
    }

    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }

    public void refreshTree() {
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
}
