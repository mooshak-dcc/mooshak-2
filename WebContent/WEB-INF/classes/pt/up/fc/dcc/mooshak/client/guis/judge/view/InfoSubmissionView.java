package pt.up.fc.dcc.mooshak.client.guis.judge.view;

import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.client.data.admin.FormDataProvider;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.StatementView;
import pt.up.fc.dcc.mooshak.shared.commands.EditorKind;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

/**
 * Submission detailed information
 * 
 * @author josepaiva
 */
public interface InfoSubmissionView extends View {

	public interface Presenter {
		void onProgramReEvaluate();
		void onChange(String objectId, MooshakValue value);
		void getDiffTest(String expected, String obtained);
		void getExpectedOutputs();
		void getObtainedOutputs();
		void validateSubmission();
	}
	
	void setPresenter(Presenter presenter);

	void setEditorKind(EditorKind code);

	void setProgramCode(byte[] code);
	void setData(Map<String, String> data);

	String getProgramName();
	byte[] getProgramCode();
	String getProblemId();

	List<String> getInputs();
	void setOutputs(List<String> outputs);
	void setInputs(List<String> inputs);
	void setExecutionTimes(List<String> times);
	void setProblemTestCases(Map<String, String> testCase);
	
	void setMessage(String message);
	void refreshProviders();
	void setFormDataProvider(FormDataProvider dataProvider);
	void setObjectId(String objectId);
	String getObjectId();
	void setStatement(StatementView statementView);

	void showDiffWindowBox(String expected, String obtained, String diff);

	void setExpectedOutputs(List<String> result);
	void setObtainedOutputs(List<String> result);

	void setObservations(String text);
	void addObservations(String text);
	void addStatus(String status);
	void addFeedback(String feedbackInHTML);
	void clearObservations();
	
}
