package pt.up.fc.dcc.mooshak.client.gadgets.leaderboard;

import pt.up.fc.dcc.mooshak.client.widgets.ResizableHtmlPanel;
import pt.up.fc.dcc.mooshak.shared.results.gamification.LeaderboardEntry;
import pt.up.fc.dcc.mooshak.shared.results.gamification.LeaderboardResponse;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class LeaderboardViewImpl extends ResizeComposite implements
		LeaderboardView {

	private static LeaderboardUiBinder uiBinder = GWT
			.create(LeaderboardUiBinder.class);

	@UiTemplate("LeaderboardView.ui.xml")
	interface LeaderboardUiBinder extends UiBinder<Widget, LeaderboardViewImpl> {
	}

	private Presenter presenter = null;
	
	@UiField
	ResizableHtmlPanel container;
	
	@UiField(provided=true)
	MyPaginationDataGrid<ScoreRow> grid; 

	public LeaderboardViewImpl() {
		grid = new MyPaginationDataGrid<ScoreRow>(ScoreDatabase.getInstance()
			.getDataProvider().getKeyProvider());

		initWidget(uiBinder.createAndBindUi(this));
		
		container.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				grid.onResize();
			}
		});
		
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void updateLeaderboard(LeaderboardResponse resp) {
		ScoreDatabase.getInstance().getDataProvider().getList().clear();
		
		Set<LeaderboardEntry> scores = new LinkedHashSet<>(resp.getItems());
		
		for (LeaderboardEntry leaderboardEntry : scores) {
			ScoreRow score = new ScoreRow(leaderboardEntry.getPlayer(),
					leaderboardEntry.getScoreValue(), 
					leaderboardEntry.getFormattedScore(), 
					leaderboardEntry.getTimeSpan(), 
					leaderboardEntry.getWriteTimestampMillis(), 
					leaderboardEntry.getScoreTag());
			
			ScoreDatabase.getInstance().addScore(score);
		}

		ScoreDatabase.getInstance().getDataProvider().refresh();
		grid.setDataList(ScoreDatabase.getInstance().getDataProvider().getList());
		grid.refreshDisplays();
	}
	
}
