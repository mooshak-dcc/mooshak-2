package pt.up.fc.dcc.mooshak.shared.events;

import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource.ResourceState;

/**
 * Event thrown when a resource is updated in Enki.
 * 
 * @author josepaiva
 */
public class ResourceStateUpdateEvent extends MooshakEvent {
	
	private String courseId;
	private String id;
	private ResourceState state;
	
	public ResourceStateUpdateEvent() {
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
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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

	
	
}
