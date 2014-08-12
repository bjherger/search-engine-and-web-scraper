package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import webUI.CSS;
import webUI.StringUtilities;
import webUI.TemplageGroup;
import database.Status;

@SuppressWarnings("serial")
public class ChangePasswordServlet extends BaseServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		prepareResponse("Change Password", response);

		// variables
		String error = request.getParameter("error");
		int code = 0;
		StringTemplateGroup templates = TemplageGroup.templates;

		// output
		PrintWriter out = response.getWriter();
		StringTemplate pageST = templates.getInstanceOf("page");
		StringTemplate bodyST = templates.getInstanceOf("passwordBody");

		if (error != null) {
			try {
				code = Integer.parseInt(error);
			} catch (Exception ex) {
				code = -1;
			}

			String errorMessage = StringUtilities.getStatus(code).message();
			bodyST.setAttribute("errorCode", errorMessage);
			// out.println("<p style=\"color: red;\">" + errorMessage + "</p>");
		}

		pageST.setAttribute("user", BaseServlet.getUsername(request));
		pageST.setAttribute("altLayout", CSS.altLayout);
		pageST.setAttribute("body", bodyST);
		out.println(pageST.toString());
		finishResponse(request, response);
		// printForm(out);
		// finishResponse(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		prepareResponse("Update Password", response);

		String user = request.getParameter("user");
		String oldPass = request.getParameter("pass");
		String newPass = request.getParameter("newpass");
		Status status = db.changePassword(user, oldPass, newPass);
		if (status == Status.OK) {
			response.sendRedirect(response
					.encodeRedirectURL("/login?changePass=true"));
		} else {
			String url = "/password?error=" + status.name();
			response.sendRedirect(response.encodeRedirectURL(url));
		}
	}

}
