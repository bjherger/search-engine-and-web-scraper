package servlets;

import indexAndSearch.InvertedIndex;
import indexAndSearch.WebCrawler;
import indexAndSearch.WorkCounter;
import indexAndSearch.WorkQueue;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import webUI.CSS;
import webUI.TemplageGroup;

@SuppressWarnings("serial")
public class PreferencesServlet extends BaseServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		prepareResponse("Login", response);

		// variables
		StringTemplateGroup templates = TemplageGroup.templates;

		// output
		PrintWriter out = response.getWriter();
		StringTemplate pageST = templates.getInstanceOf("page");
		StringTemplate bodyST = templates.getInstanceOf("preferencesBody");

		// change layout
		String altLayout = request.getParameter("altLayout");
		if ((altLayout != null)) {
			CSS.altLayout = !CSS.altLayout;
		}

		// if adding
		String add = request.getParameter("add");
		if ((add != null)) {
			bodyST.setAttribute("add", true);
		}
		String addFailed = request.getParameter("addFailed");
		if ((addFailed != null)) {
			bodyST.setAttribute("addFailed", true);
		}

		// add website
		if (db.getAdmin(BaseServlet.getUsername(request))) {
			bodyST.setAttribute("admin", true);
		}

		// printForm(out);
		pageST.setAttribute("user", BaseServlet.getUsername(request));
		pageST.setAttribute("altLayout", CSS.altLayout);
		pageST.setAttribute("body", bodyST);
		out.println(pageST.toString());
		finishResponse(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			String newSite = request.getParameter("newSite");
			Path sitePath = Paths.get(newSite);
			URL test = new URL(newSite);
			test.getProtocol();
			InvertedIndex toAdd = new InvertedIndex();
			WebCrawler trav = new WebCrawler(new WorkQueue(),
					new WorkCounter(), toAdd);
			trav.traverseDirectory(sitePath);
			HomeServlet.addToIndex(toAdd);
			response.sendRedirect(response
					.encodeRedirectURL("/preferences?add=true"));
		} catch (Exception e) {
			log.error(e);
		}
		try {
			response.sendRedirect(response
					.encodeRedirectURL("/preferences?addFailed=true"));
		} catch (IOException e) {
			log.error(e);
		}

	}
}
