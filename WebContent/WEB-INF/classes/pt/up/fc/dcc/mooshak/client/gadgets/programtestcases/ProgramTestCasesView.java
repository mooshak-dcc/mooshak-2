package pt.up.fc.dcc.mooshak.client.gadgets.programtestcases;

import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.View;

public interface ProgramTestCasesView extends View {

	public interface Presenter {
		void onProgramEvaluate(boolean consider);
		/*void onProgramPrint();*/
		/*void getPrintoutsTransactionsData();*/
		void getSubmissionsTransactionsData();
		void getValidationsTransactionsData();
		void hasTestPassed(int pos, String expected,
				String obtained);
	}
	
	void setPresenter(Presenter presenter);
	
	List<String> getInputs();
	void setOutputs(List<String> outputs);
	void setInputs(List<String> inputs);
	
	/*void setPrintTooltip(int remaining, long resetTime);*/
	void setSubmitTooltip(int remaining, long resetTime);
	void setValidateTooltip(int remaining, long resetTime);

	void insertPublicTestCases(Map<String, String> ios);
	void setTestPassed(int pos, boolean passed);

	void increaseProgramWaitingEvaluation();
	void decreaseProgramWaitingEvaluation();

}
