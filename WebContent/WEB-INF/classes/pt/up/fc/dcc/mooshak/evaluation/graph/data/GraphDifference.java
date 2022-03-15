package pt.up.fc.dcc.mooshak.evaluation.graph.data;


public interface GraphDifference {
	boolean isNodeInsertion();
	boolean isNodeDeletion();
	boolean isEdgeInsertion();
	boolean isEdgeDeletion();
	boolean isDifferentType();
	boolean isDifferentPropertyValue();
	boolean isDifferentSubPropertyValue();
	boolean isPropertyInsertion();
	boolean isPropertyDeletion();
	boolean isSubPropertyInsertion();
	boolean isSubPropertyDeletion();
	boolean isInDegreeDifference();
	boolean isOutDegreeDifference();
	String toString();
}
