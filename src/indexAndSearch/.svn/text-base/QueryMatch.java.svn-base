package indexAndSearch;

import java.nio.file.Path;

//import org.apache.logging.log4j.Logger;

/**
 * This custom class will store the data of a match type for a query, along with
 * number of matches and first match index information.
 * 
 * @author Brendan J. Herger
 * 
 */
public class QueryMatch implements Comparable<QueryMatch> {

	// Variables
	private final Path pathName;
	private Integer numberOfMatches;
	private Integer firstMatchIndex;

	// Constructors
	public QueryMatch(Path inputPathName, Integer inputNumberOfMatches,
			Integer inputFirstMatchIndex) {
		// inputPath
		if (isPathName(inputPathName)) {
			pathName = inputPathName;// .toAbsolutePath().normalize();
		} else {
			pathName = null;
		}
		// inputNumberOfMatches
		if (isNumberOfMatches(inputNumberOfMatches)) {
			this.numberOfMatches = inputNumberOfMatches;
		} else {
			this.numberOfMatches = 0;
		}
		// input firstMatchIndex
		if (isFirstMatchIndex(inputFirstMatchIndex)) {
			this.firstMatchIndex = inputFirstMatchIndex;
		} else {
			this.firstMatchIndex = Integer.MAX_VALUE;
		}

	}

	public QueryMatch(Path inputPathName) {
		this(inputPathName, 0, Integer.MAX_VALUE);
	}

	// Methods
	/**
	 * Checks whether the input path is an acceptable pathName
	 * 
	 * @param inputPathName
	 * @return
	 */
	private boolean isPathName(Path inputPathName) {
		return true;
	}

	/**
	 * Checks whether the input value is an acceptable numberOfMatches
	 * 
	 * @param inputNumberOfMatches
	 * @return
	 */
	private boolean isNumberOfMatches(Integer inputNumberOfMatches) {
		if ((inputNumberOfMatches != null) && (inputNumberOfMatches >= 0)) {
			return true;
		} else {
			// logger.debug("Bad inputNumberOfMatches caught: {}",
			// inputNumberOfMatches);
			return false;
		}
	}

	/**
	 * Checks whether the input value is an acceptable firstMatchIndex
	 * 
	 * @param inputFirstMatchIndex
	 * @return
	 */
	private boolean isFirstMatchIndex(Integer inputFirstMatchIndex) {
		if ((this.firstMatchIndex == null) && (inputFirstMatchIndex >= 0)) {
			return true;
		} else if ((this.firstMatchIndex != null)
				&& (inputFirstMatchIndex < this.firstMatchIndex)) {
			return true;
		} else {
			// logger.debug("Bad inputFirstMatchIndex caught: {}",
			// inputFirstMatchIndex);
			return false;
		}
	}

	/**
	 * Attempts to change the firstMatchIndex of this match
	 * 
	 * @param inputFirstMatchIndex
	 */
	public void addFirstMatch(Integer inputFirstMatchIndex) {
		if (isFirstMatchIndex(inputFirstMatchIndex)) {
			this.firstMatchIndex = inputFirstMatchIndex;
		}
	}

	/**
	 * Attempts to add a number of matches to this match.
	 * 
	 * @param inputNumMatches
	 */
	public void addToNumberOfMatches(Integer inputNumMatches) {
		if (isNumberOfMatches(inputNumMatches)) {
			this.numberOfMatches += inputNumMatches;
		}
	}

	// Get and set methods
	/**
	 * Returns number of matches.
	 * 
	 * @return
	 */
	public int getNumberOfMatches() {
		return this.numberOfMatches;
	}

	/**
	 * Returns first match index.
	 * 
	 * @return
	 */
	public int getFirstMatchIndex() {
		return this.firstMatchIndex;
	}

	public Path getPathName() {
		return this.pathName;
	}

	// Override Methods
	/**
	 * Compares based on(in order): highest number of matches lowest first match
	 * index paths
	 */
	@Override
	public int compareTo(QueryMatch o) {
		Integer compare;
		Integer numberOfMatchesCompare = -1
				* Integer.compare(this.numberOfMatches, o.numberOfMatches);
		if (numberOfMatchesCompare == 0) {
			Integer positionCompare = Integer.compare(this.firstMatchIndex,
					o.firstMatchIndex);
			if (positionCompare == 0) {
				compare = this.pathName.compareTo(o.pathName);
			} else {
				compare = positionCompare;
			}
		} else {
			compare = numberOfMatchesCompare;
		}
		return compare;
	}

	/**
	 * Returns a string representation, using the following format:
	 * 
	 * "path", numberOfMatches, firstMatchIndex
	 */
	@Override
	public String toString() {
		String hold = "\""
				+ this.pathName.toString().replace("http:/", "http://") + "\""
				+ ", " + this.numberOfMatches + ", " + this.firstMatchIndex;
		return hold;
	}

}
