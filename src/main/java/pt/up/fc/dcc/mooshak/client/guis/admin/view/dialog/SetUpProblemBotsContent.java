package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox;
import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox.OptionFormatter;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

/**
 * Set up bots for game problem.
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class SetUpProblemBotsContent extends Composite implements DialogContent {

	private static SetUpProblemBotsContentUiBinder uiBinder = GWT.create(SetUpProblemBotsContentUiBinder.class);

	@UiTemplate("SetUpProblemBotsContent.ui.xml")
	interface SetUpProblemBotsContentUiBinder extends UiBinder<Widget, SetUpProblemBotsContent> {
	}

	private static final OptionFormatter<SelectableOption> SELECT_FOMATTER = 
			new OptionFormatter<SelectableOption>() {
		public String getLabel(SelectableOption option) {
			return option.getLabel();
		};

		public String getValue(SelectableOption option) {
			return option.getId();
		};
	};

	@UiField
	SelectOneListBox<SelectableOption> lbGroupSelector;
	
	@UiField
	HTMLPanel pnlGroupId;

	@UiField
	TextBox txtGroupId;

	@UiField
	ListBox lbAISelector;

	public SetUpProblemBotsContent() {
		initWidget(uiBinder.createAndBindUi(this));
		lbGroupSelector.setFormatter(SELECT_FOMATTER);
		addHandlers();
		pnlGroupId.getElement().getStyle().setDisplay(Display.NONE);
	}

	/**
	 * Add handlers to inputs
	 */
	private void addHandlers() {
		
		lbGroupSelector.addValueChangeHandler(new ValueChangeHandler<SelectableOption>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<SelectableOption> e) {
				SelectableOption option = e.getValue();
				if (option.getId().equals("-1"))
					pnlGroupId.getElement().getStyle().setDisplay(Display.FLEX);
				else
					pnlGroupId.getElement().getStyle().setDisplay(Display.NONE);
			}
		});
	}

	@Override
	public MethodContext getContext() {
		MethodContext context = new MethodContext();
		
		String groupId = lbGroupSelector.getSelectedOption().getId();
		if (groupId.equals("-1"))
			groupId = txtGroupId.getValue();
		
		context.addPair("group_id", groupId);
		
		for (int i = 0; i < lbAISelector.getItemCount(); i++) {
			if (lbAISelector.isItemSelected(i)) {
				String solutionName = lbAISelector.getItemText(i);
				context.addPair("solution", solutionName);
				context.addPair(solutionName + "_path", lbAISelector.getValue(i));
			}
		}
		
		return context;
	}

	@Override
	public void setContext(MethodContext context) {
		
		LinkedList<SelectableOption> options = new LinkedList<>();
		options.add(new SelectableOption("-1", " Add new ..."));
		
		if (context == null) {
			lbGroupSelector.setSelections(options);
			return;
		}
		
		if (context.getValues("group_id") != null) {
			
			List<String> groupIds = context.getValues("group_id");
			for (String groupId : groupIds) 
				options.addFirst(new SelectableOption(groupId, context.getValue(groupId + "_name")));
		} else
			pnlGroupId.getElement().getStyle().setDisplay(Display.FLEX);
		
		lbGroupSelector.setSelections(options);
		
		lbGroupSelector.setSelectedValue(options.getFirst());
		
		if (context.getValues("solution") != null) {
			
			List<String> solutions = context.getValues("solution");
			for (String solution : solutions) {
				lbAISelector.addItem(solution, context.getValue(solution + "_path"));
			}
		}
	}

	@Override
	public String getWidth() {
		return "600px";
	}

	@Override
	public String getHeight() {
		return "200px";
	}

}
