package indexAndSearch;

import java.nio.file.Path;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import servlets.HomeServlet;
import webUI.MainServer;

/**
 * This is a program which will accept command line arguments with the following
 * flags:
 */
//-d: directory to index 
//-i: (optional) file to output directory search to. This flag may be followed by the desired output filename.
//-q: (optional) file containing query terms 
//-r: (optional) file to write query searches to. This flag may be followed by the desired output filename.
//-t: (optional) number of threads to be used.
//-u: (optional) web site to search and add to database
/**
 * Default Values: If the -d flag is provided, this program will construct an
 * inverted index of all words contained in that directory, and write it to a
 * file (default file invertedindex.txt)
 * 
 * if the -q flag is provided with a -r flag, a query search will be run, and
 * written to file (default file name searchresults.txt)
 * 
 * @author Brendan J. Herger
 * 
 */
public class Driver {

	// Variables
	private static Logger logger = LogManager.getLogger(Driver.class);

	// Methods
	/**
	 * Sets default values for specific cases. The cases handled are rather
	 * arbitrary.
	 * 
	 * @param inputParsed
	 * @param inputArgs
	 */

	public static void setDefaultValues(ArgumentParser inputParsed,
			String[] inputArgs) {
		// check output values
		if ((inputParsed.getPathValue("-i") == null)
				&& (inputParsed.getPathValue("-q") == null)) {
			inputParsed.addDefault("-i", "invertedindex.txt");
		} else if (Arrays.asList(inputArgs).contains("-i")
				&& (inputParsed.getPathValue("-i") == null)) {
			inputParsed.addDefault("-i", "invertedindex.txt");
		}
		if ((inputParsed.getPathValue("-r") == null)
				&& (Arrays.asList(inputArgs).contains("-r"))) {
			inputParsed.addDefault("-r", "searchresults.txt");
		}
		// check thread values
		try {
			if (Integer.parseInt(inputParsed.getValue("-t")) <= 0) {
				inputParsed.addDefault("-t", "5");
			}
		} catch (Exception e) {
			inputParsed.addDefault("-t", "5");
		}
	}

	/**
	 * Runs this program.
	 * 
	 * @param arguments
	 */
	public static void runAll(String[] arguments) {

		/** Build Index, Search Functionality **/

		// Parse user input values
		ArgumentParser argumentsParsed = new ArgumentParser(arguments);
		setDefaultValues(argumentsParsed, arguments);

		// User input / default values
		// where to index
		Path direcPath = argumentsParsed.getPathValue("-d");
		Path webPath = argumentsParsed.getPathValue("-u");
		Path indexOutputPath = argumentsParsed.getPathValue("-i");

		// where to search
		Path queryPath = argumentsParsed.getPathValue("-q");
		Path queryOutputPath = argumentsParsed.getPathValue("-r");

		// misc running values
		Integer numThreads = argumentsParsed.getIntegerValue("-t");
		Integer port = argumentsParsed.getIntegerValue("-p");

		// Create tools that are reused
		InvertedIndex database = new InvertedIndex();
		System.out.println(port + "here");
		WorkQueue workers = new WorkQueue(numThreads);
		WorkCounter driverCounter = new WorkCounter();

		// Add to index if necessary
		if ((direcPath != null) || (webPath != null)) {
			// Add any directories
			if (direcPath != null) {
				DirectoryTraverser trav = new DirectoryTraverser(workers,
						driverCounter);
				trav.traverseDirectory(direcPath, ".txt");
				database.addAll(InvertedIndexBuilder.parseLocalPathSet(
						trav.getPaths(), workers, driverCounter));

			}

			// Add any websites
			if (webPath != null) {
				WebCrawler trav = new WebCrawler(workers, driverCounter,
						database);
				trav.traverseDirectory(webPath);
			}

			// Write index to file if necessary
			if (indexOutputPath != null) {
				database.writeToFile(indexOutputPath);
			}

			// Search & write search to file if necessary. If search is not
			// written to file, it is not conducted.
			if ((queryPath != null) && (queryOutputPath != null)) {
				Search search = new Search(queryPath, database, workers,
						driverCounter);
				driverCounter.finish();
				search.writeQueriesToFile(queryOutputPath);
			}
		}

		/** Run Server **/
		MainServer.setPORT(port);

		HomeServlet.setIndex(database);
		MainServer.runServer();

		/** Graceful Shutdown **/
		logger.debug("Reached the End!");

		driverCounter.finish();
		workers.shutdown();

	}

	public static void main(String[] args) {
		runAll(args);
	}

}
