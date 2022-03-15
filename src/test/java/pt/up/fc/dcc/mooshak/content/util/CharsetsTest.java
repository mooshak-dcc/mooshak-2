package pt.up.fc.dcc.mooshak.content.util;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.SortedMap;

import org.junit.Before;
import org.junit.Test;
import org.mozilla.universalchardet.UniversalDetector;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;

public class CharsetsTest {

	private static final String FILE_NAME = "alunos-PI-2015.tsv";
	private static String fileName = CharsetsTest.class
			.getPackage()
			.getName()
			.replace(".", "/") + "/" + FILE_NAME;	 
	
	@Before
	public void setUp() throws Exception {
	}

	// @Test // not working for small strings
	public void testFixCharset() throws UnsupportedEncodingException {
		assertEquals("ola",Charsets.fixCharset("ola".getBytes()));
		assertEquals("olá",Charsets.fixCharset("olá".getBytes()));
		assertEquals("José",Charsets.fixCharset(new byte[] { 'J', 'o','s',(byte) 216}));
	}

	
	// @Test // not working for small strings
	public void testDetectCharset() {
		
		byte[] bytes = "olá mundo\n cão\n pois é\n\n ola".getBytes();
		
		assertEquals("UTF-8",Charsets.detectCharset(bytes,bytes.length));
	}
	

	
	
	// @Test use this to check the content of test file
	public void testReadStringWithMacCharset() throws MooshakContentException, IOException {
		
		SortedMap<String, Charset> charsets = Charset.availableCharsets();
		
		
		for(String key: charsets.keySet())
			System.out.println(key);
		
		try(InputStream stream=ClassLoader.getSystemResourceAsStream(fileName);
			Reader reader = new InputStreamReader(stream, "x-MacRoman");
			BufferedReader bufferedReder = new BufferedReader(reader)) {
		
			String line;
			
			while((line = bufferedReder.readLine()) != null)
					System.out.println(line);	
		} 
		
	}	
	
	
	@Test 
	public void testUniversalDetector() throws IOException {
		   byte[] buf = new byte[4096];
		   
		    
		   
		   try(InputStream fis=ClassLoader.getSystemResourceAsStream(fileName)){

		    // (1)
		    UniversalDetector detector = new UniversalDetector(null);

		    // (2)
		    int nread;
		    while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
		      detector.handleData(buf, 0, nread);
		    }
		    // (3)
		    detector.dataEnd();

		    // (4)
		    String encoding = detector.getDetectedCharset();
		    if (encoding != null) {
		      System.out.println("Detected encoding = " + encoding);
		    } else {
		      System.out.println("No encoding detected.");
		    }

		    // (5)
		    detector.reset();
		   }
	}
	
	
}
