package pt.up.fc.dcc.mooshak.evaluation.feedback;

import java.util.List;

import pt.up.fc.dcc.mooshak.content.types.Submissions.FeedbackCategories;
import pt.up.fc.dcc.mooshak.evaluation.ProgramAnalyzer.TestRun;


/**
 * Return a feedbacker of given type for a team and problem.
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class FeedbackerFactory {

	// default feedback that produces null feedback (to be ignored)
	private static final Feedbacker DEFAULT_FEEDBACKER = new Feedbacker() {

		@Override
		public String summarize(List<TestRun> runs) {
			return null;
		}};
	
	private static FeedbackerFactory feedbackerFactory = new FeedbackerFactory();
		
	public static FeedbackerFactory getInstance() {
		return feedbackerFactory;
	}

	private FeedbackerFactory() {}
		
	public Feedbacker getFeedbacker(
			FeedbackCategories category,
			String team,
			String problem) {
		
		switch(category) {
		case REPORT:
			return IncrementalFeedbacker.getFeedbacker(team, problem);
		case ALL:
			return  GlobalFeedbacker.getInstance();
		default:
			return DEFAULT_FEEDBACKER;
		}
	}
}
