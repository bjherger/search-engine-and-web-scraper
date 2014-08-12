package indexAndSearch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A simple class to keep track of jobs that still have not been finished. The
 * counter does not actually verify work, but rather only counts calls to
 * increment and decrement. All implementations are responsible for correctly
 * using implement and decrement
 * 
 * @author Brendan J. Herger
 * 
 */
public class WorkCounter {

	// Variables
	protected int pending;
	public Logger logger;

	// Constructors
	public WorkCounter() {
		this.pending = 0;
		this.logger = LogManager.getLogger(WorkCounter.class);
	}

	// Methods
	// Increment & decrement methods

	/**
	 * Indicates that we now have additional "pending" work to wait for.
	 */
	public synchronized void incrementPending() {
		pending++;
		logger.debug("incremented");
	}

	/**
	 * Indicates that we now have one less "pending" work, and will notify any
	 * waiting threads if we no longer have any more pending work left.
	 */
	public synchronized void decrementPending() {
		pending--;
		logger.debug("decremented");
		logger.debug(pending);

		if (pending <= 0) {
			this.notifyAll();
		}
	}

	// Other Methods
	/**
	 * Forces the current thread to wait until all pending work has been done.
	 * This prevents any work on a partially completed index. Information
	 * gathered is still retained.
	 */
	public synchronized void finish() {
		logger.debug("Finish called");
		try {
			while (pending > 0) {
				this.wait();
			}
		} catch (InterruptedException e) {
			logger.debug(e);
		}
		logger.debug("Finish complete");
	}

}
