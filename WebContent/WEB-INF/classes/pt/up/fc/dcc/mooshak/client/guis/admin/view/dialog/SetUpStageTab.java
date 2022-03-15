package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox;
import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox.OptionFormatter;
import pt.up.fc.dcc.mooshak.client.widgets.StrictIntegerBox;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

/**
 * Tab to set up a stage of the tournament.
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class SetUpStageTab extends Composite {
	
	public interface StageDataChangeHandler {
		void onStageDataChange();
	}

	private static SetUpStageTabUiBinder uiBinder = GWT.create(SetUpStageTabUiBinder.class);

	private static final OptionFormatter<SelectableOption> SELECT_FOMATTER = 
			new OptionFormatter<SelectableOption>() {
		public String getLabel(SelectableOption option) {
			return option.getLabel();
		};

		public String getValue(SelectableOption option) {
			return option.getId();
		};
	};

	@UiTemplate("SetUpStageTab.ui.xml")
	interface SetUpStageTabUiBinder extends UiBinder<Widget, SetUpStageTab> {
	}
	
	private List<StageDataChangeHandler> handlers = new ArrayList<>();

	@UiField
	SelectOneListBox<SelectableOption> modeSelector;

	@UiField
	StrictIntegerBox nrPlayersMatch;

	@UiField
	StrictIntegerBox minPlayersGroup;

	@UiField
	StrictIntegerBox maxRounds;

	@UiField
	StrictIntegerBox nrQualified;

	@UiField
	SelectOneListBox<SelectableOption> resultTypeSelector;
	
	@UiField
	TextBox matchTiebreakers;
	
	@UiField
	HTMLPanel rankingPointsPnl;
	
	private List<StrictIntegerBox> rankingPointsBoxes = new ArrayList<>();
	
	@UiField
	TextBox rankingTiebreakers;

	private int stageIndex;
	
	private Map<String, List<String>> fieldsPerMode;
	private Map<String, Integer> nrOfRankingPointsBoxes;

	public SetUpStageTab(int stageIndex, Map<String, String> modes, Map<String, List<String>> fieldsPerMode,
			Map<String, String> resultTypes, Map<String, Integer> nrOfRankingPointsBoxes) {
		initWidget(uiBinder.createAndBindUi(this));

		this.stageIndex = stageIndex;
		this.fieldsPerMode = fieldsPerMode;
		this.nrOfRankingPointsBoxes = nrOfRankingPointsBoxes;

		initialize(modes, resultTypes);

		addHandlers();
	}

	private void initialize(Map<String, String> modes, Map<String, String> resultTypes) {

		// stage modes
		modeSelector.setFormatter(SELECT_FOMATTER);
		
		List<SelectableOption> modeOptions = new ArrayList<>();
		for (String mode: modes.keySet()) {
			modeOptions.add(new SelectableOption(mode, modes.get(mode)));
		}

		modeSelector.setSelections(modeOptions);

		filterVisibleFieldsForMode(modeSelector.getSelectedOption().getId());

		// result types
		resultTypeSelector.setFormatter(SELECT_FOMATTER);
		
		List<SelectableOption> resultTypeOptions = new ArrayList<>();
		for (String resultType: resultTypes.keySet()) {
			resultTypeOptions.add(new SelectableOption(resultType, 
					resultTypes.get(resultType)));
		}

		resultTypeSelector.setSelections(resultTypeOptions);

		filterVisibleFieldsForMatchResultType(resultTypeSelector.getSelectedOption().getId());
	}

	private void addHandlers() {

		modeSelector.addValueChangeHandler(new ValueChangeHandler<SelectableOption>() {

			@Override
			public void onValueChange(ValueChangeEvent<SelectableOption> event) {

				SelectableOption option = event.getValue();

				if (option == null)
					return;

				filterVisibleFieldsForMode(option.getId());
			}
		});
		
		resultTypeSelector.addValueChangeHandler(new ValueChangeHandler<SelectableOption>() {

			@Override
			public void onValueChange(ValueChangeEvent<SelectableOption> event) {
				
				SelectableOption option = event.getValue();

				if (option == null)
					return;

				filterVisibleFieldsForMatchResultType(option.getId());
			}
		});

		nrPlayersMatch.addChangeHandler(e -> {
			filterVisibleFieldsForMatchResultType(resultTypeSelector.getSelectedOption().getId());
			fireStageDataChange();
		});
		minPlayersGroup.addChangeHandler(e -> fireStageDataChange());
		maxRounds.addChangeHandler(e -> fireStageDataChange());
		nrQualified.addChangeHandler(e -> fireStageDataChange());
	}

	private void filterVisibleFieldsForMode(String mode) {
		
		hideElement(nrPlayersMatch);
		hideElement(maxRounds);
		hideElement(nrQualified);
		hideElement(minPlayersGroup);
		hideElement(resultTypeSelector);
		hideElement(matchTiebreakers);
		hideElement(rankingPointsPnl);
		hideElement(rankingTiebreakers);
		
		List<String> fields = fieldsPerMode.get(mode);
		
		for (String field: fields) {
			
			switch (field) {
			case "NR_PLAYERS_PER_MATCH":
				showElement(nrPlayersMatch);
				break;
			case "MAX_NR_OF_ROUNDS":
				showElement(maxRounds);
				break;
			case "NR_OF_QUALIFIED_PLAYERS":
				showElement(nrQualified);
				break;
			case "MIN_NR_OF_PLAYERS_PER_GROUP":
				showElement(minPlayersGroup);
				break;
			case "RESULT_TYPE":
				showElement(resultTypeSelector);
				break;
			case "MATCH_TIEBREAKERS":
				showElement(matchTiebreakers);
				break;
			case "RANKING_POINTS":
				showElement(rankingPointsPnl);
				break;
			case "RANKING_TIEBREAKERS":
				showElement(rankingTiebreakers);
				break;
			default:
				break;
			}
		}
	}
	
	private void filterVisibleFieldsForMatchResultType(String resultType) {
		
		rankingPointsPnl.clear();
		rankingPointsBoxes.clear();
		
		Integer nrOfBoxes = nrOfRankingPointsBoxes.get(resultType);
		
		if (nrOfBoxes == null)
			return;
		
		if (nrOfBoxes < 0) {
			Integer value = nrPlayersMatch.getValue();
			nrOfBoxes = value == null ? 0 : value;
		}
		
		if (nrOfBoxes == 0) {
			hideElement(rankingPointsPnl);
			return;
		}
		
		String width = (100/nrOfBoxes) + "%";
		for (int i = 0; i < nrOfBoxes; i++) {
			StrictIntegerBox strictIntegerBox = new StrictIntegerBox();
			strictIntegerBox.setWidth(width);
			rankingPointsPnl.add(strictIntegerBox);
			rankingPointsBoxes.add(strictIntegerBox);
		}
		
		showElement(rankingPointsPnl);
	}
	
	public String getStageFormat() {
		if (modeSelector.getSelectedOption() == null)
			return null;
		return modeSelector.getSelectedOption().getId();
	}

	/**
	 * Get number of players per match
	 * 
	 * @return number of players per match
	 */
	public Integer getNrPlayersMatch() {
		return nrPlayersMatch.getValue();
	}

	/**
	 * Get minimum number of players per group
	 * 
	 * @return minimum number of players per group
	 */
	public Integer getMinPlayersGroup() {
		return minPlayersGroup.getValue();
	}

	/**
	 * Get maximum number of rounds
	 * 
	 * @return maximum number of rounds
	 */
	public Integer getMaxRounds() {
		return maxRounds.getValue();
	}

	/**
	 * Get number of qualified players
	 * 
	 * @return number of qualified players
	 */
	public Integer getNrQualified() {
		return nrQualified.getValue();
	}
	
	public String getMatchResultType() {
		if (resultTypeSelector.getSelectedOption() == null)
			return null;
		return resultTypeSelector.getSelectedOption().getId();
	}
	
	public List<String> getMatchTieBreakers() {
		
		String tieBreakersStr = matchTiebreakers.getValue().trim();
		if (tieBreakersStr.isEmpty())
			return new ArrayList<>();
		
		return Arrays.stream(tieBreakersStr.split(","))
			.map(String::trim)
			.collect(Collectors.toList());
	}
	
	public List<Integer> getRankingPoints() {
		
		List<Integer> rankingPoints = new ArrayList<>();
		for (StrictIntegerBox integerBox : rankingPointsBoxes) {
			
			Integer value = integerBox.getValue();
			if (value == null)
				rankingPoints.add(0);
			else
				rankingPoints.add(value);
		}
		
		return rankingPoints;
	}
	
	public List<String> getRankingTieBreakers() {
		
		String tieBreakersStr = rankingTiebreakers.getValue().trim();
		if (tieBreakersStr.isEmpty())
			return new ArrayList<>();
		
		return Arrays.stream(tieBreakersStr.split(","))
			.map(String::trim)
			.collect(Collectors.toList());
	}
	
	/**
	 * Add a {@link StageDataChangeHandler} handler
	 * 
	 * @param handler {@link StageDataChangeHandler}
	 */
	public void addStageDataChangeHandler(StageDataChangeHandler handler) {
		handlers.add(handler);
	}
	
	private void fireStageDataChange() {
		for (StageDataChangeHandler stageDataChangeHandler : handlers) {
			stageDataChangeHandler.onStageDataChange();
		}
	}
	
	/***********************************************************************
	 *                         Helper Functions                            *
	 ***********************************************************************/
	
	/**
	 * Hide a row of {@link Widget} widget
	 * 
	 * @param w {@link Widget} widget whose row is to hide
	 */
	private void hideElement(Widget w) {
		Element parentPanel = w.getElement()
				.getParentElement();
		parentPanel.getStyle().setDisplay(Display.NONE);
		parentPanel.getNextSiblingElement().getStyle().setDisplay(Display.NONE);
		
		if (w instanceof ValueBoxBase)
			((ValueBoxBase<?>) w).setText("");
		else if (w instanceof SelectOneListBox)
			((SelectOneListBox<?>) w).setSelectedValue(null);
			
	}
	
	/**
	 * Show a row of {@link Widget} widget
	 * 
	 * @param w {@link Widget} widget whose row is to hide
	 */
	private void showElement(Widget w) {
		Element parentPanel = w.getElement()
				.getParentElement();
		parentPanel.getStyle().setDisplay(Display.BLOCK);
		parentPanel.getNextSiblingElement().getStyle().setDisplay(Display.BLOCK);
	}
	
}
