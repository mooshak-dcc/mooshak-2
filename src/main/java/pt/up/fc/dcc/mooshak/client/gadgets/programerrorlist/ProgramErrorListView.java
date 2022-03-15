package pt.up.fc.dcc.mooshak.client.gadgets.programerrorlist;

import java.util.List;

import edu.ycp.cs.dh.acegwt.client.ace.AceAnnotationType;
import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.client.utils.ProgramError;

public interface ProgramErrorListView extends View {

	public interface Presenter {

		void clearEditorAnnotations();
		void setEditorAnnotations();
		void addEditorAnnotation(int row, int column, String error,
				AceAnnotationType type);
		
	}
	
	void setPresenter(Presenter presenter);
	
	void setErrorList(List<ProgramError> errorList);
}
