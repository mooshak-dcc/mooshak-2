package pt.up.fc.dcc.mooshak.evaluation.graph.data;

import java.util.Set;

public class GObjectDifference {
	private int grade;
	private Set<GraphDifference> differences;
	
	public GObjectDifference(int grade, Set<GraphDifference> differences) {
		this.grade = grade;
		this.differences = differences;
	}

	/**
	 * @return the grade
	 */
	public int getGrade() {
		return grade;
	}

	/**
	 * @param grade the grade to set
	 */
	public void setGrade(int grade) {
		this.grade = grade;
	}

	/**
	 * @return the differences
	 */
	public Set<GraphDifference> getDifferences() {
		return differences;
	}

	/**
	 * @param differences the differences to set
	 */
	public void setDifferences(Set<GraphDifference> differences) {
		this.differences = differences;
	}
	
}
