package pt.up.fc.dcc.mooshak.evaluation.graph;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class DoubleIterator {
	
	
	@Test 
	public void testDouble() {
		
		List<Integer> list1 = Arrays.asList(1,2,3,4,5);
		List<Integer> list2 = Arrays.asList(6,7,8,9,10);
		List<Integer> list3 = Arrays.asList(11,12,13,14,15);
		
		List<List<Integer>> lists = Arrays.asList(list1,list2,list3);
		Iterator<List<Integer>> iterator = lists.iterator();
				
		for(int n: iterator.next())
			System.out.println(n);
		System.out.println("--");
		for(int n: iterator.next())
			System.out.println(n);
		System.out.println("--");
		for(int n: iterator.next())
			System.out.println(n);
		
		System.out.println("--");
		for(List<Integer> list: lists)
			for(int n: list)
				System.out.println(n);
		
	}
	

}
