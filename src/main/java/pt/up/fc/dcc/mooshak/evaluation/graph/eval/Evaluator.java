package pt.up.fc.dcc.mooshak.evaluation.graph.eval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.evaluation.graph.RelatedGraphs;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Edge;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.EdgeDeletion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.EdgeInsertion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.GradeWithDifferences;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.GraphDifference;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.NodeDeletion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.NodeInsertion;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyName;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyValue;

public class Evaluator {
	
	private static final int DEFAULT_TIMEOUT =6;
	
	private static final int MAX_GRADE = 100;

	private static int timeout = DEFAULT_TIMEOUT;
	
	public static void setTimeout(int timeout) {
		Evaluator.timeout = timeout;
	}
	
	public static int getTimeout() {
		return timeout;
	}
	
	public enum MapperType {
		SIMPLE, OPTIMIZED
	}

	public Configs configs = Configs.getDefaultConfigs();

	long maxNodesGrade;
	long maxEdgesGrade;
	public Graph solution;
	public Graph attempt;
	boolean isReduced = false;
	Evaluation emptyEvaluation;
	public CoreMapper core = null;
	MapperType mapperType = null;
	Mapper mapper = null;
//	Map<NodePair, List<Edge>> attemptEdges = new HashMap<>();
	List<Edge> attemptEdges = new ArrayList<>();
	Set<GraphDifference> differences = new HashSet<>();
	String type = "UML";

	public Evaluator(Graph solution, Graph attempt, Configs config) {
		init(solution, attempt, MapperType.OPTIMIZED, config);
		
	}

	public Evaluator(Graph solution, Graph attempt) {
		init(solution, attempt, MapperType.OPTIMIZED,
				Configs.getDefaultConfigs());
	}
	
	public Evaluator(Graph solution, Graph attempt, String type) {
		this.type = type;
		init(solution, attempt, MapperType.OPTIMIZED,
				Configs.getDefaultConfigs());
		
	}

	public Evaluator() {
		
	}

//	private Map<NodePair, List<Edge>> groupEdges(List<Edge> edges) {
//		Map<NodePair, List<Edge>> map = new HashMap<>();
//
//		for (Edge edge : edges) {
//			NodePair pair = new NodePair(edge.getSource(), edge.getTarget());
//			if (map.containsKey(pair))
//				map.get(pair).add(edge);
//			else {
//				List<Edge> edgeList = new ArrayList<>();
//				edgeList.add(edge);
//				map.put(pair, edgeList);
//			}
//		}
//		return map;
//	}

	private void init(Graph solution, Graph attempt, MapperType mapperType,
			Configs configs) {
		this.solution = solution;
		this.attempt = attempt;
		this.configs = configs;
		

		solution.computeMaxValues(configs);
		attempt.computeMaxValues(configs);

		if (solution.getNodesProperties() == 0
				&& attempt.getNodesProperties() == 0) {
			GraphSimplifier graphSimplifier = new GraphSimplifier(configs);
			this.solution = graphSimplifier.reduceGraph(solution);
			this.solution.computeMaxValues(configs);
			if(this.solution.getNodes().size() < solution.getNodes().size() ||
					this.solution.getEdges().size() < solution.getEdges().size()){
				this.attempt = graphSimplifier.reduceGraph(attempt);
				this.attempt.computeMaxValues(configs);
			}
			isReduced = true;
		}
		
		
		this.emptyEvaluation = checkEmptyGraphs();
		if (emptyEvaluation == null) {

			this.mapperType = mapperType;
			this.attemptEdges = this.attempt.getEdges();
			this.core = new CoreMapper(this.solution, this.attempt, isReduced,
					configs, this.type);

			this.maxNodesGrade = (this.solution.getMaxNodesValue() > this.attempt
					.getMaxNodesValue()) ? this.attempt.getMaxNodesValue()
					: this.solution.getMaxNodesValue();
			this.maxEdgesGrade = (this.solution.getMaxEdgesValue() > this.attempt
					.getMaxEdgesValue()) ? this.attempt.getMaxEdgesValue()
					: this.solution.getMaxEdgesValue();

		}

	}

	private Evaluation checkEmptyGraphs() {
		int nodesGrade = 0;
		int edgesGrade = 0;
		Set<GraphDifference> diffs = new HashSet<>();
		Evaluation evaluation = new Evaluation();
		
		if (solution.getNodes().size() == 0) {
			if (attempt.getNodes().size() == 0)
				nodesGrade = configs.getNodeFactor();
			else {
				for (Node node : attempt.getNodes())
					diffs.add(new NodeDeletion(node, node.getIndistincts()));
			}
			if (attempt.getEdges().size() == 0)
				edgesGrade = configs.getEdgeFactor();
			else {
				for (Edge edge : attempt.getEdges())
					diffs.add(new EdgeDeletion(edge, edge.getIndistincts()));
			}

			evaluation.setNodesGrade(nodesGrade);
			evaluation.setEdgesGrade(edgesGrade);
			evaluation.setGrade(nodesGrade + edgesGrade);
			evaluation.setDifferences(diffs);
			evaluation.setComplete(true);
			return evaluation;
		}

		else if (attempt.getNodes().size() == 0) {
			for (Node node : solution.getNodes())
				diffs.add(new NodeInsertion(node, node.getIndistincts()));

			if (solution.getEdges().size() == 0)
				edgesGrade = configs.getEdgeFactor();
			else {
				for (Edge edge : solution.getEdges())
					diffs.add(new EdgeInsertion(edge, edge.getIndistincts()));
			}
			evaluation.setNodesGrade(nodesGrade);
			evaluation.setEdgesGrade(edgesGrade);
			evaluation.setGrade(nodesGrade + edgesGrade);
			evaluation.setDifferences(diffs);
			evaluation.setComplete(true);
			return evaluation;
		}
		return null;
	}

	
	int totalEdgePenalty = 0;
	int totalNodePenalty = 0;
	int localEdgesPenalty;

	class EvaluationRun implements Runnable {
		Evaluation evaluation = new Evaluation();
		boolean running = true;
		Thread runningThread = null;

		double oldMaxGrade = Double.MAX_VALUE;

		public void run() {
			runningThread = Thread.currentThread();
			for (RelatedGraphs pair : core) {
				if (!running)
					return;
				differences = new HashSet<>();
				int nodesPenalty = computeNodePenalty(pair.differences);
				int edgesPenalty = computeEdgePenalty(pair.differences);

				double currentMaxGrade = computeCurrentMaxGrade(nodesPenalty,
						edgesPenalty);
				double maxGrade = computeCurrentMaxGrade(nodesPenalty, 0);

				if (maxGrade <= evaluation.getGrade()) {
					break;
				}

				
				if (currentMaxGrade <= evaluation.getGrade()) {
					continue;
				}
				

				evaluateSubGraphs(pair, evaluation, nodesPenalty, edgesPenalty,
						currentMaxGrade);
				
				//System.out.println("EVALUATION.getBestMap() "+evaluation.getBestMap());
			}
			evaluation.setComplete(true);
		}

		private void evaluateSubGraphs(RelatedGraphs pair,
				Evaluation evaluation, int nodesPenalty, int edgesPenalty,
				double maxGrade) {
			
			if (mapperType == MapperType.OPTIMIZED)
				mapper = new OptimizedMapper(pair.getSolution(),
						pair.getAttempt(), core);
			else
				mapper = new SimpleMapper(pair.getSolution(),
						pair.getAttempt(), configs);
			
			double currentBestEdgeGrade = (maxEdgesGrade - totalEdgePenalty);
			currentBestEdgeGrade *= configs.getEdgeFactor();
			currentBestEdgeGrade /= (double) solution.getMaxEdgesValue();
			
			for (Map<Node, Match> map : mapper) {
				
				localEdgesPenalty = edgesPenalty;
				if (!running)
					return;
				differences = new HashSet<GraphDifference>();
				
				double nodesGrade, edgesGrade, bestPossible, grade;
				attemptEdges = pair.getAttempt().getEdges();

				nodesGrade = compareNodes(pair.getSolution(),
						pair.getAttempt(), map) - nodesPenalty;
				nodesGrade *= configs.getNodeFactor();
				nodesGrade /= (double) solution.getMaxNodesValue();
				
				
				
				if (nodesGrade < 0)
					nodesGrade = 0;

				if (currentBestEdgeGrade < 0)
					currentBestEdgeGrade = 0;

				bestPossible = nodesGrade + currentBestEdgeGrade;

				if (mapperType == MapperType.OPTIMIZED) {
					if (bestPossible < evaluation.getGrade()) {
						break;
					}
					
					/*
					 * ERRO RELATIVO
					 */

				}
				
				edgesGrade = compareEdges(pair.getSolution(),
						pair.getAttempt(), map) - localEdgesPenalty;
				
				edgesGrade *= configs.getEdgeFactor();
				if (edgesGrade == 0 && solution.getMaxEdgesValue() == 0)
					edgesGrade = configs.getEdgeFactor();
				else
					edgesGrade /= (double) solution.getMaxEdgesValue();
				
				if (edgesGrade < 0)
					edgesGrade = 0;
//				System.out.println("---------------------------------------------------------------");
//				System.out.println(edgesGrade);
//				System.out.println(map);
				

				grade = nodesGrade + edgesGrade;
				
				if (grade > evaluation.getGrade()) {
					evaluation.setGrade(grade);
					evaluation.setNodesGrade(nodesGrade);
					evaluation.setEdgesGrade(edgesGrade);
					evaluation.setDifferences(pair.differences);
					evaluation.addDifferences(differences);
					evaluation.setBestMap(map);
					if (grade == maxGrade)
						return;
				}
				else{
					double var1 = Math.abs(bestPossible - grade);
					double var2 = 70*(100 / bestPossible - 1);
//					System.out.println(bestPossible + " | " + var1 + "<" + var2);
					if(var1 < var2){
//						System.out.println("OK");
						break;
					}
				}

			}
			

			return;
		}

		/**
		 * @return the evaluation
		 */
		public Evaluation getEvaluation() {
			return evaluation;
		}

		/**
		 * @param evaluation
		 *            the evaluation to set
		 */
		public void setEvaluation(Evaluation evaluation) {
			this.evaluation = evaluation;
		}

		/**
		 * @return the running
		 */
		public boolean isRunning() {
			return running;
		}

		/**
		 * @param running
		 *            the running to set
		 */
		public void setRunning(boolean running) {
			this.running = running;
		}
		
		/**
		 * Inspect the thread running the graph evaluation
		 * This method is for debugging purposes only 
		 */
		public void inspect() {
			if(runningThread == null) 
				System.out.println("runningThread is null");
			else {
				for(int i = 0; i<10; i++) {
					System.out.println("\n -----------------"+i);
					for(StackTraceElement el : runningThread.getStackTrace()) {
						System.out.println("\t\t"+el);
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException cause) {
						LOGGER.log(Level.WARNING,"Waiting while inspecting",cause);
					}
				}
			}
		}
		

	}

	private static final Logger LOGGER = Logger.getLogger("");
	
	public Evaluation evaluate() {
		if (emptyEvaluation != null)
			return emptyEvaluation;
		EvaluationRun run = new EvaluationRun();
		ExecutorService executor = Executors.newSingleThreadExecutor();
		
		
		executor.execute(run);
		executor.shutdown();
		try {
			if (!executor.awaitTermination(timeout, TimeUnit.SECONDS)) {
				run.setRunning(false);
				if(mapper!=null)
				mapper.setRunning(false);
				
			}
		} catch (InterruptedException cause) {
			LOGGER.log(Level.SEVERE, "Waiting for assessment",cause);
		}
		
		// some assertions on the executor's status
		for(Runnable runnable :executor.shutdownNow()) 
			LOGGER.log(Level.WARNING,"Still running "+runnable.toString()); 
		if(! executor.isShutdown()) 
			LOGGER.log(Level.WARNING,"Graph timeout executor not shutdown");
		if(! executor.isTerminated()) 
			LOGGER.log(Level.WARNING,"Graph timeout executor not terminated");
		

		Evaluation evaluation = run.getEvaluation();
		if (isReduced)
			handleDifferences(evaluation);
		return evaluation;
	}

	public void handleDifferences(Evaluation evaluation) {
		// Tratar das diferenças e recalcular nota
		Map<String, List<NodeInsertion>> nodeInsertions = new HashMap<>();
		Map<String, List<NodeDeletion>> nodeDeletions = new HashMap<>();
		Map<String, List<EdgeInsertion>> edgeInsertions = new HashMap<>();
		Map<String, List<EdgeDeletion>> edgeDeletions = new HashMap<>();

		Iterator<GraphDifference> differencesIterator = evaluation
				.getDifferences().iterator();
		while (differencesIterator.hasNext()) {

			GraphDifference diff = differencesIterator.next();
			if (diff.isNodeInsertion()) {
				Node node = ((NodeInsertion) diff).getInsertion();
				String type = node.getType();
				if (nodeInsertions.containsKey(type)) {
					List<NodeInsertion> list = nodeInsertions.get(type);
					list.add((NodeInsertion) diff);
				} else {
					List<NodeInsertion> list = new ArrayList<>();
					list.add((NodeInsertion) diff);
					nodeInsertions.put(type, list);
				}
			} else if (diff.isNodeDeletion()) {
				Node node = ((NodeDeletion) diff).getDeletion();
				String type = node.getType();
				if (nodeDeletions.containsKey(type)) {
					List<NodeDeletion> list = nodeDeletions.get(type);
					list.add((NodeDeletion) diff);
				} else {
					List<NodeDeletion> list = new ArrayList<>();
					list.add((NodeDeletion) diff);
					nodeDeletions.put(type, list);
				}

			} else if (diff.isEdgeInsertion()) {
				Edge edge = ((EdgeInsertion) diff).getInsertion();
				String type = edge.getType();
				if (edgeInsertions.containsKey(type)) {
					List<EdgeInsertion> list = edgeInsertions.get(type);
					list.add((EdgeInsertion) diff);
				} else {
					List<EdgeInsertion> list = new ArrayList<>();
					list.add((EdgeInsertion) diff);
					edgeInsertions.put(type, list);
				}
			} else if (diff.isEdgeDeletion()) {
				Edge edge = ((EdgeDeletion) diff).getDeletion();
				String type = edge.getType();
				if (edgeDeletions.containsKey(type)) {
					List<EdgeDeletion> list = edgeDeletions.get(type);
					list.add((EdgeDeletion) diff);
				} else {
					List<EdgeDeletion> list = new ArrayList<>();
					list.add((EdgeDeletion) diff);
					edgeDeletions.put(type, list);
				}
			}

//			else
//				differencesIterator.remove(); //-- Helder Correia 28 Maio 2017
		}

		Iterator<String> nodeInsertionsIterator = nodeInsertions.keySet()
				.iterator();
		while (nodeInsertionsIterator.hasNext()) {
			String type = nodeInsertionsIterator.next();
			if (nodeDeletions.containsKey(type)) {
				List<NodeInsertion> insertionNodes = nodeInsertions.get(type);
				List<NodeDeletion> deletionNodes = nodeDeletions.get(type);
				int insertions = 0;
				for (NodeInsertion insertion : insertionNodes)
					insertions += insertion.getQuantity();
				int deletions = 0;
				for (NodeDeletion deletion : deletionNodes)
					deletions += deletion.getQuantity();

				int difference = insertions - deletions;
				if (difference == 0) {
					for (GraphDifference diff : insertionNodes)
						evaluation.getDifferences().remove(diff);
					for (GraphDifference diff : deletionNodes)
						evaluation.getDifferences().remove(diff);
				}

				else if (difference > 0) {
					int counter = 0;
					int index = 0;
					int limit = insertions - difference;
					while (counter < limit) {
						NodeInsertion diff = insertionNodes.get(index);
						if (counter + diff.getQuantity() <= limit) {
							evaluation.getDifferences().remove(diff);
							counter += diff.getQuantity();
						}

						else {
							int delete = limit - counter;
							diff.setQuantity(diff.getQuantity() - delete);
							counter += delete;
						}
						index++;
					}
					for (int i = 0; i < insertionNodes.size() - difference; i++) {
						GraphDifference diff = insertionNodes.get(i);
						evaluation.getDifferences().remove(diff);
					}

					for (GraphDifference diff : deletionNodes)
						evaluation.getDifferences().remove(diff);
				}

				else {
					for (GraphDifference diff : insertionNodes)
						evaluation.getDifferences().remove(diff);

					int counter = 0;
					int index = 0;
					int limit = deletions + difference;
					while (counter < limit) {
						NodeDeletion diff = deletionNodes.get(index);
						if (counter + diff.getQuantity() <= limit) {
							evaluation.getDifferences().remove(diff);
							counter += diff.getQuantity();
						}

						else {
							int delete = limit - counter;
							diff.setQuantity(diff.getQuantity() - delete);
							counter += delete;
						}
						index++;
					}

				}
			}

		}
		List<GraphDifference> toRemove = new ArrayList<>();
		for (List<EdgeInsertion> list : edgeInsertions.values()) {
			if (!list.get(0).getInsertion().getType()
					.equals("UML - Association"))
				continue;
			for (int i = 0; i < list.size(); i++) {
				for (int j = 0; j < list.size(); j++) {
					Edge edge1 = list.get(i).getInsertion();
					Edge edge2 = list.get(j).getInsertion();

					Node source1 = edge1.getSource();
					Node target1 = edge1.getTarget();

					Node source2 = edge2.getSource();
					Node target2 = edge2.getTarget();

					if (source1.equals(target2) && target1.equals(source2)) {
						GraphDifference diff = list.get(j);
						toRemove.add(diff);
						edgeInsertions.get("UML - Association").remove(diff);
					}
				}
			}
		}

		for (List<EdgeDeletion> list : edgeDeletions.values()) {
			if (!list.get(0).getDeletion().getType()
					.equals("UML - Association"))
				continue;
			for (int i = 0; i < list.size(); i++) {
				for (int j = 0; j < list.size(); j++) {
					Edge edge1 = list.get(i).getDeletion();
					Edge edge2 = list.get(j).getDeletion();

					Node source1 = edge1.getSource();
					Node target1 = edge1.getTarget();

					Node source2 = edge2.getSource();
					Node target2 = edge2.getTarget();

					if (source1.equals(target2) && target1.equals(source2)) {
						GraphDifference diff = list.get(j);
						toRemove.add(diff);
						edgeDeletions.get("UML - Association").remove(diff);
					}
				}
			}
		}
		for (GraphDifference diff : toRemove) {
			evaluation.getDifferences().remove(diff);
		}

		Iterator<String> edgeInsertionsIterator = edgeInsertions.keySet()
				.iterator();
		while (edgeInsertionsIterator.hasNext()) {
			String type = edgeInsertionsIterator.next();
			if (edgeDeletions.containsKey(type)) {
				List<EdgeInsertion> insertionEdges = edgeInsertions.get(type);
				List<EdgeDeletion> deletionEdges = edgeDeletions.get(type);
				int insertions = 0;
				for (EdgeInsertion insertion : insertionEdges)
					insertions += insertion.getQuantity();
				int deletions = 0;
				for (EdgeDeletion deletion : deletionEdges)
					deletions += deletion.getQuantity();

				int difference = insertions - deletions;

				if (difference == 0) {
					for (GraphDifference diff : insertionEdges)
						evaluation.getDifferences().remove(diff);
					for (GraphDifference diff : deletionEdges)
						evaluation.getDifferences().remove(diff);
				}

				else if (difference > 0) {
					// int counter = 0;
					// int index = 0;
					// int limit = deletions - difference;
					// while(counter < limit){
					// EdgeInsertion diff = insertionEdges.get(index);
					// if(counter + diff.getQuantity() <= limit){
					// evaluation.getDifferences().remove(diff);
					// counter += diff.getQuantity();
					// }
					//
					// else {
					// int delete = limit - counter;
					// diff.setQuantity(diff.getQuantity() - delete);
					// counter += delete;
					// }
					// index++;
					// }

					for (EdgeDeletion diff : deletionEdges) {
						Iterator<EdgeInsertion> it = insertionEdges.iterator();
						while (it.hasNext()) {
							EdgeInsertion insertion = it.next();
							if (insertion.getQuantity() == diff.getQuantity()) {
								evaluation.getDifferences().remove(diff);
								evaluation.getDifferences().remove(insertion);
								it.remove();
								break;
							}
						}
					}
				}

				else {
					for (EdgeInsertion diff : insertionEdges) {
						Iterator<EdgeDeletion> it = deletionEdges.iterator();
						while (it.hasNext()) {
							EdgeDeletion deletion = it.next();
							if (deletion.getQuantity() == diff.getQuantity()) {
								evaluation.getDifferences().remove(diff);
								evaluation.getDifferences().remove(deletion);
								it.remove();
								break;
							}
						}
					}

					// int counter = 0;
					// int index = 0;
					// int limit = deletions - difference;
					// while(counter < limit){
					// EdgeDeletion diff = deletionEdges.get(index);
					// if(counter + diff.getQuantity() <= limit){
					// evaluation.getDifferences().remove(diff);
					// counter += diff.getQuantity();
					// }
					//
					// else {
					// int delete = limit - counter;
					// diff.setQuantity(diff.getQuantity() - delete);
					// counter += delete;
					// }
					// index++;
					// }

				}
			}

		}
	}

	private double computeCurrentMaxGrade(long nodesPenalty, long edgesPenalty) {

		double nodesGrade = maxNodesGrade - nodesPenalty;
		if (nodesGrade < 0)
			nodesGrade = 0;
		double edgesGrade = maxEdgesGrade - edgesPenalty;
		if (edgesGrade < 0)
			edgesGrade = 0;

		nodesGrade = nodesGrade * configs.getNodeFactor()
				/ (double) solution.getMaxNodesValue();
		edgesGrade = edgesGrade * configs.getEdgeFactor()
				/ (double) solution.getMaxEdgesValue();

		double grade = nodesGrade + edgesGrade;
		return grade;
	}

	private int computeNodePenalty(Set<GraphDifference> differences) {
		int extraNodes = 0;
		int penalty = 0;
		totalNodePenalty = 0;
		for (GraphDifference diff : differences) {
			if (diff.isNodeDeletion())
				extraNodes++;

			else if (diff.isNodeInsertion()) {
				// totalNodePenalty += ((NodeInsertion) diff).getInsertion()
				// .getValue();
			}
		}

		penalty += extraNodes * configs.get(ConfigName.NODE_TYPE_WEIGHT); // 5
																			// penalty
																			// pts
																			// for
																			// extra
																			// node
		totalNodePenalty += penalty;
		return penalty;
	}

	int edgeInsertions;

	private int computeEdgePenalty(Set<GraphDifference> differences) {
		
		int extraEdges = 0;
		int penalty = 0;
		totalEdgePenalty = 0;
		edgeInsertions = 0;
		for (GraphDifference diff : differences) {
			if (diff.isEdgeDeletion()) {
				extraEdges++;
			} else if (diff.isEdgeInsertion()) {
				// totalEdgePenalty += ((EdgeInsertion) diff).getInsertion()
				// .getValue();
				edgeInsertions++;
			}
		}

		penalty += extraEdges * configs.get(ConfigName.EDGE_TYPE_WEIGHT); // 5
																			// penalty
																			// pts
																			// for
																			// extra
																			// edge
		totalEdgePenalty += penalty;
		return penalty;
	}

	/**
	 * Compare node solution with mapped node from attempt
	 * 
	 * @param map
	 * @return
	 */
	private long compareNodes(Graph solution, Graph attempt,
			Map<Node, Match> map) {
		long grade = 0;

		for (Node sol : map.keySet()) {
			Match match = map.get(sol);
			grade += match.value;
			if (isReduced) {
				for (GraphDifference diff : match.differences)
					if (diff.isEdgeDeletion())
						localEdgesPenalty += configs
								.get(ConfigName.EDGE_TYPE_WEIGHT);
					else if (diff.isEdgeInsertion())
						localEdgesPenalty += ((EdgeInsertion) diff)
								.getInsertion().getValue()
								* ((EdgeInsertion) diff).getQuantity();
			}
			differences.addAll(match.differences);
		}

		int solutionNodes = solution.getNodes().size();

		if (solutionNodes == 0)
			return MAX_GRADE;
		return grade;
	}
	
	private boolean containsEdge(List<Edge> listEdges, Edge edge, Map<Node, Match> map){
		for(Edge e:listEdges)
			if(e.equals2(edge,map))
				return true;
		return false;
	}
	
	private List<Edge> removeEdge(List<Edge> listEdges, Edge edge, Map<Node, Match> map){
		List<Edge> le=listEdges;
		for(int i=0;i<listEdges.size();i++)
			if(listEdges.get(i).equals2(edge,map))
			{
				le.remove(i);
			}
				
		return le;
	}

	
	
	
	
	/**
	 * Compare edge from solution with mapped edges from attempt
	 * 
	 * @param map
	 * @return
	 */
	private long compareEdges(Graph solution, Graph attempt,
			Map<Node, Match> map) {
		long grade = 0;
		int extraEdges = 0;
		int penalty = 0;
		if (solution.getEdges().size() == 0) {
			grade = maxEdgesGrade;
			extraEdges = attempt.getEdges().size();

			if (extraEdges > 0) {
				return 0;
			}
			return 0;
		}

		if (attempt.getEdges().size() == 0) {
			for (Edge e : solution.getEdges())
				differences.add(new EdgeInsertion(e, e.getIndistincts()));
			return 0;
		}
			
//		List<Edge> solutionEdges = new ArrayList<>();
//		for (Edge solutionEdge : solution.getEdges()) {
//			solutionEdges.add(solutionEdge);
//			Node s=map.get(solutionEdge.getSource()).attempt;
//			Node a=map.get(solutionEdge.getTarget()).attempt;
//			
//			for()
//			grade += computeEdgeGrade(solutionEdges,
//					attemptEdges,map);
//		}
		
		Map<NodePair, Edge> solutionEdges = new HashMap<>();
		for (Edge solutionEdge : solution.getEdges()) {
			NodePair pair = new NodePair(
					map.get(solutionEdge.getSource()).attempt,
					map.get(solutionEdge.getTarget()).attempt);

				solutionEdges.put(pair, solutionEdge);
		
		}
		
		for (Edge attempE : attemptEdges) {
			
//			System.out.println("Test "+attempE.getSource());
			NodePair pair = new NodePair(
					attempE.getSource(),
					attempE.getTarget());
					
			
			if (solutionEdges.containsKey(pair)) {
//				int diff = attemptEdges.get(attemptPair).size()
//						- solutionEdges.get(attemptPair).size();
//
//				if (diff > 0)
//					extraEdges += diff;
				
				List<Edge> edgeListS = new ArrayList<>();
				edgeListS.add(solutionEdges.get(pair));
				List<Edge> edgeListA = new ArrayList<>();
				edgeListA.add(attempE);
				
				grade += computeEdgeGrade(edgeListS,
						edgeListA,map);
				solutionEdges.remove(pair);
			}

			else {
				for (Edge e : attemptEdges) {
					differences.add(new EdgeDeletion(e, e.getIndistincts()));
				}
//
////				extraEdges += attemptEdges.get(attemptPair).size();
			}
//			System.out.println(solutionEdges);
//			
//			for (Entry<NodePair, Edge> solutionPair : solutionEdges.entrySet()) {
//				Edge e =solutionPair.getValue();
//				differences.add(new EdgeInsertion(e, e.getIndistincts()));
//			}

		}
	
		
		
		
		if (grade < 0)
			grade = 0;
		return grade;
	}

	private long computeEdgeGrade(List<Edge> solutionEdges,
			List<Edge> attemptEdges,Map<Node, Match> map) {
		int nSolutionEdges = solutionEdges.size();
		int nAttemptEdges = attemptEdges.size();
		int size = nSolutionEdges;
		if (nSolutionEdges > nAttemptEdges) {
			// addBlankEdges(attemptEdges, nSolutionEdges - nAttemptEdges);
		} else if (nAttemptEdges > nSolutionEdges) {
			// addBlankEdges(solutionEdges, nAttemptEdges - nSolutionEdges);
			size = nAttemptEdges;
		}

		long bestGrade = 0;
		long currentGrade = 0;
		long auxGrade = 0;
		long bestAuxGrade = 0;
		List<List<Integer>> permutations = generatePermutations(size);
		
		
		Set<GraphDifference> currentDifferences = new HashSet<>();
		Set<GraphDifference> bestDifferences = new HashSet<>();
		for (List<Integer> p : permutations) {
			currentDifferences = new HashSet<>();
			currentGrade = 0;
			auxGrade = 0;
			for (int i = 0; i < size; i++) {
				Edge sol = null;
				Edge att = null;

				if (i < solutionEdges.size()) {
					sol = solutionEdges.get(i);
				}

				if (p.get(i) < attemptEdges.size()) {
					att = attemptEdges.get(p.get(i));
				}

				if (sol == null) {
					currentDifferences.add(new EdgeDeletion(attemptEdges.get(p
							.get(i)), attemptEdges.get(p.get(i))
							.getIndistincts()));
					continue;
				}

				if (att == null) {
					currentDifferences.add(new EdgeInsertion(sol, sol
							.getIndistincts()));
					continue;
				}
				GradeWithDifferences comparisonGrade = sol.compareEdge(att,
						configs,map); /// Alterações Helder Correia 28/11/2017********************************
				long compareValue = comparisonGrade.getGrade();
				currentDifferences.addAll(comparisonGrade.getDifferences());
				currentGrade += compareValue;
				auxGrade += 100 * compareValue / sol.getValue();
			}
			if (currentGrade > bestGrade) {
				bestGrade = currentGrade;
				bestDifferences = new HashSet<>(currentDifferences);
				bestAuxGrade = auxGrade;
			}

			else if (currentGrade == bestGrade) {
				if (auxGrade == 0 && bestAuxGrade == 0 && currentDifferences.size() > bestDifferences.size()){
					bestGrade = currentGrade;
					bestDifferences = new HashSet<>(currentDifferences);
					bestAuxGrade = auxGrade;
				}
				
				else if (auxGrade > bestAuxGrade) {
					bestGrade = currentGrade;
					bestDifferences = new HashSet<>(currentDifferences);
					bestAuxGrade = auxGrade;
				}
			}
		}
		differences.addAll(bestDifferences);
		return bestGrade;
	}

	List<List<Integer>> perm;

	public List<List<Integer>> generatePermutations(int size) {
		perm = new ArrayList<>();
		List<Integer> indexes = new ArrayList<>();

		for (int i = 0; i < size; i++)
			indexes.add(i);

		permutations(new ArrayList<Integer>(), indexes);
		return perm;
	}

	private void permutations(List<Integer> perms, List<Integer> indexes) {

		if (indexes.size() == 0)
			perm.add(new ArrayList<>(perms));

		else {
			for (int i = 0; i < indexes.size(); i++) {
				int removeValue = indexes.remove(i);
				perms.add(0, removeValue);

				permutations(perms, indexes);

				indexes.add(i, removeValue);
				perms.remove(0);
			}
		}
	}

}
