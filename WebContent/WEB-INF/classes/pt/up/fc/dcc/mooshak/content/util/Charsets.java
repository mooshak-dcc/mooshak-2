package pt.up.fc.dcc.mooshak.content.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import org.mozilla.universalchardet.UniversalDetector;

/**
 * Character set handling
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class Charsets {

	protected static final SortedMap<String, Charset> CHARSETS = 
			Charset.availableCharsets();
	private static final String[] PREFERED_CHARSETS = {
		"ISO-8859-1", 
		"UTF-8", 
		Charset.defaultCharset().name()
	};
	protected static final List<String> CHARSET_NAMES = 
			new ArrayList<String>(CHARSETS.keySet());
	
	static {

		for(String name: PREFERED_CHARSETS) {
			if(CHARSET_NAMES.remove(name)) {
				CHARSET_NAMES.add(0, name);
			} else
				System.err.println("invalid charset: "+name);
		}		
	}
	
	
	
	

	public static String fixCharset(byte[] bytes) {
		String encoded = null;
		ByteBuffer in = ByteBuffer.wrap(bytes);
		
		for(String charsetName: CHARSET_NAMES) {
			System.out.println(charsetName);
			Charset charset = CHARSETS.get(charsetName);
			CharsetDecoder decoder = charset.newDecoder();
			CharBuffer out = CharBuffer.allocate(bytes.length);
			
			in.rewind();
			CoderResult result  = decoder.decode(in,out,true);
			if(! result.isError()) {
				encoded = new String(out.array(),0,out.position());
				break;
			}
				
		}
		return encoded;	
	}

	
	
	public static String detectCharset(byte[] buffer,int length) {
		
		UniversalDetector detector = new UniversalDetector(null);
		
		detector.handleData(buffer,0,length);
		
		if(detector.isDone()) {
			detector.dataEnd();
	
			return detector.getDetectedCharset();
		} else 
			return null;

	}
	
}
