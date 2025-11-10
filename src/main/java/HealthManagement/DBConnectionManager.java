package HealthManagement;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionManager {
	private static DBConnectionManager instance;
	private Connection conn;
	private DBConnectionManager(String url, String db_UserName, String db_Password) throws IOException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			this.conn = DriverManager.getConnection(url, db_UserName, db_Password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return this.conn;
	}

	public static DBConnectionManager getInstance(String url, String db_UserName, String db_Password)
			throws SQLException, IOException {
		if (instance == null) {
			instance = new DBConnectionManager(url, db_UserName, db_Password);
		} else if (instance.getConnection().isClosed()) {
			instance = new DBConnectionManager(url, db_UserName, db_Password);
		}
		return instance;
	}
}
