package pt.up.fc.dcc.mooshak.evaluation.graph;

import java.util.HashSet;
import java.util.Set;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.GraphDifference;

public class RelatedGraphs {
	Graph solution;
	Graph attempt;
	public Set<GraphDifference> differences;

	public RelatedGraphs(Graph original, Graph variation) {
		this.solution = original;
		this.attempt = variation;
		this.differences = new HashSet<GraphDifference>();
	}

	public RelatedGraphs(Graph solution, Graph attempt,
			Set<GraphDifference> differences) {
		this.solution = solution;
		this.attempt = attempt;
		this.differences = differences;
	}


	/**
	 * @return the solution
	 */
	public Graph getSolution() {
		return solution;
	}

	/**
	 * @param solution
	 *            the solution to set
	 */
	public void setSolution(Graph solution) {
		this.solution = solution;
	}

	/**
	 * @return the attempt
	 */
	public Graph getAttempt() {
		return attempt;
	}

	/**
	 * @param attempt
	 *            the attempt to set
	 */
	public void setAttempt(Graph attempt) {
		this.attempt = attempt;
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
		this.differences = differences;
	}

	/**
	 * @param difference
	 *            the difference to add to the set
	 */
	public void addDifference(GraphDifference difference) {
		this.differences.add(difference);
	}


}
