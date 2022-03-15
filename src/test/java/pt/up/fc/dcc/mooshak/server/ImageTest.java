package pt.up.fc.dcc.mooshak.server;

import static org.junit.Assert.*;

import java.nio.file.Path;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.CustomData;

public class ImageTest {

	Image image;
	
	@BeforeClass
	public static void setUpBeforeClass() throws MooshakContentException {
		PersistentObject.setHome(CustomData.HOME);
	}
	
	@Before
	public void setUp() throws Exception {
		image = new Image();
	}

	
	@Test
	public void testGetImageAbsolutePath() throws MooshakContentException {
		Contest contest = PersistentObject.openPath(CustomData.CONTEST_PATHNAME);
	
		Path path = image.getImageAbsolutePath(contest, "J", "buddy_fig.gif");
	
		assertTrue(path.endsWith(
				"data/contests/proto_icpc/problems/J/images/buddy_fig.gif"));
	
	}

}
