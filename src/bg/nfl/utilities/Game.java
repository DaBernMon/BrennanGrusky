package bg.nfl.utilities;

import java.util.HashMap;

public class Game {
	public int id;
	
	public HashMap<String, Player> players = null;
	
	public int week;
	public int year;
	public String team1;
	public String team2;
	
	public Game(int gameId){
		id = gameId;
		players = new HashMap<String, Player>();
	}
	
	// TODO
	public void setWeek(String date){
		week = 0;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(year).append(",week=").append(week).append(",").append(team1).append("vs").append(team2);
		if(players != null){
			sb.append(":").append(players.toString());
		}
		sb.append("\n");
		return sb.toString();
	}
}
