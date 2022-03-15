package pt.up.fc.dcc.mooshak.client.guis.enki.event;

import com.google.gwt.event.shared.GwtEvent;

public class ResourceOnSuccessEvent extends GwtEvent<ResourceOnSuccessEventHandler> {
	
	public static Type<ResourceOnSuccessEventHandler> TYPE = 
			new Type<ResourceOnSuccessEventHandler>();

	private String courseId;
	private String resourceId;
	
	public ResourceOnSuccessEvent(String courseId, String resourceId) {
		this.setResourceId(resourceId);
		this.setCourseId(courseId);
	}

	@Override
	public Type<ResourceOnSuccessEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ResourceOnSuccessEventHandler handler) {
		handler.onResourceSolvedSuccessfully(this);
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

}
