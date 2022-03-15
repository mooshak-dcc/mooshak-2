package pt.up.fc.dcc.mooshak.evaluation.feedback;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.evaluation.EvaluationParameters;
import pt.up.fc.dcc.mooshak.evaluation.ProgramAnalyzer.TestRun;

/**
 * Generators of feedback for a given team/student and problem.
 * This class cannot be instantiated. Instead, an instance must be obtained 
 * for a given  team/student and problem, so that an existing feedbacker is 
 * reused when available.
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class IncrementalFeedbacker implements Feedbacker {

	private static final Logger LOGGER = Logger.getLogger("");
	
	private Set<List<String>> pastFeedback = new HashSet<>();
	
	static class Key {
		String team;
		String problem;
		public Key(String team, String problem) {
			super();
			this.team = team;
			this.problem = problem;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((problem == null) ? 0 : problem.hashCode());
			result = prime * result + ((team == null) ? 0 : team.hashCode());
			return result;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Key other = (Key) obj;
			if (problem == null) {
				if (other.problem != null)
					return false;
			} else if (!problem.equals(other.problem))
				return false;
			if (team == null) {
				if (other.team != null)
					return false;
			} else if (!team.equals(other.team))
				return false;
			return true;
		}
	}
	
	private static class FeedbackerCache extends LinkedHashMap<Key,IncrementalFeedbacker> {

		private static final long serialVersionUID = 1L;
		private static final int INITIAL_CAPACITY = 5000;
		private static final float LOAD_FACTOR = 0.75F;
		private static final boolean ACCESS_ORDER = true;

		private static int maxEntries = 5*INITIAL_CAPACITY;

		FeedbackerCache() {
			super(INITIAL_CAPACITY, LOAD_FACTOR, ACCESS_ORDER);
		}

		protected boolean removeEldestEntry(Map.Entry<Key,IncrementalFeedbacker> eldest) {

			if(size() > maxEntries) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	private static FeedbackerCache feedbackerCache = new FeedbackerCache();
	
	public IncrementalFeedbacker() {}
	
	public static IncrementalFeedbacker getFeedbacker(String team,String problem) {
		Key key = new Key(team,problem);
		IncrementalFeedbacker feedbacker;
		
		if(feedbackerCache.containsKey(key))
			feedbacker = feedbackerCache.get(key);
		else {
			feedbacker = new IncrementalFeedbacker();
			feedbackerCache.put(key,feedbacker);
		}
			
		return feedbacker;
	}

	interface FeebackSummarizer {
		List<String> summarize(Lang lang,List<TestRun> runs);
	}
	
	public enum Lang { EN, PT };
	
	public Lang lang = Lang.EN;
	
	/**
	 * Language in which feedback is provided (EN and PT)  
	 * @return the lang
	 */
	public Lang getLang() {
		return lang;
	}

	/**
	 * Set language in which feedback is provided (EN and PT)  
	 * @param lang the lang to set
	 */
	public void setLang(Lang lang) {
		this.lang = lang;
	}

	public enum SummaryFormat {
		INPUT_OUTPUT(
				"The input <pre>%s</pre> should return <pre>%s</pre>",
				"O input <pre>%s</pre devia retornar <pre>%s</pre>"),
		INPUT_RESULT(
				"The input <pre>%s</pre> produces <b>%s</b>",
				"The input <pre>%s</pre> produces <b>%s</b>"
				),
		HINT(
				"Hint: <pre>%s</pre>",
				"Sugestão: <pre>%s</pre>"
				),
		COUNT(
				"%2d tests with <b>%s</b>",
				"%2d testes com <b>%s</b>"
				),	
		COUNT_1(
				"1 test with <b>%s</b>",
				"1 teste com <b>%s</b>"
				),		
		OBSERVATION(
				"Observation of <b>%s</b> <pre>%s</pre>",
				"Observação de <b>%s</b> <pre>%s</pre>"
				);
		
		String en,pt;
		
		SummaryFormat(String en,String pt){
			this.en = en;
			this.pt = pt;
		}
		
		public String format(Lang lang,Object...values) {
			String format;
			
			switch(lang) {
			case EN:
				format = en;
				break;
			case PT:
				format = pt;
				break;
			default:
				throw new InvalidParameterException("Unknown language:"+lang);
			}
			
			return String.format(format,values);
		}
		
	}
	
	
	static List<FeebackSummarizer> summarizerTypes = Arrays.asList(
			new WorstClassificationObservation(),
			new CountWorstClassification(),
			new AllClassificationsObservations(),
			new CountClassifications(),
			new TestCaseFeedbackHint(),
			new TestCaseInputResult(),
			new TestCaseInputOutput()
			
			);
		
	
	public String summarize(List<TestRun> runs) {
		List<String> feedback = new ArrayList<>();
		
		for(FeebackSummarizer summarizer: summarizerTypes) {
			for(String message: summarizer.summarize(lang,runs)) {
				
				if(message == null || feedback.contains(message))
					continue;
				
				feedback.add(message);
				
				if(! pastFeedback.contains(feedback)) {
					pastFeedback.add(feedback);
										
					return feedback.stream().collect(Collectors.joining("\n"));
				}
			}
		}
		return null;
	}
	
	/**
	 *  Observations of worst classification (higher severity)
	 */
	static class WorstClassificationObservation implements FeebackSummarizer {

		@Override
		public List<String> summarize(Lang lang,List<TestRun> runs) {
			List<String> summary = new ArrayList<>();
			
			int worst=0;
			String observations = null;
			String classification = null;
			
			for(TestRun run: runs) {
				int severity = run.classification.ordinal(); 
				if(severity > worst) {
					worst = severity;
					observations = run.observations;
					classification = run.classification.toString();
				}
			}
			if(observations != null)
				summary.add(SummaryFormat.OBSERVATION.format(
						lang, classification, observations));
			
			return summary;
		}
	}
	
	/**
	 * Number of classifications of each type, ordered by descending frequency
	 * ex. 3 tests with Wrong answer \n 2 tests with Time Limit Exceeded
	 */
	static class CountWorstClassification implements FeebackSummarizer {

		@Override
		public List<String> summarize(Lang lang, List<TestRun> runs) {
			List<String> summary = new ArrayList<>();
			
			int worst=0;
			String classification = "";
			int count = 0;
			int ntests = 0;
			
			for(TestRun run: runs) {
				int severity = run.classification.ordinal();
				if(severity > worst) {
					worst = run.classification.ordinal();
					classification = run.classification.toString();
					count = 0;
				} else if (severity == worst) {
					if (classification.equals(""))
						classification = run.classification.toString();
					count ++;
				}
				ntests++;
			}
			
			//  this summary makes no sense with just one test case
			if(ntests < 2) 
				return summary;
			
			if(count == 1)
				summary.add(SummaryFormat.COUNT_1.format(lang,classification));
			else
				summary.add(SummaryFormat.COUNT.
						format(lang,count,classification));
			
			return summary;
		}
	
	}
	
	/**
	 * Other observations (higher severity)
	 */
	static class AllClassificationsObservations implements FeebackSummarizer {

		@Override
		public List<String> summarize(Lang lang, List<TestRun> runs) {
			List<String> summary = new ArrayList<>();
			
			for(TestRun run: runs) {
				if(! "".equals(run.observations)) 
					summary.add(SummaryFormat.OBSERVATION.format(
						lang, run.classification, run.observations));
			}
			
			return summary;
		}
	}
	
	/**
	 * Number of classifications of each type, ordered by descending frequency
	 *  ex. 3 tests with Wrong answer \n 2 tests with Time Limit Exceeded
	 */
	static class  CountClassifications implements FeebackSummarizer {

		@Override
		public List<String> summarize(Lang lang, List<TestRun> runs) {
			List<String> summary = new ArrayList<>();
			Map<Classification,Integer> count = new HashMap<>();
			int ntests = 0; 
			
			for(TestRun run: runs) {
				count.merge(run.classification, 1,(x,y) -> x+y); 
				ntests++;
			}
			
			//  this summary makes no sense with just one on no test cases
			if(ntests < 2 )				
				return summary;
				
			List<Classification> classifications = new ArrayList<>(count.keySet());
			
			Collections.sort(classifications,new Comparator<Classification>(){

				@Override
				public int compare(Classification o1, Classification o2) {
					return count.get(o1) - count.get(o2);
				}});
			
			for(Classification classification: classifications) {
				if(count.get(classification) == 1)
					summary.add(SummaryFormat.COUNT_1.format(lang,classification));
				else
					summary.add(SummaryFormat.COUNT.format(lang,
							count.get(classification), classification));
			}		
			
			return summary;
		}
	}
	
	/**
	 *  Returns non-empty feedback messages associated with 
	 *  non-accepted test cases
	 */
	static class TestCaseFeedbackHint  implements FeebackSummarizer {

		@Override
		public List<String> summarize(Lang lang, List<TestRun> runs) {
			List<String> summary = new ArrayList<>();
			
			for(TestRun run: runs) {
				EvaluationParameters parameters = run.parameters;
				
				if(parameters == null)
					continue;
				
				switch (run.classification) {
				case ACCEPTED:
				case REQUIRES_REEVALUATION:
					break;
				default: 
					
					String hint = parameters.getFeedback();
					
					if(hint != null)
						summary.add(SummaryFormat.HINT.format(lang, hint));	
				}
			}
			return summary;
		}
	}

	/**
	 * Returns selected input files associated with non-accepted test cases
	 */
	static class TestCaseInputResult implements FeebackSummarizer {
		
		@Override
		public List<String> summarize(Lang lang, List<TestRun> runs) {
			List<String> summary = new ArrayList<>();
			
			for(TestRun run: runs) {
				EvaluationParameters parameters = run.parameters;
				
				if(parameters == null)
					continue;
				
				switch (run.classification) {
				case ACCEPTED:
				case REQUIRES_REEVALUATION:
					break;
				default:
					Path inputPath = parameters.getInput();
					
					if(parameters.isShow() 	&& 
						inputPath != null 				&&
						Files.isReadable(inputPath)) {
						try {
							String text = 
									new String(Files.readAllBytes(inputPath));
						
							summary.add(SummaryFormat.INPUT_RESULT.format(lang, 
								text,run.classification));
						} catch (IOException cause) {
							LOGGER.log(Level.WARNING,
									"I/O error on feedback files",cause);
						
						}
					}
					
				}
			}
			
			return summary;
		}
	}

	/**
	 *  Returns selected input and output files 
	 *  associated with non-accepted test cases
	 */
	static class TestCaseInputOutput implements FeebackSummarizer {
		
		@Override
		public List<String> summarize(Lang lang, List<TestRun> runs) {
			List<String> summary = new ArrayList<>();
			
			for(TestRun run: runs) {
				EvaluationParameters parameters = run.parameters;
				
				if(parameters == null)
					continue;
				
				switch (run.classification) {
				case ACCEPTED:
				case REQUIRES_REEVALUATION:
					break;
				default:
					Path inputPath = parameters.getInput();
					Path expectedPath = parameters.getExpected();
					
					if(parameters.isShow() 				&& 
							inputPath != null 				&&
							expectedPath != null			&&
							Files.isReadable(inputPath) 	&&
							Files.isReadable(expectedPath)) {
						
						try {
							String inputText = 
									new String(Files.readAllBytes(inputPath));
							String expectedText = 
									new String(Files.readAllBytes(expectedPath));
							
							summary.add(SummaryFormat.INPUT_OUTPUT.format(lang,
									inputText,expectedText));
						} catch (IOException cause) {
							LOGGER.log(Level.WARNING,
									"I/O error on feedback files",cause);
						
						}
					}
					
				}
			}
			
			return summary;
		}
	}
}
