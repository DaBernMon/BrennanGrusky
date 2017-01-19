package bg.log;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import bg.config.ConfigReader;
import bg.log.Log;

/**
 * Provides functionality to issue system prints with trace levels
 * if the provided message's specified level is lower or equal to the
 * trace level set for the class producing the message, then a message
 * will be printed in the system log of the format:
 * date unit type message, so for example:
 * Jan 1, 2016 12:30:15 AM EnterGame S Starting game 1.
 */
public class Log {

	// To be used to write to the log file
	private static PrintWriter logFile;
	private static File log;
	private static String logLocation;
	
	// When the log class is initially loaded in memory, open the log file
	static {
		try {
			logLocation = ConfigReader.getProperty("Log.File");
			// Create the log file if it doesn't exist already
			log = new File(logLocation);
			if (!log.exists()){
				log.createNewFile();
			}
			
			// Take out the hardcoded path and add it to our config file later
			logFile = new PrintWriter(new BufferedWriter(new FileWriter(logLocation, true)));
		} catch (IOException e) {
			// Freak out!  We can't create/open the log file for some reason
			e.printStackTrace();
		}
	}
	
	// Constant values for log levels
	private static final int LOG_NONE 	= 0;
	private static final int LOG_ERROR 	= 1;
	private static final int LOG_WARN 	= 2;
	private static final int LOG_INFO 	= 3;
	private static final int LOG_FLOW 	= 101;
	private static final int LOG_STATE	= 102;
	private static final int LOG_DETAIL	= 103;
	private static final int LOG_ALL 	= 999;
	private static final String LOG_UNIT = "Log";

	// Level if the specific unit was not specified (the "all" trace level)
	private static int traceLevel = 0;

	/**
	 *  Trace info where the key is the Class name and the value is the trace level, which can be:
	 *  	none, error, warn, info, flow, state, detail, all
	 */	
	private static Hashtable<String, Hashtable<String, ArrayList<Integer>>> traceInfo = null;

	/**
	 * Sets the starting trace information based on the values provided in the config file
	 * @param configInfo - string of information from the config.properties file of the format
	 * 1: "class:level,class:level..."
	 * 2: "class:level:gameID1+gameID2+...,class:level..."
	 * 3: "class:level:gameID1-gameIDx+...,class:level..."
	 */
	public static boolean setTrace(String configInfo){

		// Turns off all tracing if none is provided in the config file
		if(configInfo == null){
			configInfo = "all:none";
		}

		// Separates the different trace levels, which are expected to be comma delimited
		traceInfo = new Hashtable<String, Hashtable<String, ArrayList<Integer>>>();
		
		return addTrace(configInfo);		
	}
	
	/**
	 * Adds the specified trace settings to the existing trace
	 * follows the same convention as above
	 * @param configInfo
	 */
	public static boolean addTrace(String configInfo){
		
		// If nothing was specified, do nothing
		if(configInfo == null || configInfo.length() == 0){
			return false;
		}
		
		configInfo = configInfo.toLowerCase();

		// Get all of the trace info
		String[] pairs = configInfo.split(",");
		for(String str : pairs){
			Hashtable<String, ArrayList<Integer>> tempTable = new Hashtable<String, ArrayList<Integer>>();
			String[] tempStr = str.split(":");
			
			// If the class information already exists
			Hashtable<String, ArrayList<Integer>> existingTrace = traceInfo.get(tempStr[0]);
			if(existingTrace != null){
				tempTable = existingTrace;
			}

			// If the level was not specified for a given unit (bad format)
			if(tempStr.length < 2){
				Log.nonePrint(LOG_UNIT, "Invalid input trace level: " + str, -1);
				continue;
			}	
			// If a particular game was specified
			else if(tempStr.length == 3){
				// Split the different game IDs into separate arrays
				ArrayList<Integer> tempInt = new ArrayList<Integer>();
				
				// Check if the class and trace level already exists
				if(existingTrace != null){
					ArrayList<Integer> existingTraceLevel = existingTrace.get(tempStr[1]);
					if(existingTraceLevel != null){
						tempInt = existingTraceLevel;
					}
				}
				String[] tempGameID = tempStr[2].split("\\+");
				for(String gID : tempGameID){
					String[] gIdRange = gID.split("\\-");

					try{
						// If a range was specified
						if(gIdRange.length == 2){

							// Add each game ID, one game at a time
							for(int i = Integer.parseInt(gIdRange[0].trim()); i<=Integer.parseInt(gIdRange[1].trim()); i++){
								if(! tempInt.contains(i)){
									tempInt.add(i);
								}
							}
						}
						// If a single number was specified
						else if(gIdRange.length == 1){
							int addingInteger = Integer.parseInt(gID.trim());
							// Add the single game ID
							if(! tempInt.contains(addingInteger)){
								tempInt.add(addingInteger);	
							}
						}
						else{
							// Invalid format for number range
							Log.nonePrint(LOG_UNIT, "Invalid format for game ID range", -1);
							continue;
						}
					}
					catch(NumberFormatException nfe){
						// Format for this game ID was invalid, ignore it
						Log.nonePrint(LOG_UNIT, "Invalid format when specifying game ID", -1);
						continue;
					}
				}
				tempTable.put(tempStr[1], tempInt);
			}
			// If a particular game was not specified, specify -1, or all games
			else if(tempStr.length < 3){
				// Have at this level for all games, unless specified in another level
				ArrayList<Integer> addedArrayList = new ArrayList<Integer>(Arrays.asList(-1));
				if(tempTable.get(tempStr[1]) != null){
					addedArrayList = tempTable.get(tempStr[1]);
					if(!addedArrayList.contains(-1)){
						addedArrayList.add(-1);
					}
				}
				tempTable.put(tempStr[1], addedArrayList);
			}
			traceInfo.put(tempStr[0], tempTable);
		}

		// Set the trace level for all unspecified units
		traceLevel = findTraceLevel("all", -1);		

		Log.detailPrint(LOG_UNIT, traceInfo.toString(), -1);
		return true;
	}
	
	/**
	 * Removes the specified trace settings if they exist
	 * @param removedTrace
	 * @return
	 */
	public static boolean removeTrace(String removedTrace){
		// If nothing was specified, do nothing
		if(removedTrace == null || removedTrace.length() == 0){
			return false;
		}
		
		// Loop through each class
		String[] splitTrace = removedTrace.toLowerCase().split(",");
		for(String splitString : splitTrace){
			String[] allComponentsArray = splitString.split(":");
			if(allComponentsArray.length < 1){
				continue;
			}
			String classString = allComponentsArray[0];
			String levelString = null;
			if(allComponentsArray.length > 1){
				levelString = allComponentsArray[1];
			}
			String gameString = null;
			if(allComponentsArray.length > 2){
				gameString = allComponentsArray[2];
			}
			
			// If the trace level was not specified
			if(levelString == null){
				traceInfo.remove(classString);
				continue;
			}
			
			// If the game ID was not specified
			if(gameString == null){
				if(traceInfo.get(classString) == null){
					continue;
				}
				traceInfo.get(classString).remove(levelString);
				if(traceInfo.get(classString).size() == 0){
					traceInfo.remove(classString);
				}
				continue;
			}
			
			// Remove the specified game IDs from the space level
			if(traceInfo.get(classString) == null || traceInfo.get(classString).get(levelString) == null){
				continue;
			}
			String[] idList = gameString.split("\\+");
			for(String gID : idList){
				String[] gIdRange = gID.split("\\-");

				try{
					// If a range was specified
					if(gIdRange.length == 2){

						// Remove each game ID, one game at a time
						for(int i = Integer.parseInt(gIdRange[0].trim()); i<=Integer.parseInt(gIdRange[1].trim()); i++){
							if(traceInfo.get(classString).get(levelString).indexOf(i) != -1){
								traceInfo.get(classString).get(levelString).remove(
									traceInfo.get(classString).get(levelString).indexOf(i));
							}
						}
					}
					// If a single number was specified
					else if(gIdRange.length == 1){
						if(traceInfo.get(classString).get(levelString).indexOf(Integer.parseInt(gID.trim())) != -1){
							traceInfo.get(classString).get(levelString).remove(
								traceInfo.get(classString).get(levelString).indexOf(Integer.parseInt(gID.trim())));
						}
					}
					else{
						// Invalid format for number range
						Log.nonePrint(LOG_UNIT, "Invalid format for game ID range", -1);
						continue;
					}
					
					// Ensure nothing is left empty
					if(traceInfo.get(classString).get(levelString).size() == 0){
						traceInfo.get(classString).remove(levelString);
					}
					if(traceInfo.get(classString).size() == 0){
						traceInfo.remove(classString);
					}
					
				}
				catch(NumberFormatException nfe){
					// Format for this game ID was invalid, ignore it
					Log.nonePrint(LOG_UNIT, "Invalid format when specifying game ID", -1);
					continue;
				}
			}			
		}
		return true;
	}

	/**
	 * Will print message regardless of tracing set for provided LOG_UNIT
	 * @param LOG_UNIT - the unit which is issuing the message
	 * @param message - the message to be issued
	 * @param gameID - the game ID related to the message, -1 if game ID does not matter
	 */
	public static void nonePrint(String LOG_UNIT, String message, int gameID){
		logPrint(message, LOG_NONE, LOG_UNIT, gameID);
	}

	/**
	 * Will print message only if the provided LOG_UNIT has a trace level
	 * or the "all" trace level
	 * is set to "Error" or higher.
	 * @param LOG_UNIT - the unit which is issuing the message
	 * @param message - the message to be issued
	 * @param gameID - the game ID related to the message, -1 if game ID does not matter
	 */
	public static void errorPrint(String LOG_UNIT, String message, int gameID){
		logPrint(message, LOG_ERROR, LOG_UNIT, gameID);
	}

	/**
	 * Will print message only if the provided LOG_UNIT has a trace level
	 * or the "all" trace level
	 * is set to "Warn" or higher.
	 * @param LOG_UNIT - the unit which is issuing the message
	 * @param message - the message to be issued
	 * @param gameID - the game ID related to the message, -1 if game ID does not matter
	 */
	public static void warnPrint(String LOG_UNIT, String message, int gameID){
		logPrint(message, LOG_WARN, LOG_UNIT, gameID);
	}

	/**
	 * Will print message only if the provided LOG_UNIT has a trace level
	 * or the "all" trace level
	 * is set to "Info" or higher.
	 * @param LOG_UNIT - the unit which is issuing the message
	 * @param message - the message to be issued
	 * @param gameID - the game ID related to the message, -1 if game ID does not matter
	 */
	public static void infoPrint(String LOG_UNIT, String message, int gameID){
		logPrint(message, LOG_INFO, LOG_UNIT, gameID);
	}

	/**
	 * Will print message only if the provided LOG_UNIT has a trace level
	 * or the "all" trace level
	 * is set to "Flow" or higher.
	 * @param LOG_UNIT - the unit which is issuing the message
	 * @param message - the message to be issued
	 * @param gameID - the game ID related to the message, -1 if game ID does not matter
	 */
	public static void flowPrint(String LOG_UNIT, String message, int gameID){
		logPrint(message, LOG_FLOW, LOG_UNIT, gameID);
	}

	/**
	 * Will print message only if the provided LOG_UNIT has a trace level
	 * or the "all" trace level
	 * is set to "State" or higher.
	 * @param LOG_UNIT - the unit which is issuing the message
	 * @param message - the message to be issued
	 * @param gameID - the game ID related to the message, -1 if game ID does not matter
	 */
	public static void statePrint(String LOG_UNIT, String message, int gameID){
		logPrint(message, LOG_STATE, LOG_UNIT, gameID);
	}

	/**
	 * Will print message only if the provided LOG_UNIT has a trace level
	 * or the "all" trace level
	 * is set to "Detail" or higher.
	 * @param LOG_UNIT - the unit which is issuing the message
	 * @param message - the message to be issued
	 * @param gameID - the game ID related to the message, -1 if game ID does not matter
	 */
	public static void detailPrint(String LOG_UNIT, String message, int gameID){
		logPrint(message, LOG_DETAIL, LOG_UNIT, gameID);
	}

	/**
	 * Will print message only if the provided LOG_UNIT has a trace level
	 * or the "all" trace level
	 * is set to "All".
	 * @param LOG_UNIT - the unit which is issuing the message
	 * @param message - the message to be issued
	 * @param gameID - the game ID related to the message, -1 if game ID does not matter
	 */// Works the same as just calling logPrint, but will still check the trace level first
	public static void allPrint(String LOG_UNIT, String message, int gameID){
		logPrint(message, LOG_ALL, LOG_UNIT, gameID);
	}
	
	/**
	 * Returns the existing trace level as a string of HTML
	 * @return
	 */
	public static String getTraceInfoHtml(){
		StringBuilder returnedHtml = new StringBuilder();
		String[] traceKeys = traceInfo.keySet().toArray(new String[0]);
		for(int i = 0; i<traceKeys.length; i++){
			returnedHtml.append("<p>");
			returnedHtml.append(traceKeys[i]);
			returnedHtml.append(": ");
			returnedHtml.append(traceInfo.get(traceKeys[i]));
			returnedHtml.append("</p>");
		}
		return returnedHtml.toString();
	}

	/**
	 * Looks through the Hashmap for a particular unit and finds the highest
	 * trace level set for the particular game specified.  For example:
	 * Database has detail for game IDs 1 and 2, but info print for game IDs 1, 3, and -1
	 * Then if the game ID given was 1 for the DB class, then LOG_DETAIL will be returned
	 * @param unit   the unit being checked
	 * @param gameID the game ID being checked, or -1 for all games
	 * @return
	 */
	public static int findTraceLevel(String unit, int gameID){
		if(unit == null){
			return traceLevel;
		}

		int retVal = -1;

		// Get trace info for the particular unit
		Hashtable<String, ArrayList<Integer>> tempTable = traceInfo.get(unit);		
		if(tempTable == null){
			tempTable = traceInfo.get("all");
			if(tempTable == null){
				// Should not happen, because "all" was not set
				return traceLevel;
			}
		}
		
		Object[] tempObj = tempTable.keySet().toArray();
		ArrayList<Integer> tempList = null;

		// Loop through all the trace levels specified for this unit
		for(Object obj : tempObj){
			String str = obj.toString();
			tempList = tempTable.get(str);

			// If the particular game ID has been specified for that level
			// Only replace it if this level is finer
			if(tempList != null && tempList.contains(gameID)){
				if(str.toLowerCase().equals("none") && retVal < LOG_NONE){
					retVal = LOG_NONE;
				}
				else if(str.toLowerCase().equals("error") && retVal < LOG_ERROR){
					retVal = LOG_ERROR;
				}
				else if(str.toLowerCase().equals("warn") && retVal < LOG_WARN){
					retVal = LOG_WARN;
				}
				else if(str.toLowerCase().equals("info") && retVal < LOG_INFO){
					retVal = LOG_INFO;
				}
				else if(str.toLowerCase().equals("state") && retVal < LOG_STATE){
					retVal = LOG_STATE;
				}
				else if(str.toLowerCase().equals("flow") && retVal < LOG_FLOW){
					retVal = LOG_FLOW;
				}
				else if(str.toLowerCase().equals("detail") && retVal < LOG_DETAIL){
					retVal = LOG_DETAIL;
				}
				else if(str.toLowerCase().equals("all") && retVal < LOG_ALL){
					retVal = LOG_ALL;
				}
				else{
					// Unexpected input, do nothing
					continue;
				}
			}
		}

		// If the specific game ID was not specified, find the global trace level
		if(retVal == -1 && gameID != -1){
			return(findTraceLevel(unit, -1));
		}
		// Otherwise, return the default, if not set: 0
		else if(retVal == -1 && gameID == -1){
			return traceLevel;
		}

		return retVal;
	}


	/**
	 * Converts an integer value level to the initial of the equivalent trace level
	 * if the input is not valid, will return "U" for unknown
	 * @param level
	 * @return
	 */
	private static String getMessageInitial(int level){
		if(level == LOG_NONE){
			return "N";	
		}
		else if(level == LOG_ERROR){
			return "E";	
		}
		else if(level == LOG_WARN){
			return "W";
		}
		else if(level == LOG_INFO){
			return "I";
		}
		else if(level == LOG_STATE){
			return "S";
		}
		else if(level == LOG_FLOW){
			return "F";
		}
		else if(level == LOG_DETAIL){
			return "D";
		}
		else if(level == LOG_ALL){
			return "A";
		}
		else{
			return "U";		// Unknown
		}
	}

	/**
	 * Called from the public print methods to compare the message level and the trace level
	 * when proper tracing is enabled, it will produce a message of the format:
	 * date unit type> message
	 * For example:
	 * Jan 1, 2016 12:30:15 AM EnterGame E User name for this session is testUser
	 * @param message
	 * @param msgLevel
	 * @param unit
	 * @param gameID - the game ID related to the message, -1 if game ID does not matter
	 */
	private static void logPrint(String message, int msgLevel, String unit, int gameID){
		// Ensure traceInfo has been set already
		if(traceInfo == null){
			return;
		}

		String separator = " ";

		String[] splitUnit = unit.split("->");
		boolean tracePasses = false;
		for(int i = 0; i<splitUnit.length; i++){
			if(findTraceLevel(splitUnit[i].replaceAll("\\[.*\\]", "").toLowerCase(), gameID) >= msgLevel){
				tracePasses = true;
			}
		}

		// Compare this trace level to see if the print is necessary
		if(tracePasses){
			// Get date info
			DateFormat df = new SimpleDateFormat("MMM dd, yyyy h:mm:ss a");
			String date = df.format(new java.util.Date().getTime());

			// Print the message
			StringBuilder output = new StringBuilder()
			.append(date).append(separator)
			.append(unit).append(separator)
			.append(getMessageInitial(msgLevel)).append(separator);
			if(gameID != -1){
				output.append("Game: ").append(gameID).append(separator);
			}
			output.append(message);

			System.out.println(output.toString());
			
			try{
				if (!log.exists()){
					log.createNewFile();
					logFile = new PrintWriter(new BufferedWriter(new FileWriter(logLocation, true)));
				}
				
				logFile.println(output.toString());
				logFile.flush();
			} catch(Exception e){
				//No Worries; Be Happy! Seriously though, what happened to our log???
				System.out.println(e);
			}
		}
	}

	/**
	 * Add to all public util methods and the database, so that the LOG_UNIT can show their original caller
	 * @param log_unit - the log unit of the util class
	 * @return
	 */
	public static String getNonUtilCaller(String log_unit){
		String retVal = "";
		// Get the class that called the DB
		String callingThread = Thread.currentThread().getStackTrace()[3].toString();
		if(callingThread != null){
			callingThread = callingThread.substring(callingThread.indexOf('(')+1);
			callingThread = callingThread.substring(0, callingThread.indexOf("."));
			retVal = callingThread + "->" + log_unit;
		}

		// If the caller is a utility class, append the class that called it too
		String secondCaller = "";
		int count = 4;
		if(callingThread.equals("Database") || callingThread.equals("Email") || callingThread.equals("FormType") || callingThread.equals("Game") ||
				callingThread.equals("GameList") || callingThread.equals("Guess") || callingThread.equals("GuessInfo") ||
				callingThread.equals("Question") || callingThread.equals("Response") || callingThread.equals("Score") ||
				callingThread.equals("Security") || callingThread.equals("User") || callingThread.equals("UserQuestion")){
			secondCaller = callingThread;
			while(secondCaller.equals(callingThread) && Thread.currentThread().getStackTrace().length > count){
				secondCaller = Thread.currentThread().getStackTrace()[count].toString();
				secondCaller = secondCaller.substring(secondCaller.indexOf('(')+1);
				secondCaller = secondCaller.substring(0, secondCaller.indexOf("."));
				count++;
			}
			retVal = secondCaller + "->" + callingThread + "[" + (count-4) + "]->" + log_unit;
		}
		return retVal;
	}
}
