package pt.up.fc.dcc.mooshak.client.services;

import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.shared.commands.EvaluationSummary;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface JudgeCommandServiceAsync {

	void getProgramCode(String id, AsyncCallback<byte[]> callback);

	void reevaluate(String programName, byte[] programCode, String problemId, String submissionId,
			AsyncCallback<Void> callback);

	void comment(String problemId, String subject, String comment,
			AsyncCallback<Void> callback);

	void getSubmissionReports(String id, AsyncCallback<Map<String, String>> callback);

	void getFileSeparator(AsyncCallback<String> callback);

	void broadcastRowChange(String type, String id, AsyncCallback<Void> callback);

	void createBalloon(String objectId, AsyncCallback<Void> callback);

	void sendAlertNotificationEvent(String objectId, String message,
			AsyncCallback<Void> callback);

	void getQuestionsSubjectList(String questionId,
			AsyncCallback<List<SelectableOption>> callback);

	void getProblemId(String submissionId, AsyncCallback<String> callback);

	void updateSubmissionResult(String submissionId, String oldResult, String newResult, AsyncCallback<Void> callback);

	void isPrintoutsListPending(AsyncCallback<Boolean> callback);

	void isBalloonsListPending(AsyncCallback<Boolean> callback);

	void getObtainedOutputs(String submissionId, AsyncCallback<List<String>> callback);

	void getExpectedOutputs(String submissionId, AsyncCallback<List<String>> callback);

	void getProblemTestCases(String problemId, AsyncCallback<Map<String, String>> callback);

	void validateSubmission(String submissionId, byte[] programCode, String problemId, List<String> inputs,
			AsyncCallback<Void> asyncCallback);

	void getValidationSummary(String submissionId, AsyncCallback<EvaluationSummary> callback);

}
