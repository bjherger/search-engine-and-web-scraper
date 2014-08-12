package indexAndSearch;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A class to control multi-threaded access to search results. Search results
 * are stored in the format HashMap<String, ArrayList<QueryMatch>>
 * 
 * @author Brendan J. Herger
 * 
 */
public class SearchResult {
	// Variables
	private final HashMap<String, ArrayList<QueryMatch>> searchResults;
	private final ReadWriteLock lock;
	private final Logger logger;

	// Constructors
	public SearchResult() {
		this.searchResults = new LinkedHashMap<String, ArrayList<QueryMatch>>();
		this.lock = new ReadWriteLock();
		logger = LogManager.getLogger(SearchResult.class);
	}

	// Methods
	/**
	 * Adds a query / search result pair.
	 * 
	 * @param query
	 * @param matches
	 */
	public void addResult(String query, ArrayList<QueryMatch> matches) {
		lock.lockWrite();
		this.searchResults.put(query, matches);
		lock.unlockWrite();
	}

	/**
	 * Outputs the results in the order specified by the given queryList, to the
	 * given queryOutput Path.
	 * 
	 * @param queryList
	 * @param queryOutputPath
	 */
	public void writeQueriesToFile(Path queryOutputPath) {
		lock.lockRead();
		try (PrintWriter output = new PrintWriter(Files.newBufferedWriter(
				queryOutputPath, Charset.forName("UTF-8"),
				StandardOpenOption.CREATE))) {
			for (Entry<String, ArrayList<QueryMatch>> entry : searchResults
					.entrySet()) {
				output.write(entry.getKey());
				output.println();

				for (QueryMatch current : entry.getValue()) {

					output.write(current.toString());
					output.write("\n");
				}
				output.write("\n");
			}
		} catch (IOException e) {
			logger.debug(e);
		}
		lock.unlockRead();
	}
}
