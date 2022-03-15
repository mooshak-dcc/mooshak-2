package pt.up.fc.dcc.mooshak.client.guis.creator.view;

import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.client.data.admin.FormDataProvider;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;
import pt.up.fc.dcc.mooshak.shared.kora.ConfigInfo;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;

public interface ProblemView extends ValueChangeHandler<MooshakValue>, View {

	public interface Presenter {

		String getProblemId();

		String getSelectedItem();

		void onChange(String objectId, MooshakValue value);

		void createDefaultFile(String field);

		void uploadFile(byte[] content, String name, String dataSelectedCell,
				boolean dropped);

		void onAddTest();
		void onDeleteTest(String id);

		void onAddSkeleton();
		void onRemoveSkeleton(String id);

		void onAddSolution();
		void onRemoveSolution(String id);
		
		void onRemoveFile(String field, String file);

		void onChangeSolution(String id, MooshakValue value);

		void changeProgramType(String object, MooshakValue value, String newType);
		
		void onChangeDiagramLanguage(String lang);
		
		boolean isShowing();
	}

	// Form styles
	public interface BaseStyle extends CssResource {
		    String tip();
		    String selected();
			String hide();
	}

	void fillPossibleValues(Map<String, List<String>> values);

	void changeLanguageAceEditor();

	void setPresenter(Presenter presenter);

	void setMessage(String text, boolean expires);
	
	void addObservation(String obs);

	void setFormDataProvider(FormDataProvider provider);
	void addTestToList(String test, FormDataProvider formDataProvider);
	void addProgramDataToList(String program, MooshakObject mooshakObject);
	void setImagesDataProvider(FormDataProvider dataProvider);
	void setSolutions(MooshakValue value, String id);
	void deleteIODataProviderRow(String id);
	void deleteProgramDataProviderRow(String id);

	String getSelectedItem();

	void setSelectedValue(MooshakValue value);

	Presenter getPresenter();

	void setObjectId(String objectId);
	String getObjectId();

	void refreshProviders();
	
	void clearTestProvider();
	void clearProgramDataProvider(String field);

	void setSelectedField(String id, MooshakValue value);

	void createEshu(ConfigInfo configInfo);
	void setDiagramLanguageOptions(List<SelectableOption> options);
}
