package pt.up.fc.dcc.mooshak.client.gadgets.resourcestree;

import java.util.Date;

import com.google.gwt.event.shared.GwtEvent;

import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource.ResourceType;

/**
 * Event called when a new resource is selected
 * 
 * @author josepaiva
 */
public class SelectResourceEvent extends GwtEvent<SelectResourceEventHandler> {
	
	public static Type<SelectResourceEventHandler> TYPE = 
			new Type<SelectResourceEventHandler>();

	private String resourceId;
	private String courseId;
	private String resourceName;
	private ResourceType resourceType;
	private String link;
	private Date learningTime;
	private String language;
	
	public SelectResourceEvent(String courseId, String resourceId, String resourceName,
			ResourceType resourceType, String link, Date learningTime,String language) {
		this.courseId = courseId;
		this.resourceId = resourceId;
		this.resourceName = resourceName;
		this.resourceType = resourceType;
		this.link = link;
		this.learningTime = learningTime;
		this.language = language;
	}

	@Override
	public Type<SelectResourceEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SelectResourceEventHandler handler) {
		handler.onSelectResource(this);
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
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}

	/**
	 * @param resourceName the resourceName to set
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	/**
	 * @return the resourceType
	 */
	public ResourceType getResourceType() {
		return resourceType;
	}

	/**
	 * @param resourceType the resourceType to set
	 */
	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * @return the learningTime
	 */
	public Date getLearningTime() {
		return learningTime;
	}

	/**
	 * @param learningTime the learningTime to set
	 */
	public void setLearningTime(Date learningTime) {
		this.learningTime = learningTime;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

}
