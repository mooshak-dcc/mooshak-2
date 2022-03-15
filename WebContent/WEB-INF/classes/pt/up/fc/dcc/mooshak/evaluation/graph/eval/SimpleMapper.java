package pt.up.fc.dcc.mooshak.evaluation.graph.eval;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;

public class SimpleMapper implements Mapper {

	Graph solution;
	Graph attempt;
	Configs configs;
	int n;
	boolean running = true;
	
	long fact[];
	
	public SimpleMapper(Graph solution, Graph attempt,Configs configs) {
		super();
		this.solution = solution;
		this.attempt = attempt;
		this.configs = configs;
		
		this.n = Math.max(solution.getNodes().size(),attempt.getNodes().size());
		
		fact = new long[n+1];
		
		fact [0] = 1;
		for(int i=1; i<=n;i++)
			fact[i] = i * fact[i-1];
	}

	@Override
	public Iterator<Map<Node, Match>> iterator() {
		return new Iterator<Map<Node, Match>>(){
			Map<Node,Integer> position = new HashMap<>();
			long count = 0;
			{
				int pos = 0;
				for(Node node: solution.getNodes())
					position.put(node, pos++);
					
			}
			
			@Override
			public boolean hasNext() {
				return running && count < fact[n];
			}

			@Override
			public Map<Node, Match> next() {
				Map<Node, Match> map = new HashMap<>();
				
				for(Node solutionNode: solution.getNodes()){
					Node attemptNode = attempt.getNodes().get(position.get(solutionNode));
					map.put(solutionNode, 
							new Match(solutionNode,attemptNode,configs));
				}
			
				goNext();
				
				return map;
			}

			/**
			 * 
			 */
			private void goNext() {
				count++;
				
				for(int i = 0; i<n; i++) {
					Node solutionNode = solution.getNodes().get(i);
					
					if(count % fact[n-i-1] == 0) {
						int pos = position.get(solutionNode);
						boolean missing;
						
						do {
							pos = (pos+1) % n;
							missing = false;
							for(int j=0; j<i; j++)
								if(position.get(solution.getNodes().get(j)) == pos) {
									missing = true;
									break;
								}
						} while(missing);
					}
				}
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void forEachRemaining(
					Consumer<? super Map<Node, Match>> action) {
				// TODO Auto-generated method stub
				
			}};
	}

	@Override
	public void forEach(Consumer<? super Map<Node, Match>> action) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Spliterator<Map<Node, Match>> spliterator() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setRunning(boolean running) {
		this.running = running;
		
	}

	@Override
	public boolean isRunning() {
		return running;
	}

}
