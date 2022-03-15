package pt.up.fc.dcc.mooshak.client.guis.creator.presenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.client.AbstractAppController;
import pt.up.fc.dcc.mooshak.client.Presenter;
import pt.up.fc.dcc.mooshak.client.data.admin.DataManager;
import pt.up.fc.dcc.mooshak.client.data.admin.DataManager.Notifier;
import pt.up.fc.dcc.mooshak.client.data.admin.DataObject;
import pt.up.fc.dcc.mooshak.client.data.admin.FormDataProvider;
import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.client.guis.creator.view.ProblemView;
import pt.up.fc.dcc.mooshak.client.services.CreatorCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.KoraCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;
import pt.up.fc.dcc.mooshak.shared.kora.ConfigInfo;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class ProblemPresenter implements Presenter, ProblemView.Presenter {
	
	private static final int MAX_FILE_SIZE = 5 * 100 * 1000;

	private CreatorCommandServiceAsync rpcService;
	private KoraCommandServiceAsync koraService;
	private ParticipantCommandServiceAsync participantService;
	// private HandlerManager eventBus;
	private ProblemView view;
	private String problemId;
	
	private boolean showing = false;

	private DataManager dataManager = DataManager.getInstance();

	private static final Logger LOGGER = Logger.getLogger("");

	public ProblemPresenter(
			CreatorCommandServiceAsync rpcService,
			KoraCommandServiceAsync koraService,
			ParticipantCommandServiceAsync participantService,
			HandlerManager eventBus, ProblemView view, String problemId,
			String problemLabel) {
		this.rpcService = rpcService;
		this.koraService = koraService;
		this.participantService = participantService;
		// this.eventBus = eventBus;
		this.view = view;
		this.problemId = problemId;

		this.view.setPresenter(this);
	}

	@Override
	public void go(HasWidgets container) {
		rpcService
				.getOptionsValues(new AsyncCallback<Map<String, List<String>>>() {

					@Override
					public void onFailure(Throwable caught) {
						processfailure(caught);
					}

					@Override
					public void onSuccess(Map<String, List<String>> result) {
						view.fillPossibleValues(result);
						setDependentData();
					}
				});
	}

	public void setObjectId(String objectId) {
		view.setObjectId(objectId);
	}

	public void setDataProvider(FormDataProvider dataProvider) {
		view.setFormDataProvider(dataProvider);
	}
	
	private void setDependentData() {
		view.refreshProviders();
		EventManager.getInstance().refresh();
		
		checkLanguages();
		
		getAvailableLanguages();
	}

	void processfailure(Throwable caught) {
		Window.alert(caught.getMessage());
		AuthenticationPresenter.logout(caught);
	}

	@Override
	public String getProblemId() {
		return problemId;
	}

	/**
	 * @return the showing
	 */
	public boolean isShowing() {
		return showing;
	}

	/**
	 * @param showing the showing to set
	 */
	public void setShowing(boolean showing) {
		this.showing = showing;
	}

	@Override
	public String getSelectedItem() {
		return view.getSelectedItem();
	}
	
	private void checkLanguages() {
		rpcService.checkLanguages(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				view.addObservation("Error checking languages");
			}

			@Override
			public void onSuccess(String result) {
				view.addObservation(result);
			}
		});
	}

	/**
	 * Get the languages available from the contest
	 */
	private void getAvailableLanguages() {
		participantService.getAvailableLanguages(new AsyncCallback<Map<String, String>>() {

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.log(Level.SEVERE, caught.getMessage());
			}

			@Override
			public void onSuccess(Map<String, String> result) {
				List<SelectableOption> options = new ArrayList<SelectableOption>();

				String defaultLang = null;
				boolean first = true;
				for (Entry<String, String> lang : result.entrySet()) {
					options.add(new SelectableOption(lang.getKey(), lang.getValue()));
					if (first) {
						defaultLang = lang.getKey();
						first = false;
					}

				}
				view.setDiagramLanguageOptions(options);
				if (defaultLang != null) {
					getEshuConfiguration(defaultLang);
				}
			}
		});
	}

	private void getEshuConfiguration(String id) {

		koraService.getEshuConfig(id, new AsyncCallback<ConfigInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.severe("Error: onFailure getEshuConfig " + caught.getMessage());
			}

			@Override
			public void onSuccess(ConfigInfo result) {
				view.createEshu(result);
			}
		});
	}
	
	public void addObservation(String text)	{
		view.addObservation(text);
	}
	
	@Override
	public void onChange(String objectId, final MooshakValue value) {	
		if("".equals(objectId))
			objectId = view.getObjectId();
		
		final DataObject dataObject = dataManager.getMooshakObject(objectId);

		dataObject.getData().setFieldValue(value.getField(), value);

		dataManager.setMooshakObject(objectId, new Notifier() {
			
			@Override
			public void notify(String message) {
				
				view.setMessage(message, true);
				EventManager.getInstance().refresh();
				
				updateTestsResults(value.getField());
			}
		});
		
		LOGGER.info("!! Saving data on "+objectId);
		view.refreshProviders();
	}

	public void addTestToList(String test, FormDataProvider formDataProvider) {
		view.addTestToList(test, formDataProvider);
	}

	
	private boolean uploading = false;
	
	@Override
	public void uploadFile(byte[] content, String name, final String field,
			final boolean dropped) {

		if(content.length > MAX_FILE_SIZE) {
			view.setMessage("File size exceeded", true);
			return;
		}
		
		if (uploading) {
			Logger.getLogger("").log(Level.SEVERE, "Already Uploading");
			return;
		}
		
		final Map<Style, Cursor> cursors = AbstractAppController.setCursorsToWait();
		
		view.setMessage("Uploading " + name + " ...", false);
		uploading = true;
		rpcService.uploadFile(view.getObjectId(), content, name, field, 
				new AsyncCallback<MooshakValue>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setMessage(caught.getMessage(), true);
				
				AbstractAppController.resetCursors(cursors);
				uploading = false;
			}
	
			@Override
			public void onSuccess(MooshakValue result) {
				view.setMessage("Upload Completed!", true);
				EventManager.getInstance().refresh();
				updateTestsResults(field);
				
				if(!dropped)
					view.setSelectedField(view.getObjectId(), result);
				
				AbstractAppController.resetCursors(cursors);
				uploading = false;
			}
		});
		
		
	}

	/**
	 * Updates tests results
	 * @param field
	 */
	private void updateTestsResults(String field) {
		if(field != null) {
			switch (field.split(",")[0].toLowerCase()) {
			case "input":
			case "output":
			case "solution":
			case "program":
				rpcService.updateTestsResults(view.getObjectId(), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						view.setMessage("Error updating tests' result!", true);
					}

					@Override
					public void onSuccess(Void result) {
						view.setMessage("Tests' result updated!", true);
					}
				});
				break;

			default:
				break;
			}
		}
	}

	@Override
	public void onAddTest() {
		rpcService.addNewDefaultTest(view.getObjectId(),
				new AsyncCallback<MooshakObject>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Can't add test "
						+ caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(MooshakObject result) {
				view.setMessage("Default test added", true);
				
				addTestToList(result.getId(),
						DataManager.getInstance().getFormDataProvider(result.getId()));
				
				EventManager.getInstance().refresh();
			}
		});
	}

	@Override
	public void onDeleteTest(final String id) {
		rpcService.deleteTest(id, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				view.deleteIODataProviderRow(id);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				view.setMessage(caught.getLocalizedMessage(), true);
			}
		});
	}

	@Override
	public void onAddSkeleton() {
		rpcService.addNewDefaultSkeleton(view.getObjectId(),
				new AsyncCallback<MooshakObject>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Can't add skeleton "
						+ caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(MooshakObject result) {
				view.setMessage("Default skeleton added", true);
				EventManager.getInstance().refresh();
			}
		});
	}

	@Override
	public void createDefaultFile(String field) {
		rpcService.addNewDefaultFile(view.getObjectId(), field, 
				new AsyncCallback<MooshakValue>() {

					@Override
					public void onFailure(Throwable caught) {
						view.setMessage(caught.getLocalizedMessage(), true);
					}

					@Override
					public void onSuccess(MooshakValue result) {
						view.setSelectedValue(result);
						EventManager.getInstance().refresh();
					}
		});
	}

	public void setImagesDataProvider(FormDataProvider dataProvider) {
		view.setImagesDataProvider(dataProvider);
	}

	public void clearTestProvider() {
		view.clearTestProvider();
	}

	public void clearProgramDataProvider(String field) {
		view.clearProgramDataProvider(field);
	}

	public void addProgramDataToList(String program,
			MooshakObject mooshakObject) {
		view.addProgramDataToList(program, mooshakObject);
	}

	public void setSolutions(MooshakValue value, String id) {
		view.setSolutions(value, id);
	}

	@Override
	public void onAddSolution() {
		rpcService.addNewDefaultSolution(view.getObjectId(), 
				new AsyncCallback<MooshakObject>() {

					@Override
					public void onFailure(Throwable caught) {
						view.setMessage(caught.getLocalizedMessage(), true);
					}

					@Override
					public void onSuccess(MooshakObject result) {
						view.refreshProviders();
						view.setMessage("Default solution added", true);
						EventManager.getInstance().refresh();
					}
			
		});
	}
	
	@Override
	public void onChangeSolution(String id, final MooshakValue value) {
		
		final String name = id.substring(id.lastIndexOf("/") + 1);
		id = id.substring(0, id.lastIndexOf("/"));
		
		DataObject dataObject = dataManager.getMooshakObject(id);

		MooshakValue old = dataObject.getData().getFieldValue("Solution");
		old.removeFile(name);
		old.addFileValue(new MooshakValue("Solution", value.getName(), 
				value.getContent()));
		dataObject.getData().setFieldValue("Solution", old);
		
		final String objId = id;
		rpcService.removeFileFromObject(objId, name, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setMessage(caught.getLocalizedMessage(), true);
			}

			@Override
			public void onSuccess(Void result) {
				view.deleteProgramDataProviderRow(objId + "/" + name);
			}
		});
		
		dataManager.setMooshakObject(id, new Notifier() {
			
			@Override
			public void notify(String message) {
				
				view.setMessage(message, true);
				EventManager.getInstance().refresh();
				
				updateTestsResults("Solution");
			}
		});
		
		LOGGER.info("!! Saving data on "+id);
		view.refreshProviders();
	}

	@Override
	public void changeProgramType(String object, MooshakValue value, 
			String newType) {
		rpcService.changeProgramType(view.getObjectId(), object, value, newType, 
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						view.setMessage(caught.getLocalizedMessage(), true);
					}

					@Override
					public void onSuccess(Void result) {
						view.setMessage("Saved!", true);
						EventManager.getInstance().refresh();
						
					}
				});
	}

	@Override
	public void onRemoveSkeleton(final String id) {
		if (uploading) {
			view.setMessage("Please wait while uploading!", false);
			return;
		}
		
		rpcService.removeSkeleton(view.getObjectId(), id, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setMessage(caught.getLocalizedMessage(), true);
			}

			@Override
			public void onSuccess(Void result) {
				view.refreshProviders();
				view.setMessage("Skeleton removed", true);
				EventManager.getInstance().refresh();
			}
		});
	}

	@Override
	public void onRemoveSolution(final String id) {
		if (uploading) {
			view.setMessage("Please wait while uploading!", false);
			return;
		}
		
		rpcService.removeSolution(view.getObjectId(), id, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setMessage(caught.getLocalizedMessage(), true);
			}

			@Override
			public void onSuccess(Void result) {
				view.refreshProviders();
				view.setMessage("Solution removed", true);
				EventManager.getInstance().refresh();
			}
		});
	}
	
	@Override
	public void onRemoveFile(final String field, final String file) {
		rpcService.removeFile(view.getObjectId(), field, file, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setMessage(caught.getLocalizedMessage(), true);
			}

			@Override
			public void onSuccess(Void result) {
				view.refreshProviders();
				view.setMessage(field + " removed", true);
				EventManager.getInstance().refresh();
			}
		});
	}

	@Override
	public void onChangeDiagramLanguage(String lang) {
		getEshuConfiguration(lang);
	}

}
