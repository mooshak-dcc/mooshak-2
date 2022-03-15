package pt.up.fc.dcc.mooshak.shared.results.sequencing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a student resource 
 * @author josepaiva
 */
@XmlRootElement(name = "student")
@XmlAccessorType(XmlAccessType.FIELD)
public class Student implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement
	private String studentId;

    private Map<String, String> features = new HashMap<>();
	
	public Student() {
	}
	
	public Student(String studentId) {
		this.studentId = studentId;
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
	 * @return the features
	 */
	public Map<String, String> getFeatures() {
		return features;
	}

	/**
	 * @param features the features to set
	 */
	public void setFeatures(Map<String, String> features) {
		this.features = features;
	}
	
}
