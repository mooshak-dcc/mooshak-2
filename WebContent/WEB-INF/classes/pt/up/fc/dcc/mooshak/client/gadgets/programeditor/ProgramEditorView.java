package pt.up.fc.dcc.mooshak.client.gadgets.programeditor;

import java.util.List;
import java.util.Map;

import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

public interface ProgramEditorView extends View {

	public interface Presenter {
		void setObservations(String string);
		void onSkeletonSelectedChanged(String id);
		void updateEditable(String extension);
		void saveToLocalStorage(String name, byte[] code);
	}
	
	void setPresenter(Presenter presenter);
	
	void setLanguageEditable(boolean editable);
	boolean isLanguageEditable();
	void setProgramCode(byte[] result);
	byte[] getProgramCode();
	void setProgramName(String name);
	String getProgramName();
	void setResponsive(boolean isResponsive);

	AceEditor getEditor();

	void setSkeletonOptions(List<SelectableOption> options);

	void clearObservations();
	
	void setLanguages(Map<String, String> languages);

	void setDefaultLanguage(String language);
	
}
