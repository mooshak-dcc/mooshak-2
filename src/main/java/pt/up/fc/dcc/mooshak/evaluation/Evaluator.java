package pt.up.fc.dcc.mooshak.evaluation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.evaluation.EvaluationQueue.EvaluationRequest;
import pt.up.fc.dcc.mooshak.managers.Manager;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Evaluator of executions. Request for evaluation are fetched from an
 * evaluation queue, and results are reported back to the same queue.
 * 
 * Different types of evaluators can be created using different evaluations 
 * queues. The standard evaluation queue collects requests from the local 
 * participant manager a forwards results to events. The load balancing 
 * service implements an evaluation queue, hence it can also be used to 
 * create an evaluator linked to a remote queue. Finally, the replay queue
 * enables the reevaluation of submissions on a given submission container.
 * 
 * An evaluator starts processing requests after {@code begin()}, until
 * {@code end()} terminates. Evaluation may be paused with {@code setPaused()}. 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class Evaluator extends Thread {

	private final static long DELAY = 500L; 
	private final static Logger LOGGER = Logger.getLogger("");
	private final static long SHUTDOWN_TIMEOUT = 10*60*1000;
	
	private EvaluationQueue evaluationQueue = null;
	
	private String name;
	private boolean running;
	private boolean paused;
	
	volatile private static int evaluationsInProgress = 0;
	
	/**
	 * The number of evaluations currently being processed
	 * 
	 * @return number of evaluations currently being processed
	 */
	public static int getEvaluationsInProgress() {
		return evaluationsInProgress;
	}
	
	/**
	 * Create an evaluator using local evaluation queue
	 * @throws MooshakException 
	 */
	public Evaluator(String name, EvaluationQueue evaluationQueue) 
			throws MooshakException {
		super(name);
		
		this.name = name;
		PersistentObject.openPath("data/contests");
		
		this.evaluationQueue = evaluationQueue;
	}
	
	/**
	 * Begin processing evaluation requests
	 */
	public void begin() {
		running = true;
		paused = false;
		start();
		
		info("Starting");
	}
	
	/**
	 * End processing evaluation requests
	 */
	public void end() {
		running = false;
		
		info("Ending");
	}
	
	/**
	 * Check if evaluator is running
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Check if evaluator is paused
	 */
	public boolean isPause() {
		return paused;
	}
	
	/**
	 * Pause/restart the evaluator
	 * @param paused {@code true} to pause; {@code false} to restart
	 */
	public void setPaused(boolean paused) {
		
		if(this.paused && ! paused) {
			synchronized (this) {
				this.paused = paused; // make sure to set if before notifying
				notify();
			}
			info("Resuming");
		} else if(! this.paused && paused) 
			info("Pausing");
		
		this.paused = paused; // in any event set it
	}
	
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private Runnable terminator;
	
	/**
	 * Read evaluation requests and process them
	 * Wait a while when none is available
	 */
	public void run() {
			
		info("Started");
		while(running) {
			EvaluationRequest request = null;

			if(paused)
				synchronized (this) {
					try {
						if(paused) // double check if still paused
							wait();
					} catch (InterruptedException cause) {
						warn("Waiting to resume execution",cause);
					}
				}
			if(Manager.isHostLoadAcceptable())
				try {
					request = evaluationQueue.dequeueEvaluationRequest();
				} catch (MooshakException cause) {
					error("Dequeuing evaluation request",cause);
				}
			else {
				request = null;
				// warn("Load is high! Avoiding dequeue of evaluation requests");
			}
				
			if(request == null)
				try {
					Thread.sleep(DELAY);
				} catch (InterruptedException cause) {
					warn("Error dequeuing evaluation request",cause);
				}
			else 
				switch (request.command) {
				case EVALUATE:
					info("evaluate:\t" + request.getSubmission());
					executor.execute(new EvaluationWorker(request));
					break;
				case TERMINATE:
					end();
					break;
				case EXIT:
					warn("Exit not implemented yet");
					break;
				default:
					warn("Unexpected request command:"+request.command);
					break;
				}
		}
		
		executor.shutdown();
		try {
			executor.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS);
		} catch (InterruptedException cause) {
			error("Waiting to terminate executor",cause);
		}
		
		if(terminator != null)
			terminator.run();
		
		info("Executor ended");
	}

	/**
	 * Accept a callback to run when evaluation is concluded
	 * @param terminator
	 */
	public void onEnd(Runnable terminator) {
		this.terminator = terminator;
		
	}

	
	class EvaluationWorker implements Runnable {
		EvaluationRequest request;
		Submission submission = null;
		
		EvaluationWorker(EvaluationRequest request) {
			this.request = request;
			this.submission = request.getSubmission();
		}

		public void run() {
			evaluationsInProgress++;
			
			processRequest();
			
			try {
				evaluationQueue.concludeEvaluation(submission);
			} catch (MooshakException cause) {
				error("Sending concluded evaluation",cause);
			}
			
			evaluationsInProgress--;
		}
		
		void processRequest() {
			
			try {
				submission.analyze();
			} catch(MooshakException cause) {
				submission.setObservations(cause.getMessage());
				error("Error processing submission: ", cause);	
			}
		}
	}
	
	private void info(String message) {
		LOGGER.log(Level.INFO,"Evaluator "+name+": "+message);		
	}
	
	private void warn(String message) {
		LOGGER.log(Level.WARNING,"Evaluator "+name+": "+message);		
	}
	
	
	private void warn(String message,Throwable throwable) {
		LOGGER.log(Level.WARNING,"Evaluator "+name+": "+message,throwable);		
	}
	
	private void error(String message,Throwable throwable) {
		LOGGER.log(Level.SEVERE,"Evaluator "+name+": "+message,throwable);		
	}

}
