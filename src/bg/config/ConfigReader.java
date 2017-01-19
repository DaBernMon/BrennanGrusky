package bg.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import bg.config.ConfigReader;

public class ConfigReader {
	private static Properties properties = null;
	private static final String configPath = "config.properties";
	
	static {
		try {
			openConfigReader();
		} catch (IOException e) {
			System.err.println(e);
		}
	}
	
	private static void openConfigReader() throws IOException{
		if (properties == null){
			InputStream fileInput = ConfigReader.class.getResourceAsStream(configPath);
			properties = new Properties();
			properties.load(fileInput);
			fileInput.close();
		}
	}
	
	public static String getProperty(String key){
		return properties.getProperty(key);
	}
	
	public static boolean isProduction(){
		return Integer.parseInt(properties.getProperty("Production")) == 1;
	}
}
