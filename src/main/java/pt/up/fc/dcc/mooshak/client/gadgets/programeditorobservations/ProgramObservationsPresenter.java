package pt.up.fc.dcc.mooshak.client.gadgets.programeditorobservations;

import java.util.List;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasWidgets;

import edu.ycp.cs.dh.acegwt.client.ace.AceAnnotationType;
import pt.up.fc.dcc.mooshak.client.gadgets.GadgetPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.Token;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditor.ProgramEditorView;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditorobservations.ProgramObservationsView.Presenter;
import pt.up.fc.dcc.mooshak.client.gadgets.programerrorlist.ProgramErrorListView;
import pt.up.fc.dcc.mooshak.client.guis.enki.event.SelectTabEvent;
import pt.up.fc.dcc.mooshak.client.utils.ProgramError;

public class ProgramObservationsPresenter extends GadgetPresenter<ProgramObservationsView>
	implements Presenter {
	
	private ProgramEditorView editorView;
	private ProgramErrorListView errorListView;
	
	public ProgramObservationsPresenter(HandlerManager eventBus, 
			ProgramObservationsView view, Token token) {
		super(eventBus, null, null, null, null, null, null, view, token);
		
		this.view.setPresenter(this);
	}
	
	@Override
	public void go(HasWidgets container) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param editorView the editorView to set
	 */
	public void setEditorView(ProgramEditorView editorView) {
		this.editorView = editorView;
	}

	/**
	 * @return the errorListView
	 */
	public ProgramErrorListView getErrorListView() {
		return errorListView;
	}

	/**
	 * @param errorListView the errorListView to set
	 */
	public void setErrorListView(ProgramErrorListView errorListView) {
		this.errorListView = errorListView;
	}

	@Override
	public void clearEditorAnnotations() {
		if (editorView != null)
			editorView.getEditor().clearAnnotations();
	}

	@Override
	public void setEditorAnnotations() {
		if (editorView != null)
			editorView.getEditor().setAnnotations();
	}



	@Override
	public void addEditorAnnotation(int row, int column, String error,
			AceAnnotationType type) {
		if (editorView != null)
			editorView.getEditor().addAnnotation(row, column, error, type);
	}

	@Override
	public void setErrorList(List<ProgramError> errors) {
		if (errorListView != null)
			errorListView.setErrorList(errors);
	}

	@Override
	public void onSetObservations() {
		eventBus.fireEvent(new SelectTabEvent(view.asWidget()));
	}

}
