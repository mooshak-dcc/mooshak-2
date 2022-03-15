package pt.up.fc.dcc.mooshak.client.gadgets;

import com.google.gwt.event.shared.GwtEvent;

public class ResourceLearningTimeUpdateEvent extends GwtEvent<ResourceLearningTimeUpdateEventHandler> {
	
	public static Type<ResourceLearningTimeUpdateEventHandler> TYPE = 
			new Type<ResourceLearningTimeUpdateEventHandler>();

	private String courseId;
	private String resourceId;
	
	public ResourceLearningTimeUpdateEvent(String courseId, String resourceId) {
		this.courseId = courseId;
		this.resourceId = resourceId;
	}

	@Override
	public Type<ResourceLearningTimeUpdateEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ResourceLearningTimeUpdateEventHandler handler) {
		handler.onUpdateResourceLearningTime(this);
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

}
