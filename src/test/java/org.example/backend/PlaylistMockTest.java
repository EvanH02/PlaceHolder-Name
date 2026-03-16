package org.example.backend;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

// A fake Song class for testing purposes
class FakeSong extends Song {
    private String fakeTitle;

    public FakeSong(String title) {
        super(title, "fake/path");
        this.fakeTitle = title;
    }

    @Override
    public String getTitle() {
        return fakeTitle;
    }
}

public class PlaylistMockTest {

    @Test
    void testAddSongWithFake() {
        FakeSong fakeSong = new FakeSong("FakeSong");

        Playlist playlist = new Playlist("MyPlaylist");
        playlist.addSong(fakeSong);

        assertEquals("FakeSong", playlist.getSongs().get(0).getTitle());
    }
}