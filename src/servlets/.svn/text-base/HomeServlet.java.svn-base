package servlets;

import indexAndSearch.InvertedIndex;
import indexAndSearch.QueryMatch;
import indexAndSearch.WebsiteTools;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import webUI.CSS;
import webUI.TemplageGroup;
import webUI.StringUtilities;

@SuppressWarnings("serial")
public class HomeServlet extends BaseServlet {

	private static InvertedIndex database = null;

	public static void setIndex(InvertedIndex index) {
		HomeServlet.database = index;
	}

	public static void addToIndex(InvertedIndex index) {
		HomeServlet.database.addAll(index);
	}

	// Web handling
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			// System.setProperty("line.separator", "<br>");

			// variables
			StringTemplateGroup templates = TemplageGroup.templates;
			String queryIn = request.getQueryString();

			// System.out.println(database.formatOutput());

			// output
			PrintWriter out = response.getWriter();
			StringTemplate pageST = templates.getInstanceOf("page");
			StringTemplate bodyST = templates.getInstanceOf("homeBody");

			if (request.getRequestURI().endsWith("favicon.ico")) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			prepareResponse("Yotta: Thats 10^24 Of Knowledge", response);

			// Compute search
			if ((queryIn != null) && !queryIn.isEmpty()) {
				long startTime = System.nanoTime();
				String[] queryClean = StringUtilities
						.cleanWebQueryString(queryIn);
				String word = StringUtilities.createSaveQueryString(queryClean);
				bodyST.setAttribute("queryTerm", word);

				ArrayList<QueryMatch> matches = database
						.partialSearch(queryClean);
				System.out.println(matches.size());
				for (QueryMatch match : matches) {

					bodyST.setAttribute("match", match);
				}
				long endTime = System.nanoTime();
				double totalTime = (endTime - startTime) / (1000000000.0);
				int numResults = matches.size();
				pageST.setAttribute("time", totalTime);
				pageST.setAttribute("numResults", numResults);
			}

			// finish up
			pageST.setAttribute("user", BaseServlet.getUsername(request));
			pageST.setAttribute("altLayout", CSS.altLayout);
			pageST.setAttribute("body", bodyST);
			out.println(pageST.toString());
			finishResponse(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Header Stuff
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		// Get Search Term
		String webQuery = "";
		String saveQuery = "";
		String query = request.getParameter("query");

		// Search
		if ((query != null) && !query.isEmpty()) {
			String[] lineKeywords = WebsiteTools.cleanAndSeparateString(query);
			webQuery = StringUtilities.createWebQueryString(lineKeywords);
			saveQuery = StringUtilities.createSaveQueryString(lineKeywords);

		}

		// Add Search to history
		if (BaseServlet.getUsername(request) != null) {
			db.addQuery(BaseServlet.getUsername(request), saveQuery);
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect("/" + "?" + webQuery);
	}
}
