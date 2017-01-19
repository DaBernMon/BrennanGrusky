package bg.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bg.log.Log;

public class Projects extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String LOG_UNIT = "Projects";

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		// If the user is not logged, go to the project page with guest view
		String userName = (String) request.getSession().getAttribute("user");
		Log.detailPrint(LOG_UNIT, "User " + userName + " is viewing Projects", -1);
		
		if(userName == null){
			request.setAttribute("accessLevel", 0);
			request.getRequestDispatcher("projects.jsp").forward(request, response);
			return;
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

	}

}
