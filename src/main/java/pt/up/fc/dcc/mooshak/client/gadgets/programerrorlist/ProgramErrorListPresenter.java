package pt.up.fc.dcc.mooshak.client.gadgets.programerrorlist;

import com.google.gwt.user.client.ui.HasWidgets;

import edu.ycp.cs.dh.acegwt.client.ace.AceAnnotationType;
import pt.up.fc.dcc.mooshak.client.gadgets.GadgetPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.Token;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditor.ProgramEditorView;
import pt.up.fc.dcc.mooshak.client.gadgets.programerrorlist.ProgramErrorListView.Presenter;

public class ProgramErrorListPresenter extends GadgetPresenter<ProgramErrorListView>
	implements Presenter {
	
	private ProgramEditorView editorView;
	
	public ProgramErrorListPresenter(ProgramErrorListView view, Token token) {
		super(null, null, null, null, null, null, null, view, token);
		
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

	@Override
	public void clearEditorAnnotations() {
		editorView.getEditor().clearAnnotations();
	}



	@Override
	public void setEditorAnnotations() {
		editorView.getEditor().setAnnotations();
	}



	@Override
	public void addEditorAnnotation(int row, int column, String error,
			AceAnnotationType type) {
		editorView.getEditor().addAnnotation(row, column, error, type);
	}

}
