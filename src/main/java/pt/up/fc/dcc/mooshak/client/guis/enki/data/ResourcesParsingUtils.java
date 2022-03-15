package pt.up.fc.dcc.mooshak.client.guis.enki.data;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

import pt.up.fc.dcc.mooshak.shared.results.sequencing.Course;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseList;

public class ResourcesParsingUtils {
	/*private static final List<String> ALLOWED_VIDEO_EXTENSIONS = Arrays.asList(
			"mp4", "avi");*/

	/**
	 * Parses the resources from a xml message
	 * 
	 * @param message
	 * @return
	
	public static CourseList parseXmlResource(String message) {

		CourseList resources = new CourseList();

		// parse the XML document into a DOM
		Document messageDom = XMLParser.parse(message);

		Course course = parseXmlCourse(messageDom.getDocumentElement());
		resources.addCourse(course);		

		return resources;
	} */

	/**
	 * Parse xml Course node
	 * @param courseNode
	 * @return
	 
	private static Course parseXmlCourse(Node courseNode) {
		String id = courseNode.getAttributes().getNamedItem("id")
				.getNodeValue();
		String name = courseNode.getAttributes().getNamedItem("name")
				.getNodeValue();
		Course course = new Course(id, name);

		NodeList modules = ((Element) courseNode).getElementsByTagName("Module");
		for (int i = 0; i < modules.getLength(); i++) {
			ModuleType module = parseXmlModule(modules.item(i));
			course.addModule(module);
		}

		return course;
	}*/

	/**
	 * Parse xml Module node
	 * @param moduleNode
	 * @return
	 
	private static ModuleType parseXmlModule(Node moduleNode) {
		String id = moduleNode.getAttributes().getNamedItem("id")
				.getNodeValue();
		String name = moduleNode.getAttributes().getNamedItem("name")
				.getNodeValue();
		ModuleType module = new ModuleType(id, name);

		NodeList topics = ((Element) moduleNode).getElementsByTagName("Topic");
		for (int i = 0; i < topics.getLength(); i++) {
			TopicType topic = parseXmlTopic(topics.item(i));
			module.addTopic(topic);
		}

		return module;
	}*/

	/**
	 * Parse xml Topic node
	 * @param topicNode
	 * @return
	 
	private static TopicType parseXmlTopic(Node topicNode) {
		String id = topicNode.getAttributes().getNamedItem("id").getNodeValue();
		String name = topicNode.getAttributes().getNamedItem("name")
				.getNodeValue();
		TopicType topic = new TopicType(id, name);

		NodeList resources = ((Element) topicNode).getElementsByTagName("Resource");
		for (int i = 0; i < resources.getLength(); i++) {
			ResourceType resource = parseXmlResource(resources.item(i));
			topic.addResource(resource);
		}

		NodeList problems = ((Element) topicNode).getElementsByTagName("PROBLEM");
		for (int i = 0; i < problems.getLength(); i++) {
			Resource resource = parseXmlProblem(problems.item(i));
			topic.addResource(resource);
		}

		return topic;
	}*/

	/**
	 * Parse xml Resource node
	 * @param resourceNode
	 * @return
	 
	public static ResourceType parseXmlResource(Node resourceNode) {
		String id = resourceNode.getAttributes().getNamedItem("id").getNodeValue();
		String name = resourceNode.getAttributes().getNamedItem("title").getNodeValue();
		String href = resourceNode.getAttributes().getNamedItem("href").getNodeValue();
		String type = resourceNode.getAttributes().getNamedItem("type").getNodeValue();
		String state = resourceNode.getAttributes().getNamedItem("state").getNodeValue();

		TypeResourceType resType = null;
		if (type.equals("PROBLEM"))
			resType = TypeResourceType.PROBLEM;
		else if (type.equals("PDF"))
			resType = TypeResourceType.PDF;
		else if (type.equalsIgnoreCase("VIDEO"))
			resType = TypeResourceType.VIDEO;
		
		ResourceType resource = new ResourceType(id, name, resType, href, state);

		return resource;
	}*/

	/**
	 * Parse xml Problem node
	 * @param resourceNode
	 * @return
	 
	private static Resource parseXmlProblem(Node problemNode) {
		String id = problemNode.getAttributes().getNamedItem("ID").getNodeValue();
		//String name = problemNode.getAttributes().getNamedItem("NAME").getNodeValue();
		String href = problemNode.getAttributes().getNamedItem("HREF").getNodeValue();
		
		ResourceType type = ResourceType.PROBLEM;
		
		Resource resource = new Resource(id, id, type, href);

		return resource;
	}*/
}
