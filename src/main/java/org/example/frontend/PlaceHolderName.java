// Main application frame for PlaceHolderName music organizer
package org.example.frontend;

import org.example.backend.AuthManager;
import org.example.backend.Playlist;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class PlaceHolderName extends JFrame {
    ArrayList<Playlist> playlists = new ArrayList();

    private final MusicTreeManager treeManager;
    private final SongManager songManager;
    private final PlaylistManager playlistManager;
    private final SearchHandler searchHandler;
    private String loggedInUser;
    private boolean isAdmin;

    public PlaceHolderName() {
        //on start loads the playlists
        //on start up for every csv  in the data folder it is read from csv
        // window settings
        File loadPlaylist= new File("src/main/resources/data");

        for(File p:loadPlaylist.listFiles()){
            if (p.getName().equals("users.csv")) continue;
            Playlist l=new Playlist(p.getName());
            System.out.println( p.getName());// getAbsolutePath());
            l.readCSV(p);

            playlists.add(l);

        }
        for (Playlist p:playlists) {
            System.out.println(p.getSongs().toString());


        }
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
                                              Playlist newPlaylist=playlistManager.createPlaylist();
                                              playlists.add(newPlaylist) ;
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
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                for(Playlist p : playlists){
                    System.out.println(p.getName());
                    p.writeCSV();

                }

                System.out.println("Playlists have been saved!");
            }
        });

    }

    public void setLoggedInUser(String user) {
        this.loggedInUser = user;
    }

    public void setAdmin(boolean admin) {
        this.isAdmin = admin;
    }

    public static void main(String[] args) {
                AuthManager authManager = new AuthManager();

                LoginDialog loginDialog = new LoginDialog(authManager);
                loginDialog.setVisible(true);

                if (!loginDialog.isAuthenticated()) {
                    System.exit(0);
                }

                PlaceHolderName placeHolderName = new PlaceHolderName();
                placeHolderName.setLoggedInUser(loginDialog.getLoggedInUser());
                placeHolderName.setAdmin(authManager.isAdmin(loginDialog.getLoggedInUser()));
                placeHolderName.setTitle("PlaceHolderName - Logged in as: " + loginDialog.getLoggedInUser());
                placeHolderName.setVisible(true);
            }
}
