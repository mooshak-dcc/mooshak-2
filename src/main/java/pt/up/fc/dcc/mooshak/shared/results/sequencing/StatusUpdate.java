package pt.up.fc.dcc.mooshak.shared.results.sequencing;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gwt.user.client.rpc.IsSerializable;

import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource.ResourceState;

/**
 * Status update response representation
 * 
 * @author josepaiva
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "statusupdate")
public class StatusUpdate implements IsSerializable {

	@XmlElement
	protected String studentId;

	@XmlElement
	protected String courseId;

	@XmlElement
	protected String resourceId;
	
	@XmlElement
	protected ResourceState state;
	
	@XmlElement
	protected Integer solved;

	@XmlElement(name="completions")
	protected List<CompletionUpdate> completions;

	@XmlElement(name="updates")
	protected List<CourseUpdate> updates;
	
	
	public StatusUpdate() {
	}


	/**
	 * @return the studentId
	 */
	public String getStudentId() {
		return studentId;
	}


	/**
	 * @param studentId the studentId to set
	 */
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}


	/**
	 * @return the courseId
	 */
	public String getCourseId() {
		return courseId;
	}


	/**
	 * @param courseId the courseId to set
	 */
	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}


	/**
	 * @return the resourceId
	 */
	public String getResourceId() {
		return resourceId;
	}


	/**
	 * @param resourceId the resourceId to set
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}


	/**
	 * @return the state
	 */
	public ResourceState getState() {
		return state;
	}


	/**
	 * @param state the state to set
	 */
	public void setState(ResourceState state) {
		this.state = state;
	}


	/**
	 * @return the solved
	 */
	public Integer getSolved() {
		return solved;
	}


	/**
	 * @param solved the solved to set
	 */
	public void setSolved(Integer solved) {
		this.solved = solved;
	}


	/**
	 * @return the completions
	 */
	public List<CompletionUpdate> getCompletions() {
		if (completions == null)
			return new ArrayList<>();
		return completions;
	}


	/**
	 * @param completions the completions to set
	 */
	public void setCompletions(List<CompletionUpdate> completions) {
		this.completions = completions;
	}


	/**
	 * @return the updates
	 */
	public List<CourseUpdate> getUpdates() {
		if (updates == null)
			return new ArrayList<>();
		return updates;
	}


	/**
	 * @param updates the updates to set
	 */
	public void setUpdates(List<CourseUpdate> updates) {
		this.updates = updates;
	}


}
