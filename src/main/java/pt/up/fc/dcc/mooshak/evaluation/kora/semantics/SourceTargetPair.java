/**
 * 
 */
package pt.up.fc.dcc.mooshak.evaluation.kora.semantics;

import java.util.Map;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Edge;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Match;

/**
 * @author User
 *
 */

class SourceTargetPair {
	Node source;
	Node target;

	public SourceTargetPair(Edge edge) {
		this.source = edge.getSource();
		this.target = edge.getTarget();
	}

	public SourceTargetPair(Edge edge, Map<Node, Match> bests) {
		Match aux = bests.get(edge.getSource());
		if (aux == null)
			this.source = null;
		else
			this.source = aux.getAttempt();
		aux = bests.get(edge.getTarget());
		if (aux == null)
			this.target = null;
		else
			this.target = aux.getAttempt();
	}

	public SourceTargetPair(Node source, Node target) {
		this.source = source;
		this.target = target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SourceTargetPair other = (SourceTargetPair) obj;
		if (source.equals(other.source) && target.equals(other.target))
			return true;
		if (source.equals(other.target) && target.equals(other.source))
			return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SourceTargetPair [source=" + source.getId() + ", target=" + target.getId() + "]";
	}
}
