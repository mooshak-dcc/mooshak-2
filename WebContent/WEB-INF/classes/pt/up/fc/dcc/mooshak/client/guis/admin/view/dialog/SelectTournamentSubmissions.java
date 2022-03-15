package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

/**
 * Select tournament submissions
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class SelectTournamentSubmissions extends Composite implements DialogContent {

	private static SelectTournamentSubmissionsUiBinder uiBinder = 
			GWT.create(SelectTournamentSubmissionsUiBinder.class);

	@UiTemplate("SelectTournamentSubmissions.ui.xml")
	interface SelectTournamentSubmissionsUiBinder extends UiBinder<Widget, SelectTournamentSubmissions> {
	}

	@UiField
	ListBox lbPlayersSelector;

	public SelectTournamentSubmissions() {
		initWidget(uiBinder.createAndBindUi(this));

		addHandlers();
	}

	/**
	 * Add handlers to inputs
	 */
	private void addHandlers() {
	}

	@Override
	public MethodContext getContext() {
		MethodContext context = new MethodContext();
		
		List<String> playersIds = getSelectedPlayers();
		for (String playerId: playersIds) 
			context.addPair("submission", playerId);

		return context;
	}

	@Override
	public void setContext(MethodContext context) {
		
		if (context == null)
			return;
	
		if (context.getValues("player_id") != null) {
			
			List<String> playersIds = context.getValues("player_id");
			for (String playerId : playersIds) {
				String submissionId = context.getValue("submission_" + playerId);
				lbPlayersSelector.addItem(playerId, submissionId);
			}
		}
	}

	@Override
	public String getWidth() {
		return "300px";
	}

	@Override
	public String getHeight() {
		return "420px";
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
}
