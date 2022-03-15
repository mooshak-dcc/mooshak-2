package pt.up.fc.dcc.mooshak.evaluation.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.json.JSONException;
import org.junit.Test;

public class ControllerTest {

	@Test
	public void test() throws IOException {
		String filePath = "inputs/fcup.json";
		try {
			Controller controller = new Controller(filePath, filePath, "EER");
			//System.out.println(controller.getEvaluation().getGrade());
			assertEquals(100.0,controller.getEvaluation().getGrade(),0);
		} catch (JSONException e) {
			fail("unexpected exception");
		}
	}
	
	@Test
	public void testFcup2() throws IOException {
		String filePath = "inputs/fcup.json";
		String filePath2 = "inputs/fcup2.json";
		try {
			Controller controller = new Controller(filePath, filePath2, "EER");
			// System.out.println(controller.getEvaluation().getGrade());
			assertEquals(87.07,controller.getEvaluation().getGrade(),0.01);
		} catch (JSONException e) {
			fail("unexpected exception");
		}
	}
	
	@Test
	public void testFcup3() throws IOException {
		String filePath = "inputs/fcup.json";
		String filePath2 = "inputs/fcup3.json";
		try {
			Controller controller = new Controller(filePath, filePath2, "EER");
			//System.out.println(controller.getEvaluation().getGrade());
			assertEquals(99.58,controller.getEvaluation().getGrade(),0.01);
		} catch (JSONException e) {
			fail("unexpected exception");
		}
	}
	
	@Test
	public void testFcup4() throws IOException {
		String filePath = "inputs/fcup.json";
		String filePath2 = "inputs/fcup4.json";
		try {
			Controller controller = new Controller(filePath, filePath2, "EER");
			// System.out.println(controller.getEvaluation().getGrade());
			assertEquals(97.94,controller.getEvaluation().getGrade(),0.01);
		} catch (JSONException e) {
			fail("unexpected exception");
		}
	}
	
	@Test
	public void testFcup5() throws IOException {
		String filePath = "inputs/fcup.json";
		String filePath2 = "inputs/fcup5.json";
		try {
			Controller controller = new Controller(filePath, filePath2, "EER");
			// System.out.println(controller.getEvaluation().getGrade());
			assertEquals(96.62,controller.getEvaluation().getGrade(),0.01);
		} catch (JSONException e) {
			fail("unexpected exception");
		}
	}
	
	@Test
	public void testFcup6() throws IOException {
		String filePath = "inputs/fcup.json";
		String filePath2 = "inputs/fcup6.json";
		try {
			Controller controller = new Controller(filePath, filePath2, "EER");
			// System.out.println(controller.getEvaluation().getGrade());
			assertEquals(94.20,controller.getEvaluation().getGrade(),0.01);
		} catch (JSONException e) {
			fail("unexpected exception");
		}
	}
	
	@Test
	public void testFcup7() throws IOException {
		//AVALIACAO INCOMPLETA
		String filePath = "inputs/fcup.json";
		String filePath2 = "inputs/fcup7.json";
		try {
			Controller controller = new Controller(filePath, filePath2, "EER");
			System.out.println(controller.getEvaluation().getGrade());
			assertEquals(8.64,controller.getEvaluation().getGrade(),0.01);
		} catch (JSONException e) {
			fail("unexpected exception");
		}
	}
	
	@Test
	public void testFcup_1() throws IOException {
		String filePath = "inputs/fcup.json";
		String filePath2 = "inputs/FCUP_1.json";
		try {
			Controller controller = new Controller(filePath, filePath2, "EER");
//			System.out.println(controller.getEvaluation().getGrade());
//			assertEquals(8.64,controller.getEvaluation().getGrade(),0.01);
			System.out.println(controller.getJsonFeedback());
		} catch (JSONException e) {
			fail("unexpected exception");
		}
	}
	
	@Test
	public void testExemple() throws IOException {
//		String filePath = "inputs/fcup.json";
//		String filePath2 = "inputs/diagram.json";
		
		String filePath = "inputs/eer/empresaSolution.eer";
		String filePath2 = "inputs/eer/empresaAttempt.eer";
		
		try {
			Controller controller = new Controller(filePath, filePath2, "EER");
//			System.out.println(controller.getEvaluation().getGrade());
//			assertEquals(8.64,controller.getEvaluation().getGrade(),0.01);
//			System.out.println(controller.getJsonFeedback());
			System.out.println(controller.getTextualFeeback());
		} catch (JSONException e) {
			fail("unexpected exception");
		}
	}

}
