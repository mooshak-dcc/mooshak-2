package pt.up.fc.dcc.mooshak.client.guis.judge.i18n;

import com.google.gwt.i18n.client.Constants;

public interface JudgeConstants extends Constants {
	
	@DefaultStringValue("View")
	String view();
	
	@DefaultStringValue("Program")
	String program();
	
	@DefaultStringValue("Ask")
	String ask();
	
	@DefaultStringValue("Answer")
	String answer();
	
	@DefaultStringValue("Warning")
	String warning();
	
	@DefaultStringValue("Send Answer")
	String sendAnswer();
	
	@DefaultStringValue("Send Warning")
	String sendWarning();
	
	@DefaultStringValue("Write a Warning")
	String writeWarning();
	
	@DefaultStringValue("Submit")
	String submit();
	
	@DefaultStringValue("Close")
	String close();
	
	@DefaultStringValue("Reevaluate")
	String reevaluate();
	
	@DefaultStringValue("View Statement")
	String viewStatement();
	
	@DefaultStringValue("Clear")
	String clear();
	
	@DefaultStringValue("Validate")
	String validate();
	
	@DefaultStringValue("Print")
	String print();
	
	@DefaultStringValue("Submitted")
	String submitted();
	
	@DefaultStringValue("Processing")
	String processing();
	
	
	
	@DefaultStringValue("Problems")
	String problems();
	
	@DefaultStringValue("Problem")
	String problem();
	
	@DefaultStringValue("Comment a Problem")
	String comment();

	@DefaultStringValue("Submissions")
	String submissions();
	
	@DefaultStringValue("Questions")
	String questions();
	
	@DefaultStringValue("Printouts")
	String printouts();
	
	@DefaultStringValue("Balloons")
	String balloons();
	
	@DefaultStringValue("Rankings")
	String rankings();
	
	@DefaultStringValue("Pending")
	String pending();
	
	// submission states
	@DefaultStringValue("Accepted")
	String accepted();
	@DefaultStringValue("Presentation Error")
	String presentation_error();
	@DefaultStringValue("Wrong Answer")
	String wrong_answer();
	@DefaultStringValue("Output Limit Exceeded")
	String output_limit();
	@DefaultStringValue("Memory Limit Exceeded")
	String memory_limit();
	@DefaultStringValue("Time Limit Exceeded")
	String time_limit();
	@DefaultStringValue("Invalid Function")
	String invalid_function();
	@DefaultStringValue("Runtime Error")
	String runtime_error();
	@DefaultStringValue("Compile Time Error")
	String compile_error();
	@DefaultStringValue("Invalid Submission")
	String invalid_submission();
	@DefaultStringValue("Program Size Exceeded")
	String size_exceeded();
	@DefaultStringValue("Requires Reevaluation")
	String requires_reevaluation();
	@DefaultStringValue("Evaluating")
	String evaluating();
	
}
