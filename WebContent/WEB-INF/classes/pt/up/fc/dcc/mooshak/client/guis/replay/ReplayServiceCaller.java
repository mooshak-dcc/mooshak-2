package pt.up.fc.dcc.mooshak.client.guis.replay;

import com.google.gwt.user.client.ui.TextArea;

public class ReplayServiceCaller {
	
	private static TextArea logArea = null;

	protected static void logMessage(String message) {
		
		if (logArea != null)
			logArea.setText(logArea.getText() + "\n" + message);
	}

	/**
	 * @param logArea the logArea to set
	 */
	public static void setLogArea(TextArea logArea) {
		ReplayServiceCaller.logArea = logArea;
	}
	
	public static void callServiceMethod(String method, String... params) throws Exception {
		
		switch (method) {
		case "context":
			ReplayBasicServiceCaller.context();
			break;
		case "getContestId":
			ReplayBasicServiceCaller.getContestId();
			break;
		case "getDomains":
			ReplayBasicServiceCaller.getDomains(params[0], params[1]);
			break;
		case "initSession":
			ReplayBasicServiceCaller.initSession();
			break;
		case "login":
			ReplayBasicServiceCaller.login(params[0], params[1], params[2]);
			break;
		case "register":
			ReplayBasicServiceCaller.register(params[0], params[1], params[2]);
			break;
		case "logout":
			ReplayBasicServiceCaller.logout();
			break;
		case "getColumns":
			ReplayBasicServiceCaller.getColumns(params[0]);
			break;
		case "refreshRows":
			ReplayBasicServiceCaller.refreshRows();
			break;
		case "changeContest":
			ReplayBasicServiceCaller.changeContest(params[0]);
			break;
		case "validateCaptcha":
			ReplayBasicServiceCaller.validateCaptcha(params[0]);
			break;
		case "isSessionAlive":
			ReplayBasicServiceCaller.isSessionAlive();
			break;
		case "isLoggedInAsAdmin":
			ReplayBasicServiceCaller.isLoggedInAsAdmin();
			break;
		case "switchProfileBackToAdmin":
			ReplayBasicServiceCaller.switchProfileBackToAdmin();
			break;
		case "getPreferredLocale":
			if (params.length == 1)
				ReplayBasicServiceCaller.getPreferredLocale(params[0]);
			else
				ReplayBasicServiceCaller.getPreferredLocale();
			break;
		case "getVersion":
			ReplayBasicServiceCaller.switchProfileBackToAdmin();
			break;
		case "getProblemNameById":
			ReplayBasicServiceCaller.getProblemNameById(params[0]);
			break;
		case "getProblems":
			ReplayParticipantServiceCaller.getProblems();
			break;
		case "view":
			ReplayParticipantServiceCaller.view(params[0], params[1]);
			break;
		case "evaluate":
			ReplayParticipantServiceCaller.evaluate(params[0], params[1], params[2], params[3], params[4]);
			break;
		case "ask":
			ReplayParticipantServiceCaller.ask(params[0], params[1], params[2]);
			break;
		case "print":
			ReplayParticipantServiceCaller.print(params[0], params[1], params[2]);
			break;
		case "getEvaluationSummary":
			ReplayParticipantServiceCaller.getEvaluationSummary(params[0], params[1]);
			break;
		case "getAnsweredQuestion":
			ReplayParticipantServiceCaller.getAnsweredQuestion(params[0]);
			break;
		case "getTransactionsData":
			ReplayParticipantServiceCaller.getTransactionsData(params[0]);
			break;
		case "getProgramSkeleton":
			ReplayParticipantServiceCaller.getProgramSkeleton(params[0], params[1]);
			break;
		case "getShowOwnCode":
			ReplayParticipantServiceCaller.getShowOwnCode();
			break;
		case "getSubmissionContent":
			ReplayParticipantServiceCaller.getSubmissionContent(params[0], params[1]);
			break;
		case "getAvailableLanguages":
			ReplayParticipantServiceCaller.getAvailableLanguages();
			break;
		case "getEditorKind":
			ReplayParticipantServiceCaller.getEditorKind(params[0]);
			break;
		case "getParticipantLogged":
			ReplayParticipantServiceCaller.getParticipantLogged();
			break;
		case "getResources":
			ReplayEnkiServiceCaller.getResources();
			break;
		case "getLeaderboard":
			ReplayEnkiServiceCaller.getLeaderboard(params[0], params[1]);
			break;
		case "getProblemStatistics":
			ReplayEnkiServiceCaller.getProblemStatistics(params[0]);
			break;
		case "getAchievementsUnlocked":
			ReplayEnkiServiceCaller.getAchievementsUnlocked(params[0], params[1]);
			break;
		case "getAchievementsRevealed":
			ReplayEnkiServiceCaller.getAchievementsRevealed(params[0], params[1]);
			break;
		case "getAllSkeletons":
			ReplayEnkiServiceCaller.getAllSkeletons(params[0]);
			break;
		case "getPublicTestCases":
			ReplayEnkiServiceCaller.getPublicTestCases(params[0]);
			break;
		case "getRelatedResources":
			ReplayEnkiServiceCaller.getRelatedResources(params[0], params[1]);
			break;
		case "getProfile":
			ReplayEnkiServiceCaller.getProfile(params[0]);
			break;
		case "syncSubmissionResult":
			ReplayEnkiServiceCaller.syncSubmissionResult(params[0], params[1], params[2]);
			break;
		case "notifyResourceSeen":
			ReplayEnkiServiceCaller.notifyResourceSeen(params[0], params[1]);
			break;
		case "checkTestPassed":
			ReplayEnkiServiceCaller.checkTestPassed(params[0], params[1]);
			break;
		case "syncResourceLearningTime":
			ReplayEnkiServiceCaller.syncResourceLearningTime(params[0], params[1], params[2]);
			break;
		case "getResourceLearningTime":
			ReplayEnkiServiceCaller.getResourceLearningTime(params[0], params[1]);
			break;
		case "getFeedbackToResource":
			ReplayEnkiServiceCaller.getFeedbackToResource(params[0], params[1]);
			break;
		case "sendFeedbackToResource":
			ReplayEnkiServiceCaller.sendFeedbackToResource(params[0], params[1], params[2],
					params[3]);
			break;
		case "getProgramSkeletonByFilename":
			ReplayEnkiServiceCaller.getProgramSkeletonByFilename(params[0], params[1]);
			break;
		case "getSolution":
			ReplayEnkiServiceCaller.getSolution(params[0]);
			break;
		case "refreshMySubmissionRows":
			ReplayEnkiServiceCaller.refreshMySubmissionRows();
			break;
		case "refreshQuestionRows":
			ReplayEnkiServiceCaller.refreshQuestionRows();
			break;
		case "getProgramCode":
			ReplayJudgeServiceCaller.getProgramCode(params[0]);
			break;
		case "reevaluate":
			ReplayJudgeServiceCaller.reevaluate(params[0], params[1], params[2],
					params[3]);
			break;
		case "comment":
			ReplayJudgeServiceCaller.comment(params[0], params[1], params[2]);
			break;
		case "getSubmissionReports":
			ReplayJudgeServiceCaller.getSubmissionReports(params[0]);
			break;
		case "getFileSeparator":
			ReplayJudgeServiceCaller.getFileSeparator();
			break;
		case "broadcastRowChange":
			ReplayJudgeServiceCaller.broadcastRowChange(params[0], params[1]);
			break;
		case "createBalloon":
			ReplayJudgeServiceCaller.createBalloon(params[0]);
			break;
		case "sendAlertNotificationEvent":
			ReplayJudgeServiceCaller.sendAlertNotificationEvent(params[0], params[1]);
			break;
		case "getQuestionsSubjectList":
			ReplayJudgeServiceCaller.getQuestionsSubjectList(params[0]);
			break;
		case "getProblemId":
			ReplayJudgeServiceCaller.getProblemId(params[0]);
			break;
		case "updateSubmissionResult":
			ReplayJudgeServiceCaller.updateSubmissionResult(params[0], params[1], params[2]);
			break;
		case "isPrintoutsListPending":
			ReplayJudgeServiceCaller.isPrintoutsListPending();
			break;
		case "isBalloonsListPending":
			ReplayJudgeServiceCaller.isBalloonsListPending();
			break;
		case "getOptionsValues":
			ReplayCreatorServiceCaller.getOptionsValues();
			break;
		case "createNewDefaultProblem":
			ReplayCreatorServiceCaller.createNewDefaultProblem(params[0]);
			break;
		case "getImagesPath":
			ReplayCreatorServiceCaller.getImagesPath();
			break;
		case "getTestsPath":
			ReplayCreatorServiceCaller.getTestsPath();
			break;
		case "getSkeletonsPath":
			ReplayCreatorServiceCaller.getSkeletonsPath();
			break;
		case "getSolutionsPath":
			ReplayCreatorServiceCaller.getSolutionsPath();
			break;
		case "getProblemsPath":
			ReplayCreatorServiceCaller.getProblemsPath();
			break;
		case "uploadFile":
			ReplayCreatorServiceCaller.uploadFile(params[0], params[1], params[2], params[3]);
			break;
		case "deleteProblem":
			ReplayCreatorServiceCaller.deleteProblem(params[0]);
			break;
		case "addNewDefaultTest":
			ReplayCreatorServiceCaller.addNewDefaultTest(params[0]);
			break;
		case "deleteTest":
			ReplayCreatorServiceCaller.deleteTest(params[0]);
			break;
		case "addNewDefaultSkeleton":
			ReplayCreatorServiceCaller.addNewDefaultSkeleton(params[0]);
			break;
		case "deleteSkeleton":
			ReplayCreatorServiceCaller.deleteSkeleton(params[0]);
			break;
		case "addNewDefaultSolution":
			ReplayCreatorServiceCaller.addNewDefaultSolution(params[0]);
			break;
		case "addNewDefaultFile":
			ReplayCreatorServiceCaller.addNewDefaultFile(params[0], params[1]);
			break;
		case "updateTestsResults":
			ReplayCreatorServiceCaller.updateTestsResults(params[0]);
			break;
		case "changeProgramType":
			ReplayCreatorServiceCaller.changeProgramType(params[0], params[1], params[2],
					params[3]);
			break;
		case "removeFileFromObject":
			ReplayCreatorServiceCaller.removeFileFromObject(params[0], params[1]);
			break;
		case "getSolutionsValue":
			ReplayCreatorServiceCaller.getSolutionsValue(params[0]);
			break;
		case "removeSolution":
			ReplayCreatorServiceCaller.removeSolution(params[0], params[1]);
			break;
		case "removeSkeleton":
			ReplayCreatorServiceCaller.removeSkeleton(params[0], params[1]);
			break;
		case "getMooshakType":
			ReplayAdminServiceCaller.getMooshakType(params[0]);
			break;
		case "getMooshakObject":
			ReplayAdminServiceCaller.getMooshakObject(params[0]);
			break;
		case "setMooshakObject":
			ReplayAdminServiceCaller.setMooshakObject(params[0]);
			break;
		case "canRecover":
			ReplayAdminServiceCaller.canRecover(params[0], params[1]);
			break;
		case "createMooshakObject":
			ReplayAdminServiceCaller.createMooshakObject(params[0], params[1]);
			break;
		case "destroyMooshakObject":
			ReplayAdminServiceCaller.destroyMooshakObject(params[0]);
			break;
		case "renameMooshakObject":
			ReplayAdminServiceCaller.renameMooshakObject(params[0], params[1]);
			break;
		case "pasteMooshakObject":
			ReplayAdminServiceCaller.pasteMooshakObject(params[0], params[1]);
			break;
		case "recover":
			ReplayAdminServiceCaller.recover(params[0], params[1]);
			break;
		case "freeze":
			ReplayAdminServiceCaller.freeze(params[0]);
			break;
		case "unfreeze":
			ReplayAdminServiceCaller.unfreeze(params[0]);
			break;
		case "isFrozen":
			ReplayAdminServiceCaller.isFrozen(params[0]);
			break;
		case "execute":
			ReplayAdminServiceCaller.execute(params[0], params[1], params[2]);
			break;
		case "getMimeType":
			ReplayAdminServiceCaller.getMimeType(params[0]);
			break;
		case "importMooshakObject":
			ReplayAdminServiceCaller.importMooshakObject(params[0], params[1], params[2]);
			break;
		case "exportMooshakObject":
			ReplayAdminServiceCaller.exportMooshakObject(params[0]);
			break;
		case "isRenameable":
			ReplayAdminServiceCaller.isRenameable(params[0]);
			break;
		case "switchProfile":
			ReplayAdminServiceCaller.switchProfile(params[0], params[1]);
			break;
		case "findMooshakObjectIds":
			ReplayAdminServiceCaller.findMooshakObjectIds(params[0], params[1]);
			break;
		case "getServerStatus":
			ReplayAdminServiceCaller.getServerStatus();
			break;
		case "getSerial":
			ReplayEventServiceCaller.getSerial(params[0]);
			break;
		case "getEvents":
			ReplayEventServiceCaller.getEvents(params[0]);
			break;
			
		default:
			throw new Exception("Method " + method + " not found with provided parameters!");
		}
		
		/*for (Method m : ReplayBasicServiceCaller.class.getMethods()) {
			
			if (m.getName().equals(method) &&
            		m.getParameterCount() == params.length) {
				
				try {
					m.invoke(null, params);
				} catch (Exception e) {
					ReplayServiceCaller.logMessage("Error invoking method: " + 
							method);
				}
			}
		}
		
		for (Method m : ReplayParticipantServiceCaller.class.getMethods()) {
			
			if (m.getName().equals(method) &&
            		m.getParameterCount() == params.length) {
				
				try {
					m.invoke(null, params);
				} catch (Exception e) {
					ReplayServiceCaller.logMessage("Error invoking method: " + 
							method);
				}
			}
		}
		
		for (Method m : ReplayEnkiServiceCaller.class.getMethods()) {
			
			if (m.getName().equals(method) &&
            		m.getParameterCount() == params.length) {
				
				try {
					m.invoke(null, params);
				} catch (Exception e) {
					ReplayServiceCaller.logMessage("Error invoking method: " + 
							method);
				}
			}
		}
		
		for (Method m : ReplayJudgeServiceCaller.class.getMethods()) {
			
			if (m.getName().equals(method) &&
            		m.getParameterCount() == params.length) {
				
				try {
					m.invoke(null, params);
				} catch (Exception e) {
					ReplayServiceCaller.logMessage("Error invoking method: " + 
							method);
				}
			}
		}
		
		for (Method m : ReplayCreatorServiceCaller.class.getMethods()) {
			
			if (m.getName().equals(method) &&
            		m.getParameterCount() == params.length) {
				
				try {
					m.invoke(null, params);
				} catch (Exception e) {
					ReplayServiceCaller.logMessage("Error invoking method: " + 
							method);
				}
			}
		}
		
		for (Method m : ReplayAdminServiceCaller.class.getMethods()) {
			
			if (m.getName().equals(method) &&
            		m.getParameterCount() == params.length) {
				
				try {
					m.invoke(null, params);
				} catch (Exception e) {
					ReplayServiceCaller.logMessage("Error invoking method: " + 
							method);
				}
			}
		}
		
		throw new Exception("Method " + method + " not found with provided parameters!");*/
	}
}
