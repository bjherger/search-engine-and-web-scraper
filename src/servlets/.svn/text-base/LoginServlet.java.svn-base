package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import webUI.CSS;
import webUI.StringUtilities;
import webUI.TemplageGroup;
import database.Status;

@SuppressWarnings("serial")
public class LoginServlet extends BaseServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		prepareResponse("Login", response);

		// variables
		String error = request.getParameter("error");
		int code = 0;
		StringTemplateGroup templates = TemplageGroup.templates;

		// output
		PrintWriter out = response.getWriter();
		StringTemplate pageST = templates.getInstanceOf("page");
		StringTemplate bodyST = templates.getInstanceOf("loginBody");

		if (error != null) {
			try {
				code = Integer.parseInt(error);
			} catch (Exception ex) {
				code = -1;
			}

			String errorMessage = StringUtilities.getStatus(code).message();
			bodyST.setAttribute("errorCode", errorMessage);
		}

		if (request.getParameter("newuser") != null) {
			bodyST.setAttribute("newUser", true);
		}

		if (request.getParameter("changePass") != null) {
			bodyST.setAttribute("changePass", true);
		}

		if (request.getParameter("logout") != null) {
			bodyST.setAttribute("logout", true);
			clearCookies(request, response);
			response.sendRedirect("/");
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
		String user = request.getParameter("user");
		String pass = request.getParameter("pass");

		Status status = db.authenticateUser(user, pass);

		try {
			if (status == Status.OK) {
				// should eventually change this to something more secure
				response.addCookie(new Cookie("login", "true"));
				response.addCookie(new Cookie("name", user));
				response.sendRedirect(response.encodeRedirectURL("/"));
			} else {
				response.addCookie(new Cookie("login", "false"));
				response.addCookie(new Cookie("name", ""));
				response.sendRedirect(response
						.encodeRedirectURL("/login?error=" + status.ordinal()));
			}
		} catch (Exception ex) {
			log.error("Unable to process login form.", ex);
		}
	}

}
