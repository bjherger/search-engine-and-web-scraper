package indexAndSearch;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A class that will search an InvertedIndex for queries in a document, and
 * write them to an output document.
 * 
 * @author Brendan J. Herger
 * 
 */

public class Search {

	// Variables
	private final InvertedIndex database;
	private final SearchResult results;
	private final WorkQueue workers;
	private final WorkCounter counter;
	private final Logger logger;

	// Constructors
	public Search(Path inputPath, InvertedIndex database, WorkQueue workers,
			WorkCounter counter) {
		this.database = database;
		this.results = new SearchResult();
		this.logger = LogManager.getLogger(Search.class);
		this.workers = workers;
		this.counter = counter;
		runQueries(inputPath, database);
		counter.finish();
		logger.debug("MultithreadedSearch instantiated");

	}

	public Search() {
		this(null, new InvertedIndex(), new WorkQueue(), new WorkCounter());
	}

	// Methods
	/**
	 * Searches the given InvertedIndex for queries given in the inputPath.
	 * Output is returned without being written to file.
	 * 
	 * @param inputPath
	 * @param database
	 * @return
	 */
	public void runQueries(Path inputPath, InvertedIndex database) {
		try (BufferedReader currentDocument = Files.newBufferedReader(
				inputPath, StandardCharsets.UTF_8)) {
			String inputString = currentDocument.readLine();

			while (inputString != null) {
				// Clean strings
				String[] lineKeywords = cleanAndSeparateString(inputString);
				results.addResult(inputString, null);

				// Make minion
				workers.execute(new SearchRunnable(lineKeywords,
						cleanString(inputString), this.database, this.results));

				// Get next result
				inputString = currentDocument.readLine();
			}
		} catch (IOException e) {
			logger.debug("Error parsing input path {}", inputPath);
		}
		counter.finish();
	}

	/**
	 * Strings the input string of all non-alphanumeric characters (excluding
	 * white spaces), and then splits based on white spaces.
	 * 
	 * @param inputString
	 * @return
	 */
	public static String[] cleanAndSeparateString(String inputString) {
		if (inputString != null) {
			String[] lineParsed = inputString.split("\\s+");
			for (int i = 0; i < lineParsed.length; i++) {
				lineParsed[i] = lineParsed[i].replaceAll("[\\W_]", "")
						.toLowerCase();
			}
			return lineParsed;
		} else {
			return null;
		}
	}

	/**
	 * Strings the input string of all non-alphanumeric characters (excluding
	 * white spaces), and then splits based on white spaces.
	 * 
	 * @param inputString
	 * @return
	 */
	public static String cleanString(String inputString) {
		if (inputString != null) {
			return inputString.replaceAll("[\\W_]", " ").toLowerCase();

		} else {
			return null;
		}
	}

	/**
	 * Writes the given input to the selected file, one value at a time, in the
	 * order specified by the LinkedHashMap
	 * 
	 * @param searchResults
	 * @param queryOutputPath
	 */
	public void writeQueriesToFile(Path queryOutputPath) {
		logger.debug("Attempting to write query matches to file: {}",
				queryOutputPath);
		counter.finish();
		results.writeQueriesToFile(queryOutputPath);
		logger.debug("Finished writing query matches to file: {}",
				queryOutputPath);

	}

	// Subclasses
	/**
	 * A runnable subclass that allows separate threads to search the database.
	 * Each query is handled by a separate thread.
	 * 
	 * @author brendanherger
	 * 
	 */
	private class SearchRunnable implements Runnable {
		String[] lineKeywords;
		String cleanedString;

		public SearchRunnable(String[] lineKeywords, String cleanedString,
				InvertedIndex db, SearchResult results) {
			this.lineKeywords = lineKeywords;
			this.cleanedString = cleanedString;
			counter.incrementPending();
			logger.debug("Minion {} created to query starting with: {}",
					lineKeywords[0]);
		}

		/**
		 * Searches the given InvertedIndex for the given Query. Results are
		 * added to SearchResult instance as a group.
		 */
		@Override
		public void run() {
			logger.debug("Minion {} started for query starting with: {}", this,
					lineKeywords[0]);
			ArrayList<QueryMatch> result = database.partialSearch(lineKeywords);
			results.addResult(cleanedString, result);
			counter.decrementPending();
			logger.debug("Minion {} ending for query starting with: {}", this,
					lineKeywords[0]);
		}
	}
}
