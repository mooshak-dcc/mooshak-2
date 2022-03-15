package pt.up.fc.dcc.mooshak.evaluation.graph.eval;

import java.util.List;
import java.util.Set;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Edge;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.GradeWithDifferences;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.GraphDifference;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.IncreasingVariablePermutations.HasInteger;


public class Match implements HasInteger, Comparable<Match>{
	Node attempt;
	int value;
	int tiebreak;
	Set<GraphDifference> differences;

	public Match(Node solutionNode, Node attemptNode,Configs configs) {
		attempt = attemptNode;
		GradeWithDifferences comparison = 
				solutionNode.compareNode(attemptNode,configs);
		value = comparison.getGrade();
		differences = comparison.getDifferences();
		tiebreak = -1;
	}
	
	public Match(Node solutionNode, Node attemptNode, List<Edge> solutionEdges, List<Edge> attemptEdges, Configs configs) {
		attempt = attemptNode;
		GradeWithDifferences comparison = 
				solutionNode.compareNode(attemptNode,solutionEdges,attemptEdges,configs);
		value = comparison.getGrade();
		differences = comparison.getDifferences();
		tiebreak = -1;
	}
	
	public Match(Node attemptNode, int value) {
		this.attempt = attemptNode;
		this.value = value;
		tiebreak = -1;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * @return the attempt
	 */
	public Node getAttempt() {
		return attempt;
	}

	/**
	 * @param attempt the attempt to set
	 */
	public void setAttempt(Node attempt) {
		this.attempt = attempt;
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
	 * @return the tiebreak
	 */
	public int getTiebreak() {
		return tiebreak;
	}

	/**
	 * @param tiebreak the tiebreak to set
	 */
	public void setTiebreak(int tiebreak) {
		this.tiebreak = tiebreak;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Match [attempt=" + attempt + ", value=" + value + "]";
	}

	@Override
	public int compareTo(Match o) {
		return this.value - o.value;
	}

	@Override
	public int getIntegerValue() {
		return (int) this.value;
	}
	


}
