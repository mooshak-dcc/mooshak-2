package pt.up.fc.dcc.mooshak.evaluation.graph.data;

import java.util.HashSet;
import java.util.Set;

public class GradeWithDifferences {
	private Set<GraphDifference> differences;
	private int grade;
	
	public GradeWithDifferences() {
		this.differences = new HashSet<>();
		this.grade = 0;
	}
	
	public GradeWithDifferences(int grade) {
		this.differences = new HashSet<>();
		this.grade = grade;
	}
	
	public GradeWithDifferences(Set<GraphDifference> differences, int grade) {
		this.differences = differences;
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

	
	public void update(int grade) {
		this.grade += grade;
	}
	
	public void update(GradeWithDifferences gradeWithDifferences) {
		this.grade += gradeWithDifferences.getGrade();
		this.differences.addAll(gradeWithDifferences.getDifferences());
	}

	public void applyPenalty(int penalty) {
		// TODO Auto-generated method stub
		this.grade -= penalty;
	}

	public void addGrade(int grade) {
		this.grade += grade;
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GradeWithDifferences [differences=" + differences + ", grade="
				+ grade + "]";
	}

	public void addDifference(GraphDifference difference) {
		this.differences.add(difference);	
	}


	
	
}
