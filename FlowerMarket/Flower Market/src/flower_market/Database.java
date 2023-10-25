package flower_market;


import java.sql.Connection;
import java.sql.DriverManager;

public class Database {
	public Connection conn;
	
	public Connection getConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/fteammarket_db", "root", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
}
