package pt.up.fc.dcc.mooshak.shared.results.sequencing;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Resources abstract representation
 * 
 * @author josepaiva
 */
public class CourseList implements IsSerializable {

	private List<Course> courses = new ArrayList<Course>();
	
	public CourseList() {
	}

	/**
	 * @return the courses
	 */
	public List<Course> getCourses() {
		return courses;
	}

	/**
	 * @param courses the courses to set
	 */
	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}

	/**
	 * @param course the course to add
	 */
	public void addCourse(Course course) {
		this.courses.add(course);
	}
	
	
}
