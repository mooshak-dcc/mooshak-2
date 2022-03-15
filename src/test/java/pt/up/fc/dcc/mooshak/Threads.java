package pt.up.fc.dcc.mooshak;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Static methods for checking threads
 * 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class Threads {

	private static final long WAIT_FOR_THREAD_TERMINATION = 200L;

	private static final Set<ExpectedThread> EXPECTED = new HashSet<>();
	
	static class ExpectedThread {
		Pattern pattern;
		int count;
		
		ExpectedThread(String patternString, int count) {
			this.pattern = Pattern.compile(patternString);
			this.count = count;
		}
	}
	
	/**
	 * Clear all previously registered thread names and count
	 */
	public static void clearRegistredExpectedThreadNames() {
		EXPECTED.clear();
	}
	
	/**
	 * Register a thread name pattern with a certain maximum count
	 * @param pattern
	 * @param count
	 */
	public static void registerExpectedThreadName(String pattern,int count) {
		EXPECTED.add(new ExpectedThread(pattern,count));
	}
	
	/**
	 * Evaluate method and report unexpected threads, either those that
	 * are already running at the beginning, or those matching registered 
	 * thread name patterns
	 * 
	 * @param runnable
	 * @return
	 */
	public static boolean withoutUnexpectedThreads(Runnable runnable) {
		Set<Thread> initialThreadCount = getCurrentThreads();
		
		runnable.run();
		
		return wait4NewThreads(initialThreadCount, WAIT_FOR_THREAD_TERMINATION);
	}
	
		
	/**
	 * Set of threads currently running
	 * @return
	 */
	public static Set<Thread> getCurrentThreads() {
		return Thread.getAllStackTraces().keySet();
	}
	
	/**
	 * Wait for new threads to terminate
	 * @param initialThreads set of old threads
	 * @param max time to wait (in milliseconds)
	 * @return  {@code true} if they all terminated within the limit; 
	 * 			{@code false} otherwise 
	 */
	public static boolean wait4NewThreads(Set<Thread> initialThreads, long max) {
		long start = System.currentTimeMillis();
		
		do {
			int count = 0;
		
			for(Thread thread: getCurrentThreads()) 
				if(unexpectedThread(thread,initialThreads))
					count++;
			
			if(count > 0) {
				try {
					Thread.sleep(100L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else
				return true;
			max --;
		} while(System.currentTimeMillis() - start < max);
		
		for(Thread thread: getCurrentThreads())
			if(unexpectedThread(thread,initialThreads))
				System.out.println(thread.getName());

		
		return false;
	}

	/**
	 * Check if thread is expected, either because if is part of given list
	 * or because its name follows an expected pattern (bellow a certain count)  
	 * @param thread
	 * @param initialThreads
	 * @return
	 */
	private static boolean unexpectedThread(Thread thread,
			Set<Thread> initialThreads) {
		
		if(initialThreads.contains(thread))
			return false;
		
		String name = thread.getName();
		Map<Pattern,Integer> counters = new HashMap<>();
		
		for(ExpectedThread expected: EXPECTED)
			if(expected.pattern.matcher(name).matches()) {
				int count = counters.merge(expected.pattern, 1, (a,b) -> a+b);
				
				return count > expected.count;
			}
		
		return true;
	}
}
