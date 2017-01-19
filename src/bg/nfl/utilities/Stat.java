package bg.nfl.utilities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import bg.database.Database;
import bg.log.Log;

public class Stat {
	private String field;
	private int value;
	
	private static String LOG_UNIT = "Stat";
	
	/**
	 * Gets all of the requested stats
	 * @return
	 */
	public static ArrayList<Map<String, Object>> getStats(ArrayList<String> displayCols, ArrayList<String> scoreCols, 
			ArrayList<String> scoreVals, String year){
		String queryA = "SELECT \"Name\",";
		StringBuilder queryB = new StringBuilder();
		String queryC = " FROM \"NFL-Stats\" WHERE \"Year\"=? ";
		ArrayList<Object> parameters = new ArrayList<Object>(Arrays.asList(Integer.parseInt(year)));
		String parameterTypes = "i";
		
		for(int i = 0; i<displayCols.size(); i++){
			String display = displayCols.get(i);
			queryB.append("\"")
			.append(display)
			.append("\"");
			if(i != displayCols.size()-1){
				queryB.append(",");
			}			
		}
		
		ArrayList<Map<String, Object>> returnMap = Database.executeQuery(queryA+queryB.toString().replace("year-input", "Year")+queryC, parameters, parameterTypes, -1);
		HashMap<String, Double> scoreValMapIndex = new HashMap<String, Double>();
		String scoreValue = null;
		double pointsPerAttrib = 0;
		Map<String, Object> map = null;
		
		for(int i = 0; i<scoreCols.size(); i++){
			scoreValue = scoreVals.get(i);
			try{
				pointsPerAttrib = Double.parseDouble(scoreValue);
			}
			catch(Exception e){
				continue;
			}
			if(pointsPerAttrib == 0){
				continue;
			}
			
			scoreValMapIndex.put(scoreCols.get(i).replace("score-", ""), pointsPerAttrib);
		}
		Log.detailPrint(LOG_UNIT, scoreValMapIndex.toString(), -1);
		Log.detailPrint(LOG_UNIT, displayCols.toString(), -1);
		
		double points = 0;
		Object mapVal = 0;
		BigDecimal mapBigDecimal = null;
		for(int i = 0; i<returnMap.size(); i++){
			map = returnMap.get(i);
			points = 0;

			if(map == null || map.get("null") != null){
				continue;
			}
			for(String key : scoreValMapIndex.keySet()){
				mapVal = map.get(key.toLowerCase());
				if(mapVal == null){
					continue;
				}
				mapBigDecimal = (BigDecimal)mapVal;
				points += mapBigDecimal.doubleValue() * scoreValMapIndex.get(key);
			}
			map.put("ff-pts", points);
		}
		Log.detailPrint(LOG_UNIT, returnMap.toString(), -1);
		
		return returnMap;
	}
	
	public Stat(String field){
		this.field = field;
		this.value = 0;
	}
	
	public String getField(){
		return field;
	}
	public int getValue(){
		return value;
	}
	public int incrementValue(int increment){
		value += increment;
		return value;
	}
	
	@Override
	public String toString(){
		return "" + value;
	}
	
}
