package pt.up.fc.dcc.mooshak.managers;

import java.nio.file.Path;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Authenticable;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Profile;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.content.types.CustomData;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseList;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.StudentProfile;

public class EnkiManagerTest {

	EnkiManager manager = EnkiManager.getInstance();
	
	@BeforeClass
	public static void setUp() {
		EnkiManager.setGamificationServer("https://mooshak2.dcc.fc.up.pt/odin/");
		EnkiManager.setSequenciationServer("http://mooshak2.dcc.fc.up.pt/seqins/");
	}
	
	@Test
	public void testProblem() throws MooshakException {
		Contest contest = PersistentObject.openPath("home/data/contests/proto_enki/");
		Session session = new Session();
		
		session.setContest(contest);
		session.setParticipant(new Authenticable() {
			
			@Override
			public Profile getProfile() throws MooshakContentException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Path getPath() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getName() throws MooshakContentException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getIdName() throws MooshakContentException {
				return "jccp";
			}
			
			@Override
			public boolean authentic(String password) throws MooshakContentException {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		CourseList resources = manager.getResourcesForStudent(session);
		
		System.out.println(resources.getCourses().toString());
	}
	
	@Test
	public void testGetStudentProfile() throws MooshakException {
		Contest contest = PersistentObject.openPath("home/data/contests/proto_enki/");
		
		Session session = new Session();
		
		session.setContest(contest);
		session.setParticipant(PersistentObject.openPath("home/data/contests/proto_enki/" 
				+ "groups/myGroup/" + CustomData.TEAM_ID));
		
		StudentProfile profile = manager.getStudentProfile(session, "c101");
		Assert.assertEquals("Sun Aug 23 10:58:01 WEST 2015", 
				profile.getRegistrationDate().toString());
		Assert.assertEquals(0, profile.getSolvedExercises());
		Assert.assertEquals(0, profile.getStaticResourcesSeen());
		Assert.assertEquals(0, profile.getVideoResourcesSeen());
		Assert.assertEquals(0, profile.getAcceptedSubmissions());
		Assert.assertEquals(0, profile.getSubmissions());
		Assert.assertEquals("Variáveis", profile.getCurrentPart());
	}
}
