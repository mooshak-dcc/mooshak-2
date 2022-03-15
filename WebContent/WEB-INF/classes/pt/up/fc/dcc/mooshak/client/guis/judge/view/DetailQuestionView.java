package pt.up.fc.dcc.mooshak.client.guis.judge.view;

import java.util.List;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.client.data.admin.FormDataProvider;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

/**
 * Detailed view of a question to judge
 * 
 * @author josepaiva
 */
public interface DetailQuestionView extends View {
	
	public interface Presenter {
		void onCommentProblemClicked();
		void onChange(String objectId, MooshakValue value);
		void onSubmitClicked();
		void getSubjectsList();
	}
	
	void setPresenter(Presenter presenter);
	
	MooshakValue getAnswer();
	MooshakValue getSubject();
	MooshakValue getQuestion();
	MooshakValue getState();
	String getAnsweredSubject();
	void clearAnswer();
	

	void setObjectId(String objectId);
	String getObjectId();
	void setFormDataProvider(FormDataProvider dataProvider);
	void setMessage(String message);
	void refreshProviders();

	void setSubjectsList(List<SelectableOption> options);




}
