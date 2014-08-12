package indexAndSearch;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A custom index which holds the following data structure: TreeMap<String,
 * TreeMap<Path, TreeSet<Integer>>>, and methods to control data input and write
 * to file.
 * 
 * @author Brendan J. Herger
 * 
 */
public class InvertedIndex {

	// Variables
	private final TreeMap<String, TreeMap<Path, TreeSet<Integer>>> wordList;
	private final ReadWriteLock lock;

	// Constructors
	public InvertedIndex() {
		wordList = new TreeMap<String, TreeMap<Path, TreeSet<Integer>>>();
		lock = new ReadWriteLock();
	}

	// Methods
	/**
	 * Adds an entry to the index, if word, currentDocument and wordIndex are
	 * all in an acceptable form.
	 * 
	 * @param word
	 * @param currentDocument
	 * @param wordIndex
	 */
	public void put(String word, Path currentDocument, int wordIndex) {
		lock.lockWrite();
		if (wordList.containsKey(word)) {
			if (wordList.get(word).containsKey(currentDocument)) {
				// word list contains word, document
				wordList.get(word).get(currentDocument).add(wordIndex);

			} else {
				// word list contains word, not document
				TreeSet<Integer> indexList = new TreeSet<Integer>();
				indexList.add(wordIndex);
				wordList.get(word).put(currentDocument, indexList);

			}
		} else {
			// wordList does not contain word
			TreeMap<Path, TreeSet<Integer>> wordEntry = new TreeMap<Path, TreeSet<Integer>>();
			TreeSet<Integer> indexList = new TreeSet<Integer>();
			indexList.add(wordIndex);
			wordEntry.put(currentDocument, indexList);
			wordList.put(word, wordEntry);
		}
		lock.unlockWrite();
	}

	/**
	 * Will add the given Path and TreeSet<Integer> pair to the given word's
	 * entry. This will overwrite any existing entries.
	 * 
	 * @param word
	 * @param pathMap
	 */
	public void put(String word, Path currentDocument,
			TreeSet<Integer> indexList) {
		lock.lockWrite();
		if (wordList.containsKey(word)) {

			wordList.get(word).put(currentDocument, indexList);

		} else {
			// wordList does not contain word
			TreeMap<Path, TreeSet<Integer>> wordEntry = new TreeMap<Path, TreeSet<Integer>>();
			wordEntry.put(currentDocument, indexList);
			wordList.put(word, wordEntry);
		}
		lock.unlockWrite();

	}

	/**
	 * Adds an entire InvertedIndex to this Index. Any Path values will override
	 * existing values.
	 * 
	 * @param inputIndex
	 */
	public void addAll(InvertedIndex inputIndex) {
		this.lock.lockWrite();
		inputIndex.lock.lockRead();

		// TODONE Call "inputIndex.wordList.entrySet()" instead of
		// inputIndex.entrySet()!
		for (Entry<String, TreeMap<Path, TreeSet<Integer>>> wordEntry : inputIndex.wordList
				.entrySet()) {

			for (Entry<Path, TreeSet<Integer>> pathEntry : wordEntry.getValue()
					.entrySet()) {

				// This index does not have wordEntry
				if (!this.wordList.containsKey(wordEntry.getKey())) {

					TreeMap<Path, TreeSet<Integer>> wordEntry2 = new TreeMap<Path, TreeSet<Integer>>();
					wordEntry2.put(pathEntry.getKey(), pathEntry.getValue());
					wordList.put(wordEntry.getKey(), wordEntry2);

				}

				// This index does have wordEntry, does not have pathEntry
				// for that wordEntry
				else if (!this.wordList.get(wordEntry.getKey()).containsKey(
						pathEntry.getKey())) {

					wordList.get(wordEntry.getKey()).put(pathEntry.getKey(),
							pathEntry.getValue());

				}

				// This index has neither wordEntry nor pathEntry
				else {
					TreeSet<Integer> thisIntegerSet = this.wordList.get(
							wordEntry.getKey()).get(pathEntry.getKey());
					TreeSet<Integer> inputIntegerSet = pathEntry.getValue();
					thisIntegerSet.addAll(inputIntegerSet);
					this.wordList.get(wordEntry.getKey()).put(
							pathEntry.getKey(), thisIntegerSet);
				}
			}
		}

		this.lock.unlockWrite();
		inputIndex.lock.unlockRead();
	}

	/**
	 * Create a file with the given filename, and write the contents of
	 * InvertedIndex to it.
	 * 
	 * @param filename
	 * @param toBeWritten
	 */
	public void writeToFile(Path filename) {
		lock.lockRead();
		Charset charset = Charset.forName("UTF-8");
		try (BufferedWriter writer = Files.newBufferedWriter(filename, charset);) {

			for (String word : wordList.keySet()) {

				writer.write(word);

				for (Entry<Path, TreeSet<Integer>> entry : wordList.get(word)
						.entrySet()) {
					writer.newLine();
					writer.write("\"");
					writer.write(entry.getKey().toString()
							.replace("http:/", "http://"));
					writer.write("\"");
					for (Integer index : entry.getValue()) {
						writer.write(", ");
						writer.write(index.toString());
					}
				}
				writer.write("\n\n");
			}
		} catch (IOException e) {
			System.out.println("ERROR: Cannot write to file.");
		}
		lock.unlockRead();
	}

	/**
	 * Outputs contents of database. Use for debugging purposes only!
	 * 
	 * @param inputPath
	 * @param inputIndex
	 */
	public String formatOutput() {
		lock.lockRead();
		StringBuffer output = new StringBuffer();
		for (String word : wordList.keySet()) {

			output.append(word);

			for (Entry<Path, TreeSet<Integer>> entry : wordList.get(word)
					.entrySet()) {
				output.append("\n");
				output.append("\"");
				output.append(entry.getKey());
				output.append("\"");
				for (int index : entry.getValue()) {
					output.append(", ");
					output.append(index);
				}
			}
			output.append("\n\n");
		}
		lock.unlockRead();
		return output.toString();

	}

	/**
	 * Searches this index for words which begin with the each of the strings in
	 * queryWords. Any matches are returned as QueryMatchIndex.
	 * 
	 * @param queryWords
	 * @param queryMatches
	 * @return
	 */
	public ArrayList<QueryMatch> partialSearch(String[] queryWords) {
		lock.lockRead();
		HashMap<Path, QueryMatch> resultMap = new HashMap<>();

		for (String word : queryWords) {
			if ((word != null) && !word.isEmpty()) {
				for (String wordEntry : this.wordList.tailMap(word).keySet()) {
					if (wordEntry.startsWith(word)) {
						for (Map.Entry<Path, TreeSet<Integer>> entry : this.wordList
								.get(wordEntry).entrySet()) {

							// add results for every word in the relevant part
							// of the wordList.
							QueryMatch resultEntry = resultMap.get(entry
									.getKey());
							if (resultEntry != null) {

								// if the path is already in the database
								resultEntry.addFirstMatch(entry.getValue()
										.first());
								resultEntry.addToNumberOfMatches(entry
										.getValue().size());
							} else if (entry.getKey() != null) {

								// if the path is not in the database
								resultMap.put(entry.getKey(), new QueryMatch(
										entry.getKey(),
										entry.getValue().size(), entry
												.getValue().first()));
							}
						}
					} else {
						// occurs when out of range of words that begin with
						// input query.
						break;

					}
				}
			}
		}

		// formatting results
		ArrayList<QueryMatch> resultList = new ArrayList<>();
		lock.unlockRead();
		resultList.addAll(resultMap.values());
		Collections.sort(resultList);
		return resultList;
	}
}
