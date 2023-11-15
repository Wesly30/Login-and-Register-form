import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Jdbc extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JButton loginButton;

    public Jdbc() {
        setTitle("Registration and Login");
        setSize(300, 150);
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        registerButton = new JButton("Register");
        registerButton.addActionListener((ActionEvent e) -> {
            registerUser();
        });
        panel.add(registerButton);

        loginButton = new JButton("Login");
        loginButton.addActionListener((ActionEvent e) -> {
            loginUser();
        });
        panel.add(loginButton);

        add(panel);
    }

    private void registerUser() {
        String username = usernameField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);

        try {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://root@localhost:3306/mysql", "root", "30April*")) {
                String query = "INSERT INTO users(usernames, password) VALUES (?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);
                    preparedStatement.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this, "Registration successful!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Registration failed!");
        }
    }

    private void loginUser() {
        String username = usernameField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://root@localhost:3306/mysql", "root", "30April*");
            String query = "SELECT * FROM users WHERE usernames = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String welcomeMessage = "Welcome, " + username + "!";
                JOptionPane.showMessageDialog(this, welcomeMessage);

                if (username.equals("admin")) {
                    showUsernames();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Login failed. Please check your credentials.");
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Login failed!");
        }
    }

    private void showUsernames() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://root@localhost:3306/mysql", "root", "30April*");
            String query = "SELECT names FROM users";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            ResultSet resultSet = preparedStatement.executeQuery();

            StringBuilder usernames = new StringBuilder("Usernames:\n");
            while (resultSet.next()) {
                usernames.append(resultSet.getString("username")).append("\n");
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

            JOptionPane.showMessageDialog(this, usernames.toString(), "Usernames", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve usernames.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Jdbc().setVisible(true);
            }
        });
    }
}