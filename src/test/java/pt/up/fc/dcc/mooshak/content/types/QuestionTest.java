package pt.up.fc.dcc.mooshak.content.types;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Question.State;

public class QuestionTest {

	Question loaded;
	Question created;
	
	static Path createdPath;
	
	@BeforeClass
	public static void setUpBeforeClass() 
			throws MooshakContentException, IOException {
		PersistentObject.setHome("home");
		
		createdPath = Paths.get(CustomData.QUESTIONS_PATHNAME,"A_QUESTION");
	}
	
	@Before
	public void setUp() throws Exception {
		created = PersistentObject.create(createdPath,Question.class);
		loaded = PersistentObject.openPath(CustomData.QUESTION_PATHNAME);
	}
	
	@After
	public void cleanUp()  throws Exception {
		loaded.reopen();
		created.delete();
	}

	@Test
	public void testDate() throws MooshakContentException {
		assertEquals(new Date(0),created.getDate());
		assertEquals(1156937072,loaded.getDate().getTime()/1000);

		for(Date date: CustomData.DATES) {
			created.setDate(date);
			created.save(); created.reopen();
			assertEquals(date.getTime()/1000, created.getDate().getTime()/1000);

			loaded.setDate(date);
			assertEquals(date, loaded.getDate());
		}
	}
	
	@Test
	public void testTime() throws MooshakContentException {
		assertEquals(new Date(0),created.getTime());
		assertEquals(78593732,loaded.getTime().getTime()/1000);

		for(Date date: CustomData.DATES) {
			created.setTime(date);
			created.save(); created.reopen();
			assertEquals(date.getTime()/1000, created.getTime().getTime()/1000);

			loaded.setTime(date);
			assertEquals(date, loaded.getTime());	
		}
	}
	
	@Test
	public void testDelay() throws MooshakContentException {
		assertEquals(new Date(0),created.getDelay());
		assertEquals(new Date(0),loaded.getDelay());

		for(Date date: CustomData.DATES) {
			created.setDelay(date);
			created.save(); created.reopen();
			assertEquals(date.getTime()/1000, created.getDelay().getTime()/1000);

			loaded.setDelay(date);
			assertEquals(date, loaded.getDelay());
		}
	}
	
	@Test
	public void testProblem() throws IOException, MooshakContentException {
		Problems problems = loaded.open("../../problems");
		assertEquals(problems.getPath().resolve("C"), 
				loaded.getProblem().getPath());
		
		Problem problemJ = problems.open("J");		
		loaded.setProblem(problemJ);
		
		assertEquals(problemJ,loaded.getProblem());
		
		loaded.setProblem(null);
		assertNull(loaded.getProblem());
	}
	
	@Test
	public void testTeam() throws MooshakContentException, IOException {
		Team team = (Team) loaded.getTeam();
		
		assertEquals("team",team.getPath().getFileName().toString());
		assertEquals(null,created.getTeam());
		
		Groups groups = loaded.open("../../groups");
		Team copy = groups.find("team");
		
		loaded.setTeam(copy);
		assertEquals(copy,loaded.getTeam());	
		
		loaded.setTeam(null);
		assertNull(loaded.getTeam());
	}
	
	@Test
	public void testState() throws MooshakContentException, IOException {
		assertEquals(State.UNANSWERED, created.getState());
		assertEquals(State.WITHOUT_ANSWER, loaded.getState());
		
		for(State state: State.values()) {
			created.setState(state);
			created.save(); created.reopen();
			assertEquals(state, created.getState());
			
			assertTrue(CustomData.checkContent(created, "State", 
					state.toString().toLowerCase()));
		}
	}
	
	@Test
	public void testSubject() throws MooshakContentException {
		assertEquals("", created.getSubject());
		assertEquals("Teste", loaded.getSubject());

		for(String text: CustomData.TEXTS) {
			created.setSubject(text);
			created.save(); created.reopen();
			assertEquals(text, created.getSubject());
		}

	}	

	@Test
	public void testQuestion() throws MooshakContentException {
		assertEquals("", created.getQuestion());
		assertEquals("Hello", loaded.getQuestion());

		for(String text: CustomData.TEXTS) {
			created.setQuestion(text);
			created.save(); created.reopen();
			assertEquals(text, created.getQuestion());
		}
	}

	@Test
	public void testAnswer() throws MooshakContentException {
		assertEquals("", created.getAnswer());
		assertEquals("Hi", loaded.getAnswer());

		for(String text: CustomData.TEXTS) {
			created.setAnswer(text);
			created.save(); created.reopen();
			assertEquals(text, created.getAnswer());
		}
	}
	
	@Test
	public void testGetLine() throws MooshakContentException {
		Map<String, String> line = loaded.getRow();
		
		assertEquals("Teste",line.get("subject"));
		assertEquals("without_answer",line.get("state"));
		assertEquals("B",line.get("problem"));
		assertEquals("myGroup",line.get("group"));
		assertEquals("179 15:35",line.get("time"));
		
		loaded.setTeam(null);
		
		line = loaded.getRow();
		
		assertEquals("Teste",line.get("subject"));
		assertEquals("without_answer",line.get("state"));
		assertEquals("B",line.get("problem"));
		assertEquals("??",line.get("group"));
		assertEquals("179 15:35",line.get("time"));
	}
}
