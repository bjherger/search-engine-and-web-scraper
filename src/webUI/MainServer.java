package webUI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import servlets.ChangePasswordServlet;
import servlets.HomeServlet;
import servlets.LoginServlet;
import servlets.PreferencesServlet;
import servlets.QueryHistoryServlet;
import servlets.RegisterServlet;

public class MainServer {
	private static final Logger log = LogManager.getLogger();
	private static int PORT = 8080;

	/**
	 * Creates a server at the port specified by PORT. Note this can be changed
	 * through setPORT().
	 * 
	 * Additionally, provides mapping to various services.
	 */
	public static void runServer() {
		Server server = new Server(PORT);
		ServletHandler handler = new ServletHandler();
		server.setHandler(handler);

		handler.addServletWithMapping(LoginServlet.class, "/login");
		handler.addServletWithMapping(RegisterServlet.class, "/register");
		handler.addServletWithMapping(ChangePasswordServlet.class, "/password");
		handler.addServletWithMapping(QueryHistoryServlet.class,
				"/queryHistory");
		handler.addServletWithMapping(PreferencesServlet.class, "/preferences");
		handler.addServletWithMapping(HomeServlet.class, "/*");

		log.info("Starting server on port " + PORT + "...");

		try {
			server.start();
			server.join();

			log.info("Exiting...");
		} catch (Exception ex) {
			log.fatal("Interrupted while running server.", ex);
			System.exit(-1);
		}
	}

	// Getters & setters
	/**
	 * Will set the port to be used by the server. No value checking is done:
	 * Make sure your port is available!
	 * 
	 * @param port
	 */
	public static void setPORT(Integer port) {
		MainServer.PORT = (port != null) ? port : 8080;// MainServer.PORT;
		System.out.println(MainServer.PORT + "PORT");
	}
}