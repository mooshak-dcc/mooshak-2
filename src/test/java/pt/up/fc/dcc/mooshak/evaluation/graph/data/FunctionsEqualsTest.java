package pt.up.fc.dcc.mooshak.evaluation.graph.data;

import static org.junit.Assert.*;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Configs;


public class FunctionsEqualsTest {
	public Configs configs = Configs.getDefaultConfigs();
	

	@Test
	public void compositePropertyNameTest() {
		GraphEER graphs = new GraphEER();
		assertEquals(
				"Comparison between two composite property name with same properties",
				true, graphs.propertyCardiName1S.equals(graphs.propertyCardiName1A,graphs.map));
	}
	
	@Test
	public void compositePropertyNameTest2() {
		
		GraphEER graphs = new GraphEER();
		assertEquals(
				"Comparison between two composite property name with different properties",
				false, graphs.propertyCardiName1S.equals(graphs.propertyCardiName2A,graphs.map));
	}
	
	@Test
	public void simplePropertyNameTest() {
		
		GraphEER graphs = new GraphEER();
		assertEquals(
				"Comparison between two simple property name with same name",
				true, graphs.simplePropertyCardiName1S.equals(graphs.simplePropertyCardiName1A));
	}
	
	
	@Test
	public void compositePropertyValueTest() {
		GraphEER graphs = new GraphEER();
		assertEquals(
				"Comparison between two composite property value with same properties",
				true, graphs.propertyCardiValue1S.equals(graphs.propertyCardiValue1A));
	}
	
	
	@Test
	public void simplePropertyValueTest() {
		GraphEER graphs = new GraphEER();
		assertEquals(
				"Comparison between two simple property value with same properties",
				true, graphs.simplePropertyCardiValue1S.equals(graphs.simplePropertyCardiValue1A));
	}
	
	
	@Test
	public void CompareNodeWithoutPropertyTest() {
		GraphEER graphs = new GraphEER();
		assertEquals(
				"Comparison between two nodes differents ",
				false, graphs.node1S.equals(graphs.node1A));
	}
	
	@Test
	public void CompareNodeWithPropertyTest() {
		GraphEER graphs = new GraphEER();
		System.out.println(graphs.node6S.compareNode(graphs.node6A,configs));
		assertEquals(
				"Comparison between two nodes with same property ",
				6, graphs.node6S.compareNode(graphs.node6A,configs).getGrade());
	}
	
	@Test
	public void EdgeEqualsTest() {
		GraphEER graphs = new GraphEER();
		assertEquals(
				"Comparison between two edges equals in the attempt and solution ",
				26, graphs.edge1S.compareEdge(graphs.edge1A,configs, graphs.map).getGrade());
	}
	
	@Test
	public void EdgeEqualsTest2() {
		GraphEER graphs = new GraphEER();
		assertEquals(
				"Comparison between two edges differents ",
				18, graphs.edge3S.compareEdge(graphs.edge2A,configs, graphs.map).getGrade());
	}
	
	
	@Test
	public void EdgeEqualsSimplePropertyTest() {
		GraphEER graphs = new GraphEER();
		assertEquals(
				"Comparison between two edges same simple property ",
				22, graphs.edge4S.compareEdge(graphs.edge4A,configs, graphs.map).getGrade());

	}
	
	
//	@Test
//	public void test() {
//		GraphEER graphs = new GraphEER();
//		
//	}
	
	
}


