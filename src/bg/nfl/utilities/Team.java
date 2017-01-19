package bg.nfl.utilities;

import java.util.HashMap;

public class Team {

	private static HashMap<String, String> fullNameMapping = new HashMap<String, String>();
	private static HashMap<String, String> abbreviationMapping = new HashMap<String, String>();
	
	public String name;

	static{
		// Map an abbreviation to a name
		fullNameMapping.put("Ari", "Arizona Cardinals");
		fullNameMapping.put("Atl", "Atlanta Falcons");
		fullNameMapping.put("Bal", "Baltimore Ravens");
		fullNameMapping.put("Buf", "Buffalo Bills");
		fullNameMapping.put("Car", "Carolina Panthers");
		fullNameMapping.put("Chi", "Chicago Bears");
		fullNameMapping.put("Cin", "Cincinnati Bengals");
		fullNameMapping.put("Cle", "Cleveland Browns");
		fullNameMapping.put("Dal", "Dallas Cowboys");
		fullNameMapping.put("Den", "Denver Broncos");
		fullNameMapping.put("Det", "Detroit Lions");
		fullNameMapping.put("GB", "Green Bay Packers");
		fullNameMapping.put("Hou", "Houston Texans");
		fullNameMapping.put("Ind", "Indianapolis Colts");
		fullNameMapping.put("Jax", "Jacksonville Jaguars");
		fullNameMapping.put("KC", "Kansas City Chiefs");
		fullNameMapping.put("LA", "Los Angeles Rams");
		fullNameMapping.put("Mia", "Miami Dolphins");
		fullNameMapping.put("Min", "Minnesota Vikings");
		fullNameMapping.put("NE", "New England Patriots");
		fullNameMapping.put("NO", "New Orleans Saints");
		fullNameMapping.put("NYG", "New York Giants");
		fullNameMapping.put("NYJ", "New York Jets");
		fullNameMapping.put("Oak", "Oakland Raiders");
		fullNameMapping.put("Phi", "Philadelphia Eagles");
		fullNameMapping.put("Pit", "Pittsburgh Steelers");
		fullNameMapping.put("SD", "San Diago Chargers");
		fullNameMapping.put("SF", "San Francisco 49ers");
		fullNameMapping.put("Sea", "Seattle Seahawks");
		fullNameMapping.put("TB", "Tampa Bay Buccaneers");
		fullNameMapping.put("Ten", "Tennessee Titans");
		fullNameMapping.put("Was", "Washington Redskins");
		fullNameMapping.put("Stl", "Los Angeles Rams");
		
		// Map a full team name to an abbreviation
		abbreviationMapping.put("Arizona Cardinals ", "Ari");
		abbreviationMapping.put("Atlanta Falcons", "Atl");
		abbreviationMapping.put("Baltimore Ravens", "Bal");
		abbreviationMapping.put("Buffalo Bills", "Buf");
		abbreviationMapping.put("Carolina Panthers", "Car");
		abbreviationMapping.put("Chicago Bears", "Chi");
		abbreviationMapping.put("Cincinnati Bengals", "Cin");
		abbreviationMapping.put("Cleveland Browns", "Cle");
		abbreviationMapping.put("Dallas Cowboys", "Dal");
		abbreviationMapping.put("Denver Broncos", "Den");
		abbreviationMapping.put("Detroit Lions", "Det");
		abbreviationMapping.put("Green Bay Packers", "GB");
		abbreviationMapping.put("Houston Texans", "Hou");
		abbreviationMapping.put("Indianapolis Colts", "Ind");
		abbreviationMapping.put("Jacksonville Jaguars", "Jax");
		abbreviationMapping.put("Kansas City Chiefs", "KC");
		abbreviationMapping.put("Los Angeles Rams", "LA");
		abbreviationMapping.put("Miami Dolphins", "Mia");
		abbreviationMapping.put("Minnesota Vikings", "Min");
		abbreviationMapping.put("New England Patriots", "NE");
		abbreviationMapping.put("New Orleans Saints", "NO");
		abbreviationMapping.put("New York Giants", "NYG");
		abbreviationMapping.put("New York Jets", "NYJ");
		abbreviationMapping.put("Oakland Raiders", "Oak");
		abbreviationMapping.put("Philadelphia Eagles", "Phi");
		abbreviationMapping.put("Pittsburgh Steelers", "Pit");
		abbreviationMapping.put("San Diago Chargers", "SD");
		abbreviationMapping.put("San Francisco 49ers", "SF");
		abbreviationMapping.put("Seattle Seahawks", "Sea");
		abbreviationMapping.put("Tampa Bay Buccaneers", "TB");
		abbreviationMapping.put("Tennessee Titans", "Ten");
		abbreviationMapping.put("Washington Redskins", "Was");

	}

	/*
	 * Static methods to get team mapping between abbreviation to full name
	 */
	public static String getFullName(String abbreviation){
		return fullNameMapping.get(abbreviation);
	}
	
	public static String getAbbreviation(String fullName){
		return abbreviationMapping.get(fullName);
	}
	
	/*
	 * Constructor
	 */
	public Team(String name){
		this.name = name;
	}
}
