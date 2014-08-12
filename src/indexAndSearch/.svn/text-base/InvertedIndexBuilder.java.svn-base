package indexAndSearch;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A class to build an inverted index from a given HashSet <Path>.
 * 
 * @author Brendan J. Herger
 * 
 */
public abstract class InvertedIndexBuilder extends WorkCounter {

	// Variables
	public static Logger logger = LogManager
			.getLogger(InvertedIndexBuilder.class);

	// Methods
	/**
	 * Reads the next line, removes all non-alpha-numeric or spaces characters,
	 * and then splits the line by spaces.
	 * 
	 * @return list of 'stripped' words
	 */
	public static InvertedIndex parseLocalPathSet(Set<Path> inputPathSet,
			WorkQueue workers, WorkCounter counter) {

		logger.debug("\nDatabase Constructor begun");

		InvertedIndex database = new InvertedIndex();
		for (Path inputPath : inputPathSet) {
			workers.execute(new LocalRunnable(inputPath, database, counter));
		}
		counter.finish();
		return database;
	}

	/**
	 * A runnable subclass of InvertedIndexBuilder. Each instance of this
	 * subclass will parse one file, and create a local version of an
	 * InvertedIndex for that file. The local InvertedIndex is then added to the
	 * 'master' InvertedIndex.
	 * 
	 * @author brendanherger
	 * 
	 */
	private static class LocalRunnable implements Runnable {
		private final Path directory;
		private final InvertedIndex index;
		WorkCounter counter;

		public LocalRunnable(Path directory, InvertedIndex index,
				WorkCounter counter) {
			counter.incrementPending();
			this.directory = directory;
			this.index = index;
			this.counter = counter;
			logger.debug("Minion {} created for path {}", this, directory);
		}

		/**
		 * Takes the input file, iterates through it, and adds it to an inverted
		 * index.
		 * 
		 * @param inputPath
		 * @param inputIndex
		 */
		public static void parseFile(Path inputPath, InvertedIndex inputIndex) {
			try (BufferedReader currentDocument = Files.newBufferedReader(
					inputPath, StandardCharsets.UTF_8)) {
				int wordIndex = 0;

				if (currentDocument != null) {

					logger.debug("Adding file {} ", inputPath);
					InvertedIndex localIndex = new InvertedIndex();
					String currentReadLine = currentDocument.readLine();
					while (currentReadLine != null) {

						String[] lineParsed = currentReadLine.split("\\s+");

						for (int i = 0; i < lineParsed.length; i++) {
							lineParsed[i] = lineParsed[i].replaceAll("[\\W_]",
									"").toLowerCase();

							if (((lineParsed[i].length() > 0))) {
								wordIndex++;
								localIndex.put(lineParsed[i], inputPath,
										wordIndex);
							}

						}
						currentReadLine = currentDocument.readLine();
					}
					inputIndex.addAll(localIndex);
					logger.debug("Done adding file {} ", inputPath);

				} else {
					logger.error("Exception! Null file: {}", inputPath);
				}

			} catch (IOException e) {

				logger.error(e);
			}
		}

		@Override
		public void run() {
			logger.debug("Minion {} running", this);
			LocalRunnable.parseFile(directory, index);
			this.counter.decrementPending();
			logger.debug("Minion {} done", this);
		}
	}

}
