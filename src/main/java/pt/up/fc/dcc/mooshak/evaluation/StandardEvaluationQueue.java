package pt.up.fc.dcc.mooshak.evaluation;

import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PathManager;
import pt.up.fc.dcc.mooshak.content.PersistentObjectListener;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.managers.RowManager;
import pt.up.fc.dcc.mooshak.server.events.EventSender;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.events.Recipient;
import pt.up.fc.dcc.mooshak.shared.events.ReportNotificationEvent;

/**
 * Standard Evaluation Queue for enqueuing evaluation request sent by 
 * the participant managers.
 * It is used either by Evaluator or MooshakMaster
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 * @version 2.0
 * @since July 2013
 *
 */
public class StandardEvaluationQueue implements EvaluationQueue  {
	
	private static final int INITIAL_CAPACITY = 11;
	
	private static final int CREATE_EVALUATOR_THRESHOLD = 10;
	private static final int REMOVE_EVALUATOR_THRESHOLD = 5;

	private static final Logger LOGGER = Logger.getLogger("");
	
	private static final EventSender EVENT_SENDER=EventSender.getEventSender();
	
	PriorityQueue<EvaluationRequest> queue = new PriorityQueue<>(INITIAL_CAPACITY);
	
	private StandardEvaluationQueue() {
		super();
		
		// make sure that submissions processed outside are finalized here 
		PathManager.getInstance().addPersistentObjectListener(Submission.class, 
				new PersistentObjectListener<Submission>() {

					@Override
					public void receivedPersistentObject(Submission submission)
							throws MooshakContentException {
						concludeEvaluation(submission);
						
					}
				});
		
	}
	
	static StandardEvaluationQueue instance = null;
	
	/**
	 * Get singleton of this evaluation queue 
	 * @return
	 * @throws MooshakException on RMI Exception
	 */
	public static synchronized StandardEvaluationQueue getInstance() 
			throws MooshakException {
		if(instance == null)
				instance = new StandardEvaluationQueue();
		return instance;
	}

	
	@Override
	synchronized public EvaluationRequest dequeueEvaluationRequest() {
		return queue.poll();
	}

	@Override
	synchronized public void enqueueEvaluationRequest(EvaluationRequest request) {
		
		if(! queue.offer(request))
			LOGGER.log(Level.SEVERE,"Evaluation queue cannot hold more requests");
		
		int size = queue.size();
		if(size > CREATE_EVALUATOR_THRESHOLD) {
			// TODO create remote evaluator
		} else if(size < REMOVE_EVALUATOR_THRESHOLD) {
			// queue.add(new EvaluationRequest(Command.terminate));
		}
	}


	@Override
	public void concludeEvaluation(Submission submission) {
		
		if(submission == null)
			LOGGER.log(Level.WARNING,"Evaluation concluded with null submission");
		else {
			notifyParticipant(submission);

			if(submission.isConsider()) {			
				RowManager.getInstance().broadcast(submission);

				try {
					Contest contest = submission.getContest();
					
					if(contest != null)
						contest.getRankingPolicy().addSubmission(submission);
				} catch (MooshakException cause) {
					LOGGER.log(Level.SEVERE,"Cannot rank submission",cause);
				}
			}

		}		
	}

	
	/**
	 * send report notification just to this team/participant
	 */
	private void notifyParticipant(Submission submission) {
		ReportNotificationEvent reportEvent = new ReportNotificationEvent();
		
		reportEvent.setRecipient( new Recipient(
				submission.getTeamId(),
				submission.getSessionId()));
		reportEvent.setSubmissionId(submission.getIdName());
		reportEvent.setProblemId(submission.getProblemId());
		reportEvent.setConsider(submission.isConsider());
		EVENT_SENDER.send(submission.getContestId(),reportEvent);
	}

}
