package pt.up.fc.dcc.mooshak.client.gadgets.viewstatement;

import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.client.View;

public interface StatementView extends View {

	public interface Presenter {

	}
	
	void setPresenter(Presenter presenter);
	void setProgramIdentification(String label, String title);
	void setExternalResource(String url);
	void setHTMLStatement(String statement, String problemId);
	void setPDFStatement(String problemId);
	void setStatement(String message);
	
	Widget getContentAsWidget();

	/**
	 * Sets the source of the video
	 * @param label
	 * @param src
	 */
	void setVideoSrc(String name, String src);
	
}
