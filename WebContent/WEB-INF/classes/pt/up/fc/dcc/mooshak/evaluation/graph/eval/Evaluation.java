package pt.up.fc.dcc.mooshak.evaluation.graph.eval;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.GraphDifference;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;

public class Evaluation {
	private double grade = -1;
	private double nodesGrade = -1;
	private double edgesGrade = -1;
	private int normalizedGrade = -1;
	private Set<GraphDifference> differences = new HashSet<GraphDifference>();
	private boolean complete = false;
	private Map<Node,Match> bestMap = new HashMap<>();
	
	/**
	 * @return the grade
	 */
	public double getGrade() {
		return grade;
	}

	/**
	 * @param grade
	 *            the grade to set
	 */
	public void setGrade(double grade) {
		this.grade = grade;
	}


	public int getNormalizedGrade() {
		return normalizedGrade;
	}

	public void setNormalizedGrade(int normalizedGrade) {
		this.normalizedGrade = normalizedGrade;
	}

	/**
	 * @return the nodesGrade
	 */
	public double getNodesGrade() {
		return nodesGrade;
	}

	/**
	 * @param nodesGrade the nodesGrade to set
	 */
	public void setNodesGrade(double nodesGrade) {
		this.nodesGrade = nodesGrade;
	}

	/**
	 * @return the edgesGrade
	 */
	public double getEdgesGrade() {
		return edgesGrade;
	}

	/**
	 * @param edgesGrade the edgesGrade to set
	 */
	public void setEdgesGrade(double edgesGrade) {
		this.edgesGrade = edgesGrade;
	}

	/**
	 * @return the differences
	 */
	public Set<GraphDifference> getDifferences() {
		return differences;
	}

	/**
	 * @param differences
	 *            the differences to set
	 */
	public void setDifferences(Set<GraphDifference> differences) {
		this.differences = new HashSet<>(differences);
	}

	/**
	 * Is evaluation completed and grade is meaningful?
	 * 
	 * @return the complete
	 */
	public boolean isComplete() {
		return complete;
	}

	/**
	 * Set if evaluation completed and grade is meaningful?
	 * 
	 * @param complete
	 *            the complete to set
	 */
	public void setComplete(boolean complete) {
		this.complete = complete;
	}

//	public void computeNormalizedGrade(double maxNodesGrade, double maxEdgesGrade) {
//		normalizedGrade = (int) (grade / (maxNodesGrade * maxEdgesGrade));
//	}

	public void addDifferences(Set<GraphDifference> differences) {
		Set<GraphDifference> diff = new HashSet<>(differences);
		this.differences.addAll(diff);
		
	}

	public Map<Node,Match> getBestMap() {
		return bestMap;
	}

	public void setBestMap(Map<Node,Match> bestMap) {
		this.bestMap = bestMap;
	}

}
