package indexAndSearch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A simple custom lock that allows simultaneously read operations, but
 * disallows simultaneously write and read/write operations.
 * 
 * You do not need to implement any form or priority to read or write
 * operations. The first thread that acquires the appropriate lock should be
 * allowed to continue.
 * 
 * @author CS 212 Software Development
 * @author University of San Francisco
 */
public class ReadWriteLock {

	// Variables
	private int writers;
	private int readers;
	private final Logger logger;

	// Constructors
	/**
	 * Initializes a multi-reader (single-writer) lock.
	 */
	public ReadWriteLock() {
		this.logger = LogManager.getLogger(ReadWriteLock.class);
		writers = 0;
		readers = 0;
	}

	// Methods
	// Read related methods
	/**
	 * Will wait until there are no active writers in the system, and then will
	 * increase the number of active readers.
	 */
	public synchronized void lockRead() {
		while (writers > 0) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				logger.debug(e);
			}
		}
		readers++;
	}

	/**
	 * Will decrease the number of active readers, and notify any waiting
	 * threads if necessary.
	 */
	public synchronized void unlockRead() {
		if (readers > 0) {
			readers--;
		}
		this.notifyAll();
	}

	// Lock related methods
	/**
	 * Will wait until there are no active readers or writers in the system, and
	 * then will increase the number of active writers.
	 */
	public synchronized void lockWrite() {
		while ((readers + writers) > 0) {
			try {
				this.wait();
			} catch (InterruptedException e) {

				logger.error(e);
			}
		}
		writers++;
	}

	/**
	 * Will decrease the number of active writers, and notify any waiting
	 * threads if necessary.
	 */
	public synchronized void unlockWrite() {
		if (writers > 0) {
			writers--;
			this.notifyAll();
		}
	}
}
