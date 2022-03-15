package pt.up.fc.dcc.mooshak.client.gadgets.videoviewer;

import pt.up.fc.dcc.mooshak.client.View;

public interface VideoView extends View {

	public interface Presenter {

	}
	
	void setPresenter(Presenter presenter);

	/**
	 * Sets the source of the video
	 * @param label
	 * @param src
	 */
	void setVideoSrc(String name, String src);
}
