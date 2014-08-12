package indexAndSearch;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A class that will begin with a given directory, and will then add any files
 * in that directory ending with the given extension to a list of Paths. It will
 * then recursively search any directories found within that directory. Found
 * directories will be similarly searched.
 * 
 * @author Brendan J. Herger
 * 
 */
public class DirectoryTraverser {

	// Variables
	private final TreeSet<Path> paths;
	private final WorkQueue workers;
	private final WorkCounter counter;
	private final Logger logger;

	// Constructors
	public DirectoryTraverser(WorkQueue inputQueue, WorkCounter inputCounter) {

		this.workers = inputQueue;
		this.paths = new TreeSet<Path>();
		this.logger = LogManager.getLogger(DirectoryTraverser.class);
		this.counter = inputCounter;
		logger.debug(this.getClass().getName() + " instantiated");

	}

	public DirectoryTraverser() {
		this(new WorkQueue(), new WorkCounter());
	}

	// Methods
	/**
	 * Add all of the files in given Path directory, with the given extension,
	 * to the internal set of paths.
	 * 
	 * @param dir
	 * @param ext
	 */
	public void traverseDirectory(Path directory, String ext) {
		logger.debug("Searching Directory: {}", directory);
		if (Files.isDirectory(directory)) {
			workers.execute(new DirectoryCrawlerRunnable(directory, ext));
		} else if (Files.exists(directory)) {
			add(directory, ext);
		}
		counter.finish();
	}

	/**
	 * Will force current thread to wait until there all work has been done.
	 */
	public void finish() {
		counter.finish();
	}

	// getters and setters
	/**
	 * Returns the set of Paths found by this traverser. The set returned is a
	 * read-only copy.
	 * 
	 * @return
	 */
	public synchronized Set<Path> getPaths() {
		counter.finish();
		logger.debug("Returning paths");
		return Collections.unmodifiableSet(this.paths);
	}

	// data management
	/**
	 * Checks whether the given Path value is a file ending with the given
	 * extension
	 * 
	 * @param inputPath
	 * @param currentExtension
	 * @return
	 */
	private static boolean isValue(Path inputPath, String currentExtension) {
		return (inputPath.toString().toLowerCase().endsWith(currentExtension));
	}

	/**
	 * Adds the given set of Paths to the 'master' set of Paths.
	 * 
	 * @param inputTreeSet
	 */
	private synchronized void addAll(Set<Path> inputTreeSet) {
		paths.addAll(inputTreeSet);
	}

	/**
	 * Add the given path to the 'master' set of Paths, if it is an acceptable
	 * value.
	 * 
	 * @param inputPath
	 * @param currentExt
	 */
	private synchronized void add(Path inputPath, String currentExt) {
		if (isValue(inputPath, currentExt)) {
			paths.add(inputPath);
		}
	}

	// subclasses
	/**
	 * An Runnable subclass of DirectoryTraverser. Each instance of this
	 * subclass will parse one directory, and add all included files to the
	 * 'master' list of files. Any directories encountered are given their own
	 * instance.
	 * 
	 * @author brendanherger
	 * 
	 */
	private class DirectoryCrawlerRunnable implements Runnable {
		private final Path directory;
		String currentExt;

		public DirectoryCrawlerRunnable(Path directory, String currentExt) {
			this.directory = directory;
			this.currentExt = currentExt;
			counter.incrementPending();
			logger.debug("Minion {} created for path {}", this, directory);
		}

		@Override
		public void run() {
			logger.debug("Minion {} running for path ", this, directory);
			try (DirectoryStream<Path> stream = Files
					.newDirectoryStream(directory)) {
				TreeSet<Path> localPaths = new TreeSet<Path>();
				for (Path currentPath : stream) {
					if (Files.isDirectory(currentPath)) {
						workers.execute(new DirectoryCrawlerRunnable(
								currentPath, currentExt));
					} else {
						if (isValue(currentPath, currentExt)) {
							localPaths.add(currentPath);
						}
					}
				}
				addAll(localPaths);
			} catch (IOException e) {
				logger.debug("Minion {} ran into an issue", this, e);

			}
			counter.decrementPending();
			logger.debug("Minion {} finished with ", this, directory);
		}
	}

}