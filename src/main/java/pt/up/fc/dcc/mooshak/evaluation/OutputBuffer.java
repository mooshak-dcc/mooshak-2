package pt.up.fc.dcc.mooshak.evaluation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Buffer for collecting data from process streams (stdout, stderr) 
 * Its capacity is configured to the maximum output defined in parameters
 * The toString() method returns the collected data as a string 
 */
class OutputBuffer extends Thread {
	private static final int DEFAULT_MAX_OUTPUT = 1<<12;
	private static final long WAIT_ON_IDLE = 100L;
	private static final int MAX_IDLE_READS = 500;
	
	private final static Logger LOGGER = Logger.getLogger("");
	
	private char buffer[];
	private int size = 0;
	private InputStream stream;
	private boolean running = true;
	
	public OutputBuffer(EvaluationParameters parameters, InputStream stream) {
		super("output-buffer");
		int max = parameters.getMaxOutput();
		
		buffer = new char[max > 0 ? max : DEFAULT_MAX_OUTPUT];
		
		this.stream = stream;
		start(); 
	}
	
	@Override
	public void run() {
		
		try(Reader in = new BufferedReader(new InputStreamReader(stream))) {
				int chunk;
				int idle = 0;
				while(running && 
						(chunk=in.read(buffer,size,buffer.length-size))>-1){
					size += chunk;
					
					if(chunk == 0) {
						try {
							Thread.sleep(WAIT_ON_IDLE);
						} catch (InterruptedException cause) {
							String messsage = "Waiting on idle reading";
							LOGGER.log(Level.WARNING,messsage,cause);
						}
						idle++;
					} else 
						idle = 0;
					// avoid long cycles reading 0 bytes
					if(idle > MAX_IDLE_READS) {
						String message = "Reading idle too many times ("
								+MAX_IDLE_READS+")";
						LOGGER.log(Level.WARNING,message);
						break;
					}
				}
				
		} catch (IOException cause) {
				LOGGER.log(Level.SEVERE,"Reading process ",cause);
		}
	}
	
	/** 
	 * Force this thread to terminate, even if still waiting for EOF
	 */
	public void terminate() {
		running = false;
	}
	

	/**
	 * Get current content of the output buffer
	 */
	@Override
	public String toString() {
		return new String(buffer,0,size);
	}
}
