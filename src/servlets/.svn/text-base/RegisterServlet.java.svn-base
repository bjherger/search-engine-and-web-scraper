package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import database.Status;
import webUI.CSS;
import webUI.TemplageGroup;
import webUI.StringUtilities;

@SuppressWarnings("serial")
public class RegisterServlet extends BaseServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		prepareResponse("Register New User", response);

		// variables
		String error = request.getParameter("error");
		int code = 0;
		StringTemplateGroup templates = TemplageGroup.templates;

		// output
		PrintWriter out = response.getWriter();
		StringTemplate pageST = templates.getInstanceOf("page");
		StringTemplate bodyST = templates.getInstanceOf("registerBody");

		if (error != null) {
			try {
				code = Integer.parseInt(error);
			} catch (Exception ex) {
				code = -1;
			}

			String errorMessage = StringUtilities.getStatus(code).message();
			bodyST.setAttribute("errorCode", errorMessage);
		}

		pageST.setAttribute("user", BaseServlet.getUsername(request));
		pageST.setAttribute("altLayout", CSS.altLayout);
		pageST.setAttribute("body", bodyST);
		out.println(pageST.toString());
		finishResponse(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		prepareResponse("Register New User", response);

		String newuser = request.getParameter("user");
		String newpass = request.getParameter("pass");
		Status status = db.registerUser(newuser, newpass);
		if (status == Status.OK) {
			response.sendRedirect(response
					.encodeRedirectURL("/login?newuser=true"));
		} else {
			String url = "/register?error=" + status.name();
			response.sendRedirect(response.encodeRedirectURL(url));
		}
	}

}
