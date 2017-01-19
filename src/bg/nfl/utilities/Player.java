package bg.nfl.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import bg.database.Database;

public class Player {

	public String fullName;
	public int number;
	public String position;
	
	// Detail info
	public String firstName;
	public String lastName;
	public Team currentTeam;
	public String college;
	
	// Stat info
	public HashMap<String, Stat> stats;
	
	// Static player data
	public static ArrayList<StatFields> statFields = null;

	/**
	 * Gets all of the column headers for the player stats table
	 * @return
	 */
	public static ArrayList<StatFields> getPlayerStatFields(){
		if(statFields == null){
			String query = "SELECT column_name,data_type FROM information_schema.columns WHERE table_name=?;";
			ArrayList<Object> parameters = new ArrayList<Object>(Arrays.asList("NFL-Stats"));
			String parameterTypes = "s";
			ArrayList<Map<String, Object>> result = Database.executeQuery(query, parameters, parameterTypes, -1);

			statFields = new ArrayList<StatFields>();
			for(Map<String, Object> row : result){
				if(row.get("data_type") == null || row.get("column_name") == null || row.get("null") != null){
					statFields = null;
					return statFields;
				}
				if(((String)row.get("column_name")).equals("ID")){
					continue;
				}
				statFields.add(new StatFields((String)row.get("column_name"), (String)row.get("data_type")));						
			}
		}

		return statFields;
	}
	
	public Player(String name, String team){
		this.fullName = name;
		this.currentTeam = new Team(team);
		stats = new HashMap<String, Stat>();
	}
	
	@Override
	public String toString(){
		if(stats == null){
			return fullName;
		}
		return fullName+":"+stats.toString() + "\n\t";
	}
}