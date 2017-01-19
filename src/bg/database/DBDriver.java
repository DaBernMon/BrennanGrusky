package bg.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import bg.config.ConfigReader;
import bg.log.Log;

public class DBDriver {

	private static Connection conn = null;
	private static final String LOG_UNIT = "DBDriver";
	
	public static Connection getConnection(){
		if (conn == null){
			Log.infoPrint(LOG_UNIT, "The database has just been loaded.", -1);
			try {
				Class.forName("org.postgresql.Driver");
				
				String host = null;
				String db = null;
				String user = null;
				String pass = null;	
				
				if(!ConfigReader.isProduction()){
					host = ConfigReader.getProperty("Dev.Host");
					db = ConfigReader.getProperty("Dev.Name");
					user = ConfigReader.getProperty("Dev.Username");
					pass = ConfigReader.getProperty("Dev.Password");	
				}
				else{
					host = ConfigReader.getProperty("Prod.Host");
					db = ConfigReader.getProperty("Prod.Name");
					user = ConfigReader.getProperty("Prod.Username");
					pass = ConfigReader.getProperty("Prod.Password");	
				}
				
				String url = "jdbc:postgresql://" + host + "/" + db;
				
				conn = DriverManager.getConnection(url, user, pass);
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}
		
		return conn;
	}
	
}
