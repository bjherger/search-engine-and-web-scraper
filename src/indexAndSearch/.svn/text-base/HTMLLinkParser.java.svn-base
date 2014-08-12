package indexAndSearch;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * For this homework assignment, you must create a regular expression that is
 * able to parse links from HTML. Your code may assume the HTML is valid, and
 * all attributes are properly quoted and URL encoded.
 * 
 * <p>
 * See the following link for details on the HTML Anchor tag: <a
 * href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/a">
 * https://developer.mozilla.org/en-US/docs/Web/HTML/Element/a </a>
 * 
 * @author Brendan J. Herger
 * @see HTMLLinkTester
 */
public abstract class HTMLLinkParser {

	/**
	 * The regular expression used to parse the HTML for links.
	 */
	public static final String REGEX = "(?is)<a[^>]*?(?!>)href\\s*=\\s*\"([^\\\"]*).*?>";
	private static final Logger logger = LogManager
			.getLogger(HTMLLinkParser.class);

	/**
	 * The group in the regular expression that captures the raw link. This will
	 * usually be 1, depending on your specific regex.
	 */
	public static final int GROUP = 1;

	/**
	 * Parses the provided text for HTML links. You should not need to modify
	 * this method.
	 * 
	 * @param text
	 *            - valid HTML code, with quoted attributes and URL encoded
	 *            links
	 * @return list of links found in HTML code
	 */
	public static LinkedList<Path> listLinks(String text, URL urlBase) {
		// list to store links
		LinkedList<Path> links = new LinkedList<Path>();

		// compile string into regular expression
		Pattern p = Pattern.compile(REGEX);

		// match provided text against regular expression
		Matcher m = p.matcher(text);

		// loop through every match found in text
		while (m.find()) {
			// add the appropriate group from regular expression to list
			URL hold;
			try {
				hold = new URL(urlBase, m.group(GROUP));

				String stringHold = hold.getProtocol().replace("https", "http")
						+ "://" + hold.getHost() + hold.getPath();

				stringHold = (hold.getQuery() != null) ? stringHold + "#"
						+ hold.getQuery() : stringHold;
				links.add(Paths.get(stringHold));

			} catch (Exception e) {
				logger.error(e);
			}
		}

		return links;
	}
}
