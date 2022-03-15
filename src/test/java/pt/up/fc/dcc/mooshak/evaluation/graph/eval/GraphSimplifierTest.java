package pt.up.fc.dcc.mooshak.evaluation.graph.eval;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.evaluation.graph.GraphEvalException;
import pt.up.fc.dcc.mooshak.evaluation.graph.Parser;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;

public class GraphSimplifierTest {
	Parser parser;
	@Before
	public void setup(){
		parser = new Parser();
	}
	
	@Test
	public void testSolution() throws GraphEvalException {
		String solutionName = "useCaseDiagram1.dia";
		String fileName = "diagrams/en" + File.separator + solutionName;
		Graph solution = parser.parse(fileName);
		Graph attempt = parser.parse(fileName);
		Evaluator eval = new Evaluator(solution, attempt);
		assertEquals("Reduced to 5 nodes", 5, eval.solution.getNodes().size());
		assertEquals("Reduced to 4*2 edges", 8, eval.solution.getEdges().size());		
	}
	
	@Test
	public void testExample1() throws GraphEvalException {
		String solutionName = "useCaseDiagram1_error1.dia";
		String fileName = "diagrams/en" + File.separator + solutionName;
		Graph solution = parser.parse(fileName);
		Graph attempt = parser.parse(fileName);
		Evaluator eval = new Evaluator(solution, attempt);
		assertEquals("Reduced to 4 nodes", 4, eval.solution.getNodes().size());
		assertEquals("Reduced to 3*2 edges", 6, eval.solution.getEdges().size());		
	}
	
	@Test
	public void testExample3() throws GraphEvalException {
		String solutionName = "useCaseDiagram1_error3.dia";
		String fileName = "diagrams/en" + File.separator + solutionName;
		Graph solution = parser.parse(fileName);
		Graph attempt = parser.parse(fileName);
		Evaluator eval = new Evaluator(solution, attempt);
		assertEquals("Reduced to 5 nodes", 5, eval.solution.getNodes().size());
		assertEquals("Reduced to 4*2 edges", 8, eval.solution.getEdges().size());
	}
	
	@Test
	public void testExample4() throws GraphEvalException {
		String solutionName = "useCaseDiagram1_error4.dia";
		String fileName = "diagrams/en" + File.separator + solutionName;
		Graph solution = parser.parse(fileName);
		Graph attempt = parser.parse(fileName);
		Evaluator eval = new Evaluator(solution, attempt);
		assertEquals("Reduced to 5 nodes", 5, eval.solution.getNodes().size());
		assertEquals("Reduced to 4*2 edges", 8, eval.solution.getEdges().size());
	}
	
	@Test
	public void testExample5() throws GraphEvalException {
		String solutionName = "useCaseDiagram1_error5.dia";
		String fileName = "diagrams/en" + File.separator + solutionName;
		Graph solution = parser.parse(fileName);
		Graph attempt = parser.parse(fileName);
		Evaluator eval = new Evaluator(solution, attempt);
		eval.solution.print();
		assertEquals("Reduced to 4 nodes", 4, eval.solution.getNodes().size());
		assertEquals("Reduced to 2*2 edges", 4, eval.solution.getEdges().size());
	}
	
	@Test
	public void testExample6() throws GraphEvalException {
		String solutionName = "useCaseDiagram1_error6.dia";
		String fileName = "diagrams/en" + File.separator + solutionName;
		Graph solution = parser.parse(fileName);
		Graph attempt = parser.parse(fileName);
		Evaluator eval = new Evaluator(solution, attempt);
		assertEquals("Reduced to 2 nodes", 2, eval.solution.getNodes().size());
		assertEquals("Reduced to 1*2 edges", 2, eval.solution.getEdges().size());
	}
	
	@Test
	public void testExample7() throws GraphEvalException {
		String solutionName = "useCaseDiagram1_error7.dia";
		String fileName = "diagrams/en" + File.separator + solutionName;
		Graph solution = parser.parse(fileName);
		Graph attempt = parser.parse(fileName);
		Evaluator eval = new Evaluator(solution, attempt);
		assertEquals("Reduced to 2 nodes", 2, eval.solution.getNodes().size());
		assertEquals("Reduced to 1*2 edges", 2, eval.solution.getEdges().size());
	}
	
	

}
