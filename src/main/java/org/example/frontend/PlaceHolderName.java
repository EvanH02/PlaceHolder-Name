// Main application frame for PlaceHolderName music organizer
package org.example.frontend;

import org.example.backend.AuthManager;
import org.example.backend.Playlist;
import org.example.backend.Song;
import org.example.backend.CsvStore;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    private static class LightGradientPanel extends JPanel {
        LightGradientPanel() { super(true); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            Color c1 = new Color(220,235,255); // slightly more blue
            Color c2 = new Color(238,245,250); // slightly more blue/near-white
            GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    private static class DarkGradientPanel extends JPanel {
        DarkGradientPanel() { super(true); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            Color c1 = new Color(12,30,72); // slightly darker lyric blue
            Color c2 = new Color(6,12,36);  // darker base
            GradientPaint gp = new GradientPaint(0, 0, c2, 0, getHeight(), c1);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

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
        this.setSize(700, 500);

        // keep default content pane; apply gradients only to the two split areas below

        treeManager = new MusicTreeManager();
        songManager = new SongManager(this, treeManager);
        playlistManager = new PlaylistManager(treeManager);
        searchHandler = new SearchHandler(this, treeManager);

        buildMenuBar();

        // ensure any tree-model reloads trigger a save
        treeManager.getTreeModel().addTreeModelListener(new javax.swing.event.TreeModelListener() {
            @Override
            public void treeNodesChanged(javax.swing.event.TreeModelEvent e) { }

            @Override
            public void treeNodesInserted(javax.swing.event.TreeModelEvent e) { saveAllFromTree(); }

            @Override
            public void treeNodesRemoved(javax.swing.event.TreeModelEvent e) { saveAllFromTree(); }

            @Override
            public void treeStructureChanged(javax.swing.event.TreeModelEvent e) { saveAllFromTree(); }
        });

        // left: JTree
        JScrollPane treeScrollPane = new JScrollPane(treeManager.getTree());
        treeScrollPane.setOpaque(false);
        treeScrollPane.getViewport().setOpaque(false);
        treeManager.getTree().setOpaque(false);

        // right: lyrics display
        JTextArea lyricsArea = new JTextArea();
        lyricsArea.setEditable(false);
        lyricsArea.setBackground(Color.BLACK);
        lyricsArea.setForeground(Color.WHITE);
        lyricsArea.setFont(new Font("Serif", Font.PLAIN, 18));
        lyricsArea.setLineWrap(true);
        lyricsArea.setWrapStyleWord(true);
        JScrollPane lyricsScroll = new JScrollPane(lyricsArea);
        lyricsScroll.setBorder(BorderFactory.createTitledBorder("Lyrics"));
        lyricsScroll.setOpaque(false);
        lyricsScroll.getViewport().setOpaque(false);
        lyricsArea.setOpaque(false);

        // split pane with gradient panels
        LightGradientPanel leftPanel = new LightGradientPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(treeScrollPane, BorderLayout.CENTER);

        DarkGradientPanel rightPanel = new DarkGradientPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(lyricsScroll, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(300);
        this.add(splitPane, BorderLayout.CENTER);

        // selection listener to display lyrics when a Song is selected
        treeManager.getTree().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeManager.getTree().getLastSelectedPathComponent();
                if (node == null) return;
                Object obj = node.getUserObject();
                if (obj instanceof Song) {
                    Song s = (Song) obj;
                    String lyrics = s.getLyrics();
                    if (lyrics == null || lyrics.trim().isEmpty()) {
                        lyricsArea.setText("(No lyrics available)");
                    } else {
                        lyricsArea.setText(lyrics);
                    }
                    lyricsArea.setCaretPosition(0);
                } else {
                    lyricsArea.setText("");
                }
            }
        });

        // double-click to edit song (admin only)
        treeManager.getTree().addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeManager.getTree().getLastSelectedPathComponent();
                    if (node == null) return;
                    Object obj = node.getUserObject();
                    if (!(obj instanceof Song)) return;
                    if (!isAdmin) {
                        JOptionPane.showMessageDialog(PlaceHolderName.this, "Only admins can edit songs.", "Permission denied", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    Song song = (Song) obj;
                    // build edit dialog
                    JTextField titleField = new JTextField(song.getTitle());
                    JTextField genreField = new JTextField(song.getGenre());
                    JTextField artistField = new JTextField(song.getArtist());
                    JTextArea editLyrics = new JTextArea(10, 30);
                    editLyrics.setText(song.getLyrics());

                    JPanel panel = new JPanel(new BorderLayout(5,5));
                    JPanel fields = new JPanel(new GridLayout(3,2,5,5));
                    fields.add(new JLabel("Title:")); fields.add(titleField);
                    fields.add(new JLabel("Genre:")); fields.add(genreField);
                    fields.add(new JLabel("Artist:")); fields.add(artistField);
                    panel.add(fields, BorderLayout.NORTH);
                    panel.add(new JScrollPane(editLyrics), BorderLayout.CENTER);

                    int res = JOptionPane.showConfirmDialog(PlaceHolderName.this, panel, "Edit Song", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if (res != JOptionPane.OK_OPTION) return;

                    String newTitle = titleField.getText().trim();
                    String newGenre = genreField.getText().trim();
                    String newArtist = artistField.getText().trim();
                    String newLyrics = editLyrics.getText();

                    // update root songs file (Title is unique identifier here)
                    List<Song> rootSongs = CsvStore.readSongsFromCsv(CsvStore.ROOT_CSV);
                    boolean updated = false;
                    for (Song s : rootSongs) {
                        if (s.getTitle().equals(song.getTitle())) {
                            s.setTitle(newTitle);
                            s.setGenre(newGenre);
                            s.setArtist(newArtist);
                            s.setLyrics(newLyrics);
                            updated = true;
                            break;
                        }
                    }
                    if (updated) {
                        CsvStore.writeSongsToCsv(CsvStore.ROOT_CSV, rootSongs);
                    }

                    // update lyrics map
                    java.util.Map<String, String> lyricsMap = CsvStore.readLyricsMap(CsvStore.LYRICS_CSV);
                    // remove old title key if title changed
                    if (!song.getTitle().equals(newTitle)) {
                        lyricsMap.remove(song.getTitle());
                    }
                    lyricsMap.put(newTitle, newLyrics);
                    CsvStore.writeLyricsMap(CsvStore.LYRICS_CSV, lyricsMap);

                    treeManager.refreshTree();
                }
            }
        });

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
                saveAllFromTree();
             }
         });

    }

    public void saveAllFromTree() {
        try {
            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeManager.getTreeModel().getRoot();
            if (rootNode == null) return;

            // save root songs
            Object rootObj = rootNode.getUserObject();
            if (rootObj instanceof Playlist) {
                List<Song> rootSongs = new ArrayList<>();
                for (int i = 0; i < rootNode.getChildCount(); i++) {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) rootNode.getChildAt(i);
                    Object u = child.getUserObject();
                    if (u instanceof Song) rootSongs.add((Song) u);
                }
                CsvStore.writeSongsToCsv(CsvStore.ROOT_CSV, rootSongs);
            }

            // save playlist CSVs
            for (int i = 0; i < rootNode.getChildCount(); i++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) rootNode.getChildAt(i);
                Object u = child.getUserObject();
                if (u instanceof Playlist) {
                    Playlist p = (Playlist) u;
                    List<Song> songs = new ArrayList<>();
                    for (int j = 0; j < child.getChildCount(); j++) {
                        DefaultMutableTreeNode songNode = (DefaultMutableTreeNode) child.getChildAt(j);
                        Object so = songNode.getUserObject();
                        if (so instanceof Song) songs.add((Song) so);
                    }
                    String filename = CsvStore.DATA_DIR + p.getName() + ".csv";
                    CsvStore.writeSongsToCsv(filename, songs);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

        // only for non-admin users: remove from playlist
        if (!isAdmin) {
            JMenuItem removeFromPlaylist = new JMenuItem("Remove from Playlist");
            removeFromPlaylist.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    PlaylistRemover playlistRemover = new PlaylistRemover(PlaceHolderName.this, treeManager);
                    playlistRemover.showstheplaylistremovedDialog();
                }
            });
            menuBar.add(removeFromPlaylist);
        }

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
