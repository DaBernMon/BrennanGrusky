package bg.nfl.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bg.log.Log;

public class Setup extends HttpServlet{
	
	public static String LOG_UNIT = "Setup";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String userName = (String) request.getSession().getAttribute("user");
		if(userName == null){
			request.setAttribute("accessLevel", 0);
		}
		
		Log.infoPrint(LOG_UNIT, userName + " is setting up an NFL table request.", -1);

		request.getRequestDispatcher("setup.jsp").forward(request, response);
		return;
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response){
		
	}
}
