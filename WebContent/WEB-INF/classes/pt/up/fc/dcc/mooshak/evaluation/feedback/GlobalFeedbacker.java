package pt.up.fc.dcc.mooshak.evaluation.feedback;

import static pt.up.fc.dcc.mooshak.content.types.Submission.Classification.ACCEPTED;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pt.up.fc.dcc.mooshak.content.util.Filenames;
import pt.up.fc.dcc.mooshak.evaluation.EvaluationParameters;
import pt.up.fc.dcc.mooshak.evaluation.ProgramAnalyzer.TestRun;

public class GlobalFeedbacker implements Feedbacker {

	private static GlobalFeedbacker globalFeedbacker = new GlobalFeedbacker();
	
	public static Feedbacker getInstance() {
		
		return globalFeedbacker;
	}
	
	private class Table {
		StringBuilder builder = new StringBuilder();
		
		Table() {
			builder.append("<table width=\"100%\" "
					+ "cellspacing=\"2\" cellpadding=\"2\" "
					+ "rules=\"all\" frame=\"box\">");
		}
		
		void startRow() {
			builder.append("<tr>");
		}
		
		void endRow() {
			builder.append("</tr>");
		}
		
		void addHeader(String data,String... parameters) {
			 addCell("th",data,parameters);
		}
		 
		void addData(String data,String... parameters) {
			 addCell("td",data,parameters);
		 }
		
		void addCell(String type,String header,String... parameters) {
			builder.append("<");
			builder.append(type);
			for(String parameter: parameters) {
				builder.append(" ");
				builder.append(parameter);
			}
			builder.append(">");
			builder.append(header);
			builder.append("</");
			builder.append(type);
			builder.append(">");
		}
		
		 void end() {
			builder.append("</table>");
		}
		
		public String toString() {
			return builder.toString();
		}

	}

	@Override
	public String summarize(List<TestRun> runs) {
		Table table = new Table();
		
		table.startRow();
		for(String header: Arrays.asList(
				"#","Result","Points","Hint", 
				// "Observation", 
				"Test Data")) 
			table.addHeader(header);
		table.endRow();
		
		int count = 0;
		
		Collections.sort(runs,new Comparator<TestRun>() {

			@Override
			public int compare(TestRun tr1, TestRun tr2) {

				if(tr1 == null || tr1.parameters == null || 
				   tr2 == null || tr2.parameters == null)
					return 0;
				else 
					return  tr1.parameters.getTestOrder() - 
						    tr2.parameters.getTestOrder();
			}});
		
		for(TestRun run: runs) {
			EvaluationParameters parameters = run.parameters;
			
			if(parameters == null)
				continue;
			
			String hint = parameters.getFeedback();
			String color = run.classification == ACCEPTED ? "green" : "red";
			String testData;
			
			if(parameters.isShow()) {
				String problemId = parameters.getProblem().getIdName();
				String testId = Filenames.getSafeFileName(parameters.getInput().getParent());
				String urlBase = "test-data/"+problemId+"/"+testId+"/";
				
				testData = "";
				testData += link(urlBase+"input","input");
				testData += "&nbsp;";
				testData += link(urlBase+"output","output");
			} else {
				testData = font("unavailable","grey");
			}
			
			count++;
			table.startRow();
			table.addData(Integer.toString(count));
			table.addData(font(run.classification.toString(),color));
			table.addData(Integer.toString(parameters.getPoints()));
			table.addData(hint == null ? "&nbsp;" : hint);
			// table.addData(run.observations);
			table.addData(testData);
			table.endRow();
		}
		table.end();
		
		return  "<br>"+table.toString();
	}

	private String font(String message,String color) {
		return "<font color=\""+color+"\">"+message+"</font>";
	}
	
	private String link(String url, String anchor) {
		return "<a target=\"_blank\" href=\""+url+"\">"+anchor+"</a>";
	}

}
