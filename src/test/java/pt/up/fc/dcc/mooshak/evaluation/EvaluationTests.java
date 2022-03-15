package pt.up.fc.dcc.mooshak.evaluation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import pt.up.fc.dcc.mooshak.evaluation.policy.PolicyTests;
import pt.up.fc.dcc.mooshak.evaluation.special.feedback.IncrementalFeedbackerTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	EvaluatorTest.class,
	PolicyTests.class,
	ProgramAnalyzerTest.class,
	SafeExecutionTest.class,
	IncrementalFeedbackerTest.class,
	ReporterTest.class,
	StandardEvaluationQueueTest.class
	})
public class EvaluationTests {

}




