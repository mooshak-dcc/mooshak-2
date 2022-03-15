package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox;
import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox.OptionFormatter;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

/**
 * Generate output for tests
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class GenerateOutputsContent extends Composite implements DialogContent {

	private static GenerateOutputsContentUiBinder uiBinder = GWT.create(GenerateOutputsContentUiBinder.class);

	@UiTemplate("GenerateOutputsContent.ui.xml")
	interface GenerateOutputsContentUiBinder extends UiBinder<Widget, GenerateOutputsContent> {
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
	SelectOneListBox<SelectableOption> lbSolutionSelector;

	public GenerateOutputsContent() {
		initWidget(uiBinder.createAndBindUi(this));

		addHandlers();
		lbSolutionSelector.setFormatter(SELECT_FOMATTER);
	}

	/**
	 * Add handlers to inputs
	 */
	private void addHandlers() {
	}

	@Override
	public MethodContext getContext() {
		MethodContext context = new MethodContext();
		
		context.addPair("solution", lbSolutionSelector.getSelectedOption().getId());
		
		return context;
	}

	@Override
	public void setContext(MethodContext context) {
		
		LinkedList<SelectableOption> options = new LinkedList<>();
		
		if (context == null) {
			lbSolutionSelector.setSelections(options);
			return;
		}
		
		if (context.getValues("solution") != null) {
			
			List<String> solutions = context.getValues("solution");
			for (String solution : solutions) 
				options.add(new SelectableOption(context.getValue(solution + "_path"), solution));
		} 
		
		lbSolutionSelector.setSelections(options);
		
		if (!options.isEmpty())
			lbSolutionSelector.setSelectedValue(options.getFirst());
	}

	@Override
	public String getWidth() {
		return "600px";
	}

	@Override
	public String getHeight() {
		return "120px";
	}

}
