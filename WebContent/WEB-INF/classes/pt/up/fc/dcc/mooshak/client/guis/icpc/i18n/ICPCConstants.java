package pt.up.fc.dcc.mooshak.client.guis.icpc.i18n;

import com.google.gwt.i18n.client.Constants;

public interface ICPCConstants extends Constants {
	
	@DefaultStringValue("Domain")
	String domain();
	@DefaultStringValue("User")
	String user();
	@DefaultStringValue("Password")
	String password();
	@DefaultStringValue("Confirm Password")
	String confirmPassword();
	@DefaultStringValue("Login")
	String login();
	@DefaultStringValue("Guest")
	String guest();
	@DefaultStringValue("Register")
	String register();

	@DefaultStringValue("View")
	String view();
	
	@DefaultStringValue("Program")
	String program();
	
	@DefaultStringValue("Ask")
	String ask();
	
	@DefaultStringValue("Answered")
	String answered();
	
	@DefaultStringValue("Feedback")
	String feedback();
	
	@DefaultStringValue("Submit")
	String submit();
	
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

	@DefaultStringValue("Submissions")
	String submissions();
	
	@DefaultStringValue("Questions")
	String questions();
	
	@DefaultStringValue("Printouts")
	String printouts();
	
	@DefaultStringValue("Balloons")
	String balloons();

	@DefaultStringValue("Statistics")
	String statistics();
	
	@DefaultStringValue("Rankings")
	String rankings();
	
	@DefaultStringValue("Pending")
	String pending();
	

	
	@DefaultStringValue("Contest Name")
	String contestName();
	@DefaultStringValue("Team Name")
	String teamName();
	@DefaultStringValue("Open Mooshak Tutorial")
	String tutorialTip();
	@DefaultStringValue("Logout")
	String logout();
	@DefaultStringValue("Do you really want to proceed with logout?")
	String logoutConfirmation();
	
	@DefaultStringValue("Subject")
	String subject();
	@DefaultStringValue("Question")
	String question();
	@DefaultStringValue("Answer")
	String answer();

	@DefaultStringValue("Program file name")
	String programName();
	
	

	@DefaultStringValue("Time")
	String time();
	@DefaultStringValue("Team")
	String team();
	@DefaultStringValue("Group")
	String group();
	@DefaultStringValue("Problem")
	String problem();
	@DefaultStringValue("Classification")
	String classification();
	@DefaultStringValue("State")
	String state();
	@DefaultStringValue("Rank")
	String rank();
	@DefaultStringValue("Solved")
	String solved();
	@DefaultStringValue("Points")
	String points();
	@DefaultStringValue("Mark")
	String mark();
	
	
	
	/** Tutorial texts **/
	@DefaultStringValue("Click \nhere\n to leave")
	String leaveTutorial();
	@DefaultStringValue("Do you want to hide this tutorial permanently?")
	String hidePermanently();
	@DefaultStringValue("Use mouse wheel to navigate through the tutorial")
	String navTip();
	@DefaultStringValue("Select a statistic to view")
	String selectStat();
	@DefaultStringValue("Select an action"
			+ "\n\tView: area to read the problem"
			+ "\n\tProgram: area to edit and test code"
			+ "\n\tAsk: area to ask a question related to the problem"
			+ "\n\tAnswered: area to view answers to questions")
	String actionsText();
	@DefaultStringValue("Select a problem")
	String selectProblem();
	@DefaultStringValue("Click an header to sort"
			+ " \n the table by its column")
	String gridHeader();
	@DefaultStringValue("View all submissions to this contest")
	String submissionsText();
	@DefaultStringValue("View all questions on this contest")
	String questionsText();
	@DefaultStringValue("View all printouts on this contest")
	String printoutsText();
	@DefaultStringValue("View all balloons on this contest")
	String balloonsText();
	@DefaultStringValue("View ranking of the contest")
	String rankingsText();
	@DefaultStringValue("You can use a local"
			+ " file \nchoosing it here or dropping it\n anywhere"
			+ " in the Mooshak window")
	String fileUploadText();
	@DefaultStringValue("You must place a"
			+ " name\n here with the extension to your file.\nThe"
			+ " editor will automatically\nadjust the highlighting"
			+ " for the choosen extension")
	String fileNameText();
	@DefaultStringValue("Here you can "
			+ "code \n or edit an uploaded answer to the problem. "
			+ "\nThis editor offers syntax highlighting \nand "
			+ "annotates lines with compilation errors.")
	String fileEditorText();
	@DefaultStringValue("You can see "
				+ "compilation\n and execution errors here")
	String observationsText();
	@DefaultStringValue("To send a solution"
			+ " click Submit\nTo test a solution click Validate\n"
			+ "To print the program click Print")
	String programButtonsText();
	@DefaultStringValue("You can add some"
			+ " inputs \nhere by clicking the desired\n"
			+ " line and Validate your code to get "
			+ "\nthe outputs given to them")
	String inputOutputText();
	@DefaultStringValue("Here you can read \nthe statement of the problem")
	String viewText();
	@DefaultStringValue("Here you can ask a question \nrelated to the "
			+ "selected problem.\nMake sure you fill the "
			+ "subject\n and the question areas.")
	String askText();
	@DefaultStringValue("Click a line on"
			+ " this grid \nto view the answer to the desired "
			+ "question")
	String answeredGridClick();
	@DefaultStringValue("Here you can ask a question or see"
			+ " a question\n detail after you click a line \n"
			+ "on the grid")
	String answeredDetail();
	

	@DefaultStringValue("Answer Notification")
	String notificationTitle();
	@DefaultStringValue("An answer was given to the question: ")
	String notification();
	
	@DefaultStringValue("Change editor content to this submission program")
	String resetEditor();
	
	@DefaultStringValue("Are you sure you want to replace the editor content by this submission?")
	String replaceSubmissionConfirmation();

	@DefaultStringValue("Export")
	String export();
	@DefaultStringValue("Download")
	String download();
	@DefaultStringValue("Close")
	String close();
	@DefaultStringValue("Cancel")
	String cancel();

	@DefaultStringValue("Contest Progress")
	String contestProgressChartTitle();
	@DefaultStringValue("Submissions Result")
	String submissionsResultsChartTitle();
}
