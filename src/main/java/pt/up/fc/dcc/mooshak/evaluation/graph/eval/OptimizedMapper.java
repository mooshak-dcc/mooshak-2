package pt.up.fc.dcc.mooshak.evaluation.graph.eval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;

public class OptimizedMapper implements Mapper {

	Map<Node, Match> bests = new HashMap<>();
	Map<Node, Integer> attemptBests = new HashMap<>();
	List<Alternative> alternatives = new ArrayList<>();
	boolean running = true;
	
	OptimizedMapper(Graph solution, Graph attempt, CoreMapper core) {
		this.bests = core.getBests(solution, attempt);
		this.alternatives = core.getAlternatives(solution, attempt);
		
	}

	@Override
	public Iterator<Map<Node, Match>> iterator() {
		
		return new Iterator<Map<Node, Match>>() {
			IncreasingVariablePermutations<Alternative> adder = 
					new IncreasingVariablePermutations<>(alternatives);
			Iterator<List<Alternative>> iterator = adder.iterator();
			Map<Node, Match> next = getNext();

			@Override
			public boolean hasNext() {
				return running && next != null;
			}

			@Override
			public Map<Node, Match> next() {
				Map<Node, Match> map = next;
				next = getNext();
				return map;
			}

			private Map<Node, Match> getNext() {
				while (iterator.hasNext()) {
					if(! running)
						return null;
					Map<Node, Match> map = applyAlternatives(iterator.next());
					if (valid(map)) {
						return map;
					}
				}
				return null;
			}

			private Map<Node, Match> applyAlternatives(List<Alternative> alts) {

				Map<Node, Match> map = new HashMap<>(bests);
				List<Node> keys = new ArrayList<>();

				for (Alternative alt : alts) {
					if (keys.contains(alt.solution)) {
						return null;
					}

					keys.add(alt.solution);
					map.put(alt.solution, alt.match);

				}
				return map;
			}

			private boolean valid(Map<Node, Match> map) {
				List<Node> values = new ArrayList<>();

				if (map == null)
					return false;
				for (Match value : map.values())
					if (values.contains(value.attempt))
						return false;
					else
						values.add(value.attempt);

				return true;
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub

			}

			@Override
			public void forEachRemaining(
					Consumer<? super Map<Node, Match>> action) {
				// TODO Auto-generated method stub

			}
		};
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
