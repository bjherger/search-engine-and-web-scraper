package indexAndSearch;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A simple work queue implementation based on the IBM developerWorks article by
 * Brian Goetz.
 * 
 * @see <a
 *      href="http://www.ibm.com/developerworks/library/j-jtp0730/index.html">Java
 *      Theory and Practice: Thread Pools and Work Queues</a>
 */
public class WorkQueue {

	// Variables
	private final PoolWorker[] workers;
	private final Logger logger;
	private final LinkedList<Runnable> queue;
	private volatile boolean shutdown;
	public static final int DEFAULT = 5;

	// Constructors
	/**
	 * Starts a work queue with the specified number of threads.
	 * 
	 * @param threads
	 *            number of worker threads; should be greater than 1
	 */
	public WorkQueue(int threads) {
		this.queue = new LinkedList<Runnable>();
		this.workers = new PoolWorker[threads];
		this.logger = LogManager.getLogger(WorkQueue.class);

		shutdown = false;

		// Start the threads so they are waiting in the background
		for (int i = 0; i < threads; i++) {
			workers[i] = new PoolWorker();
			workers[i].start();
		}
	}

	/**
	 * Starts a work queue with the default number of threads.
	 * 
	 * @see #WorkQueue(int)
	 */
	public WorkQueue() {
		this(DEFAULT);
	}

	// Methods

	/**
	 * Adds a work request to the queue. A thread will process this request when
	 * available.
	 * 
	 * @param r
	 *            work request (in the form of a {@link Runnable} object)
	 */
	public void execute(Runnable r) {
		synchronized (queue) {
			queue.addLast(r);
			queue.notifyAll();
		}
	}

	/**
	 * Asks the queue to shutdown. Any unprocessed work will not be finished,
	 * but threads in-progress will not be interrupted.
	 */
	public void shutdown() {
		shutdown = true;

		synchronized (queue) {
			queue.notifyAll();
		}
		for (PoolWorker worker : workers) {
			try {
				worker.join();
			} catch (InterruptedException e) {

				logger.debug(e);
			}
		}
	}

	/**
	 * Returns the number of worker threads being used by the work queue.
	 * 
	 * @return number of worker threads
	 */
	public int size() {
		return workers.length;
	}

	// sub-classes
	/**
	 * Waits until work is available in the work queue. When work is found, will
	 * remove the work from the queue and run it. If a shutdown is detected,
	 * will exit instead of grabbing new work from the queue. These threads will
	 * continue running in the background until a shutdown is requested.
	 */
	private class PoolWorker extends Thread {

		@Override
		public void run() {
			Runnable r = null;

			while (true) {
				synchronized (queue) {
					while (queue.isEmpty() && !shutdown) {
						try {
							queue.wait();
						} catch (InterruptedException ignored) {
							System.out
									.println("Warning: Work queue interrupted "
											+ "while waiting.");
						}
					}

					if (shutdown) {
						break;
					} else {
						r = queue.removeFirst();
					}
				}

				try {
					r.run();
				} catch (RuntimeException e) {
					System.out.println("Warning: Work queue encountered an "
							+ "exception while running.");
					logger.error(e);
				}
			}
		}
	}
}
