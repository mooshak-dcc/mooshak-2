package pt.up.fc.dcc.mooshak.evaluation.graph.eval;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.IncreasingVariablePermutations.HasInteger;

public class Alternative implements HasInteger, Comparable<Alternative> {
	Node solution;
	Match match;
	int delta;

	public Alternative(Node solution, Match match) {
		super();
		this.solution = solution;
		this.match = match;
		this.delta = solution.getValue() - match.value;
	}


	/**
	 * @return the solution
	 */
	public Node getSolution() {
		return solution;
	}


	/**
	 * @param solution the solution to set
	 */
	public void setSolution(Node solution) {
		this.solution = solution;
	}


	/**
	 * @return the match
	 */
	public Match getMatch() {
		return match;
	}


	/**
	 * @param match the match to set
	 */
	public void setMatch(Match match) {
		this.match = match;
	}


	/**
	 * @return the delta
	 */
	public int getDelta() {
		return delta;
	}


	/**
	 * @param delta the delta to set
	 */
	public void setDelta(int delta) {
		this.delta = delta;
	}


	@Override
	public int compareTo(Alternative other) {
		return delta - other.delta;
	}

	@Override
	public int getIntegerValue() {
		return (int) delta;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Alternative [solution=" + solution.getId() + ", match=[" + match.attempt.getId() + "," + match.value + "], delta=" + delta + "]";
	}
	
	

}