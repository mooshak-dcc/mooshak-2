package pt.up.fc.dcc.mooshak.client.gadgets.programeditorobservations;

import java.util.List;

import edu.ycp.cs.dh.acegwt.client.ace.AceAnnotationType;
import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.client.utils.ProgramError;

public interface ProgramObservationsView extends View {

	public interface Presenter {

		void clearEditorAnnotations();
		void setEditorAnnotations();
		void addEditorAnnotation(int row, int column, String error,
				AceAnnotationType type);
		void setErrorList(List<ProgramError> errors);
		void onSetObservations();
		
	}
	
	void setPresenter(Presenter presenter);
	
	void clearObservations();
	void setObservations(String observations);
	void addStatus(String status);
	void addObservations(String observations);
	void addFeedback(String feedback);
}
