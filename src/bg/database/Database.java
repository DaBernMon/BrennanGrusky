package bg.database;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import bg.log.Log;

import bg.database.DBDriver;

public class Database {

	private static String LOG_UNIT = "Database";

	private static Connection db = DBDriver.getConnection();
	private static PreparedStatement ps = null;
	private static ResultSet rs = null;

	/**
	 * This method should be called whenever a SQL INSERT, SQL UPDATE, or SQL DELETE query needs to happen.
	 * It will handle the basic database classes needed and will also handle escaping strings to prevent XSS.
	 * 
	 * Example Usage:
	 * String query = "INSERT INTO <table-name> (\"Column-String\", \"Column-Integer\") VALUES (?, ?)";
	 * 
	 * int id = Database.executeUpdate(query, new ArrayList,Object>(Arrays.asList("String", 5)), "si", gameID);
	 * 
	 * @param query - a normal SQL query
	 * @param parameters - ArrayList of Objects (can really be integers, strings etc)
	 * @param parameterTypes - a string that matches the types of the parameters in the ArrayList
	 * @param gameID - the game ID related to the message, -1 if game ID does not matter
	 * @return - the id of newly inserted/updated row or -1 if the insert/update failed
	 * @throws Exception - Can be a generic exception or a SQL exception
	 */
	public static int executeUpdate(String query, ArrayList<Object> parameters, String parameterTypes, int gameID){
		LOG_UNIT = Log.getNonUtilCaller("Database");
		StringBuilder sb = new StringBuilder();
		try{
			//Check to make sure we have the same number of parameters as we have types
			if (parameters.size() != parameterTypes.length()){
				Log.errorPrint(LOG_UNIT, "The number of parameters (" + parameters.size() +
						") doesn't match the number of parameter types (" + parameterTypes.length() + ")!", gameID);
			}

			//Convert the parameter types string to lower case so we don't have to deal with checking both cases
			parameterTypes = parameterTypes.toLowerCase();

			//Close the prepared statement and result set just in case
			if (ps != null){
				ps.close();
			}

			if (rs != null){
				rs.close();
			}

			//Create the prepared statement
			String executeType = query.split(" ")[0];

			// For the detail print
			String[] queryPieces = query.split("\\?");
			sb.append("Query: ").append(" ").append(queryPieces[0]);

			String executeAddOn = "";
			if(executeType.trim().toLowerCase().equals("update")){
				executeAddOn = "";
			}
			else if(executeType.trim().toLowerCase().equals("delete")){
				executeAddOn = "";
			}
			else if(executeType.trim().toLowerCase().equals("insert")){
				executeAddOn = " RETURNING \"ID\"";
			}
			else{
				executeAddOn = "";
			}
			ps = db.prepareStatement(query + executeAddOn);

			for (int x = 0; x < parameters.size(); x++){
				char paramType = parameterTypes.charAt(x);

				// Append the value and its type and the next part of the query
				sb.append(parameters.get(x).toString()).append("[" + parameterTypes.charAt(x) + "]");
				if(queryPieces.length > x+1){
					sb.append(queryPieces[x+1]);
				}

				//Parameter is a string
				if (paramType == 's'){
					String param = (String) parameters.get(x);
					param = StringEscapeUtils.escapeHtml4(param);
					ps.setString(x+1, param);
				}
				//Parameter is an integer
				else if (paramType == 'i'){
					int param = (int) parameters.get(x);
					ps.setInt(x+1, param);
				}
				//Parameter is a double
				else if (paramType == 'd'){
					double param = (double) parameters.get(x);
					ps.setDouble(x+1, param);
				}
				//P
				//Parameter is a boolean
				else if (paramType == 'b'){
					boolean param = (boolean) parameters.get(x);
					ps.setBoolean(x+1, param);
				}
			}

			sb.append(executeAddOn);
			int id = 0;

			// If an update, return the number of rows changed
			if(executeType.toLowerCase().trim().equals("update")){
				id = ps.executeUpdate();
				sb.append("\nThe update has altered (" + id + ") row(s).");
			}	
			else if(executeType.toLowerCase().trim().equals("delete")){
				id =  ps.executeUpdate();
				sb.append("\nThe delete has altered (" + id + ") row(s).");
			}
			else if(executeType.trim().toLowerCase().equals("insert")){
				//Obtain the id of the insert/updated row if applicable
				id = -1;
				rs = ps.executeQuery();
				if (rs.next()){
					id = rs.getInt(1);
				}
				sb.append("\nThe insert created number (" + id + ") in the table.");
			}
			else{
				id =  ps.executeUpdate();
				sb.append("\nThe delete has altered (" + id + ") row(s).");
			}

			//Close the ResultSet and the PreparedStatement
			if(rs != null){
				rs.close();
			}
			ps.close();

			Log.detailPrint(LOG_UNIT, 
					hideValue(hideValue(hideValue(sb.toString(), "\"Email\""), "\"Password\""), "\"Token\""), 
					gameID);
			return id;
		}catch(Exception e){
			Log.detailPrint(LOG_UNIT, 
					hideValue(hideValue(hideValue(sb.toString(), "\"Email\""), "\"Password\""), "\"Token\""), 
					gameID);
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			Log.statePrint(LOG_UNIT, "Exception occurred: \n" + errors.toString(), gameID);

			
			return -1;
		}
	}

	/**
	 * This method should be called in place of a SQL SELECT queries.
	 * It will handle the basic database classes needed and will also handle escaping strings to prevent XSS.
	 * 
	 * @param query - a normal SQL query
	 * @param parameters - ArrayList of Objects (can really be integers, strings etc)
	 * @param parameterTypes - a string that matches the types of the parameters in the ArrayList
	 * @param gameID - the game ID related to the message, -1 if game ID does not matter
	 * @return - ArrayList<Map<String,Object> containing the results of the query - the keys will be returned in all lower case
	 */
	public static ArrayList<Map<String,Object>> executeQuery(String query, ArrayList<Object> parameters, String parameterTypes, int gameID){
		LOG_UNIT = Log.getNonUtilCaller("Database");
		StringBuilder sb = new StringBuilder();
		try{
			//Check to make sure we have the same number of parameters as we have types
			if (parameters.size() != parameterTypes.length()){
				throw new Exception("The number of parameters doesn't match the number of parameter types!");
			}

			//Convert the parameter types string to lower case so we don't have to deal with checking both cases
			parameterTypes = parameterTypes.toLowerCase();

			//Close the prepared statement and result set just in case
			if (ps != null){
				ps.close();
			}

			if (rs != null){
				rs.close();
			}


			//Create the prepared statement
			ps = db.prepareStatement(query);

			// For the detail print
			String[] queryPieces = query.split("\\?");
			sb.append("Query: ").append(queryPieces[0]);

			for (int x = 0; x < parameters.size(); x++){
				char paramType = parameterTypes.charAt(x);

				// Append the value and its type and the next part of the query
				sb.append(parameters.get(x).toString()).append("[" + parameterTypes.charAt(x) + "]");
				if(queryPieces.length > x+1){
					sb.append(queryPieces[x+1]);
				}

				//Parameter is a string
				if (paramType == 's'){
					String param = (String) parameters.get(x);
					param = StringEscapeUtils.escapeHtml4(param);
					ps.setString(x+1, param);
				}
				//Parameter is a integer
				else if (paramType == 'i'){
					int param = (int) parameters.get(x);
					ps.setInt(x+1, param);
				}
				//Parameter is a boolean
				else if (paramType == 'b'){
					boolean param = (boolean) parameters.get(x);
					ps.setBoolean(x+1, param);
				}
			}

			// Execute the query
			rs = ps.executeQuery();

			// Necessary variables to return the query
			ResultSetMetaData returnKeys = rs.getMetaData();
			ArrayList<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
			Map<String, Object> row = null;

			// Loop through each row returned
			int count = 0;
			while (rs.next()){
				count++;
				// Initialize a new Hashmap object
				row = new HashMap<String, Object>();
				Object currObject = null;
				// For each field to be returned
				for(int i = 0; i<returnKeys.getColumnCount(); i++){
					currObject = rs.getObject(i+1);
					row.put(returnKeys.getColumnName(i+1).trim().toLowerCase(), currObject);
				}
				results.add(row);

			}

			// No rows were returned
			if(count == 0){
				throw new Exception("There were no rows returned from the query.");
			}

			//Close the prepared statement and result set
			ps.close();
			rs.close();

			Log.detailPrint(LOG_UNIT, sb.toString()
					.replaceAll("\"Password\"=[^\\[]*", "\"Pasword\"=****")
					.replaceAll("\"Email\"=[^\\[]*", "\"Email\"=****")
					.replaceAll("\"Token\"=[^\\[]*", "\"Token\"=****")
					.replaceAll("\\},", "\\},\n"), gameID);
			Log.detailPrint(LOG_UNIT, results.toString()
					.replaceAll("password=[^,}]*", "password=****")
					.replaceAll("email=[^,}]*", "email=****")
					.replaceAll("token=[^,}]*", "token=****")
					.replaceAll("\\},", "\\},\n"), gameID);					

			return results;
		}catch(Exception e){
			try{
				Log.detailPrint(LOG_UNIT, sb.toString(), gameID);
				Log.statePrint(LOG_UNIT, "Exception occurred: " + e.toString(), gameID);

				// Necessary variables to return a blank set
				ResultSetMetaData returnKeys = rs.getMetaData();
				ArrayList<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
				Map<String, Object> row = new HashMap<String, Object>();

				// Create a blank row to return
				for(int i=0; i<returnKeys.getColumnCount(); i++){
					row.put(returnKeys.getColumnName(i+1).trim().toLowerCase(), null);
				}
				results.add(row);

				//Close the prepared statement and result set
				ps.close();
				rs.close();

				Log.detailPrint(LOG_UNIT, results.toString()
						.replaceAll("password=[^,}]*", "password=****")
						.replaceAll("email=[^,}]*", "email=****"), gameID);
				return results;
			}catch(Exception collException){
				Log.statePrint(LOG_UNIT, "Error trying to get column names.", gameID);
				ArrayList<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
				Map<String, Object> row = new HashMap<String, Object>();

				row.put("null", true);
				results.add(row);
				return results;
			}
		}
	}
	
	private static String hideValue(String s, String hiddenString){
		if(s.toUpperCase().contains("INSERT") && s.contains(hiddenString)){
			int firstParPos = s.indexOf("(");
			String tempKeys = s.substring(firstParPos+1, s.indexOf(")"));

			int hidePos = tempKeys.indexOf(hiddenString);
			int secondParPos = s.indexOf("(", firstParPos+1);
			int commaCount = 0;

			// Determine the index of the value to be replaced
			for(int i = 0; i<hidePos; i++){
				if(tempKeys.charAt(i) == ','){
					commaCount++;
				}
			}

			// Step through string looking for value to replace
			int replaceStart = secondParPos;
			for(int i = commaCount; i>0; i--){
				replaceStart = s.indexOf(',', replaceStart)+1;
			}

			String startString = s.substring(0, replaceStart);
			String endString = s.substring(replaceStart);

			endString = endString.replaceFirst("[^\\[]*", "****");
			s = startString + endString;
		}
		return s;
	}
}
