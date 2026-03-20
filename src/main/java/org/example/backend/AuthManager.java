// Handles user authentication, registration
package org.example.backend;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class AuthManager {

    private static final String USERS_CSV_PATH = "src/main/resources/data/users.csv";
    private static final String CSV_HEADER = "username,password,isAdmin";
    // username -> hashed password
    private HashMap<String, String> users = new HashMap<>();
    private HashMap<String, Boolean> admins = new HashMap<>();

    public AuthManager() {
        loadUsers();
        seedAdminIfNeeded();
    }

    private void loadUsers() {
        File file = new File(USERS_CSV_PATH);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            String header = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String username = parts[0].trim();
                    String hashedPassword = parts[1].trim();
                    boolean isAdmin = parts[2].trim().equalsIgnoreCase("YES");
                    users.put(username, hashedPassword);
                    admins.put(username, isAdmin);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load users.csv: " + e.getMessage());
        }
    }

    private void seedAdminIfNeeded() {
        if (!users.containsKey("admin")) {
            String hashedPass = hashPassword("admin");
            users.put("admin", hashedPass);
            admins.put("admin", true);
            saveUsers();
        }
    }

    public boolean authenticate(String username, String password) {
        if (username == null || password == null) return false;
        String storedHash = users.get(username);
        if (storedHash == null) return false;
        String inputHash = hashPassword(password);
        return storedHash.equals(inputHash);
    }

    public boolean registerUser(String username, String password) {
        if (username == null || password == null) return false;
        if (username.trim().isEmpty() || password.trim().isEmpty()) return false;
        if (username.contains(",") || password.contains(",")) return false;
        if (users.containsKey(username)) return false;

        String hashedPass = hashPassword(password);
        users.put(username, hashedPass);
        admins.put(username, false);
        saveUsers();
        return true;
    }

    public boolean isAdmin(String username) {
        return admins.getOrDefault(username, false);
    }

    // write all users back to CSV
    private void saveUsers() {
        File file = new File(USERS_CSV_PATH);
        file.getParentFile().mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(CSV_HEADER);
            writer.newLine();
            for (Map.Entry<String, String> entry : users.entrySet()) {
                String username = entry.getKey();
                String hashedPass = entry.getValue();
                String admin = admins.getOrDefault(username, false) ? "YES" : "NO";
                writer.write(username + "," + hashedPass + "," + admin);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Could not save users.csv: " + e.getMessage());
        }
    }

    // MD5 hash -> hex string
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not available", e);
        }
    }
}
