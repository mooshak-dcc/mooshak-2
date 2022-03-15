
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
import pt.up.fc.dcc.mooshak.content.types.Balloon.State;

public class BalloonTest {

	Balloon loaded;
	Balloon created;

	static Path createdPath;
	
	@BeforeClass
	public static void setUpBeforeClass() 
			throws MooshakContentException, IOException {
		PersistentObject.setHome("home");
		
		createdPath =  Paths.get(CustomData.BALLOONS_PATHNAME,"SOME_BALLOON");
	}

	@Before
	public void setUp() throws Exception {
		created = PersistentObject.create(createdPath,Balloon.class);
		loaded = PersistentObject.openPath(CustomData.BALLOON_PATHNAME);
	}
	
	@After
	public void cleanUp() throws MooshakContentException {
		loaded.reopen();
		created.delete();
	}

	@Test
	public void testDate() throws MooshakContentException {
		assertEquals(1351528529,loaded.getDate().getTime()/1000);
		assertEquals(new Date(0),created.getDate());
		
		for(Date date: CustomData.DATES) {			
			loaded.setDate(date);
			assertEquals(date, loaded.getDate());
			
			created.setDate(date);
			created.save(); created.reopen();
			assertEquals(date.getTime()/1000, created.getDate().getTime()/1000);
		}
	}

	@Test
	public void testTime() throws MooshakContentException {
		assertEquals(new Date(0),created.getTime());
		assertEquals(72466529,loaded.getTime().getTime()/1000);

		for(Date date: CustomData.DATES) {
			created.setTime(date);
			created.save();	created.reopen();
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
			created.save();
			created.reopen();
			assertEquals(date.getTime()/1000, created.getDelay().getTime()/1000);

			loaded.setDelay(date);
			assertEquals(date, loaded.getDelay());
		}
	}

	@Test
	public void testProblem() throws IOException, MooshakContentException {
		Path problemsPath = loaded.open("../../problems").getPath();
		assertEquals(problemsPath.resolve("C"), loaded.getProblem().getPath());
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
	}

	@Test
	public void testState() throws MooshakContentException, IOException {
		assertEquals(State.UNDELIVERED, created.getState());
		assertEquals(State.DELIVERED, loaded.getState());

		created.save();
		assertTrue(CustomData.checkContent(created, "State", "")); // default
		
		created.setState(State.DELIVERED);
		created.save();created.reopen();
		assertEquals(State.DELIVERED, created.getState());

		assertTrue(CustomData.checkContent(created, "State", "delivered"));
	}

	@Test
	public void testSubmission() throws MooshakContentException {
		assertNull(created.getSubmission());
		assertNull("",loaded.getSubmission());

		created.setSubmission(loaded.getSubmission());
		created.save();created.reopen();
		assertEquals(loaded.getSubmission(),created.getSubmission());
	}	

	@Test
	public void testGetLine() throws MooshakContentException {
		Map<String, String> line = loaded.getRow();

		assertEquals("delivered",line.get("state"));
		assertEquals("B",line.get("problem"));
		assertEquals("myGroup",line.get("group"));
		assertEquals("108 17:35",line.get("time"));

		loaded.setTeam(null);

		line = loaded.getRow();

		assertEquals("delivered",line.get("state"));
		assertEquals("B",line.get("problem"));
		assertEquals("??",line.get("group"));
		assertEquals("108 17:35",line.get("time"));
	}
}
