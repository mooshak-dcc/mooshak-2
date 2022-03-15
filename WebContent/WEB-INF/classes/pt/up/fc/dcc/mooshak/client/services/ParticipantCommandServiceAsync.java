package pt.up.fc.dcc.mooshak.client.services;

import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.commands.EditorKind;
import pt.up.fc.dcc.mooshak.shared.commands.EvaluationSummary;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;
import pt.up.fc.dcc.mooshak.shared.commands.TransactionQuota;
import pt.up.fc.dcc.mooshak.shared.results.ProblemInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ParticipantCommandServiceAsync {
	
	void getProblems(AsyncCallback<List<SelectableOption>> callback);
	
	void ask(String problem, String subject, String question,
			AsyncCallback<Void> callback);

	void evaluate(
			String programName, byte[] programCode,
			String problemId, List<String> inputs,boolean consider,
			AsyncCallback<Void> callback);

	void print( String problem, String content,String fileName, 
			AsyncCallback<Void> callback);

	void view(String problem, boolean description, 
			AsyncCallback<ProblemInfo> callback);

	void getEvaluationSummary(String submission, boolean consider,
			AsyncCallback<EvaluationSummary> callback);

	void getAnsweredQuestion(String id,
			AsyncCallback<MooshakObject> callback);

	void getTransactionsData(String type,
			AsyncCallback<TransactionQuota> callback);

	void getProgramSkeleton(String problemId, String extension,
			AsyncCallback<String> callback);

	void getParticipantLogged(AsyncCallback<String> callback);

	void getSubmissionContent(String id, String team,
			AsyncCallback<MooshakValue> callback);

	void getShowOwnCode(AsyncCallback<Boolean> callback);

	void getAvailableLanguages(AsyncCallback<Map<String, String>> callback);

	void getEditorKind(String problemId, AsyncCallback<EditorKind> callback);

	void getSubmissionContentsWithoutId(String teamId, String problemId, long evalTime, String programName,
			AsyncCallback<String> callback);

	void isEditableContents(String extension, AsyncCallback<Boolean> callback);

	void getOpponents(String problemId, AsyncCallback<Map<String, String>> callback);

}
