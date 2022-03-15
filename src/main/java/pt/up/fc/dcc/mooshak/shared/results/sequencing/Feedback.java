package pt.up.fc.dcc.mooshak.shared.results.sequencing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Feedback of a student about a resource
 * 
 * @author josepaiva
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "feedback")
public class Feedback implements IsSerializable {

	@XmlElement
	private String studentId;

	@XmlElement
	private String courseId;

	@XmlElement
	private String resourceId;

	@XmlElement
	private Integer rating;

	@XmlElement
	private String comment;
	
	public Feedback() { }

	public Feedback(String studentId, String courseId, String resourceId) {
		super();
		this.studentId = studentId;
		this.courseId = courseId;
		this.resourceId = resourceId;
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
	 * @return the rating
	 */
	public Integer getRating() {
		return rating;
	}

	/**
	 * @param rating the rating to set
	 */
	public void setRating(Integer rating) {
		this.rating = rating;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	

}
