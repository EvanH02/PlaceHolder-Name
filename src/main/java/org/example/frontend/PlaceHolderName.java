// Main application frame for PlaceHolderName music organizer
package org.example.frontend;

import org.example.backend.Playlist;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class PlaceHolderName extends JFrame {
    ArrayList<Playlist> playlists = new ArrayList();

    private final MusicTreeManager treeManager;
    private final SongManager songManager;
    private final PlaylistManager playlistManager;
    private final SearchHandler searchHandler;

    public PlaceHolderName() {
        //on start loads the playlists
        // window settings
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("PlaceHolderName");
        this.setSize(500, 400);

        treeManager = new MusicTreeManager();
        songManager = new SongManager(this, treeManager);
        playlistManager = new PlaylistManager(treeManager);
        searchHandler = new SearchHandler(this, treeManager);

        //menubar containing the Add song/playlisy
        JMenuBar menuBar = new JMenuBar();
        JMenu searchMenu = new JMenu("Search");
        JMenuItem searchItem = new JMenuItem("Find Song:");
        searchItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchHandler.showSearchDialog();
            }
        });
        searchMenu.add(searchItem);
        menuBar.add(searchMenu);

        JMenuItem addSong = new JMenuItem("Add Song");
        addSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                songManager.addSong();
            }
        });

        menuBar.add(addSong);
        JMenuItem newPlaylist = new JMenuItem("Add Playlist");
        newPlaylist.addActionListener(new ActionListener() {
                                          @Override
                                          public void actionPerformed(ActionEvent e) {
                                              playlistManager.createPlaylist();
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
                songManager.removeSong();
            }
        });

        menuBar.add(removeSong);


        // JPanel containing the JTree
        JPanel treePanel = new JPanel();
        treePanel.setLayout(new BorderLayout());

        // create JTree from resource folder
        JScrollPane treeScrollPane = new JScrollPane(treeManager.getTree());
        treePanel.add(treeScrollPane, BorderLayout.CENTER);
        this.add(treePanel, BorderLayout.CENTER);

    }

    public static void main(String[] args) {
                PlaceHolderName placeHolderName = new PlaceHolderName();
                placeHolderName.setVisible(true);
            }
}
