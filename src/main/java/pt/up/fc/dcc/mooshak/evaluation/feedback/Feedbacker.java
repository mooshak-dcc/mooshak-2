package pt.up.fc.dcc.mooshak.evaluation.feedback;

import java.util.List;

import pt.up.fc.dcc.mooshak.evaluation.ProgramAnalyzer.TestRun;

/**
 * Generators of feedback for a given team/student and problem.
 * Given a list of test runs they produce information to the user 
 * */
public interface Feedbacker {

	public String summarize(List<TestRun> runs);
}
