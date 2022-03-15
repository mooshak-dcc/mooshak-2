package pt.up.fc.dcc.mooshak.shared.results.sequencing;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Summary of the student's state in a course
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "summary")
public class Summary implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement
    private String studentId;

    @XmlElement
    private String courseId;

    @XmlElement
    private Integer solved;

    @XmlElement
    private Integer videos;

    @XmlElement
    private Integer staticResources;

    @XmlElement
    private String part;

    public Summary() { }

    public Summary(String studentId, String courseId) {
        super();
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public Integer getSolved() {
        return solved;
    }

    public void setSolved(Integer solved) {
        this.solved = solved;
    }

    public Integer getVideos() {
        return videos;
    }

    public void setVideos(Integer videos) {
        this.videos = videos;
    }

    public Integer getStaticResources() {
        return staticResources;
    }

    public void setStaticResources(Integer staticResources) {
        this.staticResources = staticResources;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }
}
