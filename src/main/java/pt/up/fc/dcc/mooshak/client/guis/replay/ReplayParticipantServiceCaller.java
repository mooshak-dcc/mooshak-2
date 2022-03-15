package pt.up.fc.dcc.mooshak.client.guis.replay;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;

import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandService;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.commands.EditorKind;
import pt.up.fc.dcc.mooshak.shared.commands.EvaluationSummary;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;
import pt.up.fc.dcc.mooshak.shared.commands.TransactionQuota;
import pt.up.fc.dcc.mooshak.shared.results.ProblemInfo;

public class ReplayParticipantServiceCaller extends ReplayServiceCaller {
	private static ParticipantCommandServiceAsync participantService = 
			GWT.create(ParticipantCommandService.class);
	
	public static void getProblems() {
		
		logMessage("Executing getProblems");
		participantService.getProblems(new AsyncCallback<List<SelectableOption>>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getProblems failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(List<SelectableOption> result) {
				logMessage("getProblems succeeded");
			}
		});
	}
	
	public static void view(String problem, String description)  {
		
		logMessage("Executing view: " + problem + " " + description);
		participantService.view(problem, Boolean.parseBoolean(description), 
				new AsyncCallback<ProblemInfo>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("view failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(ProblemInfo result) {
						logMessage("view succeeded");
					}
				});
	}
	
	public static void evaluate(final String programName, final String problemId,
			final String consider, String participantId, String evalDate) {
		
		logMessage("Executing evaluate: " + programName + " " + problemId
				+ " " + consider + " " + participantId + " " + evalDate);
		DateTimeFormat df = DateTimeFormat.getFormat("dd-MM-yy_hh:mm:ss");
		
		participantService.getSubmissionContentsWithoutId(participantId, problemId, 
				df.parse(evalDate).getTime(), programName, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("evaluate failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(String programCode) {
						
						programCode += new String(new char[(int) (Math.random() * 100)]).replace("\0", 
								"\n");
						
						participantService.evaluate(programName, programCode.getBytes(), problemId, null,
								Boolean.parseBoolean(consider), new AsyncCallback<Void>() {

									@Override
									public void onFailure(Throwable caught) {
										logMessage("evaluate failed: " + caught.getMessage());
									}

									@Override
									public void onSuccess(Void result) {
										logMessage("evaluate succeeded");
									}
								});
					}
				});
		
	}
	
	public static void ask(String problem, String subject, String question) {
		
		logMessage("Executing ask: " + problem + " " + subject + " " + question);
		participantService.ask(problem, subject, question, 
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("ask failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						logMessage("ask succeeded");
					}
				});
	}
	
	public static void print(String problem, String content, String fileName) {
		
		logMessage("Executing print: " + problem + " " + content + " " + fileName);
		participantService.print(problem, content, fileName, 
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("print failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						logMessage("print succeeded");
					}
				});
	}
	
	public static void getEvaluationSummary(String submission, String consider) {
		
		logMessage("Executing getEvaluationSummary: " + submission + " " + consider);
		participantService.getEvaluationSummary(submission, Boolean.parseBoolean(consider), 
				new AsyncCallback<EvaluationSummary>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("getEvaluationSummary failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(EvaluationSummary result) {
						logMessage("getEvaluationSummary succeeded");
					}
				});
	}
	
	public static void getAnsweredQuestion(String id) {
		
		logMessage("Executing getAnsweredQuestion: " + id);
		participantService.getAnsweredQuestion(id, new AsyncCallback<MooshakObject>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getAnsweredQuestion failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(MooshakObject result) {
				logMessage("getAnsweredQuestion succeeded");
			}
		});
	}
	
	public static void getTransactionsData(String type) {
		
		logMessage("Executing getTransactionsData: " + type);
		participantService.getTransactionsData(type, new AsyncCallback<TransactionQuota>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getTransactionsData failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(TransactionQuota result) {
				logMessage("getTransactionsData succeeded");
			}
		});
	}
	
	public static void getProgramSkeleton(String problemId, String extension) {
		
		logMessage("Executing getProgramSkeleton: " + problemId + " " + extension);
		participantService.getProgramSkeleton(problemId, extension, 
				new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("getProgramSkeleton failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(String result) {
						logMessage("getProgramSkeleton succeeded");
					}
				});
	}
	
	public static void getParticipantLogged() {
		
		logMessage("Executing getParticipantLogged");
		participantService.getParticipantLogged(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getParticipantLogged failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				logMessage("getParticipantLogged succeeded: " + result);
			}
		});
	}
	
	public static void getShowOwnCode() {
		
		logMessage("Executing getShowOwnCode");
		participantService.getShowOwnCode(new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getShowOwnCode failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Boolean result) {
				logMessage("getShowOwnCode succeeded: " + result.booleanValue());
			}
		});
	}
	
	public static void getSubmissionContent(String id, String team) {
		
		logMessage("Executing getSubmissionContent: " + id + " " + team);
		participantService.getSubmissionContent(id, team, 
				new AsyncCallback<MooshakValue>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("getSubmissionContent failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(MooshakValue result) {
						logMessage("getSubmissionContent succeeded");
					}
				});
	}
	
	public static void getAvailableLanguages() {
		
		logMessage("Executing getAvailableLanguages");
		participantService.getAvailableLanguages(new AsyncCallback<Map<String,String>>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getAvailableLanguages failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Map<String, String> result) {
				logMessage("getAvailableLanguages succeeded");
			}
		});
	}
	
	public static void getEditorKind(String problemId) {
		
		logMessage("Executing getEditorKind: " + problemId);
		participantService.getEditorKind(problemId, new AsyncCallback<EditorKind>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getEditorKind failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(EditorKind result) {
				logMessage("getEditorKind succeeded");
			}
		});
	}
	
}
