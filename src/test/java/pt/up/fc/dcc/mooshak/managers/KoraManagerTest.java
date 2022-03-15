/**
 * 
 */
package pt.up.fc.dcc.mooshak.managers;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * @author User
 *
 */
public class KoraManagerTest {

	public  KoraManager koraManager ;
	@Test
	public void test() {
		fail("Not yet implemented");
	}

	
	@Test
	public void testGetEshuConfiguration() throws MooshakException, IOException {
		koraManager =KoraManager.getInstance();
		Contest contest = PersistentObject.openPath("home/data/contests/proto_diagram/");
		Session session = new Session();
		
		session.setContest(contest);
		System.out.println(koraManager.getEshuConfiguration(session,"id").getTextBox());
		System.out.println(koraManager.getEshuConfiguration(session,"id").getEditorStyle());
		System.out.println(koraManager.getEshuConfiguration(session,"id").getVertice());
		System.out.println(koraManager.getEshuConfiguration(session,"id").getEditorStyle());
		System.out.println(koraManager.getEshuConfiguration(session,"id").getNodeTypes());
		System.out.println(koraManager.getEshuConfiguration(session,"id").getEdgeTypes());
		System.out.println(koraManager.getEshuConfiguration(session,"id").getImagesSVG().size());
////		
		
		
	//assertEquals("home/data/contests/proto_diagram/languages/Diagram/Caso.xml", );	
		
		 
		
	}
	
	
	@Test
	public void testGetFiles() throws MooshakException, IOException {
		koraManager =KoraManager.getInstance();
		String path ="inputs/caso.zip";
//		Map<String, String> imagesMap=koraManager.getImageSVG(path);
//		//System.out.println(imagesMap.size());
//		assertEquals(imagesMap.size(), 10);
		
	}
	
}
