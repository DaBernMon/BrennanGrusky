package bg.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import bg.log.Log;
import bg.nfl.utilities.Game;
import bg.nfl.utilities.Player;
import bg.nfl.utilities.Stat;

public class PlayByPlayReader{

	private static String LOG_UNIT = "PlayByPlayReader";

	// Necessary constants
	private static final String currentYear = "15";
	private static final int GAME_ID	   	= 0;
	private static final int DATE			= 1;
	private static final int OFFENSE_TEAM	= 5;
	private static final int DEFENSE_TEAM	= 6;
	private static final int DOWN			= 7;
	private static final int TO_GO			= 8;
	private static final int YARD_LINE	 	= 9;
	private static final int DESCRIPTION 	= 14;

	private static String defense = null;
	private static String offense = null;
	private static Game currentGame = null;
	private static boolean playIs2Pt = false;

	private static PlayByPlayReader pbpr = new PlayByPlayReader();

	public PlayByPlayReader(){
		Log.infoPrint(LOG_UNIT, "Loading PlayByPlayReader", -1);

		// For information about the runtime
		long startTime = System.nanoTime();
		long endTime = -1;
		int fileCount = 0;

		// For manipulating the CSV files
		String path = "../../../pbp-2015.csv";
		InputStream folder = getClass().getResourceAsStream(path);
		BufferedReader reader = new BufferedReader(new InputStreamReader(folder));

		// Variables needed relating to a specific line of the file
		int lineCount = 0;
		String line = null;
		String[] quotedRegions = null;
		StringBuffer sb = null;
		String[] lineVals = null;
		ArrayList<String> headers = null;

		// Variables needed for game data
		String gameIDstr = null;
		int gameId = -1;
		String description = null;
		String shortDes = null;
		int desPos = -1;
		int numDigits = -1;				// Number of digits for jersey number
		int jerseyNumber = -1;
		int spaceAfterNameLoc = -1;
		String playerName = null;
		Player currPlayer = null;
		String passDefensedStr = null;

		// For storing the game data before putting it in a database
		HashMap<Integer, Game> gameList = new HashMap<Integer, Game>();	

		// Loop through each CSV file
		//for(File file:listOfFiles){
		// DEBUG: Only do 2015 for now
		//			if(!file.getName().contains("2015")){
		//				continue;
		//			}
		//			fileCount++;
		//
		Log.infoPrint(LOG_UNIT, "Analyzing file: " + path, -1);
		//			gameList = new HashMap<Integer, Game>();
		//
		//			// Try to read the file as a buffered reader
		//			try {
		//				reader = new BufferedReader(new FileReader(path + "/" + file.getName()));
		//			} catch (FileNotFoundException e) {
		//				e.printStackTrace();
		//				continue;
		//			}

		// Try to read through each line of the file
		try {
			lineCount = 0;
			while((line = reader.readLine()) != null){
				lineCount++;

				/*
				 *  If it is the first line, it is the headers line
				 */
				if(lineCount == 1){
					headers = new ArrayList<String>(Arrays.asList(line.split(",")));
					continue;
				}

				/*
				 *  First remove all quoted commas
				 */
				sb = new StringBuffer();
				quotedRegions = line.split("\"");
				for(int i = 0; i<quotedRegions.length; i++){
					sb.append((i % 2 == 0)? quotedRegions[i] : quotedRegions[i].replaceAll(",", "@!@"));
				}

				/*
				 *  Now separate the values and process them
				 */
				lineVals = sb.toString().split(",");

				// Game ID
				gameIDstr = lineVals[GAME_ID];
				if(gameIDstr == null){
					continue;
				}
				try{
					gameId = Integer.parseInt(gameIDstr);
				}
				catch(Exception nfe){
					nfe.printStackTrace();
					continue;
				}
				currentGame = gameList.get(gameId);

				// Ensure that game exists - if not, create it
				if(currentGame == null){
					currentGame = new Game(gameId);
					currentGame.setWeek(lineVals[DATE]);
					currentGame.year = Integer.parseInt(lineVals[DATE].split("-")[0]);
					gameList.put(gameId, currentGame);
				}

				// Get the teams
				offense = lineVals[OFFENSE_TEAM];
				defense = lineVals[DEFENSE_TEAM];

				// Ensure both team names are populated
				if(currentGame.team1 == null || currentGame.team2 == null || currentGame.team1 == currentGame.team2){
					currentGame.team1 = offense;
					currentGame.team2 = defense;
				}

				/*************************/
				/* Parse the description */
				/*************************/
				playIs2Pt = false;
				description = lineVals[DESCRIPTION].replaceAll("@!@", ",");
				description = description.replaceFirst("^.*AND THE PLAY WAS REVERSED. ", "");
				//
				//					if(lineCount > 43000 && lineCount < 43099){
				//						System.out.println(description);
				//					}

				// Known Name complications
				description = description.replaceAll(" JR.", "");
				description = description.replaceAll("SHORTS III", "SHORTS");
				description = description.replaceAll("17 C.KEENUM", "17-C.KEENUM");
				description = description.replaceAll("LATERAL PASS FROM.*", "");
				description = description.replaceAll("\\(3:01\\) \\(SHOTGUN\\) 2-J.MANZIEL PASS DEEP LEFT TO 11-T.BENJAMIN FOR 50 YARDS, TOUCHDOWN. CLE # 74 REPORTS AS AN ELIGIBLE RECEIVER. QB SCRAMBLE ON THE PLAY",
						"(3:01) (SHOTGUN) 2-J.MANZIEL PASS DEEP LEFT TO 11-T.BENJAMIN FOR 50 YARDS, TOUCHDOWN.");
				description = description.replaceAll("\\(11:10\\) 38-A.ELLINGTON UP THE MIDDLE FOR 1 YARD, TOUCHDOWN. ARI #53 REPORTED AS ELIGIBLE. THE REPLAY OFFICIAL REVIEWED THE RUNNER BROKE THE PLANE RULING, AND THE PLAY WAS UPHELD. THE RULING ON THE FIELD STANDS.",
						"(11:10) 38-A.ELLINGTON UP THE MIDDLE FOR 1 YARD, TOUCHDOWN.");



				// Move past flea flicker
				description = description.replaceAll("^.*FLEA-FLICKER.*\\)", "");

				// Replace "PASS BACK" with Lateral.. same thing?
				description = description.replaceAll("PASS BACK", "LATERAL");

				// Move past changes to QB
				description = description.replaceAll("^.*IN AT QB. ", "");
				description = description.replaceAll("^.*NOW AT QB. ", "");
				description = description.replaceAll("^.*\\{.*NEW.*QB.*\\} ", "");
				description = description.replaceAll("^.*NEW CIN QB MCCARRON ", "");
				description = description.replaceAll("^.*NEW QB #8 MOORE, NAT. ", "");
				description = description.replaceAll("^.*NEW PIT QB L. JONES. ", "");
				description = description.replaceAll("^.*NEW QB #3 - MANUEL, EJ", ""); 
				description = description.replaceAll("^.*CLAUSEN IN AT QB ", "");
				description = description.replaceAll("^.*68. M SLAUSEN IN AT CENTER ", "");
				description = description.replaceAll("^.*\\(11:15\\) M.CASSEL AT QB \\(T.TAYLOR AT WR\\)." ,"");

				// Ignore 'direction change'
				description = description.replaceAll("^.*DIRECTION CHANGE. ", "");

				// Ignore extra plays
				description = description.replaceAll("^.*ELECTS TO HAVE ONE UNTIMED PLAY. ", "");

				// Ignore yardline change
				description = description.replaceAll("\\[YARD LINE CHANGES WITH CHANGE OF POSSESSION\\] ", "");
				description = description.replaceAll("^.*WITH CHANGE OF POSSESSION. ", "");
				description = description.replaceAll("^.*ON CHANGE OF POSSESSION. ", "");
				description = description.replaceAll("^.*YARD LINE AFTER THE TURNOVER ON DOWNS.  ", "");

				// Ignore nearly intercepted teaser
				description = description.replaceAll("NEARLY INTERCEPTED", "");

				// Ignore records
				description = description.replaceAll("SACKS FOR SEASON", "");
				//					if(!description.contains("F.ZOMBO")){ // TODO remove
				//						continue;
				//					}
				//					System.out.println(description);

				if(description.indexOf("-") == -1 || description.contains("NO PLAY") || description.contains("TWO-MINUTE WARNING") ||
						description.contains("END OF QUARTER") || description.contains("END OF HALF") || description.contains("END OF GAME") || description.contains("END GAME") ||
						description.contains("DELAY OF GAME") || description.contains("OFFSIDE") || description.contains("TIMEOUT #") ||
						((description.contains("NEUTRAL ZONE INFRACTION") || description.contains("OFFENSIVE HOLDING")) && description.contains("ENFORCED")) ||
						description.contains("12 ON-FIELD") || description.contains("FALSE START")){
					continue;	// No player was listed
				}

				desPos = 0;

				// Move past players reporting in as eligible - JAMES HURST REPORTS ELIGIBLE 23232532 WAYS
				while(description.indexOf("ELIGIBLE. ", desPos) != -1){
					desPos = description.indexOf("ELIGIBLE. ",desPos)+10;
				}	
				while(description.indexOf("ELIGIBLE RECEIVER. ", desPos) != -1){
					desPos = description.indexOf("ELIGIBLE RECEIVER. ", desPos)+19;
				}
				while(description.indexOf("REPORTS ELIGIBLE ", desPos) != -1){
					desPos = description.indexOf("REPORTS ELIGIBLE ", desPos)+17;
				}
				while(description.indexOf("REPORTS AS ELIGIBLE ", desPos) != -1){
					desPos = description.indexOf("REPORTS AS ELIGIBLE ", desPos)+20;
				}


				// Move past time and "No huddle" or "Shotgun" notes
				int indexOfFirst = description.indexOf("(", desPos);
				int indexOfSecond = description.indexOf("-", desPos);
				while(indexOfFirst < indexOfSecond && indexOfFirst != -1 && indexOfSecond != -1){
					desPos = description.indexOf(")",desPos)+1;
					indexOfFirst = description.indexOf("(", desPos);
					indexOfSecond = description.indexOf("-", desPos);
				}

				// Ignore "DIRECT SNAP"
				description = (description.indexOf("DIRECT SNAP TO") < desPos)?
						description : description.replaceAll("DIRECT SNAP TO .*\\.\\s ", "");
				shortDes = description.substring(desPos);

				// Move past any whitespace at the front of the line
				while(description.indexOf(" ", desPos) == desPos){
					shortDes = shortDes.replaceFirst(" ", "");
					desPos++;
				}

				// Capture that it is a 2 pt play
				if(shortDes.contains("TWO-POINT CONVERSION ATTEMPT. ")){
					playIs2Pt = true;
					description = description.replaceAll("TWO-POINT CONVERSION ATTEMPT. ", "");
					shortDes = shortDes.replaceAll("TWO-POINT CONVERSION ATTEMPT. ", "");
				}

				// Get the (first) player number
				numDigits = shortDes.indexOf("-");
				if(numDigits > 2 || numDigits < 0){
					//Error here, what should I do?
					Log.infoPrint(LOG_UNIT, "Unexpected format: " + description + "\n" + shortDes, -1);
					continue;
				}
				try{
					jerseyNumber = Integer.parseInt(shortDes.substring(0,numDigits));
				}
				catch(Exception e){
					Log.infoPrint(LOG_UNIT, "NFE Unexpected format: " + description + "\n" + shortDes, -1);
					continue;
				}

				// Get the (first) player name
				desPos += numDigits+1;	// Also get past '-'
				shortDes = description.substring(desPos);
				spaceAfterNameLoc = shortDes.indexOf(" ");
				if(spaceAfterNameLoc < 0){
					Log.infoPrint(LOG_UNIT, "No space after dash: " + description + "\n" + shortDes, -1);
					continue;
				}
				playerName = shortDes.substring(0, spaceAfterNameLoc);
				desPos += spaceAfterNameLoc+1;
				shortDes = description.substring(desPos);

				// Check if the player exists in the game currently, if not add them to the game
				currPlayer  = currentGame.players.get(jerseyNumber + "-" + offense);
				if(currPlayer == null){
					currPlayer = new Player(playerName, lineVals[OFFENSE_TEAM]);
					currPlayer.number = jerseyNumber;
					currentGame.players.put(jerseyNumber + "-" + lineVals[OFFENSE_TEAM], currPlayer);
				}

				// Determine if there was a QB hit, and remove it
				if(shortDes.contains("[")){
					setStat(currPlayer, "pass-qbht");	// This QB was hit

					try {
						Player[] qbHitrs = getDefensivePlayer(description, shortDes.replace("]", ")").substring(shortDes.indexOf("[")+1));
						for(Player qbHit:qbHitrs){
							setStat(qbHit, "def-qbht");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					description = description.replaceFirst("\\[.*\\]" , "");
					shortDes = shortDes.replaceFirst("\\[.*\\]" , "");
				}

				// TODO Stop ignoring Field goals
				if(shortDes.contains("FIELD GOAL")){
					continue;
				}

				/*
				 *  Determine what that player did, to decide what to do next
				 */
				// For passing plays
				if(shortDes.substring(0,5).contains("PASS") && !shortDes.contains("ATTEMPT FAILS")){
					// Was a pass attempt
					setStat(currPlayer, "pass-att");
					desPos += 5;	// Move past 'PASS '
					shortDes = description.substring(desPos);

					/*
					 * Passing
					 */
					// Check if the pass was completed
					if(shortDes.substring(0,11).contains("INCOMPLETE")){
						// Done for the QB

						// Maybe defender defensed the pass?
						if(shortDes.contains("(")){
							if(shortDes.contains(" TO ")){
								passDefensedStr = shortDes.replaceFirst("^.* TO [0-9][^\\s]* \\(", "");
							}
							else{
								passDefensedStr = shortDes.replaceFirst("^.*\\(", "");
							}
							try {
								for(Player p : getDefensivePlayer(shortDes, passDefensedStr)){
									setStat(p, "def-psdf");
								}

							} catch (Exception e) {
								Log.infoPrint(LOG_UNIT, "Problem getting defensive player: " + description + "\n\t" + shortDes, -1);
								e.printStackTrace();
							}
						}

					}
					else if(shortDes.contains("INTERCEPTED")){
						// Add the interception
						setStat(currPlayer, "pass-int");

						passDefensedStr = shortDes.replaceFirst("^.*INTERCEPTED BY ", "");
						try {
							Player p = getDefensivePlayer(shortDes, passDefensedStr)[0];
							setStat(p, "def-psdf");

							if(processIntFailed(passDefensedStr, p)){
								Log.infoPrint(LOG_UNIT, "Processing the interception failed: " + description + "\n\t" + passDefensedStr, -1);
								continue;
							}

						} catch (Exception e) {
							Log.infoPrint(LOG_UNIT, "Problem getting intercepting player: " + description + "\n\t" + shortDes, -1);
							e.printStackTrace();
						}
					}
					else if(playIs2Pt && shortDes.contains("ATTEMPT SUCCEEDS")){
						setStat(currPlayer, "pass-2pt");
					}
					// The pass must have been completed then
					else{
						// Add the completion
						setStat(currPlayer, "pass-cmp");

						// How may yards were gained?
						if(updateYardsFailed(shortDes, currPlayer, "pass-yds")){
							continue;
						}

						// Maybe the receiver fumbled
						if(shortDes.contains("FUMBLE")){
							// Ignore, handled later
						}
						// If not, and a TD was scored, must have been a passing TD
						else{
							if(shortDes.contains("TOUCHDOWN")){
								// Add the touchdown
								setStat(currPlayer, "pass-td");
							}
						}
					}

					/*
					 * Receiving
					 */
					if(processRecFailed(shortDes, lineVals[OFFENSE_TEAM], currentGame)){
						continue;
					}

				}	// End Pass
				else if(shortDes.substring(0,6).contains("KICKS")){
					swapTeams();
					setStat(currPlayer, "k-kick");

					int endKickSent = shortDes.indexOf(". ");
					if(shortDes.contains("ONSIDE") || shortDes.contains("RECOVERED BY")){
						// TODO Nothing for now
					}
					else if(shortDes.contains("TOUCHBACK")){
						setStat(currPlayer, "k-tb");
					}
					else if(endKickSent != -1){
						endKickSent += 2;
						shortDes = shortDes.substring(endKickSent);

						Player p = getPlayer(shortDes);
						setStat(p, "kr-cat");

						updateYardsFailed(shortDes, p, "kr-yds");

						if(!shortDes.contains("FUMBLE") && shortDes.contains("TOUCHDOWN")){
							setStat(p, "kr-td");
						}

					}
				}	// End Kicks
				else if(shortDes.substring(0,6).contains("PUNTS")){
					swapTeams();
					setStat(currPlayer, "p-punt");

					int fairCatchLoc = shortDes.indexOf("FAIR CATCH BY ");
					int perLoc = shortDes.indexOf(". ");
					if(shortDes.contains("TOUCHBACK")){
						setStat(currPlayer, "p-tb");
					}
					else if(fairCatchLoc != -1){
						fairCatchLoc += 14;
						shortDes = shortDes.substring(fairCatchLoc);

						Player p = getPlayer(shortDes);
						setStat(p, "pr-fc");							
					}
					else if(shortDes.indexOf("OUT OF BOUNDS") != -1 && shortDes.indexOf("OUT OF BOUNDS") < shortDes.indexOf(". ")){
						// OOB Kick, do nothing TODO
					}
					else if(shortDes.contains("DOWNED BY")){
						// Punting team downed it							
					}
					else if(shortDes.contains("PUNT WAS DEFLECTED")){
						// Do nothing for now TODO
					}
					else if(perLoc != -1){
						perLoc += 2;
						shortDes = shortDes.substring(perLoc);

						Player p = getPlayer(shortDes);
						setStat(p, "pr-cat");

						updateYardsFailed(shortDes, p, "pr-yds");

						if(!shortDes.contains("FUMBLE") && shortDes.contains("TOUCHDOWN")){
							setStat(p, "pr-td");
						}
					}
				}	// End PUNTS
				else if(shortDes.substring(0,7).contains("SPIKED")){
					// Was a pass attempt
					setStat(currPlayer, "pass-att");						

					// Was a spike
					setStat(currPlayer, "pass-spk");
				}	// End Spike
				else if(shortDes.substring(0,7).contains("SACKED")){
					// Was a sack
					setStat(currPlayer, "pass-sack");

					// Lost some yards for it
					if(updateYardsFailed(shortDes, currPlayer, "pass-syl")){
						continue;
					}
				} 	// End Sack
				else if(shortDes.substring(0,7).contains("KNEELS")){
					// Was a knee
					setStat(currPlayer, "pass-knee");

					if(processRushFailed(shortDes, currPlayer)){
						continue;
					}
				}	// End Kneels
				else if(shortDes.substring(0,10).contains("SCRAMBLES")){
					// Was a scramble
					setStat(currPlayer, "pass-scbl");

					if(processRushFailed(shortDes, currPlayer)){
						continue;
					}
				}	// End Scrambles
				else if(shortDes.substring(0, 14).contains("UP THE MIDDLE")){
					if(processRushFailed(shortDes, currPlayer)){
						continue;
					}
				}	// End rush up the middle
				else if(shortDes.substring(0, 11).contains("LEFT GUARD")){
					if(processRushFailed(shortDes, currPlayer)){
						continue;
					}
				}	// End Rush off left guard
				else if(shortDes.substring(0, 12).contains("RIGHT GUARD")){
					if(processRushFailed(shortDes, currPlayer)){
						continue;
					}
				}	// End rush off right guard
				else if(shortDes.substring(0, 12).contains("LEFT TACKLE")){
					if(processRushFailed(shortDes, currPlayer)){
						continue;
					}
				}	// End rush off left tackle
				else if(shortDes.substring(0, 13).contains("RIGHT TACKLE")){
					if(processRushFailed(shortDes, currPlayer)){
						continue;
					}
				}	// End rush off right tackle
				else if(shortDes.substring(0, 9).contains("LEFT END")){
					if(processRushFailed(shortDes, currPlayer)){
						continue;
					}
				}	// End rush off left end
				else if(shortDes.substring(0, 10).contains("RIGHT END")){
					if(processRushFailed(shortDes, currPlayer)){
						continue;
					}
				}	// End rush off right end
				else{
					//System.out.println("No category found: " + description + "\n\t" + shortDes);
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		updateDatabase(gameList);
		Log.detailPrint(LOG_UNIT, "The headers for this file are:\n" + headers.toString(), -1);
		//Log.infoPrint(LOG_UNIT, "Game list: " + gameList.toString(), -1);
		Log.detailPrint(LOG_UNIT, "The file had " + lineCount + " lines in it.", -1);


		endTime = System.nanoTime();
		Log.infoPrint(LOG_UNIT, "Read through " + fileCount + " files  and " + lineCount + 
				" rows in " + ((double)(endTime-startTime)/1000000000.0) + " seconds.", -1);
	}


	private Stat setStat(Player p, String field){
		Stat s = p.stats.get(field);
		if(s == null){
			s = new Stat(field);
			p.stats.put(field, s);
		}
		s.incrementValue(1);

		return s;
	}

	/**
	 * Returns true if an exception occurred
	 * @param str
	 * @param p
	 * @param typePrefix
	 * @return
	 */
	private boolean updateYardsFailed(final String str, Player p, String field){
		if(!str.contains("FOR ")){
			//	System.out.println(field + " No for in yard gained: " + str);
			return false;
		}

		String yardsStr = str.substring(str.indexOf("FOR ")+4);
		int yardsGained =-1;
		try{
			if((yardsStr.indexOf("YARDS") > yardsStr.indexOf("NO GAIN") || yardsStr.indexOf("YARDS") == -1 ) &&
					yardsStr.indexOf("NO GAIN") != -1){
				yardsGained = 0;
				yardsStr = yardsStr.substring(yardsStr.indexOf("NO GAIN ")+9);  // Move past to what should be the '(' of the tackling player
			}
			else{
				try{
					yardsGained = Integer.parseInt(yardsStr.substring(0,yardsStr.indexOf(" ")));
					yardsStr = yardsGained == 1? yardsStr.substring(yardsStr.indexOf(" YARD")+5) : yardsStr.substring(yardsStr.indexOf(" YARDS")+6); 	

					// Move past to what should be the '(' of the tackling player
					yardsStr = yardsStr.replaceFirst("^\\S* ", "");

					// If it was a lateral then the player wasn't tackled
					if(yardsStr.contains("LATERAL TO ")){
						String handleLateral = yardsStr;
						do{
							handleLateral = handleLateral.substring(handleLateral.indexOf("LATERAL TO ")+11);
							Player latPlayer = getPlayer(handleLateral);
							Stat s = setStat(p, field);
							s.incrementValue(yardsGained-1);	// Because getStat adds one
							if(processRushFailed(handleLateral, latPlayer)){
								return true;
							}
						} while(handleLateral.contains("LATERAL TO "));
						return false;
					}
					else if(yardsStr.contains("LATERAL PASS ")){
						String handleLateral = yardsStr;
						do{
							handleLateral = handleLateral.substring(handleLateral.indexOf("LATERAL PASS ")+13);
							Player latPlayer = getPlayer(handleLateral);
							Stat s = setStat(p, field);
							s.incrementValue(yardsGained-1);	// Because getStat adds one
							if(processRushFailed(handleLateral, latPlayer)){
								return true;
							}
						} while(handleLateral.contains("LATERAL PASS "));
						return false;
					}
					else if(yardsStr.contains("SAFETY")){
						String handleSafety = yardsStr.substring(yardsStr.indexOf("SAFETY")+6);
						if(handleSafety.contains("(")){
							handleSafety = handleSafety.substring(2);
							swapTeams();
							Player safetyPlayer = getPlayer(handleSafety);
							swapTeams();
							setStat(safetyPlayer, "def-sfty");
						}
					}
					else if(yardsStr.contains("NO TACKLE")){
						Stat s = setStat(p, field);
						s.incrementValue(yardsGained-1);	// Because getStat adds one
						return false;
					}
				}
				catch(Exception e){
					Log.infoPrint(LOG_UNIT, "NFE " + field + " Unexpected format: " + str, -1);
					e.printStackTrace();
					return true;
				}
			}
		}
		catch(StringIndexOutOfBoundsException e){
			Log.infoPrint(LOG_UNIT, "String OOB: " + str, -1);
			return true;
		}

		// Determine who tackled the player
		if(!field.contains("pass-") && !str.contains("TOUCHDOWN") && yardsStr.length() > 1 && yardsStr.substring(0, 1).matches("\\d")){
			try { 
				yardsStr = yardsStr.substring(1);
				Player[] plrs = getDefensivePlayer(str, yardsStr);
				for(Player defP : plrs){
					if(plrs.length == 1){
						setStat(defP, "def-stkl");
					}
					else{
						setStat(defP, "def-gtkl");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(field.contains("pass-syl")){
			try { 
				yardsStr = yardsStr.substring(1);
				Player[] plrs = getDefensivePlayer(str, yardsStr);
				for(Player defP : plrs){
					Stat defSyl = setStat(defP, "def-syl");
					defSyl.incrementValue(yardsGained-1);
					if(plrs.length == 1){
						setStat(defP, "def-ssck");
					}
					else{
						setStat(defP, "def-gsck");
					}					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Stat s = setStat(p, field);
		s.incrementValue(yardsGained-1);	// Because getStat adds one

		// Now handle fumbles that may have occurred during the play
		String fumbleString = yardsStr;
		int fumbleLoc = fumbleString.indexOf("FUMBLES ");
		if(fumbleLoc != -1){
			fumbleString = fumbleString.substring(fumbleLoc+9);

			// Who forced it?
			Player ffPlayer;
			try {
				ffPlayer = getDefensivePlayer(str, fumbleString)[0];
				setStat(ffPlayer, "def-ff");

				int recByLoc = fumbleString.indexOf(" RECOVERED BY ");
				if(recByLoc != -1){
					recByLoc += 14;	// Move past " RECOVERED BY "

					Player recPlayer = null;
					if(fumbleString.substring(recByLoc, recByLoc+offense.length()).equals(offense)){
						recByLoc += offense.length()+1; // Move past CLE-  (Offense recovered their own fumble)
						swapTeams();
						recPlayer = getDefensivePlayer(str, fumbleString.substring(recByLoc))[0];
						swapTeams();
						proccessFumbleRecovery(fumbleString.substring(recByLoc), recPlayer, true);
					}
					else if (fumbleString.substring(recByLoc, recByLoc+defense.length()).equals(defense)){
						setStat(p, "off-fbls");
						recByLoc += defense.length()+1; // Move past PIT- (Defense recovered the fumble)
						recPlayer = getDefensivePlayer(str, fumbleString.substring(recByLoc))[0];
						proccessFumbleRecovery(fumbleString.substring(recByLoc), recPlayer, false);
					}
					else if(fumbleString.contains("RECOVERS")){	// Fumbler recovered
						return false;
					}
					else{
						System.out.println("Off:" + fumbleString.indexOf(offense, recByLoc) + " Def:" + fumbleString.indexOf(defense, recByLoc));
						Log.infoPrint(LOG_UNIT, "Neither team ("+ offense + "/" + defense +") recovered?\n\t" + 
								fumbleString.substring(recByLoc) + "\n\t" + str, -1);
						return true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}			
			fumbleLoc = fumbleString.indexOf("FUMBLES ");
		}

		return false;
	}

	private boolean processRushFailed(String str, Player p){
		// Was a rush
		setStat(p, "rush-att");

		// Yards gained or lost
		if(updateYardsFailed(str, p, "rush-yds")){
			return true;
		}

		if(str.contains("FUMBLES")){
			setStat(p, "rush-fmb");
		}
		else if(str.contains("TOUCHDOWN")){
			setStat(p, "rush-td");
		}

		return false;
	}

	private boolean processRecFailed(String strIn, String team, Game g){
		String str = strIn;

		if(!str.contains("-")){
			return false;
		}

		if(str.contains("INCOMPLETE") && str.indexOf("-") > str.indexOf(".") && str.indexOf(".") != -1){
			return false;
		}

		// Ignore thrown away passes
		if(str.contains("THROWN AWAY")){
			return false;
		}

		// Get the targeted player
		String targetStr = null;

		if(str.contains("INTERCEPTED BY ")){
			if(str.contains(" INTENDED FOR ")){
				targetStr = str.substring(str.indexOf(" INTENDED FOR ")+14);
			}
			else if(str.contains(" TO ") && str.indexOf(" TO ") < str.indexOf(".")){
				targetStr = str.replaceAll("ATTEMPT FAILS.*","ATTEMPT FAILS");
			}
			else{
				return true;
			}
		}
		else{
			if(str.indexOf("TO ") == -1){
				return false;	// No target
			}
			targetStr = str.substring(str.indexOf("TO ")+3);
		}

		int numDigits = targetStr.indexOf("-");
		if(numDigits > 2 || numDigits == -1){
			Log.infoPrint(LOG_UNIT, "Invalid jersey number:" + str, -1);
			return true;
		}
		String jerseyStr = targetStr.substring(0,numDigits);
		int jerseyNumber = -1;
		String playerName = null;
		int nameEndLoc = targetStr.indexOf(" ");
		if(nameEndLoc == -1){
			nameEndLoc = targetStr.substring(numDigits+5).indexOf(".")+5;
		}
		try{
			jerseyNumber = Integer.parseInt(jerseyStr);
			playerName = targetStr.substring(numDigits+1, nameEndLoc);
		}
		catch(Exception e){
			Log.infoPrint(LOG_UNIT, "Jersey Problem: " + str + "\n\t" + targetStr, -1);
			return true;
		}

		// Check if the player exists in the game currently, if not add them to the game
		Player p = g.players.get(jerseyNumber + "-" + team);
		if(p == null){
			p = new Player(playerName, team);
			p.number = jerseyNumber;
			g.players.put(jerseyNumber + "-" + team, p);
		}

		// Add a target
		if(playIs2Pt){
			setStat(p, "xpt-tar");
			if(str.contains("ATTEMPT SUCCEEDS.")){
				setStat(p, "xpt-scr");
			}
		}
		else{
			setStat(p, "rec-tar");

			if(str.contains("INCOMPLETE ")){
				if(str.contains(" DROPPED")){
					setStat(p, "rec-drp");
				}
			}
			else if(str.substring(0,11).contains("SHORT LEFT")){
				return processCompletionFailed(str, p);
			}
			else if(str.substring(0,13).contains("SHORT MIDDLE")){
				return processCompletionFailed(str, p);
			}
			else if(str.substring(0,12).contains("SHORT RIGHT")){
				return processCompletionFailed(str, p);
			}
			else if(str.substring(0,5).contains("LEFT")){
				return processCompletionFailed(str, p);
			}
			else if(str.substring(0,7).contains("MIDDLE")){
				return processCompletionFailed(str, p);
			}
			else if(str.substring(0,6).contains("RIGHT")){
				return processCompletionFailed(str, p);
			}
			else if(str.substring(0,10).contains("DEEP LEFT")){
				return processCompletionFailed(str, p);
			}
			else if(str.substring(0,12).contains("DEEP MIDDLE")){
				return processCompletionFailed(str, p);
			}
			else if(str.substring(0,11).contains("DEEP RIGHT")){
				return processCompletionFailed(str, p);
			}
			else if(str.substring(0,3).contains("TO")){
				return processCompletionFailed(str, p);
			}
			else{
				System.out.println("Unhandled pass play: " + str);
				return true;
			}	
		}

		return false;
	}

	public boolean processCompletionFailed(String str, Player p){
		if(str.contains(" INTERCEPTED BY ")){
			return false;
		}
		setStat(p, "rec-cmp");

		if(updateYardsFailed(str, p, "rec-yds")){
			return true;
		}

		if(str.contains("FUMBLE")){
			setStat(p, "rec-fmb");
		}

		return false;
	}

	public Player[] getDefensivePlayer(String str, String replacedStr) throws Exception{
		if(!replacedStr.substring(0, 1).matches("\\d") || replacedStr.contains(" COVERAGE BY") || !replacedStr.contains("-")){
			return new Player[0];
		}
		int numDigits = -1;
		int jerseyNumber = -1;
		int endParenLoc = -1;
		String[] playerList = null;
		String playerName = null;
		Player p = null;
		String tempString;
		Player[] pArray = null;

		if(replacedStr.contains("SACK SPLIT BY")){
			replacedStr = replacedStr.substring(replacedStr.indexOf("SACK SPLIT BY ")+14);
			playerList = replacedStr.split(" AND ");
		}
		else if(replacedStr.indexOf(")") == -1 &&replacedStr.indexOf(" ") < replacedStr.indexOf(", ")){
			replacedStr = replacedStr.substring(0, replacedStr.indexOf(" "));
			playerList = new String[]{replacedStr};
		}
		else if(replacedStr.indexOf(")") != -1){
			int spaceLoc = replacedStr.indexOf(" ");
			if(spaceLoc != -1){
				int commaLoc = replacedStr.indexOf(", ");
				int semiColLoc = replacedStr.indexOf("; ");
				if(((spaceLoc < commaLoc && commaLoc != -1) ||
						(spaceLoc < semiColLoc && semiColLoc != -1)) &&
						(spaceLoc < replacedStr.indexOf(")"))){
					replacedStr = replacedStr.substring(0,replacedStr.indexOf(" "));
					playerList = new String[]{replacedStr};
				}
				else{
					replacedStr = replacedStr.substring(0, replacedStr.indexOf(")"));

					if(replacedStr.contains(";")){
						playerList = replacedStr.split("; ");
					}
					else{
						playerList = replacedStr.split(", ");
					}
				}
			}
			else{
				replacedStr = replacedStr.substring(0, replacedStr.indexOf(")"));

				if(replacedStr.contains(";")){
					playerList = replacedStr.split("; ");
				}
				else{
					playerList = replacedStr.split(", ");
				}
			}
		}
		else{
			playerList = new String[]{replacedStr};
		}
		pArray = new Player[playerList.length];

		for(int i = 0; i<playerList.length; i++){
			String s = playerList[i];
			s = s.indexOf(" ") == -1 ? s : s.substring(0, s.indexOf(" "));
			numDigits = s.indexOf("-");
			if(numDigits > 2 || numDigits < 0){
				//Error here, what should I do?
				throw new Exception("Numdigits was not a valid number: " + str + "\n\t" + replacedStr);
			}
			jerseyNumber = Integer.parseInt(s.substring(0,numDigits));

			tempString = s.substring(numDigits+1);		// Get past jersey number and '-'
			endParenLoc = tempString.indexOf(")");

			playerName = (endParenLoc == -1)? tempString: tempString.substring(0, endParenLoc);

			// Check if the player exists in the game currently, if not add them to the game
			p  = currentGame.players.get(jerseyNumber + "-" + defense);
			if(p == null){
				p = new Player(playerName, defense);
				p.number = jerseyNumber;
				currentGame.players.put(jerseyNumber + "-" + defense, p);
			}
			pArray[i] = p;
		}
		return pArray;
	}

	private boolean processIntFailed(String str, Player p){
		// Was an int
		setStat(p, "def-int");

		if(str.contains("TOUCHBACK")){
			return false;
		}

		// Yards gained or lost
		swapTeams();
		if(updateYardsFailed(str, p, "def-ityd")){
			return true;
		}
		swapTeams();

		if(str.contains("FUMBLES")){
			setStat(p, "def-itfb");
		}
		else if(str.contains("TOUCHDOWN")){
			setStat(p, "def-ittd");
		}

		return false;
	}

	public void swapTeams(){
		String temp = offense;
		offense = defense;
		defense = temp;
		return;
	}

	public void proccessFumbleRecovery(String s, Player p, boolean isOffense){
		String prefix = isOffense? "off-" : "def-";
		setStat(p, prefix + "fbrc");
		updateYardsFailed(s, p, prefix+"fryd");

		if(!s.contains("FUMBLE") && s.contains("TOUCHDOWN")){
			setStat(p, prefix+"frtd");
		}
	}

	public Player getPlayer(String input){
		int numDigits = input.indexOf("-");
		if(numDigits > 2 || numDigits < 0){
			//Error here, what should I do?
			Log.infoPrint(LOG_UNIT, "Numdigits was not a valid number: " + input, -1);
		}
		int jerseyNumber = Integer.parseInt(input.substring(0,numDigits));

		input = input.substring(numDigits+1);		// Get past jersey number and '-'
		int spaceLoc = input.indexOf(" ");
		int perLoc = spaceLoc== -1? input.substring(4).indexOf(".")+4 : spaceLoc;

		String playerName = input.substring(0, perLoc);

		// Check if the player exists in the game currently, if not add them to the game
		Player p  = currentGame.players.get(jerseyNumber + "-" + offense);
		if(p == null){
			p = new Player(playerName, offense);
			p.number = jerseyNumber;
			currentGame.players.put(jerseyNumber + "-" + offense, p);
		}

		return p;
	}

	public void updateDatabase(HashMap<Integer, Game> gameList){
		HashMap<String, Player> playerList = new HashMap<String, Player>();
		ArrayList<String> fieldNames = new ArrayList<String>();

		// Loop through each game
		for(Integer key : gameList.keySet()){
			Game g = gameList.get(key);

			// For each game, loop through each player
			for(String s : g.players.keySet()){

				// Ensure the player is in the new player list
				Player p = playerList.get(s);
				if(p == null){
					p = new Player(g.players.get(s).fullName, s.split("-")[1]);
					p.number = Integer.parseInt(s.split("-")[0]);
					playerList.put(s, p);
				}

				// Add all of his stats
				for(String f : g.players.get(s).stats.keySet()){
					if(!fieldNames.contains(f)){
						fieldNames.add(f);
					}

					// Ensure the stat exists
					Stat stat = p.stats.get(f);
					if(stat == null){
						stat = new Stat(f);
						p.stats.put(f, stat);
					}

					stat.incrementValue(g.players.get(s).stats.get(f).getValue());
				}
			}
		}

		// Add the fields to the table
		StringBuffer query = new StringBuffer();
		query.append("ALTER TABLE \"NFL-Stats\" ");

		String[] fields = fieldNames.toArray(new String[0]);
		Arrays.sort(fields);
		System.out.println(new ArrayList<String>(Arrays.asList(fields)));
		for(int i = 0; i<fields.length; i++){
			String s = fields[i];
			query.append("ADD COLUMN \"")
			.append(s)
			.append("\" NUMERIC");

			if(i<fields.length-1){
				query.append(", ");
			}			
		}

		System.out.println(query.toString());
		//Database.executeUpdate(query.toString(), new ArrayList<Object>(0), "", -1);

		/*
		 *  Add the players to the table
		 */
		// Set their fields 
		query = new StringBuffer();
		query.append("INSERT INTO \"NFL-Stats\" (\"Name\", \"Team\", \"Number\", \"Year\", ");
		ArrayList<Object> parameters = null;
		StringBuffer parameterTypes = new StringBuffer();
		parameterTypes.append("ssii");

		for(int i = 0; i<fields.length; i++){
			String s = fields[i];
			query.append("\"")
			.append(s)
			.append("\"");

			if(i<fields.length-1){
				query.append(", ");
			}	

			parameterTypes.append("i");
		}
		query.append(") VALUES (?,?,?,?,");		

		for(int i = 0; i<fields.length; i++){
			query.append("?");

			if(i<fields.length-1){
				query.append(",");
			}	
		}
		query.append(")");
		System.out.println(query.toString());

		for(String key : playerList.keySet()){
			parameters = new ArrayList<Object>();
			Player player = playerList.get(key);

			// Add the player name
			parameters.add(player.fullName);

			// Team
			parameters.add(player.currentTeam.name);

			// Number
			parameters.add(player.number);

			// Year
			parameters.add(2015);

			// All remaining stats
			for(int i = 0; i<fields.length; i++){
				Stat s = player.stats.get(fields[i]);
				if(s == null){
					s = new Stat(fields[i]);
				}

				parameters.add(s.getValue());
			}

			Database.executeUpdate(query.toString(), parameters, parameterTypes.toString(), -1);		
		}
		System.out.println(parameters.toString());
		System.out.println(parameterTypes.toString());


	}
}
