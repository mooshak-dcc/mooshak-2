package pt.up.fc.dcc.mooshak.client.services;

import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CreatorCommandServiceAsync {

	void getImagesPath(AsyncCallback<String> callback);
	void getTestsPath(AsyncCallback<String> callback);
	void getSkeletonsPath(AsyncCallback<String> callback);
	void getProblemsPath(AsyncCallback<String> callback);
	void getSolutionsPath(AsyncCallback<String> callback);

	void getOptionsValues(AsyncCallback<Map<String, List<String>>> callback);

	void createNewDefaultProblem(String objectId, AsyncCallback<String> callback);
	
	void uploadFile(String objectId, byte[] content, String name,
			String field, AsyncCallback<MooshakValue> callback);
	
	void deleteProblem(String id, AsyncCallback<Void> asyncCallback);
	
	void addNewDefaultTest(String objectId,
			AsyncCallback<MooshakObject> callback);
	void deleteTest(String id, AsyncCallback<Void> callback);
	
	void addNewDefaultFile(String problemId, String field,
			AsyncCallback<MooshakValue> callback);
	void updateTestsResults(String problemId, AsyncCallback<Void> callback);
	void addNewDefaultSkeleton(String objectId,
			AsyncCallback<MooshakObject> callback);
	void deleteSkeleton(String id, AsyncCallback<Void> callback);
	void addNewDefaultSolution(String objectId,
			AsyncCallback<MooshakObject> callback);
	void changeProgramType(String problemId, String objectId,
			MooshakValue value, String newType, AsyncCallback<Void> callback);
	void removeFileFromObject(String problemId, String name,
			AsyncCallback<Void> callback);
	void getSolutionsValue(String problemId, AsyncCallback<MooshakValue> callback);
	void removeSolution(String problemId, String name,
			AsyncCallback<Void> callback);
	void removeSkeleton(String problemId, String name,
			AsyncCallback<Void> callback);
	void checkLanguages(AsyncCallback<String> callback);
	void removeFile(String problemId, String field, String name, AsyncCallback<Void> callback);

}
