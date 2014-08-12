package indexAndSearch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A class designed to make fetching the results of different HTTP operations
 * easier. This particular class handles the GET operation.
 * 
 * @author Brendan J. Herger
 * 
 * @see HTTPFetcher
 * @see WebsiteTools
 * @see HeaderFetcher
 */
public abstract class WebsiteTools {

	// Variables
	private static final Logger logger = LogManager
			.getLogger(WebsiteTools.class);
	private static final int PORT = 80;

	// Misc.
	/**
	 * Strings the input string of all non-alphanumeric characters (excluding
	 * white spaces), and then splits based on white spaces.
	 * 
	 * @param inputString
	 * @return
	 */
	public static String[] cleanAndSeparateString(String inputString) {
		if (inputString != null) {
			ArrayList<String> stringAL = HTMLCleaner
					.cleanHTMLFormatting(inputString);
			String[] lineParsed = new String[stringAL.size()];
			int i = 0;

			for (String word : stringAL) {
				lineParsed[i] = word.replaceAll("[\\W_]", "").toLowerCase();
				i++;
			}
			return lineParsed;
		} else {
			return null;
		}
	}

	// Methods
	/**
	 * Crafts the HTTP GET request from the URL.
	 * 
	 * @return HTTP request
	 */
	protected static String craftRequest(URL inputURL) {
		String host = inputURL.getHost();
		String resource = inputURL.getFile().isEmpty() ? "/" : inputURL
				.getFile();

		StringBuffer output = new StringBuffer();
		output.append("GET " + resource + " HTTP/1.1\n");
		output.append("Host: " + host + "\n");
		output.append("Connection: close\n");
		output.append("\r\n");

		return output.toString();
	}

	/**
	 * Will attempt to download read all text sent from server, remove header,
	 * and return as String.
	 * 
	 * @param site
	 * @return String of everything except headers
	 */
	public static String fetch(Path site) {
		URL url;
		String toReturn = "";
		try {
			url = new URL(site.toString().replace("http:/", "https://"));
		} catch (MalformedURLException e) {
			logger.error(e);
			return toReturn;
		}
		try (Socket socket = new Socket(url.getHost(), PORT);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				PrintWriter writer = new PrintWriter(socket.getOutputStream());) {

			// Request page
			String request = craftRequest(url);
			writer.println(request);
			writer.flush();

			// Go through web page, line by line
			String line = reader.readLine();
			StringBuffer buffer = new StringBuffer();
			boolean htmlFlag = false;

			while (line != null) {

				if (line.toLowerCase().contains("<html")) {
					htmlFlag = true;
					line = reader.readLine();
				} else if (line.toLowerCase().contains("</html")) {
					htmlFlag = false;
					line = reader.readLine();
				}
				if (htmlFlag) {
					buffer.append(line);
					buffer.append(" ");
				}
				line = reader.readLine();
			}
			toReturn = buffer.toString();
		} catch (Exception e) {
			logger.error(e);
		}
		return toReturn;
	}

	// Building methods
	/**
	 * Builds an InvertedIndex of the provided HTML text
	 * 
	 * @param site
	 * @param HTMLString
	 * @return
	 */
	public static InvertedIndex buildInvertedIndex(Path site, String HTMLString) {
		// Create Index and clean text
		InvertedIndex index = new InvertedIndex();
		ArrayList<String> wordList = HTMLCleaner.cleanHTMLFormatting(HTMLString
				.toString());

		// Build Index
		Integer wordIndex = 1;
		for (String word : wordList) {
			if ((word.length() > 0)) {
				index.put(word, site, wordIndex);
				wordIndex++;
			}
		}

		return index;
	}

	/**
	 * Returns the list of links contained in the provided HTML.
	 * 
	 * @param links
	 * @return
	 */
	public static ArrayList<Path> getLinks(String HTML, Path base) {

		ArrayList<Path> links = new ArrayList<Path>();

		URL urlBase;
		try {
			urlBase = new URL(base.toString().replace("http:/", "https://"));

			for (Path path : HTMLLinkParser.listLinks(HTML.toString(), urlBase)) {

				links.add(path);

			}
		} catch (MalformedURLException e) {
			logger.error(e);
		}

		return links;

	}
}