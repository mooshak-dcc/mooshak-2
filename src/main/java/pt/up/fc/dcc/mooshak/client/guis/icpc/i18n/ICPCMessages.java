package pt.up.fc.dcc.mooshak.client.guis.icpc.i18n;

import com.google.gwt.i18n.client.Messages;

public interface ICPCMessages extends Messages {

	@DefaultMessage("Do you really want to submit {0} to problem {1}?")
	String submitConfirmation(String name, String problem);
	@DefaultMessage("Do you really want to validate {0} in problem {1}?")
	String validateConfirmation(String name, String problem);
	@DefaultMessage("Do you really want to print {0}?")
	String printConfirmation(String name);
	@DefaultMessage("Do you really want to submit this diagram in problem {0}?")
	String submitDiagramConfirmation(String problem);
	@DefaultMessage("Do you really want to validate this diagram in problem {0}?")
	String validateDiagramConfirmation(String problem);
	@DefaultMessage("Do you really want to print this diagram?")
	String printDiagramConfirmation();
	@DefaultMessage("Do you really want to send the question ''{0}'' about problem {1}?")
	String askConfirmation(String subject, String problem);
	@DefaultMessage("Do you really want to change the current language to ''{0}''?")
	String changeLanguageConfirmation(String language);
	@DefaultMessage("Do you want to replace the file contents for the skeleton provided?")
	String skeletonReplaceConfirmation();
	@DefaultMessage("Submission ACCEPTED in problem ''{0}''")
	String acceptedSubmission(String problem);
	@DefaultMessage("Question answered: ''{0}''")
	String answeredQuestion(String subject);
	@DefaultMessage("Judge has sent a comment: ''{0}''")
	String judgeComment(String subject);

}
