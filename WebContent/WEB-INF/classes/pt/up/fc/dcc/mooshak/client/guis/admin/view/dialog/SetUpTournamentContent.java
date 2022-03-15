package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog.SetUpStageTab.StageDataChangeHandler;
import pt.up.fc.dcc.mooshak.client.widgets.StrictIntegerBox;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

/**
 * Set up tournament configuration
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class SetUpTournamentContent extends Composite implements DialogContent, StageDataChangeHandler {

	private static SetUpTournamentContentUiBinder uiBinder = GWT.create(SetUpTournamentContentUiBinder.class);

	@UiTemplate("SetUpTournamentContent.ui.xml")
	interface SetUpTournamentContentUiBinder extends UiBinder<Widget, SetUpTournamentContent> {
	}
	
	private String tournamentId = null;
	
	@UiField
	TabPanel stagePanel;

	@UiField
	ListBox lbPlayersSelector;

	@UiField
	Label lblPlayersCount;

	@UiField
	Label lblMaxPlayersMatch;

	@UiField
	Label lblMinPlayersMatch;
	
	@UiField
	StrictIntegerBox tbNrStages;

	private Map<String, String> resultTypes = new HashMap<>();
	private Map<String, String> stageModes = new HashMap<>();
	
	private Map<String, List<String>> fieldsPerMode = new HashMap<>();
	private Map<String, Integer> nrOfRankingPointsBoxes = new HashMap<>();

	public SetUpTournamentContent() {
		initWidget(uiBinder.createAndBindUi(this));
		
		initialize();

		addHandlers();
	}

	private void initialize() {

	}

	/**
	 * Add handlers to inputs
	 */
	private void addHandlers() {
		lbPlayersSelector.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				int nrPlayers = getSelectedPlayers().size();
				lblPlayersCount.setText(Integer.toString(nrPlayers));
			}
		});
		
		final SetUpTournamentContent that = this;
		tbNrStages.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				
				Integer value = tbNrStages.getValue();
				
				if (value == null || value < 0)
					return;
				
				if (value.intValue() > stagePanel.getWidgetCount()) {
					for (int i = stagePanel.getWidgetCount(); i < value.intValue(); i++) {
						SetUpStageTab stageTab = new SetUpStageTab(i + 1, stageModes, 
								fieldsPerMode, resultTypes, nrOfRankingPointsBoxes);
						stageTab.addStageDataChangeHandler(that);
						stagePanel.add(stageTab, "Stage " + (i + 1));
					}
				} else {
					int nrTabs = stagePanel.getWidgetCount();
					for (int i = value.intValue(); i < nrTabs; i++) {
						int indexToRemove = stagePanel.getWidgetCount() - 1;
						stagePanel.remove(indexToRemove);
					}
				}

				stagePanel.selectTab(0);
			}
		});
	}

	@Override
	public MethodContext getContext() {
		MethodContext context = new MethodContext();
		
		context.addPair("tournament_id", tournamentId);
		
		for (int i = 0; i < lbPlayersSelector.getItemCount(); i++) {
			if (lbPlayersSelector.isItemSelected(i))
				context.addPair("submission", lbPlayersSelector.getValue(i));
		}
		
		int nrStages = tbNrStages.getValue() == null ? 0 : tbNrStages.getValue();
		context.addPair("nr_stages", String.valueOf(nrStages));
		
		/*context.addPair("match_type", resultTypeSelector.getSelectedOption().getId());
		
		int win = pointsWin.getValue() == null ? -1 : pointsWin.getValue();
		context.addPair("win_points", String.valueOf(win));
		
		int draw = pointsDraw.getValue() == null ? -1 : pointsDraw.getValue();
		context.addPair("draw_points", String.valueOf(draw));
		
		int loss = pointsLoss.getValue() == null ? -1 : pointsLoss.getValue();
		context.addPair("loss_points", String.valueOf(loss));*/
		
		for (int i = 0; i < nrStages; i++) {
			
			String keyPrefix = "stage_" +  (i + 1) + "_";
			
			SetUpStageTab stageTab = (SetUpStageTab) stagePanel.getWidget(i);
			
			String stageFormat = stageTab.getStageFormat();
			Integer nrPlayersMatch = stageTab.getNrPlayersMatch();
			Integer minPlayersGroup = stageTab.getMinPlayersGroup();
			Integer maxRounds = stageTab.getMaxRounds();
			Integer nrQualified = stageTab.getNrQualified();
			String matchResultType = stageTab.getMatchResultType();
			List<String> matchTieBreakers = stageTab.getMatchTieBreakers();
			List<Integer> rankingPoints = stageTab.getRankingPoints();
			List<String> rankingTieBreakers = stageTab.getRankingTieBreakers();
			
			if (stageFormat != null)
				context.addPair(keyPrefix + "type", stageFormat);
			
			if (nrPlayersMatch != null)
				context.addPair(keyPrefix + "nr_players_match",
					Integer.toString(nrPlayersMatch));
			
			if (minPlayersGroup != null)
				context.addPair(keyPrefix + "min_players_group",
					Integer.toString(minPlayersGroup));
			
			if (maxRounds != null)
				context.addPair(keyPrefix + "max_rounds",
					Integer.toString(maxRounds));
			
			if (nrQualified != null)
				context.addPair(keyPrefix + "nr_qualified",
					Integer.toString(nrQualified));
			
			if (matchResultType != null)
				context.addPair(keyPrefix + "match_result_type", matchResultType);
			
			for (String tiebreaker : matchTieBreakers)
				context.addPair(keyPrefix + "match_tiebreaker", tiebreaker);
			
			for (Integer rankingPoint : rankingPoints)
				context.addPair(keyPrefix + "ranking_points", Integer.toString(rankingPoint));
			
			for (String tiebreaker : rankingTieBreakers)
				context.addPair(keyPrefix + "ranking_tiebreaker", tiebreaker);
		}

		return context;
	}

	@Override
	public void setContext(MethodContext context) {
		
		if (context == null)
			return;
		
		tournamentId = context.getValue("tournament_id");

		if (context.getValue("max_players_match") != null)
			lblMaxPlayersMatch.setText(context.getValue("max_players_match"));
		if (context.getValue("min_players_match") != null)
			lblMinPlayersMatch.setText(context.getValue("min_players_match"));
		if (context.getValues("player_id") != null) {
			
			List<String> playersIds = context.getValues("player_id");
			for (String playerId : playersIds) {
				String submissionId = context.getValue("submission_" + playerId);
				lbPlayersSelector.addItem(playerId, submissionId);
			}
		}
		if (context.getValues("stage_format") != null) {
			
			List<String> stageFormats = context.getValues("stage_format");
			for (String stageFormat: stageFormats) {
				String stageFormatDsc = context.getValue(stageFormat + "_dsc");
				stageModes.put(stageFormat, stageFormatDsc);
				
				List<String> fields = context.getValues(stageFormat + "_fields");
				if (fields != null)
					fieldsPerMode.put(stageFormat, fields);
			}
		}
		if (context.getValues("match_type") != null) {
			
			List<String> matchTypes = context.getValues("match_type");
			for (String matchType: matchTypes) {
				String matchTypeDsc = context.getValue(matchType + "_dsc");
				resultTypes.put(matchType, matchTypeDsc);
				
				String rankingPointsBoxes = context.getValue(matchType + "_nr_ranking_points");
				if (rankingPointsBoxes != null)
					nrOfRankingPointsBoxes.put(matchType, Integer.parseInt(rankingPointsBoxes));
			}
		}		
		
	}

	@Override
	public String getWidth() {
		return "700px";
	}

	@Override
	public String getHeight() {
		return "620px";
	}

	@Override
	public void onStageDataChange() {
		//updateStageBounds();
	}
	
	
	/**
	 * Get players selected for tournament
	 * 
	 * @return players selected for tournament
	 */
	private List<String> getSelectedPlayers() {

		List<String> selected = new ArrayList<>();
		for (int i = 0; i < lbPlayersSelector.getItemCount(); i++) {
			if (lbPlayersSelector.isItemSelected(i))
				selected.add(lbPlayersSelector.getValue(i));
		}

		return selected;
	}

	
	/***********************************************************************
	 *                         Helper Functions                            *
	 ***********************************************************************/
	
	/**
	 * Hide a row of {@link ValueBoxBase} widget
	 * 
	 * @param w {@link ValueBoxBase} widget whose row is to hide
	 */
	private <T> void hideElement(ValueBoxBase<T> w) {
		Element parentPanel = w.getElement().getParentElement();
		parentPanel.getStyle().setDisplay(Display.NONE);
		w.setText("");
	}
	
	/**
	 * Show a row of {@link ValueBoxBase} widget
	 * 
	 * @param w {@link ValueBoxBase} widget whose row is to hide
	 */
	private <T> void showElement(ValueBoxBase<T> w) {
		Element parentPanel = w.getElement().getParentElement();
		parentPanel.getStyle().setDisplay(Display.BLOCK);
	}
	
}
