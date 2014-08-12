package indexAndSearch;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class will search a given website for any links, and then add those
 * links to a master list of links. Any links encountered will be recursively
 * searched, up to a maximum number of links. Duplicates are not added to the
 * list again, do not count towards the maximum and are not searched again.
 * 
 * @author Brendan J. Herger
 * 
 */

public class WebCrawler {

	// Variables
	private final HashSet<Path> pathsMaster;
	private final InvertedIndex index;

	private final WorkQueue workers;
	private final WorkCounter counter;
	private final ReadWriteLock lock;
	private final static Logger logger = LogManager.getLogger(WebCrawler.class);

	private static final int maxPaths = 50;

	// Constructors
	public WebCrawler(WorkQueue inputQueue, WorkCounter inputCounter,
			InvertedIndex index) {

		this.workers = inputQueue;
		this.pathsMaster = new HashSet<Path>();
		this.counter = inputCounter;
		this.index = index;
		this.lock = new ReadWriteLock();
		logger.debug(this.getClass().getName() + " instantiated");

	}

	public WebCrawler() {
		this(new WorkQueue(), new WorkCounter(), new InvertedIndex());
	}

	// Methods
	/**
	 * Adds up to the maximum number of paths to the internal set of paths to
	 * the internal set of paths.
	 * 
	 * @param dir
	 * @param ext
	 */
	public void traverseDirectory(Path inputSite) {
		logger.debug("Searching Directory: {}", inputSite);
		if (WebCrawler.isValue(inputSite)) {
			counter.incrementPending();
			lock.lockWrite();
			pathsMaster.add(inputSite);
			lock.unlockWrite();
			workers.execute(new WebTraverserRunnable(inputSite, inputSite));

		} else {
			logger.error("Website provided cannot be traversed:{}", inputSite);
		}
		this.finish();
	}

	/**
	 * Will force current thread to wait until there all work has been done.
	 */
	public void finish() {
		counter.finish();
	}

	// getter and setter methods
	/**
	 * Returns the set of Paths found by this traverser. The set returned is a
	 * read-only copy.
	 * 
	 * @return
	 */
	public Set<Path> getPaths() {
		counter.finish();
		logger.debug("Returning paths");
		lock.lockRead();
		Set<Path> hold = Collections.unmodifiableSet(this.pathsMaster);
		lock.unlockRead();
		return hold;
	}

	// data management methods
	/**
	 * Checks whether the given Path value is a file ending with the given
	 * extension
	 * 
	 * @param inputPath
	 * @param currentExtension
	 * @return
	 */
	private static boolean isValue(Path inputPath) {
		if (inputPath != null) {
			return true;
		} else {
			logger.error("BAD PATH!!!");
			return false;
		}

	}

	// Subclasses
	/**
	 * An Runnable subclass of WebTraverser. Each instance of this subclass will
	 * parse one website, and add all found links to the 'master' list of files,
	 * up to the maxNum of sites. Any directories websites encountered are given
	 * their own instance.
	 * 
	 * @author Brendan J. Herger
	 * 
	 */
	private class WebTraverserRunnable implements Runnable {
		private final Path directory;
		private final Path base;

		public WebTraverserRunnable(Path directory, Path base) {
			this.directory = directory;
			this.base = base;
			logger.debug("Minion {} created for path {}", this, directory);
		}

		@Override
		public void run() {
			logger.debug("Minion {} running for path {}", this, directory);
			try {
				LinkedList<Path> pathsLocal = new LinkedList<Path>();

				// Get text to work with
				String pageString = WebsiteTools.fetch(this.directory);

				// Get list of links
				ArrayList<Path> foundPaths = WebsiteTools.getLinks(pageString,
						this.base);

				// Add to local list
				lock.lockWrite();
				for (Path entry : foundPaths) {
					if (((pathsMaster.size() + pathsLocal.size()) < maxPaths)
							&& isValue(entry) && !pathsMaster.contains(entry)
							&& !pathsLocal.contains(entry)) {
						pathsLocal.add(entry);
					}

				}
				// add local list to master list
				pathsMaster.addAll(pathsLocal);
				lock.unlockWrite();

				// search local path list
				for (Path toSearch : pathsLocal) {

					workers.execute(new WebTraverserRunnable(toSearch,
							this.base));
					counter.incrementPending();

				}
				// add to local InvertedIndex
				InvertedIndex siteIndex = WebsiteTools.buildInvertedIndex(
						this.directory, pageString);

				// Add all to master InvertedIndex
				index.addAll(siteIndex);
			} catch (Exception e) {
				logger.debug("Minion {} ran into an issue {}", this, e);
			}
			counter.decrementPending();
			logger.debug("Minion {} finished with {}", this, directory);
		}
	}
}
