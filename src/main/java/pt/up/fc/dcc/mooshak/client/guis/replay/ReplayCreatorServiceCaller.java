package pt.up.fc.dcc.mooshak.client.guis.replay;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import pt.up.fc.dcc.mooshak.client.services.CreatorCommandService;
import pt.up.fc.dcc.mooshak.client.services.CreatorCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

public class ReplayCreatorServiceCaller extends ReplayServiceCaller {
	private static CreatorCommandServiceAsync creatorService = 
			GWT.create(CreatorCommandService.class);

	public static void getOptionsValues() {

		logMessage("Executing getOptionsValues");
		creatorService.getOptionsValues(new AsyncCallback<Map<String,List<String>>>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getOptionsValues failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Map<String, List<String>> result) {
				logMessage("getOptionsValues succeeded");
			}
		});
	}

	public static void createNewDefaultProblem(String objectId) {

		logMessage("Executing createNewDefaultProblem: " + objectId);
		creatorService.createNewDefaultProblem(objectId, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("createNewDefaultProblem failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				logMessage("createNewDefaultProblem succeeded: " + result);
			}
		});
	}

	public static void getImagesPath() {

		logMessage("Executing getImagesPath");
		creatorService.getImagesPath(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getImagesPath failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				logMessage("getImagesPath succeeded: " + result);
			}
		});
	}

	public static void getTestsPath() {

		logMessage("Executing getTestsPath");
		creatorService.getTestsPath(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getTestsPath failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				logMessage("getTestsPath succeeded: " + result);
			}
		});
	}

	public static void getSkeletonsPath() {

		logMessage("Executing getSkeletonsPath");
		creatorService.getSkeletonsPath(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getSkeletonsPath failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				logMessage("getSkeletonsPath succeeded: " + result);
			}
		});
	}

	public static void getSolutionsPath() {

		logMessage("Executing getSolutionsPath");
		creatorService.getSolutionsPath(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getSolutionsPath failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				logMessage("getSolutionsPath succeeded: " + result);
			}
		});
	}

	public static void getProblemsPath() {

		logMessage("Executing getProblemsPath");
		creatorService.getProblemsPath(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getProblemsPath failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				logMessage("getProblemsPath succeeded: " + result);
			}
		});
	}

	public static void uploadFile(String objectId, String content, String name,
			String field) {
// TODO
		logMessage("Executing uploadFile: " + objectId + " [content] " + name 
				+ " " + field);
		creatorService.uploadFile(objectId, null, name, field, 
				new AsyncCallback<MooshakValue>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("uploadFile failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(MooshakValue result) {
						logMessage("uploadFile succeeded");
					}
		});
	}

	public static void deleteProblem(String id) {

		logMessage("Executing deleteProblem: " + id);
		creatorService.deleteProblem(id, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("deleteProblem failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				logMessage("deleteProblem succeeded");
			}
		});
	}

	public static void addNewDefaultTest(String objectId) {

		logMessage("Executing addNewDefaultTest: " + objectId);
		creatorService.addNewDefaultTest(objectId, new AsyncCallback<MooshakObject>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("addNewDefaultTest failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(MooshakObject result) {
				logMessage("addNewDefaultTest succeeded");
			}
		});
	}

	public static void deleteTest(String id) {

		logMessage("Executing deleteTest: " + id);
		creatorService.deleteTest(id, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("deleteTest failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				logMessage("deleteTest succeeded");
			}
		});
	}

	public static void addNewDefaultSkeleton(String objectId) {

		logMessage("Executing addNewDefaultSkeleton: " + objectId);
		creatorService.addNewDefaultTest(objectId, new AsyncCallback<MooshakObject>() {
			
			@Override
			public void onFailure(Throwable caught) {
				logMessage("addNewDefaultSkeleton failed: " + caught.getMessage());
			}
			
			@Override
			public void onSuccess(MooshakObject result) {
				logMessage("addNewDefaultSkeleton succeeded");
			}
		});
	}

	public static void deleteSkeleton(String id) {

		logMessage("Executing deleteSkeleton: " + id);
		creatorService.deleteSkeleton(id, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("deleteSkeleton failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				logMessage("deleteSkeleton succeeded");
			}
		});
	}

	public static void addNewDefaultSolution(String objectId) {

		logMessage("Executing addNewDefaultSolution: " + objectId);
		creatorService.addNewDefaultSkeleton(objectId,
				new AsyncCallback<MooshakObject>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("addNewDefaultSolution failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(MooshakObject result) {
						logMessage("addNewDefaultSolution succeeded");
					}
		});
	}

	public static void addNewDefaultFile(String problemId, String field) {

		logMessage("Executing addNewDefaultFile: " + problemId + " " + field);
		creatorService.addNewDefaultFile(problemId, field, 
				new AsyncCallback<MooshakValue>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("addNewDefaultFile failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(MooshakValue result) {
						logMessage("addNewDefaultFile succeeded");						
					}
				});
	}

	public static void updateTestsResults(String problemId) {

		logMessage("Executing updateTestsResults: " + problemId);
		creatorService.updateTestsResults(problemId, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				logMessage("updateTestsResults succeeded");			
			}
			
			@Override
			public void onFailure(Throwable caught) {
				logMessage("updateTestsResults failed: " + caught.getMessage());
			}
		});
	}

	public static void changeProgramType(String problemId, String objectId, 
			String value, String newType) {

		logMessage("Executing changeProgramType: " + problemId + " " + objectId
				+ " [MooshakValue] " + newType);
		creatorService.changeProgramType(problemId, objectId, null, newType, 
				new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						logMessage("changeProgramType succeeded");			
					}
					
					@Override
					public void onFailure(Throwable caught) {
						logMessage("changeProgramType failed: " + caught.getMessage());
					}
				});
	}

	public static void removeFileFromObject(String problemId, String name) {

		logMessage("Executing removeFileFromObject: " + problemId + " " + name);
		creatorService.removeFileFromObject(problemId, name, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("removeFileFromObject failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				logMessage("removeFileFromObject succeeded");
			}
		});
	}

	public static void getSolutionsValue(String problemId) {

		logMessage("Executing getSolutionsValue: " + problemId);
		creatorService.getSolutionsValue(problemId, new AsyncCallback<MooshakValue>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getSolutionsValue failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(MooshakValue result) {
				logMessage("getSolutionsValue succeeded");
			}
		});
	}

	public static void removeSolution(String problemId, String name) {

		logMessage("Executing removeSolution: " + problemId + " " + name);
		creatorService.removeSolution(problemId, name, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("removeSolution failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				logMessage("removeSolution succeeded");
			}
		});
	}

	public static void removeSkeleton(String problemId, String name) {

		logMessage("Executing removeSkeleton: " + problemId + " " + name);
		creatorService.removeSkeleton(problemId, name, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				logMessage("removeSkeleton succeeded");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				logMessage("removeSkeleton failed: " + caught.getMessage());
			}
		});
	}
}
