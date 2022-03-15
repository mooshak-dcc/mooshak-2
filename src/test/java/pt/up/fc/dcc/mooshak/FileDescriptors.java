package pt.up.fc.dcc.mooshak;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

import org.junit.Test;

/**
 * Check open file descriptors of the JVM
 * 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class FileDescriptors {
	
	
	static String pid = getPid();
	
	@Test
	public void testPid() throws IOException {
		System.out.println(pid+":"+countWith("/data/")+":"+countUnusual());
	}
	
	/**
	 * Count unusual file descriptors, 
	 * i.e. those that should not normally be open
	 * 
	 * @return
	 * @throws IOException
	 */
	public static int countUnusual() throws IOException {
		return countWith(FileDescriptors::isUnusual);
	}
	
	/**
	 * Count file descriptors of files whose absolute pathname
	 * contains given name
	 *   
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public static int countWith(String name) throws IOException {
		return countWith(pathname -> pathname.contains(name));
	}
	
	/**
	 * Count file descriptors of files whose absolute pathname is matched
	 * by given predicate
	 * 
	 * @param predicate
	 * @return
	 * @throws IOException
	 */
	public static int countWith(Predicate<String> predicate) throws IOException {
		Path fdProcPath = Paths.get(String.format("/proc/%s/fd",pid));
		int count = 0;
		
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(fdProcPath)) {
			for(Path link : stream) 
				if(Files.isSymbolicLink(link)) {
					Path path = Files.readSymbolicLink(link);
					String pathname = path.toString();
					
					if(predicate.test(pathname)) {
						System.out.println(pathname);
						count++;
					}
				}
		}
		return count;
	}
	
	static boolean isUnusual(String pathname) {
		return ! (pathname.startsWith("pipe:[") 			||
				pathname.startsWith("socket:[") 			||
				pathname.startsWith("/dev/") 				||
				pathname.startsWith("/proc/") 				||
				pathname.startsWith("anon_inode:inotify") 	||
				pathname.endsWith(".jar"));
	}
	

	static String getPid() {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		return name.split("@")[0];
	}
	
}
