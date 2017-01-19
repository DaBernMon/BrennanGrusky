/*
 * Generated by the Jasper component of Apache Tomcat
 * Version: Apache Tomcat/8.0.24
 * Generated at: 2016-09-01 21:47:38 UTC
 * Note: The last modified time of this file was set to
 *       the last modified time of the source file after
 *       generation to assist with modification tracking.
 */
package org.apache.jsp.nfl;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.ArrayList;
import java.util.Map;
import bg.nfl.utilities.Player;
import bg.nfl.utilities.StatFields;

public final class display_005fstats_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent,
                 org.apache.jasper.runtime.JspSourceImports {

  private static final javax.servlet.jsp.JspFactory _jspxFactory =
          javax.servlet.jsp.JspFactory.getDefaultFactory();

  private static java.util.Map<java.lang.String,java.lang.Long> _jspx_dependants;

  private static final java.util.Set<java.lang.String> _jspx_imports_packages;

  private static final java.util.Set<java.lang.String> _jspx_imports_classes;

  static {
    _jspx_imports_packages = new java.util.HashSet<>();
    _jspx_imports_packages.add("javax.servlet");
    _jspx_imports_packages.add("javax.servlet.http");
    _jspx_imports_packages.add("javax.servlet.jsp");
    _jspx_imports_classes = new java.util.HashSet<>();
    _jspx_imports_classes.add("bg.nfl.utilities.StatFields");
    _jspx_imports_classes.add("java.util.Map");
    _jspx_imports_classes.add("bg.nfl.utilities.Player");
    _jspx_imports_classes.add("java.util.ArrayList");
  }

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.tomcat.InstanceManager _jsp_instancemanager;

  public java.util.Map<java.lang.String,java.lang.Long> getDependants() {
    return _jspx_dependants;
  }

  public java.util.Set<java.lang.String> getPackageImports() {
    return _jspx_imports_packages;
  }

  public java.util.Set<java.lang.String> getClassImports() {
    return _jspx_imports_classes;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_instancemanager = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(getServletConfig());
  }

  public void _jspDestroy() {
  }

  public void _jspService(final javax.servlet.http.HttpServletRequest request, final javax.servlet.http.HttpServletResponse response)
        throws java.io.IOException, javax.servlet.ServletException {

final java.lang.String _jspx_method = request.getMethod();
if (!"GET".equals(_jspx_method) && !"POST".equals(_jspx_method) && !"HEAD".equals(_jspx_method) && !javax.servlet.DispatcherType.ERROR.equals(request.getDispatcherType())) {
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "JSPs only permit GET POST or HEAD");
return;
}

    final javax.servlet.jsp.PageContext pageContext;
    javax.servlet.http.HttpSession session = null;
    final javax.servlet.ServletContext application;
    final javax.servlet.ServletConfig config;
    javax.servlet.jsp.JspWriter out = null;
    final java.lang.Object page = this;
    javax.servlet.jsp.JspWriter _jspx_out = null;
    javax.servlet.jsp.PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("<html>\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      org.apache.jasper.runtime.JspRuntimeLibrary.include(request, response, "/header.jsp", out, false);
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("<body align=\"left\">\n");
      out.write("\t<div class=\"container\">\n");
      out.write("\t\t<div class=\"well\">The table will be displayed here.</div>\n");
      out.write("\t</div>\n");
      out.write("\t<div class=\"main-content\">\n");
      out.write("\t\t<div class=\"container\">\n");
      out.write("\t\t\t");

				@SuppressWarnings("unchecked")
				ArrayList<Map<String, Object>> dataMap = (ArrayList<Map<String, Object>>) request.getAttribute("dataMap");
			
      out.write("\n");
      out.write("\t\t\t<table id=\"player-stats\">\n");
      out.write("\t\t\t\t<thead>\n");
      out.write("\t\t\t\t\t<tr>\n");
      out.write("\t\t\t\t\t\t<th>Player</th>\n");
      out.write("\t\t\t\t\t\t<th>Fantasy Points</th>\n");
      out.write("\t\t\t\t\t\t");

							String[] cleanKey;
							for(String key: dataMap.get(0).keySet()){
								if(key.equals("name") || key.equals("ff-pts")){
									continue;
								}
								cleanKey = StatFields.cleanValue(key);
								if(cleanKey[1].equals("N/A")){
									
      out.write("\n");
      out.write("\t\t\t\t\t\t\t\t\t<th>");
      out.print( cleanKey[0]);
      out.write("</th>\n");
      out.write("\t\t\t\t\t\t\t\t\t");

								}
								else{
									
      out.write("\n");
      out.write("\t\t\t\t\t\t\t\t\t<th>");
      out.print( cleanKey[0] + cleanKey[1]);
      out.write("</th>\n");
      out.write("\t\t\t\t\t\t\t\t\t");

								}
							}
						
      out.write("\n");
      out.write("\t\t\t\t\t</tr>\n");
      out.write("\t\t\t\t</thead>\n");
      out.write("\t\t\t\t\n");
      out.write("\t\t\t\t<tbody>\n");
      out.write("\t\t\t\t\t");

						for(Map<String,Object> row : dataMap){
							
      out.write("\n");
      out.write("\t\t\t\t\t\t\t<tr>\n");
      out.write("\t\t\t\t\t\t\t\t<td>");
      out.print(row.get("name") );
      out.write("</td>\n");
      out.write("\t\t\t\t\t\t\t\t<td>");
      out.print(String.format("%.2f", row.get("ff-pts")) );
      out.write("</td>\n");
      out.write("\t\t\t\t\t\t\t\t");

									Object keyVal;
									for(String key: dataMap.get(0).keySet()){
										if(key.equals("name") || key.equals("ff-pts")){
											continue;
										}
										keyVal = row.get(key);
										
      out.write("\n");
      out.write("\t\t\t\t\t\t\t\t\t\t<td>");
      out.print((keyVal == null)? "N/A": keyVal );
      out.write("</td>\n");
      out.write("\t\t\t\t\t\t\t\t\t\t");

									} 
								
      out.write("\n");
      out.write("\t\t\t\t\t\t\t</tr>\n");
      out.write("\t\t\t\t\t\t\t");

						}
						
      out.write("\n");
      out.write("\t\t\t\t</tbody>\n");
      out.write("\t\t\t</table>\n");
      out.write("\t\t</div>\n");
      out.write("\t</div>\n");
      out.write("\n");
      out.write("\t<script src=\"/resources/js/nfl/display_stats.js\"></script>\n");
      out.write("\t");
      org.apache.jasper.runtime.JspRuntimeLibrary.include(request, response, "/footer.jsp", out, false);
      out.write("\n");
      out.write("\n");
      out.write("</body>\n");
      out.write("</html>");
    } catch (java.lang.Throwable t) {
      if (!(t instanceof javax.servlet.jsp.SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try {
            if (response.isCommitted()) {
              out.flush();
            } else {
              out.clearBuffer();
            }
          } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}