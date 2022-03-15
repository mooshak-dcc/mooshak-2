package pt.up.fc.dcc.mooshak.client.guis.judge.view;

import java.util.List;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

public interface CommentProblemView extends View {
	
	public interface Presenter {
		void onComment();
		void onClear();
	}
	
	String getSubject();
	String getComment();
	String getProblem();
	
	void setPresenter(Presenter presenter);
	void setComment(String comment);
	void setSubject(String subject);
	void clearSelectedProblem();
	void setMessage(String text);
	
	void setProblems(List<SelectableOption> problemOptions);

}
