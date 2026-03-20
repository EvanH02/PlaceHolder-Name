// Login dialog that gates access to the main application
package org.example.frontend;

import org.example.backend.AuthManager;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {

    private AuthManager authManager;
    private boolean authenticated = false;
    private String loggedInUser = null;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    public LoginDialog(AuthManager authManager) {
        super((Frame) null, "PlaceHolderName - Login", true);
        this.authManager = authManager;

        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // title
        JLabel titleLabel = new JLabel("PlaceHolderName Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // input fields
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // error/status message
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        bottomPanel.add(statusLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Create Account");
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // button actions
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> handleRegister());

        // enter key shortcuts
        passwordField.addActionListener(e -> handleLogin());
        usernameField.addActionListener(e -> passwordField.requestFocus());
    }

    // verify
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password.");
            return;
        }

        if (authManager.authenticate(username, password)) {
            authenticated = true;
            loggedInUser = username;
            dispose();
        } else {
            statusLabel.setText("Incorrect username or password.");
            passwordField.setText("");
        }
    }

    // create new account
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password.");
            return;
        }

        if (authManager.registerUser(username, password)) {
            statusLabel.setForeground(new Color(0, 128, 0));
            statusLabel.setText("Account created! You can now log in.");
            passwordField.setText("");
        } else {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Username already taken or invalid.");
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }
}
