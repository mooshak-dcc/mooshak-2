package pt.up.fc.dcc.mooshak.client.guis.icpc.view;

import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.View;

public interface ProgramView extends View {

	public interface Presenter {
		void onProgramEvaluate(boolean consider);
		void onProgramPrint();
		void getPrintoutsTransactionsData();
		void getSubmissionsTransactionsData();
		void getValidationsTransactionsData();
		void getProgramSkeleton(String extension);
		void updateEditable(String extension);
		void saveToLocalStorage(String name, byte[] code, List<String> inputs);
	}
	
	void setPresenter(Presenter presenter);
	
	byte[] getProgramCode();
	String getProgramName();
	boolean isLanguageEditable();
	List<String> getInputs();
	
	void setProgramIdentification(String id,String name);
	void setOutputs(List<String> outputs);
	void setInputs(List<String> inputs);
	void setExecutionTimes(List<String> times);
	void clearObservations();
	void setObservations(String observations);
	void addStatus(String status);
	void addObservations(String observations);
	void addFeedback(String feedback);
	
	void setPrintTooltip(int remaining, long resetTime);
	void setSubmitTooltip(int remaining, long resetTime);
	void setValidateTooltip(int remaining, long resetTime);

	void setProgramCode(byte[] result);
	void setProgramName(String name);
	void setEditable(boolean editable);

	void increaseProgramWaitingEvaluation();
	void decreaseProgramWaitingEvaluation();
	
	void setLanguages(Map<String, String> languages);


}
