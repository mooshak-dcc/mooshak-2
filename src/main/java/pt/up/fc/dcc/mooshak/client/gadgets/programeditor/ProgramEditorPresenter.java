package pt.up.fc.dcc.mooshak.client.gadgets.programeditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageMap;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.Token;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditor.ProgramEditorView.Presenter;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditorobservations.ProgramObservationsView;
import pt.up.fc.dcc.mooshak.client.gadgets.programtestcases.ProgramTestCasesView;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

public class ProgramEditorPresenter extends GadgetPresenter<ProgramEditorView> implements Presenter {

	private boolean acceptedSubmission = false;

	// external views that this view needs access (MVP problem? need to re-think
	// this)
	private ProgramObservationsView observationsView;
	private ProgramTestCasesView testCasesView;

	private Storage storage = null;
	private StorageMap storageMap = null;

	public ProgramEditorPresenter(ParticipantCommandServiceAsync participantService,
			EnkiCommandServiceAsync enkiService, ProgramEditorView view, Token token) {
		super(null, null, participantService, enkiService, null, null, null, view, token);

		this.view.setPresenter(this);
		this.view.setDefaultLanguage(this.language);

		storage = Storage.getLocalStorageIfSupported();
		if (storage != null) {
			String prefix = contextInfo.getactivityId() + "." + problemId + "." + contextInfo.getParticipantId();
			storageMap = new StorageMap(storage);
			if (storageMap.containsKey(prefix + ".name")) {
				view.setProgramName(new String(Base64Coder.decodeLines(storageMap.get(prefix + ".name"))));
			}
			if (storageMap.containsKey(prefix + ".code")) {
				view.setProgramCode(Base64Coder.decode(storageMap
						.get(prefix + ".code")));
			}
		}
	}

	@Override
	public void go(HasWidgets container) {
		setDependentData();
	}

	private void setDependentData() {
		getAvailableLanguages();
		getAllProblemSkeletons();

		if (storageMap == null)
			getSolutionIfSolved();
	}

	/**
	 * @param observationsView
	 *            the observationsView to set
	 */
	public void setObservationsView(ProgramObservationsView observationsView) {
		this.observationsView = observationsView;
	}

	/**
	 * @return the testCasesView
	 */
	public ProgramTestCasesView getTestCasesView() {
		return testCasesView;
	}

	/**
	 * @param testCasesView
	 *            the testCasesView to set
	 */
	public void setTestCasesView(ProgramTestCasesView testCasesView) {
		this.testCasesView = testCasesView;
		if (storageMap != null) {
			String prefix = contextInfo.getactivityId() + "." + problemId + "." + contextInfo.getParticipantId();
			if (storageMap.containsKey(prefix + ".inputs.size")) {

				int size = Integer.parseInt(storageMap.get(prefix + ".inputs.size"));
				List<String> inputs = new ArrayList<>();
				for (int i = 0; i < size; i++) {
					if (storageMap.containsKey(prefix + ".inputs." + i))
						inputs.add(new String(Base64Coder.decodeLines(storageMap.get(prefix + ".inputs." + i))));
				}
				testCasesView.setInputs(inputs);
			}
		}
	}

	/**
	 * Get all skeletons of this problem
	 */
	private void getAllProblemSkeletons() {
		enkiService.getAllSkeletons(problemId, new AsyncCallback<MooshakValue>() {

			@Override
			public void onSuccess(MooshakValue result) {
				List<SelectableOption> options = new ArrayList<SelectableOption>();
				for (String name : result.getFileNames()) {
					options.add(new SelectableOption(name, name));
				}

				if (options.size() == 1 && !acceptedSubmission) {
					String prefix = contextInfo.getactivityId() + "." + problemId + "."
							+ contextInfo.getParticipantId();
					if (storageMap == null || !storageMap.containsKey(prefix + ".code"))
						view.setProgramCode(result.getContent(options.get(0).getId()));
				}
				view.setSkeletonOptions(options);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Could not get program skeleton(s): " + caught.getMessage());
			}
		});
	}

	@Override
	public void setObservations(String string) {
		if (observationsView != null)
			observationsView.setObservations(string);
	}

	@Override
	public void onSkeletonSelectedChanged(String id) {

		if (id.equals("")) {
			new OkCancelDialog(ENKI_MESSAGES.skeletonReplaceEmptyConfirmation()) {
			}.addDialogHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					view.setProgramCode("".getBytes());
				}
			});
			return;
		}

		enkiService.getProgramSkeletonByFilename(problemId, id, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(final String result) {
				if (result.equals(""))
					return;

				new OkCancelDialog(ENKI_MESSAGES.skeletonReplaceConfirmation()) {
				}.addDialogHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						view.setProgramCode(result.getBytes());
					}
				});
			}

		});
	}

	/**
	 * Get the solution submitted by this student if one is available
	 */
	private void getSolutionIfSolved() {
		enkiService.getSolution(problemId, new AsyncCallback<byte[]>() {

			@Override
			public void onFailure(Throwable caught) {
				// ignore silently
				Logger.getLogger("").log(Level.SEVERE, caught.getMessage());
			}

			@Override
			public void onSuccess(byte[] result) {
				view.setProgramCode(result);
				acceptedSubmission = true;
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
				// ignore silently
				Logger.getLogger("").log(Level.SEVERE, caught.getMessage());
			}

			@Override
			public void onSuccess(Map<String, String> result) {
				view.setLanguages(result);
			}
		});
	}

	@Override
	public void saveToLocalStorage(String name, byte[] code) {
		if (storageMap != null) {
			String prefix = contextInfo.getactivityId() + "." + problemId + "." + contextInfo.getParticipantId();
			storageMap.put(prefix + ".name", Base64Coder.encodeString(name));
			storageMap.put(prefix + ".code", new String(Base64Coder.encode(code == null ? new byte[0] : code)));

			if (testCasesView != null) {
				List<String> inputs = testCasesView.getInputs();
				storageMap.put(prefix + ".inputs.size", inputs.size() + "");
				int i = 0;
				for (String input : inputs) {
					storageMap.put(prefix + ".inputs." + i, Base64Coder.encodeString(input));
					i++;
				}
			}
		}
	}
	
	/**
	 * Check if files uploaded must be editable
	 * 
	 * @param extension Extension of the file
	 */
	@Override
	public void updateEditable(final String extension) {
		
		participantService.isEditableContents(extension, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				
				Logger.getLogger("").severe(caught.getMessage());
				
				// default behavior
				view.setLanguageEditable(true);
			}

			@Override
			public void onSuccess(Boolean editable) {
				if (editable != null)
					view.setLanguageEditable(editable.booleanValue());
				else
					view.setLanguageEditable(true);
			}
		});
	}
}
