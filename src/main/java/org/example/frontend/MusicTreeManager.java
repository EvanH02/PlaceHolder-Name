// Manages the JTree
package org.example.frontend;

import org.example.backend.Playlist;
import org.example.backend.Song;
import org.example.backend.CsvStore;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class MusicTreeManager {

    private JTree tree;
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;

    public MusicTreeManager() {
        rootNode = buildResourceTree();
        treeModel = new DefaultTreeModel(rootNode, true);
        tree = new JTree(treeModel);

        // playlists get folder icon, songs get music note emoji
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
                        setText("\uD83C\uDFB5 " + ((Song) userObj).getTitle());
                        setIcon(null);
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
        rootNode = buildResourceTree();
        treeModel.setRoot(rootNode);
        treeModel.reload();
        // expand all nodes
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    public DefaultMutableTreeNode buildResourceTree (){
        // root playlist from RootSongs.csv
        Playlist rootPlaylist = new Playlist("PlaceHolder Collection");
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootPlaylist);
        root.setAllowsChildren(true);

        List<Song> rootSongs = CsvStore.readSongsFromCsv(CsvStore.ROOT_CSV);
        for (Song s : rootSongs) {
            rootPlaylist.addSong(s);
            DefaultMutableTreeNode songNode = new DefaultMutableTreeNode(s);
            songNode.setAllowsChildren(false);
            root.add(songNode);
        }

        // playlists from data directory (each playlist is a csv file other than RootSongs.csv and users.csv)
        File dataDir = new File(CsvStore.DATA_DIR);
        String lyricsFileName = new File(CsvStore.LYRICS_CSV).getName();
        if (dataDir.exists() && dataDir.isDirectory()) {
            File[] files = dataDir.listFiles((dir, name) -> {
                return name.toLowerCase().endsWith(".csv") && !name.equalsIgnoreCase("RootSongs.csv") && !name.equalsIgnoreCase("users.csv") && !name.equalsIgnoreCase(lyricsFileName);
            });
            if (files != null) {
                for (File f : files) {
                    String playlistName = f.getName().replaceFirst("\\.csv$", "");
                    Playlist p = new Playlist(playlistName);
                    DefaultMutableTreeNode pNode = new DefaultMutableTreeNode(p);
                    pNode.setAllowsChildren(true);
                    List<Song> songs = CsvStore.readSongsFromCsv(f.getAbsolutePath());
                    for (Song s : songs) {
                        p.addSong(s);
                        DefaultMutableTreeNode sNode = new DefaultMutableTreeNode(s);
                        sNode.setAllowsChildren(false);
                        pNode.add(sNode);
                    }
                    root.add(pNode);
                }
            }
        }

        return root;
    }
}
