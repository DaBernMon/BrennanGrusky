package bg.nfl.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bg.log.Log;
import bg.nfl.utilities.Player;
import bg.nfl.utilities.StatFields;
import bg.nfl.utilities.Stat;

public class DisplayStats extends HttpServlet{
	
	public static String LOG_UNIT = "DisplayStats";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String userName = (String) request.getSession().getAttribute("user");
		if(userName == null){
			request.setAttribute("accessLevel", 0);
		}
		
		Log.statePrint(LOG_UNIT, userName + " is setting up an NFL table request.", -1);
		ArrayList<StatFields> fields = Player.getPlayerStatFields();
		
		request.setAttribute("fields", fields);
		request.getRequestDispatcher("setup.jsp").forward(request, response);
		return;
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{		
		Enumeration<String> attributeNames = request.getParameterNames();
		ArrayList<String> displayColumns = new ArrayList<String>();
		ArrayList<String> scoreColumns = new ArrayList<String>();
		ArrayList<String> scoreValues = new ArrayList<String>();
		
		while(attributeNames.hasMoreElements()){
			String s = attributeNames.nextElement();
			if(s.contains("submit")){
				continue;
			}
			else if(s.contains("score-")){
				scoreColumns.add(s);
				scoreValues.add(request.getParameter(s));
			}
			else{
				displayColumns.add(s);
			}
		}
		Log.detailPrint(LOG_UNIT, "Display Columns: " + displayColumns, -1);
		Log.detailPrint(LOG_UNIT, "Score Columns:   " + scoreColumns, -1);
		Log.detailPrint(LOG_UNIT, "Score Columns:   " + scoreValues, -1);
		
		String year = request.getParameter("year-input");
		
		ArrayList<Map<String, Object>> dataMap = Stat.getStats(displayColumns, scoreColumns, scoreValues, year);
		
		request.setAttribute("dataMap", dataMap);
		request.getRequestDispatcher("display_stats.jsp").forward(request, response);
		return;
	}
}
