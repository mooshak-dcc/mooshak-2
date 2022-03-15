package pt.up.fc.dcc.mooshak.evaluation.special.feedback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static pt.up.fc.dcc.mooshak.content.types.Submission.Classification.WRONG_ANSWER;
import static pt.up.fc.dcc.mooshak.evaluation.feedback.IncrementalFeedbacker.Lang.EN;
import static pt.up.fc.dcc.mooshak.evaluation.feedback.IncrementalFeedbacker.SummaryFormat.COUNT;
import static pt.up.fc.dcc.mooshak.evaluation.feedback.IncrementalFeedbacker.SummaryFormat.COUNT_1;
import static pt.up.fc.dcc.mooshak.evaluation.feedback.IncrementalFeedbacker.SummaryFormat.OBSERVATION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.evaluation.ProgramAnalyzer;
import pt.up.fc.dcc.mooshak.evaluation.ProgramAnalyzer.TestRun;
import pt.up.fc.dcc.mooshak.evaluation.feedback.IncrementalFeedbacker;

public class IncrementalFeedbackerTest {
	IncrementalFeedbacker feedbacker;
	
	@Before
	public void setUp() throws Exception {
		feedbacker = new IncrementalFeedbacker();
	}

	@Test
	public void test() {
		List<TestRun> runs = new ArrayList<>();
		List<String> feedback;
		String observation = "This is not an observation";
		
		runs.add(makeRun(WRONG_ANSWER,observation));
		
		feedback = Arrays.asList(feedbacker.summarize(runs).split("\\n"));
		
		assertEquals(1,feedback.size());
		assertEquals(OBSERVATION.format(EN,WRONG_ANSWER.toString(),observation),
				feedback.get(0));
		
		// no new feedback available
		assertNull(feedbacker.summarize(runs));
		
		runs.add(makeRun(WRONG_ANSWER,observation));
		feedback =  Arrays.asList(feedbacker.summarize(runs).split("\\n"));
		
		assertEquals(2,feedback.size());
		assertEquals(OBSERVATION.format(EN,WRONG_ANSWER.toString(),observation),
				feedback.get(0));
		assertEquals(COUNT_1.format(EN,WRONG_ANSWER.toString(),observation),
				feedback.get(1));
		
		runs.add(makeRun(WRONG_ANSWER,observation));
		feedback =  Arrays.asList(feedbacker.summarize(runs).split("\\n"));
		assertEquals(2,feedback.size());
		assertEquals(OBSERVATION.format(EN,WRONG_ANSWER.toString(),observation),
				feedback.get(0));
		assertEquals(COUNT.format(EN,2,WRONG_ANSWER.toString()),
				feedback.get(1));
	}
	
	
	Submission submission = new Submission();
	ProgramAnalyzer analyzer = new ProgramAnalyzer(submission);
	
	TestRun makeRun(Classification classification,String observations) {
		TestRun run = analyzer.new TestRun();
		
		run.classification = classification;
		run.observations = observations;
		return run;
	}
	

}
