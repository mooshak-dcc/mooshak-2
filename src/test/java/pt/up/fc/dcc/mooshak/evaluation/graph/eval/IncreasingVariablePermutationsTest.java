package pt.up.fc.dcc.mooshak.evaluation.graph.eval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.evaluation.graph.eval.IncreasingVariablePermutations;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.IncreasingVariablePermutations.HasInteger;

public class IncreasingVariablePermutationsTest {

	static class MyInteger implements HasInteger, Comparable<MyInteger> {
		int value;
		
		MyInteger(int value) {
			this.value = value;
		}
		
		
		@Override
		public int compareTo(MyInteger o) {
			return value - o.value;
		}

		@Override
		public int getIntegerValue() {
			return value;
		}
		
		static List<MyInteger> makeList(int ...values) {
			List<MyInteger> list = new ArrayList<>();
			
			for(int value: values)
				list.add(new MyInteger(value));
			
			return list;
		}


		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return Integer.toString(value);
		}
		
		 
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testEmpty() {
		IncreasingVariablePermutations<MyInteger> adder = new IncreasingVariablePermutations<>(MyInteger.makeList());
		
		// show(adder);
		
		checkAllNumbersUpTo(1,adder);
		checkAllExpectedSums(adder);
	}
	
	@Test
	public void testOne() {
		IncreasingVariablePermutations<MyInteger> adder = new IncreasingVariablePermutations<>(MyInteger.makeList(1));
		
		// show(adder);
		
		checkAllNumbersUpTo(2,adder);
		checkAllExpectedSums(adder);
	}
	
	
	@Test
	public void testOneTwo() {
		IncreasingVariablePermutations<MyInteger> adder = new IncreasingVariablePermutations<>(MyInteger.makeList(1,2));
		
		// show(adder);
		
		checkAllExpectedSums(adder);
		checkIncreasingSums(adder);
	}
	
	@Test
	public void testDuplicates() {
		IncreasingVariablePermutations<MyInteger> adder = new IncreasingVariablePermutations<>(MyInteger.makeList(1,1));
		
		// show(adder);
		
		checkIncreasingSums(adder);
		checkAllExpectedSums(adder);
	}
	
	@Test
	public void testDoubleDuplicates() {
		IncreasingVariablePermutations<MyInteger> adder = new IncreasingVariablePermutations<>(MyInteger.makeList(1,1,2,2));
		
		// show(adder);
		
		checkIncreasingSums(adder);
		checkAllExpectedSums(adder);
	}
	
	@Test
	public void testTriplicates() {
		IncreasingVariablePermutations<MyInteger> adder = new IncreasingVariablePermutations<>(MyInteger.makeList(1,1,1));
		
		// show(adder);
		
		checkIncreasingSums(adder);
		checkAllExpectedSums(adder);
	}
	

	@Test
	public void testQuadricates() {
		IncreasingVariablePermutations<MyInteger> adder = new IncreasingVariablePermutations<>(MyInteger.makeList(1,1,1,1));
		
		// show(adder);
		
		checkIncreasingSums(adder);
		checkAllExpectedSums(adder);
	}
	
	@Test
	public void testDoubleQuadriplcates() {
		IncreasingVariablePermutations<MyInteger> adder = new IncreasingVariablePermutations<>(MyInteger.makeList(0,0,0,0,1,1,1,1));
		
		// show(adder);
		
		checkIncreasingSums(adder);
		checkAllExpectedSums(adder);
	}
	
	
	// @Test
	public void testWithLotsOfZeros() {
		List<MyInteger> list = new ArrayList<>();
		
		for(int n=1; n < 20; n++) {
			list.add(new MyInteger(0));
			IncreasingVariablePermutations<MyInteger> adder = new IncreasingVariablePermutations<>(list);
		
			// show(adder);
			
			int c = 0;
			for(@SuppressWarnings("unused") List<MyInteger> l: adder)
				c++;
			System.out.println(n+":"+c);
		
		
			checkIncreasingSums(adder);
			checkAllExpectedSums(adder);
		}
	}
	
	
	@Test
	public void testTripleQuadriplcates() {
		IncreasingVariablePermutations<MyInteger> adder = new IncreasingVariablePermutations<>(MyInteger.makeList(0,0,0,0,1,1,1,1,2,2,2,2));
		
		// show(adder);
		
		checkIncreasingSums(adder);
		// checkAllExpectedSums(adder);
	}
	
	@Test
	public void testSimple() {
		IncreasingVariablePermutations<MyInteger> adder = new IncreasingVariablePermutations<>(MyInteger.makeList(1,3,5));
		
		// show(adder);
		
		checkIncreasingSums(adder);
		checkAllExpectedSums(adder);
	}
	
	@Test
	public void testSequentialTo4() {
		IncreasingVariablePermutations<MyInteger> adder = new IncreasingVariablePermutations<>(MyInteger.makeList(1,2,3,4));
		
		// show(adder);
		
		checkIncreasingSums(adder);
		checkAllExpectedSums(adder);
		
	}
	
	@Test
	public void testSequentialTo6() {
		IncreasingVariablePermutations<MyInteger> adder = new IncreasingVariablePermutations<>(MyInteger.makeList(1,2,3,4,5,6));
		
		// show(adder);
		
		checkIncreasingSums(adder);
		checkAllExpectedSums(adder);
	}
	
	@Test
	public void testWithZeros() {
		IncreasingVariablePermutations<MyInteger> adder = new IncreasingVariablePermutations<>(MyInteger.makeList(0,0,1,2,3));
		
		// show(adder);
		
		checkIncreasingSums(adder);
		checkAllExpectedSums(adder);
	}
	
	@Test
	public void testPowersOf2UpTo8() {
		IncreasingVariablePermutations<MyInteger> adder = new IncreasingVariablePermutations<>(MyInteger.makeList(1,2,4,8));
		
		// show(adder);
		
		checkIncreasingSums(adder);
		checkAllNumbersUpTo(16,adder);
		checkAllExpectedSums(adder);
	}
	
	@Test
	public void testPowerOf2UpTo2power10() {
		testPowerOf2UpTo2power(10);
	}
	
	
	@Test
	public void testPowerOf2UpTo2power12() {
		testPowerOf2UpTo2power(12);
	}
	
	private void testPowerOf2UpTo2power(int n) {
		List<MyInteger> values = new ArrayList<>();
		IncreasingVariablePermutations<MyInteger> adder = new IncreasingVariablePermutations<>(values);
		
		for(int p=0; p<=n; p++)
			values.add(new MyInteger(1<<p));
		
		// show(adder);
		
		checkIncreasingSums(adder);
		// checkAllExpectedSums(adder);
	}
	
		
	private void checkAllNumbersUpTo(int limit, IncreasingVariablePermutations<MyInteger> adder) {
		int count = 0;
		for(List<MyInteger> seq: adder) {
			int sum = sum(seq);
			assertEquals(count++,sum);
		}
		assertEquals(limit,count);
	}
	
	private void checkIncreasingSums(IncreasingVariablePermutations<MyInteger> adder) {
		int prev = 0;
		for(List<MyInteger> seq: adder) {
			int sum = sum(seq);
			assertTrue(sum >= prev);
			prev=sum;
		}
	}
	
	@SuppressWarnings("unused")
	private void show(IncreasingVariablePermutations<MyInteger> adder) {
		
		for(List<MyInteger> seq: adder)
			System.out.println(String.format("%3d %s",sum(seq),seq));
		System.out.println("");
	}
	
	private void checkAllExpectedSums(IncreasingVariablePermutations<MyInteger> adder) {
		Map<Integer,Boolean> sums = new HashMap<>();
	
		allSums(0,sums,adder.getValues());
		for(List<MyInteger> seq: adder) {
			sums.put(sum(seq),true);
		}
		
		for(Integer key: sums.keySet())
			assertEquals(true,sums.get(key));
	}
	
	void allSums(int sum,Map<Integer,Boolean> sums,List<MyInteger> values) {
		
		sums.put(sum,false);
		if(values.size() > 0) {
			for(int i=0; i< values.size(); i++) {
				List<MyInteger> sub = new ArrayList<>(values);
				sub.remove(i);
				allSums(sum+values.get(i).getIntegerValue(),sums,sub);
			}
		}
	}
	
	
	private int sum(List<MyInteger> seq) {
		int sum = 0;
		
		for(MyInteger value: seq)
			sum += value.getIntegerValue();
		
		return sum;
	}

}
