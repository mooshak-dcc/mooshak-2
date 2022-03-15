package pt.up.fc.dcc.mooshak.content.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for a script file
 * 
 * @author josepaiva
 */
public class ScriptFileParser {

	private static final String REGEX_AUDIT_LOG_LINE = "^([0-9]{4}-[0-9]{2}-[0-9]{2})\\s"
			+ "([0-9]{2}:[0-9]{2}:[0-9]{2})\\s([^\\s]+)\\s([0-9]+)\\s([a-zA-Z0-9]+)\\s"
			+ "((([^\\s\\[]*|\\[[^\\]]*\\])\\s)*)\\s?$";
	private static final String REGEX_EXTRACT_CONTENT_PARAMETER = "(\\[[^\\]\\]]*\\])";
	private static final String REGEX_EXTRACT_STRING_PARAMETER = "([^\\s\\[]+)";
	private static final Pattern PATTERN_AUDIT_LOG_LINE = Pattern.compile(REGEX_AUDIT_LOG_LINE);
	private static final Pattern PATTERN_CONTENT_PARAMETER = Pattern.compile(REGEX_EXTRACT_CONTENT_PARAMETER);
	private static final Pattern PATTERN_STRING_PARAMETER = Pattern.compile(REGEX_EXTRACT_STRING_PARAMETER);
	
	private BufferedReader reader = null;
	private LineNumberReader lineReader = null;
	
	private Matcher matcher = PATTERN_AUDIT_LOG_LINE.matcher("");
	
	public ScriptFileParser(String script) throws IOException {
		InputStream is = new ByteArrayInputStream(script.getBytes());
		reader = new BufferedReader(new InputStreamReader(is));
		lineReader = new LineNumberReader(reader);
	}

	/**
	 * Parse the script
	 * @return a list of a list of tokens per task
	 */
	public List<List<String>> parseScript() throws IOException {
		List<List<String>> result = new ArrayList<>();
		
		String line = null;
		while ((line = lineReader.readLine()) != null) {
			matcher.reset(line); 
			if (matcher.find()) {
				List<String> lineResult = new ArrayList<String>();

				lineResult.add(matcher.group(0));
				lineResult.add(matcher.group(1));
				lineResult.add(matcher.group(2));
				lineResult.add(matcher.group(3));
				lineResult.add(matcher.group(4));
				lineResult.add(matcher.group(5));

				if (matcher.group(6) != null) {
					String parameters = matcher.group(6).trim();
					
					List<String> contentParams = new ArrayList<String>();
					Matcher contentMatcher = PATTERN_CONTENT_PARAMETER.matcher(parameters);
					while (contentMatcher.find()) {
						contentParams.add(contentMatcher.group(1));
						parameters = parameters.replace(contentMatcher.group(1), "");
						parameters = parameters.trim();
					}
					
					Matcher wordMatcher = PATTERN_STRING_PARAMETER.matcher(parameters);
					while (wordMatcher.find()) {
						lineResult.add(wordMatcher.group(1));
						parameters = parameters.replace(wordMatcher.group(1), "");
						parameters = parameters.trim();
					}
					lineResult.addAll(contentParams);
				}
				
				result.add(lineResult);
			}
		}
		
		close();
		
		return result;
	}
	
	/**
	 * Close the file reader
	 * @throws IOException 
	 */
	public void close() throws IOException {
		reader.close();
		lineReader.close();
	}
}
