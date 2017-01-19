package bg.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import bg.database.Database;
import bg.log.Log;
import bg.nfl.utilities.Team;

public class CSVReader {

	private static String LOG_UNIT = "CSVReader";
	private static final String currentYear = "15";
	public static CSVReader csv = new CSVReader();
	
	public CSVReader(){
		long startTime = System.nanoTime();
		long endTime = -1;
		int fileCount = 0;
		int rowCount = 0;
		String path = "../NFLStats";
		BufferedReader reader = null;
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		String[] headers = null;
		ArrayList<String> playerList = new ArrayList<String>();
		ArrayList<String> duplicatePlayers = new ArrayList<String>();
		ArrayList<String> stringHeaders = new ArrayList<String>();
		ArrayList<String> numericHeaders = new ArrayList<String>();

		// Database parameters 
		String query = "INSERT INTO \"NFL-Stats\" (\"Player\",\"Last\",\"First\",\"Pos\",\"Team\",\"College\", \"Year\") VALUES (?,?,?,?,?,?,2016)";;
		ArrayList<Object> parameters = new ArrayList<Object>(0);
		String parameterTypes = "ssssss";	
		String query2 = "SELECT count(*) FROM \"NFL-Stats\" WHERE \"Player\"=? AND \"Team\"=?";
		ArrayList<Object> parameters2 = null;
		String parameterTypes2 = "ss";

		/*
		 *  Get the player list for '15
		 */
		String line = null;
		List<String> thisFileHeaders = null;
		try {
			reader = new BufferedReader(new FileReader(path + "/PlayerList.csv"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			while((line = reader.readLine()) != null){
				parameters = new ArrayList<Object>();
				parameters2 = new ArrayList<Object>();
				if(headers == null){
					headers = line.split(",");
					thisFileHeaders = Arrays.asList(headers);
				}
				else{
					String[] values = line.split(",");
					for(int i = 0; i<values.length; i++){
						if(i==0){ // Player
							String[] s = line.split("\"")[1].split(",");
							parameters.add(s[1].trim() + " " + s[0].trim());
							parameters2.add(s[1].trim() + " " + s[0].trim());
						}
						if(headers[i].equals("Team")){
							parameters2.add(values[i].trim().replaceAll("\"", ""));
						}
						parameters.add(values[i].trim().replaceAll("\"", ""));
					}	
					if((long)Database.executeQuery(query2, parameters2, parameterTypes2, -1).get(0).get("count") == 0){
						Database.executeUpdate(query, parameters, parameterTypes, -1);
					}
				}
				rowCount++;
			}
			fileCount++;
		} catch (IOException e1) {
			e1.printStackTrace();
		}


		/*
		 * Get the 2015 stats
		 */
		String query3 = "ALTER TABLE \"NFL-Stats\" ";
		ArrayList<Object> parameters3 = new ArrayList<Object>(0);
		String parameterTypes3 = "";
		String query4 = "SELECT \"ID\" FROM \"NFL-Stats\" WHERE \"Player\"=? AND \"Team\"=? AND \"Year\"=?";
		ArrayList<Object> parameters4 = null;
		String query5 = "INSERT INTO \"NFL-Stats\" (\"Player\",\"Last\",\"First\",\"Team\", \"Year\") VALUES (?,?,?,?,?)";
		ArrayList<Object> parameters5 = null;
		String parameterTypes5 = "ssssi";		
		String parameterTypes4 = "ssi";
		String query6a = "UPDATE \"NFL-Stats\" SET ";
		StringBuilder query6b = null;
		String query6c = " WHERE \"ID\"=?";
		ArrayList<Object> parameters6 = null;
		StringBuilder parameterTypes6 = null;
		String[] quoteSeparatedString = null;


		StringBuilder sb = null;
		String typeModifier = null;

		for(int x = 0; x<4; x++){
			int yearVal = Integer.parseInt(currentYear)-x;
			String thisYear = "" + yearVal;
			Log.infoPrint(LOG_UNIT, "Checking for CSVs for the year 20" + thisYear + ".", -1);
			for(File file : listOfFiles){
				if(!file.getName().contains(".csv") || file.getName().contains("PlayerList") || !file.getName().contains(thisYear)){
					continue;
				}
				else{
					fileCount++;
				}

				query6b = new StringBuilder();
				sb = new StringBuilder();

				Log.infoPrint(LOG_UNIT, "Analyzing file: " + file.getName(), -1);
				typeModifier = file.getName().split(" ")[2];

				playerList = new ArrayList<String>();

				headers = null;
				try {
					reader = new BufferedReader(new FileReader(path + "/" + file.getName()));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				try {
					while((line = reader.readLine()) != null){
						parameters5 = new ArrayList<Object>();
						parameters6 = new ArrayList<Object>();
						parameterTypes6 = new StringBuilder();

						if(headers == null){
							headers = line.split(",");
							thisFileHeaders = Arrays.asList(headers);
							Log.infoPrint(LOG_UNIT, "Headers: " + thisFileHeaders.toString(), -1);
							for(int i = 0; i<headers.length; i++){
								String s = headers[i];
								if(s.equals("Player") || s.equals("Team")){
									continue;
								}
								if(s.equals("Gms")){
									sb.append("ADD COLUMN \"" + s + "\" NUMERIC");
									query6b.append("\"" + s + "\"=?");
								}
								else{
								sb.append("ADD COLUMN \"" + typeModifier + "-" + s + "\" NUMERIC");
								query6b.append("\"" + typeModifier + "-" + s + "\"=?");
								}
								if(i != headers.length-1){
									sb.append(", ");
									query6b.append(", ");
								}
							}
							try{
								Database.executeUpdate(query3 + sb.toString(), parameters3, parameterTypes3, -1);
							}catch(Exception e){
								Log.detailPrint(LOG_UNIT, "Some of the columns were already added: \n" + sb.toString().replaceAll("ADD COLUMN", ""), -1);
							}
						}
						else{
							quoteSeparatedString = line.split("\"");
							StringBuilder qss = new StringBuilder();
							for(int i = 0; i<quoteSeparatedString.length; i++){
								if(i % 2 != 0){	// Odd indexes   Example: 16,135,"1,834",123 - [0]:16,135, [1]:1,834 [2]:,123
									quoteSeparatedString[i] = quoteSeparatedString[i].replaceAll(",", "");
								}
								qss.append(quoteSeparatedString[i]);
							}

							// Get all of the values for this row
							String[] values = qss.toString().split(",");
							parameters4 = new ArrayList<Object>(Arrays.asList(values[0], Team.getFullName(values[1]), Integer.parseInt("20" + thisYear)));

							// Add the updated/inserted values
							for(int i = 2; i<values.length; i++){
								try{
									parameters6.add(Integer.parseInt(values[i].trim().replaceAll("t", "").replaceAll("\"", "")));
									parameterTypes6.append("i");
								}
								catch(NumberFormatException e){
									parameters6.add(Double.parseDouble(values[i].trim().replaceAll("t", "").replaceAll("\"", "")));
									parameterTypes6.append("d");
								}
							}

							// Check if the player already exists in the table
							Map<String, Object> obj = Database.executeQuery(query4, parameters4, parameterTypes4, -1).get(0);
							int id = -1;
							// If they don't exist, first add the player to the table
							if(obj == null || obj.get("id") == null){
								parameters5.add(values[0]);								// Player
								String[] names = values[0].split(" ");
								if(names.length > 1){
									parameters5.add(values[0].split(" ")[1]);  			// Last
									parameters5.add(values[0].split(" ")[0]);			// First
								}
								else{
									parameters5.add(values[0]); 		 				// Last
									parameters5.add(" ");								// First
								}
								parameters5.add(Team.getFullName(values[1]));			// Team
								parameters5.add(Integer.parseInt("20" + thisYear));	// Year

								id = Database.executeUpdate(query5, parameters5, parameterTypes5, -1);
							}
							else{
								id = (int)obj.get("id");
							}

							parameterTypes6.append("i");
							parameters6.add(id);

							Database.executeUpdate(query6a + query6b.toString() + query6c, parameters6, parameterTypes6.toString(), -1);

							if(file.getName().contains(thisYear) && thisFileHeaders.contains("Team")){
								String playerName = line.split(",")[0];
								String team = line.split(",")[1];
								if(playerList.contains(playerName)){
									duplicatePlayers.add(playerName + "-" + team);
								}
								else{
									playerList.add(playerName);
								}
							}
							rowCount++;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				for(String colName : headers){
					if(colName.equals("Player") || colName.equals("Team")){
						if(!stringHeaders.contains(colName)){
							stringHeaders.add(colName);
						}
					}
					else{
						if(!numericHeaders.contains(colName)){
							numericHeaders.add(colName);
						}
					}
				}
			}
		}

		System.out.println(duplicatePlayers.toString());
		System.out.println(stringHeaders.toString());
		System.out.println(numericHeaders.toString());
		endTime = System.nanoTime();
		Log.infoPrint(LOG_UNIT, "Read through " + fileCount + " files  and " + rowCount + 
				" rows in " + ((double)(endTime-startTime)/1000000000.0) + " seconds.", -1);
	}
}
