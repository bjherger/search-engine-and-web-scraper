package indexAndSearch;

import java.util.ArrayList;

/**
 * A helper class with several static methods that will help fetch a webpage,
 * strip out all of the HTML, and parse the resulting plain text into words.
 * Meant to be used for the web crawler project.
 * 
 * @author Brendan J. Herger
 * 
 * @see HTMLCleaner
 * @see HTMLCleanerTest
 */
public abstract class HTMLCleaner {

	// Methods
	/**
	 * Removes all style and script tags (and any text in between those tags),
	 * all HTML tags, and all special characters/entities.
	 * 
	 * @param html
	 *            - html code to parse
	 * @return plain text
	 */
	public static ArrayList<String> cleanHTMLFormatting(String html) {
		String text = html;
		text = stripBetweenGivenTags("script", text);
		text = stripBetweenGivenTags("style", text);
		text = stripTags(text);
		text = stripEntities(text);

		ArrayList<String> wordList = new ArrayList<String>();

		for (String word : text.split("\\s+")) {
			word = word.toLowerCase().replaceAll("[\\W_]+", "").trim();

			if (!word.isEmpty()) {
				wordList.add(word);
			}
		}

		return wordList;
	}

	/**
	 * Removes all style and script tags (and any text in between those tags),
	 * all HTML tags, and all special characters/entities.
	 * 
	 * @param html
	 *            - html code to parse
	 * @return plain text
	 */
	public static ArrayList<String> cleanHTMLFormatting(String html,
			String splitter) {
		String text = html;
		text = stripBetweenGivenTags("script", text);
		text = stripBetweenGivenTags("style", text);
		text = stripTags(text);
		text = stripEntities(text);

		ArrayList<String> wordList = new ArrayList<String>();

		for (String word : text.split(splitter)) {
			word = word.toLowerCase().replaceAll("[\\W_]+", "").trim();

			if (!word.isEmpty()) {
				wordList.add(word);
			}
		}

		return wordList;
	}

	/**
	 * Removes everything between the element tags, and the element tags
	 * themselves. For example, consider the html code:
	 * 
	 * <pre>
	 * &lt;style type="text/css"&gt;body { font-size: 10pt; }&lt;/style&gt;
	 * </pre>
	 * 
	 * If removing the "style" element, all of the above code will be removed,
	 * and replaced with the empty string.
	 * 
	 * @param name
	 *            - name of the element to strip, like style or script
	 * @param html
	 *            - html code to parse
	 * @return html code without the element specified
	 */
	public static String stripBetweenGivenTags(String name, String html) {
		String regex = "(?is)<\\s*" + name + ".*?/" + name + "\\s*>";
		return html.replaceAll(regex, "");
	}

	/**
	 * Removes all HTML tags, which is essentially anything between the < and >
	 * symbols. The tag will be replaced by the empty string.
	 * 
	 * @param html
	 *            - html code to parse
	 * @return text without any html tags
	 */
	public static String stripTags(String html) {
		return html.replaceAll("(?is)<.*?>", " ");
	}

	/**
	 * Replaces all HTML entities in the text with the empty string. For
	 * example, "2010&ndash;2012" will become "20102012".
	 * 
	 * @param html
	 *            - the text with html code being checked
	 * @return text with HTML entities replaced by a space
	 */
	public static String stripEntities(String html) {
		return html.replaceAll("(?is)&.*?;", " ");
	}
}