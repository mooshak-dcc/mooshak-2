package pt.up.fc.dcc.mooshak.content;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.Attributes.Attribute;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

public class AttributesTest {


	private static Attribute consider;
	private static Attribute date;
	private static Attribute problem;
	private static Attribute team;
	
	private static Attribute showErrors;

	@BeforeClass
	public static void setUpClass() throws Exception {
		consider 	= Attributes.getAttribute(Submission.class, "Consider");
		date 		= Attributes.getAttribute(Submission.class, "Date");
		problem 	= Attributes.getAttribute(Submission.class, "Problem");
		team 	= Attributes.getAttribute(Submission.class, "Team");
		
		showErrors 	= Attributes.getAttribute(Submissions.class, "Show_errors");
	}

	
	@Test
	public void testAttributeGetNameOfConsider() {
		
		assertEquals("Consider",consider.getName());
	}

	@Test
	public void testAttributeGetNameOfDate() {
		
		assertEquals("Date",date.getName());
	}
	
	@Test
	public void testAttributeGetNameOfProblem() {
		
		assertEquals("Problem",problem.getName());
	}

	
	@Test
	public void testAttributeGetTypeMenuOfConsider() {
		
		assertEquals(AttributeType.MENU,consider.getType());
	}

	@Test
	public void testAttributeGetTypeMenuOfDate() {
		
		assertEquals(AttributeType.DATE,date.getType());
	}
	
	@Test
	public void testAttributeGetTypeMenuOfProblem() {
		
		assertEquals(AttributeType.PATH,problem.getType());
	}

	
	@Test
	public void testAttributeGetPossibleValuesOfConsider() {
		
		List<String> expected = Arrays.asList("yes","no");
		assertEquals(expected,consider.getPossibleValues());
	}

	
	@Test
	public void testAttributeGetPossibleValuesOfDate() {
		
		assertEquals(null,date.getPossibleValues());
	}
	
	@Test
	public void testAttributeGetPossibleValuesOfProblem() {
		
		// empty list, signaling that it has a relative complement 
		assertTrue(problem.getPossibleValues().size() == 0);
	}
	
	@Test
	public void testAttributeGetPossibleValuesOfShowErrors() {
		List<String> expected = new ArrayList<>();
		
		for(Classification classification: Classification.values()) 
			expected.add(classification.toString());
		
		assertEquals(expected,showErrors.getPossibleValues());
	}
	
	@Test
	public void testGetComplement() {
		assertEquals("../../problems",problem.getComplement());
		assertEquals("../../groups/*",team.getComplement());
	}
	
	@Test
	public void testGetComplementBase() {
		assertEquals("../../problems",problem.getComplementBase());
		assertEquals("../../groups",team.getComplementBase());
	}
	
	
}
