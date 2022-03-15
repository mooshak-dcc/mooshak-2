package pt.up.fc.dcc.mooshak.client.guis.enki.i18n;

import com.google.gwt.i18n.client.Messages;

public interface EnkiMessages extends Messages {

	@DefaultMessage("Do you really want to submit {0} to problem {1}?")
	String submitConfirmation(String name, String problem);
	@DefaultMessage("Do you really want to validate {0} in problem {1}?")
	String validateConfirmation(String name, String problem);
	@DefaultMessage("Do you really want to print {0}?")
	String printConfirmation(String name);
	@DefaultMessage("Do you really want to send the question ''{0}'' about problem {1}?")
	String askConfirmation(String subject, String problem);
	@DefaultMessage("Do you really want to change the current language to ''{0}''?")
	String changeLanguageConfirmation(String language);
	@DefaultMessage("Do you want to replace the file contents for the skeleton provided?")
	String skeletonReplaceConfirmation();
	@DefaultMessage("Do you want to clear the file contents?")
	String skeletonReplaceEmptyConfirmation();

}
