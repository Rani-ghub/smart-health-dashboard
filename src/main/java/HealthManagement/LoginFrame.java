package HealthManagement;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;

public class LoginFrame extends JFrame {
	public static String databaseName = "smart_health";

	public LoginFrame() {
		setTitle("Login");
		setSize(300, 150);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new GridLayout(3, 2));

		JTextField usernameField = new JTextField();
		JPasswordField passwordField = new JPasswordField();
		JButton loginBtn = new JButton("Login");

		add(new JLabel("Username:"));
		add(usernameField);
		add(new JLabel("Password:"));
		add(passwordField);
		add(new JLabel());
		add(loginBtn);

		loginBtn.addActionListener(e -> {
			String username = usernameField.getText();
			String password = new String(passwordField.getPassword());
			try {
				String query = String.format("SELECT * FROM users WHERE username='%s' AND password='%s'", username,
						password);
				ResultSet rs = DBManager.FetchDataFromDB(query, databaseName);
				if (rs.next()) {
					dispose();
					new DashboardFrame(username);
				} else {
					JOptionPane.showMessageDialog(this, "Invalid credentials.");
				}
			} catch (SQLException | IOException ex) {
				JOptionPane.showMessageDialog(this, "Login error: " + ex.getMessage());
			}
		});

		setVisible(true);
	}
}
