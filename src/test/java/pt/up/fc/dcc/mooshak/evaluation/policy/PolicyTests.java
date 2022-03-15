package pt.up.fc.dcc.mooshak.evaluation.policy;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	ExamRankingPolicyTest.class,
	IcpcRankingPolicyTest.class, 
	IoiRankingPolicyTest.class,
	RankingPolicyTest.class
})
public class PolicyTests {

}
