package pt.up.fc.dcc.mooshak.evaluation.graph.eval;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CoreMapperTest.class, DecreasingFixedPermutationsTest.class,
		EvaluatorTest.class, GraphSimplifierTest.class,
		IncreasingFixedPermutationsTest.class,
		IncreasingVariablePermutationsTest.class, OptimizedMapperTest.class,
		SimpleMapperTest.class })
public class EvalTests {

}
