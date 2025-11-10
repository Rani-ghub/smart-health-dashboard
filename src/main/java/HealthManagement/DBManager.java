package HealthManagement;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSetMetaData;

public class DBManager {
	public static String db_UserName="root";
	public static String db_Password="Shiva@007";
	public static String port = "3306";
	
	public static ResultSet FetchDataFromDB(String querry, String dataBaseName) throws SQLException, IOException {
		String url="jdbc:mysql://localhost:"+port+"/"+dataBaseName;
		DBConnectionManager DBInstance=DBConnectionManager.getInstance(url,db_UserName,db_Password);
		Connection conn = DBInstance.getConnection();
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery(querry);
       return rs;
	}
	
	public static int UpdateDeleteInsertDataIntoDB(String querry, String dataBaseName) throws SQLException, IOException {
		String url="jdbc:mysql://localhost:"+port+"/"+dataBaseName;
		DBConnectionManager DBInstance=DBConnectionManager.getInstance(url,db_UserName,db_Password);
		Connection conn = DBInstance.getConnection();
		Statement stat = conn.createStatement();
		int rs = stat.executeUpdate(querry);
       return rs;
	}

}