/*
 * Generated by the Jasper component of Apache Tomcat
 * Version: Apache Tomcat/8.0.24
 * Generated at: 2016-03-30 02:57:31 UTC
 * Note: The last modified time of this file was set to
 *       the last modified time of the source file after
 *       generation to assist with modification tracking.
 */
package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class aboutme_jsp extends org.apache.jasper.runtime.HttpJspBase
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
    _jspx_imports_classes = null;
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
      org.apache.jasper.runtime.JspRuntimeLibrary.include(request, response, "header.jsp", out, false);
      out.write("\n");
      out.write("\n");
      out.write("<body align=\"center\">\n");
      out.write("<div class=\"container\">  \n");
      out.write("\t<div class=\"row\">\n");
      out.write("\t\t<div class=\"col-xs-2\"><img class=\"img-responsive img-rounded\" src=\"/resources/img/theB.jpg\" /></div>\n");
      out.write("\t\t<div class=\"col-xs-4\">\n");
      out.write("\t\t\t<div class=\"dropdown\">\n");
      out.write("\t\t\t\t<div class=\"btn-group-vertical\" aria-expanded=\"true\">\t\t\n");
      out.write("\t\t\t\t\t<div class=\"btn-group btn-group-justified\">\n");
      out.write("\t\t\t\t\t\t<button class=\"btn btn-success\" type=\"button\" id=\"education\">Education</button>\n");
      out.write("\t\t\t\t\t</div>\n");
      out.write("\t\t\t\t\t\n");
      out.write("\t\t\t\t\t<div class=\"btn-group btn-group-justified\">\n");
      out.write("\t\t\t\t\t\t<button class=\"btn btn-info\" type=\"button\" id=\"work-history\">Work History</button>\n");
      out.write("\t\t\t\t\t</div>\n");
      out.write("\t\t\t\t\t\n");
      out.write("\t\t\t\t\t<div class=\"btn-group btn-group-justified\">\n");
      out.write("\t\t\t\t\t\t<button class=\"btn btn-warning\" type=\"button\" id=\"cs-langs\">Programming Language Skills</button>\n");
      out.write("\t\t\t\t\t</div>\n");
      out.write("\t\t\t\t\t\n");
      out.write("\t\t\t\t\t<div class=\"btn-group btn-group-justified\">\n");
      out.write("\t\t\t\t\t\t<button class=\"btn btn-danger\" type=\"button\" id=\"achievements\">Achievements</button>\n");
      out.write("\t\t\t\t\t</div>\n");
      out.write("\t\t\t\t</div>\n");
      out.write("\t\t\t</div>\n");
      out.write("\t\t</div>\n");
      out.write("\t\t<div class=\"col-xs-4\">\n");
      out.write("\t\t\t<div class=\"btn-success\" id=\"education-info\">\n");
      out.write("\t\t\t    <p align=\"left\"><strong>University of North Carolina at Chapel Hill</strong></p>\n");
      out.write("\t\t\t    <p align=\"left\">Class of 2014</p>\n");
      out.write("\t\t\t    <p align=\"left\">Major: Applied Sciences Computer Engineering</p>\n");
      out.write("\t\t\t    <p align=\"left\">Minor: Chemistry</p>\n");
      out.write("\t\t\t</div>\n");
      out.write("\t\t\t<div class=\"btn-info\" id=\"work-history-info\" style=\"display: none;\">\n");
      out.write("\t\t\t    <p align=\"left\"><strong>IBM</strong></p>\n");
      out.write("\t\t\t    <p align=\"left\">Software Engineer</p>\n");
      out.write("\t\t\t    <p align=\"left\">August 2014 - Present</p>\n");
      out.write("\t\t\t</div>\n");
      out.write("\t\t\t<div class=\"btn-warning\" id=\"cs-langs-info\" style=\"display: none;\">\n");
      out.write("\t\t\t    <p align=\"left\">Java</p>\n");
      out.write("\t\t\t    <p align=\"left\">C/C++</p>\n");
      out.write("\t\t\t    <p align=\"left\">Python</p>\n");
      out.write("\t\t\t    <p align=\"left\">Matlab</p>\n");
      out.write("\t\t\t    <p align=\"left\">HTML/CSS</p>\n");
      out.write("\t\t\t    <p align=\"left\">JavaScript</p>\n");
      out.write("\t\t\t    <p align=\"left\">Assembler</p>\n");
      out.write("\t\t\t    <p align=\"left\">SQL</p>\n");
      out.write("\t\t\t</div>\n");
      out.write("\t\t\t<div class=\"btn-danger\" id=\"achievements-info\" style=\"display: none;\">\n");
      out.write("\t\t\t    <p align=\"left\">Eagle Scout</p>\n");
      out.write("\t\t\t</div>\n");
      out.write("\t\t</div>\n");
      out.write("\t\t<div class=\"col-xs-2\">\n");
      out.write("\t\t\t<div>\n");
      out.write("\t\t\t\t<div id=\"education-pic\" style=\"display: none;\"><img class=\"img-responsive img-rounded\" src=\"/resources/img/unc-old-well.jpg\" /></div>\n");
      out.write("\t\t\t\t<div id=\"work-history-pic\" style=\"display: none;\"><img class=\"img-responsive img-rounded\" src=\"/resources/img/ibm-logo.png\" /></div>\n");
      out.write("\t\t\t\t<div id=\"cs-langs-pic\" style=\"display: none;\"><img class=\"img-responsive img-rounded\" src=\"/resources/img/cs-langs.jpg\" /></div>\n");
      out.write("\t\t\t\t<div id=\"achievements-pic\" style=\"display: none;\"><img class=\"img-responsive img-rounded\" src=\"/resources/img/eagle.jpg\" /></div>\n");
      out.write("\t\t\t</div>\n");
      out.write("\t\t</div>\n");
      out.write("\t</div>\n");
      out.write("</div>\n");
      out.write("\n");
      out.write("<script src=\"/resources/js/aboutme.js\"></script>\n");
      org.apache.jasper.runtime.JspRuntimeLibrary.include(request, response, "footer.jsp", out, false);
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