package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox;
import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox.OptionFormatter;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

/**
 * First step of the tournament creation procedure
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class CreateTournamentContent extends Composite implements DialogContent {

	private static CreateTournamentContentUiBinder uiBinder = 
			GWT.create(CreateTournamentContentUiBinder.class);

	private static final OptionFormatter<SelectableOption> SELECT_FOMATTER = 
			new OptionFormatter<SelectableOption>() {
		public String getLabel(SelectableOption option) {
			return option.getLabel();
		};

		public String getValue(SelectableOption option) {
			return option.getId();
		};
	};

	@UiTemplate("CreateTournamentContent.ui.xml")
	interface CreateTournamentContentUiBinder extends UiBinder<Widget, CreateTournamentContent> {
	}

	@UiField
	TextBox tournamentTitle;

	@UiField
	SelectOneListBox<SelectableOption> gameSelector;

	public CreateTournamentContent() {
		initWidget(uiBinder.createAndBindUi(this));

		gameSelector.setFormatter(SELECT_FOMATTER);
	}

	@Override
	public MethodContext getContext() {
		MethodContext context = new MethodContext();

		context.addPair("tournament_title", tournamentTitle.getText());

		if (gameSelector.getSelectedOption() != null)
			context.addPair("game_id", gameSelector.getSelectedOption().getId());

		return context;
	}

	@Override
	public void setContext(MethodContext context) {

		if (context == null)
			return;

		List<String> gameIds = context.getValues("game_id");
		if (gameIds == null)
			return;

		List<String> gameNames = context.getValues("game_name");
		if (gameNames == null)
			return;

		List<SelectableOption> selections = new ArrayList<>();
		for (int i = 0; i < gameIds.size(); i++) {

			SelectableOption option = new SelectableOption(gameIds.get(i),
					i < gameNames.size() ? gameNames.get(i) : gameIds.get(i));
			selections.add(option);
		}

		gameSelector.setSelections(selections);
	}

	@Override
	public String getWidth() {
		return "600px";
	}

	@Override
	public String getHeight() {
		return "150px";
	}

}
