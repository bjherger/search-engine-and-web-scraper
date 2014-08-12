package indexAndSearch;

import database.DatabaseHandler;

public class Playground {
	public static void main(String[] args) throws Exception {

		// DatabaseHandler queries = DatabaseHandler.getInstance();
		// queries.addQuery("1", "fuck a duck");
		// ArrayList<String> results = queries.getQueries("1");
		// System.out.println(results);
		//
		// System.out.println(queries.removeUserQueries("1"));
		// results = queries.getQueries("1");
		// System.out.println(results);

		DatabaseHandler queries = DatabaseHandler.getInstance();
		boolean hold = queries.getAdmin("asdfsdfsda");
		System.out.println(hold);
	}
}
