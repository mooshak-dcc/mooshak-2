package pt.up.fc.dcc.mooshak.client.gadgets.gameviewer;

import com.google.gwt.user.client.ui.HasText;

import pt.up.fc.dcc.asuraviewer.client.player.AsuraViewer.Mode;
import pt.up.fc.dcc.mooshak.client.View;

public interface GameViewerView extends View {
	
	public interface Presenter {
		void setTournament(String id);
		void requestMatchMovie(String id);
	}
	
	void setPresenter(Presenter presenter);
	
	void setSubmissionId(String submissionId);
	
	void setProblemId(String problemId);
	
	void setObservationsDisplay(HasText display);

	void setMode(Mode mode);
	
	void setMovie(String json);
	
	void setTournament(String json);
	
}
