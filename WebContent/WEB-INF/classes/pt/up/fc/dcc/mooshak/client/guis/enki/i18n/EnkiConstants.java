package pt.up.fc.dcc.mooshak.client.guis.enki.i18n;

import com.google.gwt.i18n.client.Constants;

public interface EnkiConstants extends Constants {
	
	@DefaultStringValue("Domain")
	String domain();
	@DefaultStringValue("User")
	String user();
	@DefaultStringValue("Password")
	String password();
	@DefaultStringValue("Login")
	String login();

	@DefaultStringValue("Submit")
	String submit();
	@DefaultStringValue("A submission sends the program to evaluation. Although you can do more than one, the number of trials is registered.")
	String submitTip();

	@DefaultStringValue("Clear")
	String clear();
	@DefaultStringValue("Copy")
	String copy();
	@DefaultStringValue("Paste")
	String paste();
	@DefaultStringValue("Clears the panel contents")
	String clearDiagramTip();
	@DefaultStringValue("Do you really want to clear the diagram in the panel?")
	String clearDiagramConfirmation();

	@DefaultStringValue("FullScreen")
	String fullscreen();
	@DefaultStringValue("Enter fullscreen")
	String fullscreenTip();
	@DefaultStringValue("Exit FullScreen")
	String exitFullscreen();
	@DefaultStringValue("Exit fullscreen")
	String exitFullscreenTip();
	
	@DefaultStringValue("Validate")
	String validate();
	@DefaultStringValue("The validation works as follows:\n - For public test cases, if the output of the program coincides with the value in the output column, then the test row is marked with green, otherwise it is marked with red;\n - For inputs written by you, it is placed in the output column the value obtained with your program fed with the value in the input column")
	String validateTip();
	@DefaultStringValue("The validation does not register the trial")
	String validateDiagramTip();
	
	@DefaultStringValue("Print")
	String print();
	
	@DefaultStringValue("Submitted")
	String submitted();
	
	@DefaultStringValue("Processing")
	String processing();
	
	
	
	@DefaultStringValue("Contest Name")
	String contestName();
	@DefaultStringValue("Team Name")
	String teamName();
	@DefaultStringValue("Open Mooshak Tutorial")
	String tutorialTip();
	@DefaultStringValue("Logout")
	String logout();
	@DefaultStringValue("Tutorial")
	String help();
	@DefaultStringValue("Customize")
	String customize();
	@DefaultStringValue("Do you really want to proceed with logout?")
	String logoutConfirmation();
	
	@DefaultStringValue("Subject")
	String subject();
	@DefaultStringValue("Question")
	String question();
	@DefaultStringValue("Answer")
	String answer();

	@DefaultStringValue("Progam file name")
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

	@DefaultStringValue("Empty skeleton")
	String noSkeleton();
	
	/** Tab Titles **/
	@DefaultStringValue("Achievements")
	String achievements();
	@DefaultStringValue("Ask a Question")
	String askQuestion();
	@DefaultStringValue("Leaderboard")
	String leaderboard();
	@DefaultStringValue("My Data")
	String myData();
	@DefaultStringValue("My Submissions")
	String mySubmissions();
	@DefaultStringValue("Editor")
	String editor();
	@DefaultStringValue("Diagram Editor")
	String diagramEditor();
	@DefaultStringValue("Observations")
	String observations();
	@DefaultStringValue("Error List")
	String errorList();
	@DefaultStringValue("Tests")
	String tests();
	@DefaultStringValue("Related Resources")
	String relatedResources();
	@DefaultStringValue("Rate this Resource")
	String rateResource();
	@DefaultStringValue("Resources")
	String resources();
	@DefaultStringValue("Skeleton")
	String skeleton();
	@DefaultStringValue("Statistics")
	String statistics();
	@DefaultStringValue("Video Player")
	String videoPlayer();
	@DefaultStringValue("Statement")
	String statement();
	@DefaultStringValue("PDF Resource")
	String pdfResource();
	@DefaultStringValue("Game Submission")
	String gameSubmission();
	@DefaultStringValue("Game Viewer")
	String gameViewer();
	@DefaultStringValue("Quiz")
	String quiz();
	
	/** My Profile **/
	@DefaultStringValue("Participant Name")
	String participantName();
	@DefaultStringValue("Registration Date")
	String regDate();
	@DefaultStringValue("Number of Exercises Solved")
	String solvedExercises();
	@DefaultStringValue("Number of Videos Seen")
	String seenVideos();
	@DefaultStringValue("Number of Static Resources Seen")
	String seenStatic();
	@DefaultStringValue("Number of Submissions")
	String numberSubmissions();
	@DefaultStringValue("Number of Accepted Submissions")
	String numberAcceptedSubmissions();
	@DefaultStringValue("Current Part")
	String currentPart();
	
	/** Leaderboard **/
	@DefaultStringValue("Player")
	String player();
	@DefaultStringValue("Score")
	String score();
	@DefaultStringValue("No Data to Display")
	String noDataToDisplay();
	
	/** Error List **/
	@DefaultStringValue("Type")
	String type();
	@DefaultStringValue("Description")
	String description();
	
	/** Resource Rating **/
	@DefaultStringValue("Tell others what you think about this resource. Would you recommend it and why?")
	String commentPlaceholder();
	@DefaultStringValue("Rate")
	String rate();
	
	/** Stats Chart **/
	@DefaultStringValue("Solved At 1st submission")
	String solvedAt1st();
	@DefaultStringValue("Solved At 2nd submission")
	String solvedAt2nd();
	@DefaultStringValue("Solved At 3rd submission")
	String solvedAt3rd();
	@DefaultStringValue("Solved At 4+ submission")
	String solvedAt4thMore();
	@DefaultStringValue("Not Yet Solved")
	String notSolved();
	
	/** Resources Tree **/
	@DefaultStringValue("Resource Unavailable! Please finish previous resources first")
	String unavailableText();
		
	
	/** Tutorial texts **/
	@DefaultStringValue("Click \nhere\n to leave")
	String leaveTutorial();
	@DefaultStringValue("Do you want to hide this tutorial permanently?")
	String hidePermanently();
	@DefaultStringValue("Use mouse wheel to navigate through the tutorial")
	String navTip();
	@DefaultStringValue("You can select here the resource that you want to view"
			+ "\nResources may have different colors:\n - BLUE means AVAILABLE\n - GREEN means SEEN/SOLVED"
			+ "\n - GREY means UNAVAILABLE\n - YELLOW means RECOMMENDED\n - ORANGE means AUXILIAR RESOURCE")
	String resourcesText();
	@DefaultStringValue("Here you can see your achievements,\n the leaderboard and your profile just by selecting\n the desired tab")
	String eastTopPanelText();
	@DefaultStringValue("Enki provides a drag-and-drop interface.\nYou can drag tabs from panel to panel or \n drag panels to a different zone.\n "
			+ "You can also resize panels")
	String interfaceIntroText();
	@DefaultStringValue("Here you can see the video resource \nand share it on social networks using the \nbottom right buttons")
	String videoGadgetText();
	@DefaultStringValue("Here you can rate the current resource or see related resources")
	String bottomPanelExpResourcesText();
	@DefaultStringValue("Here you can see the pdf resource")
	String pdfGadgetText();
	@DefaultStringValue("Here you can view the statement of the problem. \nSwitching to another tab you can code your solution \nand grab the skeleton for it.")
	String problemCenterPanelText();
	@DefaultStringValue("Here you can view the statement of the problem. \nSwitching to another tab you can build your solution \n using a diagram editor.\n You can also submit your questions.")
	String diagramProblemCenterPanelText();
	@DefaultStringValue("In this part you have the objects that you can use to build the diagram, \non the left, and three buttons on the right: to submit, clear and enter fullscreen.\n"
			+ "The diagram objects include an attribute, an entity, a relationship, \na specialization/generalization, a simple line and a \nspecialization/generalization line.\nTo use the diagram objects you click on it (the border will become red)\n and then, you click on the place you want to place it.")
	String diagramEditorButtonsText();
	@DefaultStringValue("To edit the properties of a diagram object, you \ncan click with the right button over the object and edit them in \na popup as shown above.")
	String diagramEditorPropertiesText();
	@DefaultStringValue("Here you can see the error list and observations of \nyour submissions. When you finish the problem,\n you could also rate it here.")
	String problemBottomPanelText();
	@DefaultStringValue("Here you can see the statistics of this problem,\n the leaderboard and your profile just by selecting\n the desired tab")
	String problemEastTopPanelText();
	@DefaultStringValue("You can add some inputs \nhere by clicking the desired"
			+ " line and \nValidate your code to get the outputs given to them.\n Some public tests will also appear here.\n At the bottom there are 3 buttons:\nTo send a solution"
			+ " click Submit\nTo test a solution click Validate")
	String problemEastBottomPanelText();
	
	
	@DefaultStringValue("Rating saved! Thanks...")
	String resourceRated();

	@DefaultStringValue("Congratulations")
	String congratulations();
	@DefaultStringValue("Congratulations! You solved it.\nKeep it up and you will have success!")
	String msgCongratulations();
	@DefaultStringValue("Cancelar")
	String cancel();
	@DefaultStringValue("Proceed To Next Resource")
	String proceedToNextResource();
}
