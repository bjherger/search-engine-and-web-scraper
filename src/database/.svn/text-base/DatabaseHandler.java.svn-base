package database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import webUI.MainServer;
import webUI.StringUtilities;

/**
 * Part of the {@link MainServer} example. Handles all database-related actions.
 * 
 * @see MainServer
 * @see DatabaseConnector
 */
public class DatabaseHandler {

	/** A logger for debugging. */
	private static final Logger log = LogManager.getLogger();

	/** Makes sure only one database handler is instantiated. */
	private static DatabaseHandler singleton = new DatabaseHandler();

	// login_users accounts

	/** Used to determine if tables are already setup. */
	private static final String TABLES_SQL = "SHOW TABLES LIKE 'login_users';";

	/** Used to create necessary tables for this example. */
	private static final String CREATE_SQL = "CREATE TABLE login_users ("
			+ "userid INTEGER AUTO_INCREMENT PRIMARY KEY, "
			+ "username VARCHAR(32) NOT NULL UNIQUE, "
			+ "password CHAR(64) NOT NULL, " + "usersalt CHAR(32) NOT NULL,"
			+ "admin TINYINT(1)" + ");";

	/** Used to insert a new user into the database. */
	private static final String REGISTER_SQL = "INSERT INTO login_users (username, password, usersalt, admin) "
			+ "VALUES (?, ?, ?,0);";

	/** Used to change user's password */
	private static final String CHANGE_PASS = "UPDATE login_users SET password=? WHERE username = ?;";

	/** Used to determine if a username already exists. */
	private static final String USER_SQL = "SELECT username FROM login_users WHERE username = ?";

	/** Used to retrieve the salt associated with a specific user. */
	private static final String SALT_SQL = "SELECT usersalt FROM login_users WHERE username = ?";

	/** Used to retrieve the salt associated with a specific user. */
	private static final String ADMIN_SQL = "SELECT admin FROM login_users WHERE username = ?";

	/** Used to authenticate a user. */
	private static final String AUTH_SQL = "SELECT username FROM login_users "
			+ "WHERE username = ? AND password = ?";

	/** Used to remove a user from the database. */
	private static final String DELETE_SQL = "DELETE FROM login_users WHERE username = ?";

	/** Used to configure connection to database. */
	private DatabaseConnector db;

	/**
	 * Initializes a database handler for the Login example. Private constructor
	 * forces all other classes to use singleton.
	 */
	private DatabaseHandler() {
		Status status = Status.OK;

		try {
			this.db = new DatabaseConnector();

			if (!db.testConnection()) {
				status = Status.CONNECTION_FAILED;
			} else {
				status = this.setupTables();
				status = this.setupQueryTables();
			}
		} catch (FileNotFoundException e) {
			status = Status.MISSING_CONFIG;
		} catch (IOException e) {
			status = Status.MISSING_VALUES;
		}

		// We cannot move on if the database handler fails, so exit
		if (status != Status.OK) {
			log.fatal(status.message());
			System.exit(-status.ordinal());
		}
	}

	/**
	 * Gets the single instance of the database handler.
	 * 
	 * @return instance of the database handler
	 */
	public static DatabaseHandler getInstance() {
		return singleton;
	}

	/**
	 * Checks if necessary table exists in database, and if not tries to create
	 * it.
	 * 
	 * @return {@link Status.OK} if table exists or create is successful
	 */
	private Status setupTables() {
		Status status = Status.ERROR;
		ResultSet results = null;

		try (Connection connection = db.getConnection();
				Statement statement = connection.createStatement();) {
			// check if table exists in database
			statement.executeQuery(TABLES_SQL);
			results = statement.getResultSet();

			if (!results.next()) {
				log.debug("Creating tables...");

				// create table and check if successful
				statement.executeUpdate(CREATE_SQL);
				statement.executeQuery(TABLES_SQL);

				results = statement.getResultSet();
				status = (results.next()) ? Status.OK : Status.CREATE_FAILED;
			} else {
				log.debug("Tables found.");
				status = Status.OK;
			}
		} catch (Exception ex) {
			status = Status.CREATE_FAILED;
			log.debug(status, ex);
		}

		return status;
	}

	/**
	 * Tests if a user already exists in the database. Requires an active
	 * database connection.
	 * 
	 * @param connection
	 *            - active database connection
	 * @param user
	 *            - username to check
	 * @return Status.OK if user does not exist in database
	 * @throws SQLException
	 */
	private Status duplicateUser(Connection connection, String user)
			throws SQLException {
		Status status = Status.ERROR;

		try (PreparedStatement statement = connection
				.prepareStatement(USER_SQL);) {
			statement.setString(1, user);
			statement.executeQuery();

			ResultSet results = statement.getResultSet();
			status = results.next() ? Status.DUPLICATE_USER : Status.OK;
		}

		return status;
	}

	/**
	 * Tests if a user already exists in the database.
	 * 
	 * @see #duplicateUser(Connection, String)
	 * @param user
	 *            - username to check
	 * @return Status.OK if user does not exist in database
	 */
	public Status duplicateUser(String user) {
		Status status = Status.ERROR;

		try (Connection connection = db.getConnection();) {
			status = duplicateUser(connection, user);
		} catch (SQLException e) {
			status = Status.CONNECTION_FAILED;
			log.debug(e.getMessage(), e);
		}

		return status;
	}

	/**
	 * Registers a new user, placing the username, password hash, and salt into
	 * the database if the username does not already exist.
	 * 
	 * @param newuser
	 *            - username of new user
	 * @param newpass
	 *            - password of new user
	 * @return {@link Status.OK} if registration successful
	 * @throws SQLException
	 */
	private Status registerUser(Connection connection, String newuser,
			String newpass) throws SQLException {
		Status status = Status.ERROR;

		byte[] saltbyte = StringUtilities.randomBytes(16);
		String usersalt = StringUtilities.encodeHex(saltbyte, 32);
		String passhash = StringUtilities.getHash(newpass, usersalt);

		try (PreparedStatement statement = connection
				.prepareStatement(REGISTER_SQL);) {
			statement.setString(1, newuser);
			statement.setString(2, passhash);
			statement.setString(3, usersalt);
			statement.executeUpdate();
			status = Status.OK;
		}

		return status;
	}

	/**
	 * Registers a new user, placing the username, password hash, and salt into
	 * the database if the username does not already exist.
	 * 
	 * @param newuser
	 *            - username of new user
	 * @param newpass
	 *            - password of new user
	 * @return {@link Status.OK} if registration successful
	 */
	public Status registerUser(String newuser, String newpass) {
		Status status = Status.ERROR;

		log.debug("Registering " + newuser + ".");

		// make sure we have non-null and non-emtpy values for login
		if (StringUtilities.checkString(newuser)
				|| StringUtilities.checkString(newpass)) {
			status = Status.INVALID_LOGIN;
			log.debug(status);
			return status;
		}

		// try to connect to database and test for duplicate user
		try (Connection connection = db.getConnection();) {
			status = duplicateUser(connection, newuser);

			// if okay so far, try to insert new user
			if (status == Status.OK) {
				status = registerUser(connection, newuser, newpass);
			}
		} catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			log.debug(status, ex);
		}

		return status;
	}

	/**
	 * Gets the salt for a specific user.
	 * 
	 * @param connection
	 *            - active database connection
	 * @param user
	 *            - which user to retrieve salt for
	 * @return salt for the specified user or null if user does not exist
	 * @throws SQLException
	 *             if any issues with database connection
	 */
	private String getSalt(Connection connection, String user)
			throws SQLException {
		String salt = null;

		try (PreparedStatement statement = connection
				.prepareStatement(SALT_SQL);) {
			statement.setString(1, user);
			statement.executeQuery();

			ResultSet results = statement.getResultSet();
			salt = results.next() ? results.getString("usersalt") : null;
		}

		return salt;
	}

	/**
	 * Checks if the provided username and password match what is stored in the
	 * database. Requires an active database connection.
	 * 
	 * @param username
	 *            - username to authenticate
	 * @param password
	 *            - password to authenticate
	 * @return {@link Status.OK} if authentication successful
	 * @throws SQLException
	 */
	private Status authenticateUser(Connection connection, String username,
			String password) throws SQLException {

		Status status = Status.ERROR;

		try (PreparedStatement statement = connection
				.prepareStatement(AUTH_SQL);) {
			String usersalt = getSalt(connection, username);
			String passhash = StringUtilities.getHash(password, usersalt);

			statement.setString(1, username);
			statement.setString(2, passhash);
			statement.executeQuery();

			ResultSet results = statement.getResultSet();
			status = results.next() ? status = Status.OK : Status.INVALID_LOGIN;
		}

		return status;
	}

	/**
	 * Checks if the provided username and password match what is stored in the
	 * database. Must retrieve the salt and hash the password to do the
	 * comparison.
	 * 
	 * @param username
	 *            - username to authenticate
	 * @param password
	 *            - password to authenticate
	 * @return {@link Status.OK} if authentication successful
	 */
	public Status authenticateUser(String username, String password) {
		Status status = Status.ERROR;

		log.debug("Authenticating user " + username + ".");

		try (Connection connection = db.getConnection();) {
			status = authenticateUser(connection, username, password);
		} catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			log.debug(status, ex);
		}

		return status;
	}

	/**
	 * Removes a user from the database if the username and password are
	 * provided correctly.
	 * 
	 * @param username
	 *            - username to remove
	 * @param password
	 *            - password of user
	 * @return {@link Status.OK} if removal successful
	 * @throws SQLException
	 */
	private Status removeUser(Connection connection, String username,
			String password) throws SQLException {
		Status status = Status.ERROR;

		try (PreparedStatement statement = connection
				.prepareStatement(DELETE_SQL);) {
			statement.setString(1, username);
			int count = statement.executeUpdate();
			status = (count == 1) ? Status.OK : Status.INVALID_USER;
		}

		return status;
	}

	/**
	 * Removes a user from the database if the username and password are
	 * provided correctly.
	 * 
	 * @param username
	 *            - username to remove
	 * @param password
	 *            - password of user
	 * @return {@link Status.OK} if removal successful
	 */
	public Status removeUser(String username, String password) {
		Status status = Status.ERROR;

		log.debug("Removing user " + username + ".");

		try (Connection connection = db.getConnection();) {
			status = authenticateUser(connection, username, password);

			if (status == Status.OK) {
				status = removeUser(connection, username, password);
			}
		} catch (Exception ex) {
			status = Status.CONNECTION_FAILED;
			log.debug(status, ex);
		}

		return status;
	}

	/**
	 * Checks if the provided username and password match what is stored in the
	 * database. Must retrieve the salt and hash the password to do the
	 * comparison.
	 * 
	 * @param username
	 *            - username to authenticate
	 * @param oldPassword
	 *            - password to authenticate
	 * @param newPassword
	 *            - password to put in database
	 * @return {@link Status.OK} if authentication successful
	 */
	public Status changePassword(String username, String oldPassword,
			String newPassword) {
		Status status = Status.ERROR;

		try (Connection connection = db.getConnection();) {
			status = authenticateUser(connection, username, oldPassword);
		} catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			log.debug(status, ex);
			System.out.println("error1: " + status);
		}
		if (status == Status.OK) {
			System.out.println("updating: " + status);
			status = Status.ERROR;
			try (Connection connection = db.getConnection();
					PreparedStatement statement = connection
							.prepareStatement(CHANGE_PASS);) {
				String usersalt = getSalt(connection, username);

				String passhash = StringUtilities
						.getHash(newPassword, usersalt);

				statement.setString(1, passhash);
				statement.setString(2, username);
				statement.executeUpdate();

				status = Status.OK;
				System.out.println("updated: " + status);
			} catch (Exception e) {
				status = Status.ERROR;
				log.error(e);
			}

		}

		return status;
	}

	/**
	 * Gets the salt for a specific user.
	 * 
	 * @param connection
	 *            - active database connection
	 * @param userID
	 *            - which user to retrieve salt for
	 * @return salt for the specified user or null if user does not exist
	 * @throws SQLException
	 *             if any issues with database connection
	 */
	public boolean getAdmin(String userID) {
		boolean toReturn = false;
		try (Connection connection = db.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(ADMIN_SQL);) {
			statement.setString(1, userID);
			statement.executeQuery();

			ResultSet results = statement.getResultSet();
			while (results.next()) {
				System.out.println();
				if (!results.getString("admin").equals("0")) {
					toReturn = true;
				}
			}
		} catch (SQLException e) {
			// logger
			e.printStackTrace();
		}

		return toReturn;
	}

	// user_queries entries

	/** Used to determine if queries is already setup. */
	private static final String SHOW_QUERIES_SQL = "SHOW TABLES LIKE 'user_queries';";

	/** Used to create necessary tables for query */
	private static final String CREATE_QUERIES_SQL = "CREATE TABLE user_queries ("
			+ "id INTEGER AUTO_INCREMENT PRIMARY KEY, "
			+ "userid VARCHAR(32) NOT NULL , "
			+ "query VARCHAR(64) NOT NULL); ";

	/** Used to insert a new query into the database. */
	private static final String ADD_QUERY_SQL = "INSERT INTO user_queries ( userid, query) "
			+ "VALUES (?, ?);";

	/** Used to retrieve the query associated with a specific user. */
	private static final String QUERY_SQL = "SELECT query FROM user_queries WHERE userid = ?";

	/** Used to remove a user's queries from the database. */
	private static final String DELETE_QUERY_SQL = "DELETE FROM user_queries WHERE userid = ?";

	/**
	 * Checks if necessary table exists in database, and if not tries to create
	 * it.
	 * 
	 * @return {@link Status.OK} if table exists or create is successful
	 */
	private Status setupQueryTables() {
		Status status = Status.ERROR;
		ResultSet results = null;

		try (Connection connection = db.getConnection();
				Statement statement = connection.createStatement();) {
			// check if table exists in database
			statement.executeQuery(SHOW_QUERIES_SQL);
			results = statement.getResultSet();

			if (!results.next()) {
				log.debug("Creating tables...");

				// create table and check if successful
				statement.executeUpdate(CREATE_QUERIES_SQL);
				statement.executeQuery(SHOW_QUERIES_SQL);

				results = statement.getResultSet();
				status = (results.next()) ? Status.OK : Status.CREATE_FAILED;
			} else {
				log.debug("Tables found.");
				status = Status.OK;
			}
		} catch (Exception ex) {
			status = Status.CREATE_FAILED;
			log.debug(status, ex);
		}

		return status;
	}

	/**
	 * Add query to database
	 * 
	 * @param userID
	 *            - username of new user
	 * @param query
	 *            - password of new user
	 * @return {@link Status.OK} if registration successful
	 * @throws SQLException
	 */
	private Status addQuery(Connection connection, String userID, String query)
			throws SQLException {
		Status status = Status.ERROR;

		try (PreparedStatement statement = connection
				.prepareStatement(ADD_QUERY_SQL);) {
			statement.setString(1, userID);
			statement.setString(2, query);
			statement.executeUpdate();
			status = Status.OK;
		}

		return status;
	}

	/**
	 * Add query to database
	 * 
	 * @param userID
	 *            - username of new user
	 * @param query
	 *            - password of new user
	 * @return {@link Status.OK} if registration successful
	 */
	public Status addQuery(String userID, String query) {
		Status status = Status.ERROR;

		log.debug("Registering " + userID + ".");

		// make sure we have non-null and non-emtpy values for login
		if (StringUtilities.checkString(userID)
				|| StringUtilities.checkString(query)) {
			status = Status.ERROR;
			log.debug(status);
			return status;
		}

		// try to connect to database and test for duplicate user
		try (Connection connection = db.getConnection();) {
			status = Status.OK;

			// if okay so far, try to insert new user
			if (status == Status.OK) {
				status = addQuery(connection, userID, query);
			}
		} catch (SQLException ex) {
			status = Status.CONNECTION_FAILED;
			log.debug(status, ex);
		}

		return status;
	}

	/**
	 * Gets the queries for a specific user.
	 * 
	 * @param connection
	 *            - active database connection
	 * @param userID
	 *            - which user to retrieve salt for
	 * @return salt for the specified user or null if user does not exist
	 * @throws SQLException
	 *             if any issues with database connection
	 */
	public ArrayList<String> getQueries(String userID) throws SQLException {
		ArrayList<String> toReturn = new ArrayList<String>();
		try (Connection connection = db.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(QUERY_SQL);) {
			statement.setString(1, userID);
			statement.executeQuery();

			ResultSet results = statement.getResultSet();
			while (results.next()) {
				toReturn.add(results.getString("query"));
			}
		}

		return toReturn;
	}

	/**
	 * Removes a user's queries from the database if the username is provided
	 * correctly.
	 * 
	 * @param userID
	 *            - username to remove
	 * @param password
	 *            - password of user
	 * @return {@link Status.OK} if removal successful
	 * @throws SQLException
	 */
	private Status removeUserQueries(Connection connection, String userID)
			throws SQLException {
		Status status = Status.ERROR;

		try (PreparedStatement statement = connection
				.prepareStatement(DELETE_QUERY_SQL);) {
			statement.setString(1, userID);
			int count = statement.executeUpdate();
			status = (count != 0) ? Status.OK : Status.ERROR;
		}

		return status;
	}

	/**
	 * Removes a user from the database if the username and password are
	 * provided correctly.
	 * 
	 * @param userID
	 *            - username to remove
	 * @param password
	 *            - password of user
	 * @return {@link Status.OK} if removal successful
	 */
	public Status removeUserQueries(String userID) {
		Status status = Status.ERROR;

		log.debug("Removing user " + userID + ".");

		try (Connection connection = db.getConnection();) {
			status = removeUserQueries(connection, userID);
		} catch (Exception ex) {
			status = Status.CONNECTION_FAILED;
			log.debug(status, ex);
		}

		return status;
	}

}
