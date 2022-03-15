package pt.up.fc.dcc.mooshak.client.gadgets.gameviewer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.asuraviewer.client.player.AsuraViewer;
import pt.up.fc.dcc.asuraviewer.client.player.AsuraViewer.Compression;
import pt.up.fc.dcc.asuraviewer.client.player.AsuraViewer.Mode;
import pt.up.fc.dcc.asuraviewer.client.player.utils.HasObservationsDisplay;
import pt.up.fc.dcc.mooshak.client.widgets.ResizableHtmlPanel;

public class GameViewerViewImpl extends ResizeComposite 
	implements GameViewerView {

	private static GameViewerViewUiBinder uiBinder = GWT
			.create(GameViewerViewUiBinder.class);

	@UiTemplate("GameViewerView.ui.xml")
	interface GameViewerViewUiBinder extends
			UiBinder<Widget, GameViewerViewImpl> {}
	
	private Presenter presenter = null;
	
	@UiField
	ResizableHtmlPanel panel;
	
	@UiField
	AsuraViewer gameViewer;
	
	private String problemId;
	
	public GameViewerViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		
		initialize();
		
		attachHandlers();
	}

	/**
	 * @param problemId the problemId to set
	 */
	public void setProblemId(String problemId) {
		this.problemId = problemId;
	}

	/**
	 * @param submissionId the submissionId to set
	 */
	public void setSubmissionId(String submissionId) {
		gameViewer.setPlayerId(submissionId);
	}

	/**
	 * Initialize game viewer view
	 */
	private void initialize() {
	}

	/**
	 * Attach handlers to view
	 */
	private void attachHandlers() {
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {				
				panel.onResize();
			}
		});
		
		// request match with specific id
		gameViewer.addRequestMatchHandler(e -> {
			
			presenter.requestMatchMovie(e.getMatchId());
		});
		
		// request image
		gameViewer.addRequestImageHandler(e -> {

            final Image sprite = new Image();
            sprite.addLoadHandler(event -> { gameViewer.onImageLoaded(e.getKey(), sprite);});
            sprite.setUrl("image/" + problemId + "/" + e.getImage());
            sprite.setVisible(false);
            RootPanel.get().add(sprite);
        });
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setObservationsDisplay(HasText display) {
		gameViewer.setObservationsDisplay(new HasObservationsDisplay() {
			
			@Override
			public void show(String observation) {
				display.setText(observation);
			}
			
			@Override
			public void prepend(String observation) {
				String prevObs = display.getText();
				display.setText(observation + "\n" + prevObs);
			}
			
			@Override
			public void clear() {
				display.setText("");
			}
			
			@Override
			public void append(String observation) {
				String prevObs = display.getText();
				display.setText(prevObs + "\n" + observation);
			}
		});
	}

	@Override
	public void setMode(Mode mode) {
		gameViewer.setMode(mode);
	}

	@Override
	public void setMovie(String json) {
		gameViewer.setMovie(json, Compression.LZ77);
	}

	@Override
	public void setTournament(String json) {
		gameViewer.setTournament(json, Compression.LZ77);
	}
	
}
