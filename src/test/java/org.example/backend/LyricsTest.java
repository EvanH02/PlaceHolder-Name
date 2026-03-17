// Assuming our database/playlist is going to be a hashmap (key can be the songname and value can be lyrics etc)
// we need to test it by making a hashmap then making a fakes song with the values (name,genre,duration,artist, and lyrics)
//then check the hashmap for a specific song selected and show the lyrics etc.
//uhm this i did not knowing it had to be a iteration 2 thing. keeping it because we need it later anyways
package org.example.backend;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class LyricsTest {

    @Test
    void testRetrieveLyricsFromMockDatabase() {

        // 🧪 Fake database
        HashMap<String, Song> mockDB = new HashMap<>();

        // 🎵 Create fake song
        Song song = new Song("Test Song", "/path/file.mp3");
        song.setArtist("Test Artist");
        song.setGenre("Pop");
        song.setLyrics("These are the lyrics");

        // Add to "database"
        mockDB.put(song.getTitle(), song);

        // 🔍 Retrieve
        Song result = mockDB.get("Test Song");

        // ✅ Assertions
        assertNotNull(result);
        assertEquals("These are the lyrics", result.getLyrics());
        assertEquals("Test Artist", result.getArtist());
        assertEquals("Pop", result.getGenre());
    }
}
