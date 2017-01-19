package bg.nfl.utilities;

public class StatFields{
	public String value;
	public String fieldType;

	public StatFields(String value, String fieldType){
		this.value = value;
		this.fieldType = fieldType;
	}

	/**
	 * Returns a cleaned value of the column name that should look nice when printed
	 * @param value
	 * @return
	 */
	public static String[] cleanValue(String value){
		String cleanedValue = value;

		// Clean the attribute type
		cleanedValue = cleanedValue
				.replaceAll("^kr-", "Kick Returns: ,")
				.replaceAll("^pr-", "Punt Returns: ,")
				.replaceAll("^rec-", "Receiving: ,")
				.replaceAll("^rush-", "Rushing: ,")
				.replaceAll("^def-", "Defense: ,")
				.replaceAll("^off-", "Offense: ,")
				.replaceAll("^pass-", "Passing: ,")
				.replaceAll("^year", "Year")
				.replaceAll("^team", "Team")
				.replaceAll("^k-", "Kicking: ,")
				.replaceAll("^K-", "Kicking: ,")
				.replaceAll("^p-", "Punting: ,")
				.replaceAll("^p-", "Punting: ,")
				.replaceAll("^k-", "Kicking: ,")
				.replaceAll("^K-", "Kicking: ,")
				.replaceAll("^xpt-", "Extra Point: ,")
				.replaceAll("^Rec-", "Receiving: ,")
				.replaceAll("^Rush-", "Rushing: ,")
				.replaceAll("^KR-", "Kick Returns: ,")
				.replaceAll("^PR-", "Punt Returns: ,")
				.replaceAll("^Def-", "Defense: ,")
				.replaceAll("^Pass-", "Passing: ,")

				// Clean the attribute
				.replaceAll(",lg", 		",Long")
				.replaceAll("^gms", 	"Games Played")
				.replaceAll(",rec$", 	",Receptions")
				.replaceAll(",ydsl$", 	",Yards Lost")
				.replaceAll(",yds$", 	",Yards")
				.replaceAll(",avg$", 	",Average")
				.replaceAll(",tar$", 	",Targets")
				.replaceAll(",att$", 	",Attempts")
				.replaceAll(",num$", 	",Number")
				.replaceAll(",ast$", 	",Assisted")
				.replaceAll(",To$t", 	",Total")
				.replaceAll(",tot$", 	",Total")
				.replaceAll(",td$", 	",Touchdowns")
				.replaceAll(",cmp$", 	",Completions")
				.replaceAll(",pct$", 	", %")
				.replaceAll(",Int$", 	",Interceptions")
				.replaceAll(",int$", 	",Interceptions")
				.replaceAll(",itfb$", 	",Int Fumbles")
				.replaceAll(",ittd$", 	",Int TDs")
				.replaceAll(",ityd$", 	",Int Return Yards")
				.replaceAll(",tb$", 	",Touchbacks")
				.replaceAll(",fd$", 	",First Downs")
				.replaceAll(",ssck$", 	",Solo Sacks")
				.replaceAll(",gsck$", 	",Assisted Sacks")
				.replaceAll(",stkl$", 	",Solo Tackles")
				.replaceAll(",gtkl$", 	",Assisted Tackles")
				.replaceAll(",syl$", 	",Sack Yards Lost")
				.replaceAll(",ff$", 	",Forced Fumbles")
				.replaceAll(",fmb$", 	",Fumbles")
				.replaceAll(",drp$", 	",Drops")
				.replaceAll(",spk$", 	",Spikes")
				.replaceAll(",scbl$", 	",Scrambles")
				.replaceAll(",sack$", 	",Sacks")
				.replaceAll(",knee$", 	",Kneels")
				.replaceAll(",fbrc$", 	",Fumble Recs")
				.replaceAll(",punt$", 	",Punts")
				.replaceAll(",psdf$", 	",Passes Defensed")
				.replaceAll(",sfty$",	",Safeties")
				.replaceAll(",qbht$", 	",QB Hits")
				.replaceAll(",cat$", 	",Catches")
				.replaceAll(",fbls$", 	",Fumble Lost")
				.replaceAll(",scr$", 	",Score")
				.replaceAll(",kick$", 	",Kickoffs")
				.replaceAll(",fc$", 	",Fair Catch")
				.replaceAll(",fryd$", 	",Fumble Return Yards")
				.replaceAll(",frtd$", 	",TD from Fumble Rec")
				.replaceAll(",2pt$", ",2 pt converions")
				// Uppercase
				.replaceAll("^Gms", "Games Played")
				.replaceAll(",Rec$", ",Receptions")
				.replaceAll(",YdsL$", ",Yards Lost")
				.replaceAll(",Yds$", ",Yards")
				.replaceAll(",Avg$", ",Average")
				.replaceAll(",Tar$", ",Targets")
				.replaceAll(",Att$", ",Attempts")
				.replaceAll(",Lg$", ",Long")
				.replaceAll(",Num$", ",Number")
				.replaceAll(",Ast$", ",Assisted")
				.replaceAll(",Cmp$", ",Completions")
				.replaceAll(",FD$", ",First Downs")
				.replaceAll(",Sack$", ",Sacks")
				.replaceAll(",Pct$", ", %");

		String[] splitCleanedValue = cleanedValue.split(",");
		String[] returnedValue = new String[2];
		returnedValue[0] = splitCleanedValue[0];
		if(splitCleanedValue.length < 2){
			returnedValue[1] = "N/A";
		}
		else{
			returnedValue[1] = splitCleanedValue[1];
		}


		return returnedValue;
	}
}
