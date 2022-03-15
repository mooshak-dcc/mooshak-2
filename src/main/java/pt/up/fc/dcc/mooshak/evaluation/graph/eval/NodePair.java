package pt.up.fc.dcc.mooshak.evaluation.graph.eval;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;

public class NodePair {
	Node source;
	Node target;

	public NodePair(Node source, Node target) {
		super();
		this.source = source;
		this.target = target;
	}



	/* (non-Javadoc)
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



	/* (non-Javadoc)
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
		NodePair other = (NodePair) obj;
		if (source == null) {
			if (other.source != null)
				return false;
		} /*else if (!source.equals(other.source))
			return false;*/
		if (target == null) {
			if (other.target != null)
				return false;
		}/* else if (!target.equals(other.target))
			return false;*/
		if(target.equals(other.source) && source.equals(other.target))
			return true;
		if(target.equals(other.target) && source.equals(other.source) )
			return true;
//		
//		System.out.println("FALSEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
		return false;
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Pair [source=" + source.getId() + ", target=" + target.getId()
				+ "]\n";
	}

}
