package pt.up.fc.dcc.mooshak.shared.results.sequencing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gwt.user.client.rpc.IsSerializable;

import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource.ResourceState;

/**
 * An update to a resource in a course
 * 
 * @author josepaiva
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CourseUpdate")
public class CourseUpdate implements IsSerializable {

	@XmlElement
	private String nodeId;
	@XmlElement
	private ResourceState state;

	public CourseUpdate() {
		super();
	}

	public CourseUpdate(String nodeId, ResourceState state) {
		super();
		this.nodeId = nodeId;
		this.state = state;
	}

	/**
	 * @return the nodeId
	 */
	public String getNodeId() {
		return nodeId;
	}

	/**
	 * @param nodeId
	 *            the nodeId to set
	 */
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	/**
	 * @return the state
	 */
	public ResourceState getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(ResourceState state) {
		this.state = state;
	}

}
