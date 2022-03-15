package pt.up.fc.dcc.mooshak.evaluation.graph;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.DataTests;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.EvalTests;

@RunWith(Suite.class)
@SuiteClasses({
		EvalTests.class,
		DataTests.class,
		ControllerTest.class, DoubleIterator.class,
		GraphGeneratorTest.class, GraphTest.class, MainTest.class,
		MessengerTest.class, NodeTest.class, PropertyValueTest.class,
		RelatedGraphsTest.class
})
public class GraphTests {

}
