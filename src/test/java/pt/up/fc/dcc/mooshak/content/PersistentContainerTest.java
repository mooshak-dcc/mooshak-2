package pt.up.fc.dcc.mooshak.content;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.FileDescriptors;
import pt.up.fc.dcc.mooshak.content.PersistentContainer.POStream;
import pt.up.fc.dcc.mooshak.content.types.Group;
import pt.up.fc.dcc.mooshak.content.types.Groups;
import pt.up.fc.dcc.mooshak.content.types.Language;
import pt.up.fc.dcc.mooshak.content.types.Languages;
import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.content.types.Team;
import pt.up.fc.dcc.mooshak.content.types.CustomData;

public class PersistentContainerTest {

	Groups groups;
	Languages languages;
	Submissions submissions;
	

	@BeforeClass
	public static void setUpBeforeClass() throws MooshakContentException {
		PersistentObject.setHome(CustomData.HOME);
	}
	
	@Before
	public void setUp() throws Exception {
		groups= PersistentObject.openPath(CustomData.GROUPS_PATHNAME);
		languages=PersistentObject.openPath(CustomData.LANGUAGES);
		submissions=PersistentObject.openPath(CustomData.SUBMISSIONS_PATHNAME);
	}
	
	@Test
	public void testGetContainedClass() {
		assertEquals(Team.class,groups.getContainedClass());
		assertEquals(Language.class,languages.getContainedClass());
	}

	@Test
	public void testGetContent() throws MooshakContentException {
		
		Set<Language> expected = getLanguages();
		Set<Language> obtained = new HashSet<>(languages.getContent());
		
		assertEquals(expected,obtained);
	}
	
	@Test
	public void testFindLanguages() throws MooshakContentException {

		for(String name: CustomData.LANGUAGE_NAMES)
			assertEquals(languages.open(name),languages.find(name));
		
	}
	
	@Test
	public void testFindTeam() throws MooshakContentException {

			assertEquals(groups.open("myGroup/team"),groups.find("team"));		
	}
	
	@Test
	public void testTimeFindTeam() throws MooshakContentException {
		
		timeFind(groups,"team",1);
		assertTrue(timeFind(groups,"team",1000)<1);
	}
	
	
	@Test
	public void testFindContaineddWithinContainerWithSameName() 
			throws MooshakContentException {
		String name = "Duplicate";
	
		Group group = groups.create(name, Group.class);
		Team team   = group.create(name, Team.class);
		
		PersistentObject found = groups.find(name);
		
		assertEquals(Team.class,found.getClass());
		assertTrue( found == team );
		
		group.delete();
	}
	

	@Test
	public void testTimeFindSubmission() throws MooshakContentException {
		
		final Path path = Paths.get(CustomData.SUBMISSION_PATHNAME);
		final String name = path.getName(path.getNameCount()-1).toString();
		
		timeFind(submissions,name,1);
		assertTrue(timeFind(groups,name,1000)<1);
	}
	
	private long timeFind(PersistentContainer<?> container,String id, int n)
			throws MooshakContentException {
		Date start = new Date();
		for(int c=0; c<n; c++)
			container.find(id);
		Date now = new Date();
		return (now.getTime()-start.getTime())/n;
	}
	
	
	@Test
	public void testIterableLanguages() throws Exception {
		
		Set<Language> expected = new HashSet<>(languages.getContent());
		Set<Language> obtained = new HashSet<>();
		
		expected = getLanguages();
		
		try(POStream<Language> stream = languages.newPOStream()) {
			for(Language language: stream)
				obtained.add(language);
		}
		
		assertEquals(expected,obtained);
	}
	
	@Test
	public void testIterableGroups() throws Exception {
	
		Set<Team> expected = getTeams();
		
		Set<Team> obtained = new HashSet<>();
		
		try(POStream<Team> stream = groups.newPOStream()) {
			for(Team team: stream)
				obtained.add(team);
		}
		
		assertEquals(expected,obtained);
	}
	
	@Test
	public void testIterableFileLeak() throws Exception {
		
		try(POStream<Language> stream = languages.newPOStream()) {
			for(@SuppressWarnings("unused") Language language: stream) {
				break;
			}	
		}
		assertEquals(0,FileDescriptors.countUnusual());
	}
	
	
	private Set<Team> getTeams() 
			throws MooshakContentException {
		Set<Team>  set = new Populator<Team>().populate(
				"data/contests/proto_icpc/groups/Default",
				new String[] {"jpaiva"}
				);
		
		set.addAll (new Populator<Team>().populate(
				CustomData.GROUP_PATHNAME,
				new String[] {"team"}
				));
		
		return set;
	}
	
	private Set<Language> getLanguages() 
			throws MooshakContentException {
		return new Populator<Language>().populate(
				CustomData.LANGUAGES,CustomData.LANGUAGE_NAMES);
	}
	
	private static class Populator<T extends PersistentObject> {
		
		Set<T> populate(String prefix,String ... names) 
				throws MooshakContentException {
			Set<T> expected = new HashSet<>();
		
			for(String name: names) {
				Path path = Paths.get(prefix,name);
				T lang = PersistentObject.open(path);
				expected.add(lang);
			}
			return expected;
	 	}
	}

}
