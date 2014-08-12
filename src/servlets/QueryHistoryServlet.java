package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import webUI.CSS;
import webUI.TemplageGroup;

@SuppressWarnings("serial")
public class QueryHistoryServlet extends BaseServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		prepareResponse("Login", response);

		// variables
		StringTemplateGroup templates = TemplageGroup.templates;

		// output
		PrintWriter out = response.getWriter();
		StringTemplate pageST = templates.getInstanceOf("page");
		StringTemplate bodyST = templates.getInstanceOf("queryHistoryBody");

		// Generate body

		String delete = request.getParameter("delete");
		if ((delete != null) && (BaseServlet.getUsername(request) != null)) {
			db.removeUserQueries(BaseServlet.getUsername(request));
			System.out.println("Reached");
		}

		if (BaseServlet.getUsername(request) != null) {
			try {
				ArrayList<String> queries = db.getQueries(BaseServlet
						.getUsername(request));
				for (String query : queries) {
					bodyST.setAttribute("query", query);
				}
			} catch (SQLException e) {
				log.error(e);
			}
		}

		// Return
		pageST.setAttribute("user", BaseServlet.getUsername(request));
		pageST.setAttribute("altLayout", CSS.altLayout);
		pageST.setAttribute("body", bodyST);
		out.println(pageST.toString());
		finishResponse(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		// Header Stuff
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		response.setStatus(HttpServletResponse.SC_OK);
		try {
			response.sendRedirect(request.getServletPath());
		} catch (IOException e) {
			log.error(e);
		}
	}
}
