package indexAndSearch;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Class that handles parsing an array of arguments into flag/value pairs. A
 * flag is considered to be a non-null String that starts with a "-" dash
 * symbol. A value optionally follows a flag, and must not start with a "-" dash
 * symbol.
 * 
 * @author // Brendan J. Herger
 */

public class ArgumentParser {

	// Variables
	/** Stores flag/value pairs of arguments. */
	private final HashMap<String, String> argumentMap;

	// Constructors
	public ArgumentParser(String[] args) {
		argumentMap = new HashMap<String, String>();
		if (args != null) {
			parseArgs(args);
		}
	}

	// Methods
	/**
	 * Parses the provided array of arguments into flag/value pairs, storing the
	 * results in an internal map.
	 * 
	 * @param arguments
	 *            to parse
	 */
	private void parseArgs(String[] arguments) {
		int argLength = arguments.length;
		for (int i = 0; i < argLength; i++) {
			if (isFlag(arguments[i]) && (i != (argLength - 1))) {
				if (isValue(arguments[i + 1])) {
					argumentMap.put(arguments[i], arguments[i + 1]);
				} else {
					continue;
				}
			}
			if ((i == (argLength - 1)) && isFlag(arguments[i])) {
				argumentMap.put(arguments[i], null);
			}
		}
	}

	/**
	 * Tests whether the provided String is a flag, i.e. whether the String is
	 * non-null, starts with a "-" dash, and has at least one character
	 * following the dash.
	 * 
	 * @param text
	 *            to test
	 * @return <code>true</code> if the text is non-null, start with the "-"
	 *         dash symbol, and has a flag name of at least one character
	 */
	public static boolean isFlag(String text) {
		if (text == null) {
			return false;
		} else if (text.length() < 2) {
			return false;
		} else if (!(text.startsWith("-"))) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Tests whether the provided String is a value, i.e. whether the String is
	 * non-null, non-empty, and does NOT start with a "-" dash.
	 * 
	 * @param text
	 *            to test
	 * @return <code>true</code> if the text is non-null, non-empty, and does
	 *         NOT start with the "-" dash symbol
	 */
	public static boolean isValue(String text) {
		if (text == null) {
			return false;
		} else if ((text.length() < 1)) {
			return false;
		} else if (text.startsWith("-")) {
			return false;
		} else {

			return true;
		}
	}

	/**
	 * Returns the number of flags stored in the argument map.
	 * 
	 * @return number of flags stored in the argument map
	 */
	public int numFlags() {
		return argumentMap.size();
	}

	/**
	 * Checks whether the provided flag exists in the argument map.
	 * 
	 * @param flag
	 *            to check for
	 * @return <code>true</code> if the flag exists
	 */
	public boolean hasFlag(String flag) {

		return argumentMap.containsKey(flag);
	}

	/**
	 * Checks whether the provided flag has an associated non-null value.
	 * Returns <code>false</code> if there is no value for the flag, or if the
	 * flag does not exist.
	 * 
	 * @param flag
	 *            to check for value
	 * @return <code>true</code> if the flag has a non-null value, or
	 *         <code>false</code> if the value or flag does not exist
	 */
	public boolean hasValue(String flag) {
		return (argumentMap.get(flag) != null);
	}

	/**
	 * Allows arguments to be overridden in the case that default values must be
	 * implemented. Use with extreme caution!
	 * 
	 * Only inputs which satisfy flag, value requirements will be added.
	 * Otherwise map will remain unchanged.
	 * 
	 * @param flag
	 * @param defaultValue
	 */
	public void addDefault(String flag, String defaultValue) {
		if (isFlag(flag) && isValue(defaultValue)) {

			this.argumentMap.put(flag, defaultValue);
		}
	}

	// getters and setters
	/**
	 * Returns the value associated with a flag converted to a path, or
	 * <code>null</code> if the flag does not exist, does not have an associated
	 * value, or cannot be converted to Path.
	 * 
	 * @param flag
	 *            to fetch associated value
	 * @return value of the flag if it exists, or <code>null</code>
	 */
	public Path getPathValue(String flag) {
		try {
			return Paths.get(argumentMap.get(flag));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns the value associated with a flag converted to a path, or
	 * <code>null</code> if the flag does not exist, the flag does not have an
	 * associated value, or cannot be converted to Integer.
	 * 
	 * @param flag
	 *            to fetch associated value
	 * @return value of the flag if it exists, or <code>null</code>
	 */
	public Integer getIntegerValue(String flag) {
		try {
			return Integer.valueOf(argumentMap.get(flag));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns the value associated with a flag, or <code>null</code> if the
	 * flag does not exist, or the flag does not have an associated value.
	 * 
	 * @param flag
	 *            to fetch associated value
	 * @return value of the flag if it exists, or <code>null</code>
	 */
	public String getValue(String flag) {
		return argumentMap.get(flag);
	}

}
