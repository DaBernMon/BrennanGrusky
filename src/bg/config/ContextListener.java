package bg.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import bg.log.Log;

import bg.config.ConfigReader;

public class ContextListener implements ServletContextListener{

	private final String LOG_UNIT = "AmazingPhrazingServletContextListener";
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		Log.infoPrint(LOG_UNIT, "The system is now shutting down", -1);
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		Log.setTrace(ConfigReader.getProperty("Logtrace").toLowerCase());
		Log.infoPrint(LOG_UNIT, "Log tracing has successfully been configured.", -1);
		 try {
			Class.forName("bg.database.Database");
//			Class.forName("bg.database.CSVReader");
//			Class.forName("bg.database.PlayByPlayReader");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}

}
