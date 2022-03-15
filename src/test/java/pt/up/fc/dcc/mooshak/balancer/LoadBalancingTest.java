package pt.up.fc.dcc.mooshak.balancer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;

public class LoadBalancingTest {
	private static String JAVA_HOME = "/usr/java/jdk1.8.0/bin/";
	private static String SECURITY_ARGS = "-Djava.security.policy=server.policy";
	private static String CLASS_PATH = "WebContent/WEB-INF/classes";
	private static String MASTER_CLASS = "pt.up.fc.dcc.mooshak.balancer.MooshakMaster";
	
	static LoadBalancingService master = null;
	
	@BeforeClass 
	public static void setUpClass() throws Exception {
		// this is unsafe but for testing purposes is OK
		System.setProperty("java.security.policy", "client.policy");
		
		PersistentObject.setHome("home");
		
		startMaster();
		startWorker();
	}
	
	@Before
	public void setUp() throws Exception {
	}

	/*
	@Test
	public void test() throws RemoteException {
	
		System.out.println(master.echo("hello", 5));
		
		PersistentObject persistent = master.getPersistentObject();
		System.out.println(persistent);
		
		Clean clean = master.getClean();
		System.out.println(clean);
		
		
		Foo foo = master.getFoo();
		System.out.println(foo);
		
		Contest contest = master.getPersistentObject(TestData.CONTEST_PATHNAME);
		System.out.println(contest);
	}
	*/
	
	protected int count;
	
	@Test
	public void testLoadAllPO() throws Exception {
		
		Path data = PersistentObject.openPath("data").getAbsoluteFile();
		Path root = data.getParent();
		count = 0;
		
		Files.walkFileTree(data, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, 
					BasicFileAttributes attrs)
							throws IOException {

				if(Files.exists(dir.resolve(".data.tcl"))) {

					try {
						String pathname = root.relativize(dir).toString();
						System.out.println(pathname);
						PersistentObject remote = master.getPersistentObject(pathname);
						PersistentObject local = PersistentObject.openPath(pathname);
						org.junit.Assert.assertEquals(local,remote);
						count++;
					} catch (MooshakContentException e) {
						System.err.println(e.getMessage());
					}

					return FileVisitResult.CONTINUE;
				} else
					return FileVisitResult.CONTINUE;
			}
		} );
		
		System.out.println("loaded POs:"+count);
	}
	
	
	
	private static void startMaster() throws Exception {

		ProcessBuilder builder = new ProcessBuilder();
		List<String> command = builder.command();
		
		command.add(JAVA_HOME+"java");
		command.add(SECURITY_ARGS);
		command.add("-cp");
		command.add(CLASS_PATH);
		command.add(MASTER_CLASS);
		
		System.out.println(command);
		
		Process process = builder.start();
		
		new MonitorStream(process.getErrorStream(),System.err);
		new MonitorStream(process.getInputStream(),System.out);
		
		Thread.sleep(1000);
	}
	
	static class MonitorStream extends Thread {
		InputStream in;
		PrintStream out;
		
		MonitorStream(InputStream in,PrintStream out) {
			super("monitor");
			this.in = in;
			this.out=out;
			setName("Monitor Stream");
			start();
		}
		
		public void run() {
			try(Reader reader = new InputStreamReader(in);
				BufferedReader buffer = new BufferedReader(reader)) {
				String line;
				while((line = buffer.readLine()) != null)
					out.println(line);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private static void startWorker() {
		
		MooshakWorker worker = new MooshakWorker();
		master = worker.getLoadBalancingService(LoadBalancingService.RMI_URL);
	}

}
