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
    private final AuthManager authManager;
    private JMenuBar menuBar;

    public PlaceHolderName(AuthManager authManager, boolean isAdmin) {
        this.authManager = authManager;
        this.isAdmin = isAdmin;

        //on start loads the playlists
        //on start up for every csv  in the data folder it is read from csv
        // window settings
        File loadPlaylist= new File("src/main/resources/data");

        if (loadPlaylist.exists() && loadPlaylist.isDirectory()) {
            File[] files = loadPlaylist.listFiles();
            if (files != null) {
                for (File p : files) {
                    String fileName = p.getName();
                    // skip non-csv and users file
                    if (!fileName.toLowerCase().endsWith(".csv")) continue;
                    if (fileName.equalsIgnoreCase("users.csv")) continue;

                    // strip .csv extension to get the playlist name
                    String playlistName = fileName.replaceFirst("(?i)\\.csv$", "");
                    Playlist l = new Playlist(playlistName);
                    System.out.println(fileName); // original file name
                    l.readCSV(p);

                    playlists.add(l);

                }
            }
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

        buildMenuBar();

        // JPanel containing the JTree
        JPanel treePanel = new JPanel();
        treePanel.setLayout(new BorderLayout());

        // create JTree from resource folder
        JScrollPane treeScrollPane = new JScrollPane(treeManager.getTree());
        treePanel.add(treeScrollPane, BorderLayout.CENTER);
        this.add(treePanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginDialog loginDialog = new LoginDialog(authManager);
                loginDialog.setVisible(true);
                if (loginDialog.isAuthenticated()) {
                    boolean admin = authManager.isAdmin(loginDialog.getLoggedInUser());
                    setAdmin(admin);
                    setLoggedInUser(loginDialog.getLoggedInUser());
                    setTitle("PlaceHolderName - Logged in as: " + loginDialog.getLoggedInUser());
                    buildMenuBar();
                }
            }
        });
        bottomPanel.add(logoutBtn);
        this.add(bottomPanel, BorderLayout.SOUTH);

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

    private void buildMenuBar() {
        if (menuBar != null) {
            try {
                this.remove(menuBar);
            } catch (Exception ignored) {}
        }
        menuBar = new JMenuBar();
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

        // admin-only: add song to root
        if (isAdmin) {
            JMenuItem addSong = new JMenuItem("Add Song");
            addSong.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    songManager.addSong();
                }
            });
            menuBar.add(addSong);
        }

        // all users: add songs to a playlist
        JMenuItem addToPlaylist = new JMenuItem("Add to Playlist");
        addToPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                songManager.addToPlaylist();
            }
        });
        menuBar.add(addToPlaylist);

        // admin-only: remove songs
        if (isAdmin) {
            JMenuItem removeSong = new JMenuItem("Remove Song");
            removeSong.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("aaaaaaa");
                    songManager.removeSong();
                }
            });
            menuBar.add(removeSong);
        }

        JMenuItem removeFromPlaylist = new JMenuItem("Remove from Playlist");
        removeFromPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlaylistRemover playlistRemover = new PlaylistRemover(PlaceHolderName.this, treeManager);
                playlistRemover.showstheplaylistremovedDialog();
            }
        });
        menuBar.add(removeFromPlaylist);

        this.add(menuBar, BorderLayout.PAGE_START);
        this.revalidate();
        this.repaint();



        JMenuItem sortPlaylist = new JMenuItem("Sort Playlist");
        sortPlaylist.addActionListener(e -> {
            SorterPlaylist sorter = new SorterPlaylist(PlaceHolderName.this, treeManager);
            sorter.showSortDialog();
        });
        menuBar.add(sortPlaylist);

        this.add(menuBar, BorderLayout.PAGE_START);
        this.revalidate();
        this.repaint();
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

                boolean admin = authManager.isAdmin(loginDialog.getLoggedInUser());
                PlaceHolderName placeHolderName = new PlaceHolderName(authManager, admin);
                placeHolderName.setLoggedInUser(loginDialog.getLoggedInUser());
                placeHolderName.setTitle("PlaceHolderName - Logged in as: " + loginDialog.getLoggedInUser());
                placeHolderName.setVisible(true);
            }
}
