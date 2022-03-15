/**
 * 
 */
package pt.up.fc.dcc.mooshak.evaluation.kora;

import static org.junit.Assert.*;
import org.junit.Test;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * @author Helder Correia
 *
 */
public class ConfigurationTest {

	@Test
	public void test() throws MooshakException {
		//"src\\data\\CasoUsoConfig.xml
		//"src/test/pt/up/fc/dcc/mooshak/evaluation/kora/CasoUsoConfig.xml"
//		 Configuration fileConfig= new Configuration("inputs/casouso/case.xml");
//		String borderColor= fileConfig.getEshu().getStyle().getEditorStyle().getBorderColor();
//		 assertEquals("#000",borderColor);
	}
	
	@Test
	public void testConfigZip() throws MooshakException {
			Configuration configuration= new Configuration("inputs/casouso/case.zip");
			System.out.println(configuration.getDL2().getDiagram().getExtension() +" test");
			System.out.println(configuration.getDL2().getDiagram().toString());
			System.out.println(configuration.getDL2().getStyle().toString());
			System.out.println(configuration.getDL2().getDiagram().getUrlMap());
			System.out.println(configuration.getImagesSVG().toString()+"###################");
			//System.out.println(configuration.getEshu());

			//assertEquals(configuration.getEshu().getStyle().getEditorStyle().getBorderColor(), "#000");
		 
	}
	
	@Test
	public void testConfigXML() throws MooshakException {
//		 Configuration configuration= new Configuration("inputs/casouso/case.xml");
//		 Configuration configuration= new Configuration("inputs/class/class.xml");
		 Configuration configuration= new Configuration("inputs/eer/eer2.xml");
		 
			System.out.println(configuration.getDL2().getDiagram().getExtension() +" test");
			System.out.println(configuration.getDL2().getDiagram().toString());
//			System.out.println(configuration.getDL2().getStyle().toString());
//			System.out.println(configuration.getDL2().getDiagram().getUrlMap());
//			System.out.println(configuration.getImagesSVG().toString()+"###################");
//			
			System.out.println(configuration.getDL2().getDiagram().getNodeTypes().get(0).getNodeStyle());
			

			assertEquals(configuration.getDL2().getStyle().getEditorStyle().getBackground(), "white");
		 
		 
	}
}
