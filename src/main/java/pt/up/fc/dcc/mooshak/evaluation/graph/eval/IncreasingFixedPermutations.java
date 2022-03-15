package pt.up.fc.dcc.mooshak.evaluation.graph.eval;

import java.util.*;
import java.util.function.Consumer;

import pt.up.fc.dcc.mooshak.evaluation.graph.eval.IncreasingVariablePermutations.HasInteger;

public class IncreasingFixedPermutations<T extends HasInteger & Comparable<T>>
		implements Iterable<List<T>> {
	private List<T> values;
	private int nPermutations;
	
	interface HasInteger {
		int getIntegerValue();
	}
	
	IncreasingFixedPermutations(List<T> values, int n) {
		this.values = values;
		this.nPermutations = n;
		Collections.sort(values);
	}
	
	public List<T> getValues() {
		return values;
	}
	
	private int sum() {
		int sum = 0;
		
		for(T value: values)
			sum += value.getIntegerValue();
		
		return sum;
	}

	class Cursor {
		int position;
		int limit;
		int max;
		
		Cursor(Cursor cursor) {
			this(cursor.position,cursor.slack());
		}
		
		Cursor(int limit) {
			this(limit,Integer.MAX_VALUE);
		}
		
		Cursor(int limit, int max) {
			this.position = 0;
			this.limit = limit;
			this.max = max;
		}
		
		boolean reached() {
			return getValue() == max;
		}
		
		boolean advance() {
			position++;
			return isValid();
		}
			
		boolean isValid() {
			if(position == limit)
				return false;
			else if(values.size() < position)
				return false;
			else if(values.get(position).getIntegerValue() > max)
				return false;
			else
				return true;
		}

		int slack() {
			return max-getValue();
		}

		int getValue() {
			return getValue(position);
			
		}
		
		private int getValue(int pos) {
			if(pos >= values.size())
				return Integer.MAX_VALUE;
			else
				return values.get(pos).getIntegerValue();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Cursor [position=" + position + ", limit=" + limit
					+ ", max=" + max + "]";
		}		
		
	}
		
	@Override
	public Iterator<List<T>> iterator() {
		
		return new Iterator<List<T>>(){
			Stack<Cursor> cursors = new Stack<>();
			int target = values.size() > 0 ? values.get(0).getIntegerValue() : 0;
			int limit = sum(); 
			
			@Override
			public boolean hasNext() {
				return advance();
			}

			@Override
			public List<T> next() {			
				return makeNext();
			};
			
			@Override
			public void remove() {
				throw new RuntimeException("Unimplementd method");
			}
			
			private List<T> makeNext() {
				List<T> next = new ArrayList<>();
				
				for(Cursor cursor: cursors)
					next.add(values.get(cursor.position));
				
				return next;
			}

			private boolean advance() {
				boolean moved = false;
				if(cursors.isEmpty()) {
					cursors.push(new Cursor(values.size(),target));
					moved = true;
				}
	
				while(! cursors.isEmpty()) {
					Cursor top = cursors.peek();

					if(top.isValid()) {
						if(moved && top.reached() && cursors.size() == nPermutations)
							return true;
						
						Cursor add = new Cursor(top);
						if(add.isValid() && cursors.size() < nPermutations) 
							cursors.push(add);
						else
							top.advance();
					} else {
						while(! top.isValid()) {
							cursors.pop();
		
							if(cursors.isEmpty()) {
								if(target++ > limit)
									return false;
								else
									cursors.push(new Cursor(values.size(),target));
							}
							top = cursors.peek();
							top.advance();
						}	
					}
					moved = true;
				}
				return false;
			}

			@Override
			public void forEachRemaining(Consumer<? super List<T>> action) {
				// TODO Auto-generated method stub
				
			}
		};

	}
	@Override
	public void forEach(Consumer<? super List<T>> action) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Spliterator<List<T>> spliterator() {
		// TODO Auto-generated method stub
		return null;
	}
}
