package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Content of dialog box for collecting data for contest submissions replay
 * 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class ReplayFormContent extends Composite implements DialogContent {

	private final int VISIBLE_ITEM_COUNT = 4;
	private final boolean MULTIPLE_SELECT = true; 
	
	private static ReplayFormContentUiBinder uiBinder = 
			GWT.create(ReplayFormContentUiBinder.class);
	
	@UiTemplate("ReplayFormContent.ui.xml")
	interface ReplayFormContentUiBinder extends UiBinder<Widget, ReplayFormContent> {}
	
	
	public ReplayFormContent() {
		
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiField
	ListBox problemList; 
	
	@UiField
	ListBox languageList; 
	
	@UiField
	ListBox delayList;
	
	@Override
	public MethodContext getContext() {
		MethodContext context = new MethodContext();
		
		listToContext(context,languageList,"language");
		listToContext(context,problemList,"problem");
		listToContext(context,delayList,"maxDelay");
	
		return context;
	}
	
	private void listToContext(MethodContext context,ListBox list,String name) {
		for(int i=0; i< list.getItemCount(); i++)
			if(list.isItemSelected(i))
				context.addPair(name, list.getItemText(i));	
	}
	

	@Override
	public void setContext(MethodContext context) {
		
		contextToList(context,languageList,"language");
		contextToList(context,problemList,"problem");
	}

	
	
	private void contextToList(MethodContext context,ListBox list,String name) {
	
		list.setMultipleSelect(MULTIPLE_SELECT);
		list.setVisibleItemCount(VISIBLE_ITEM_COUNT);
		
		
		for(String value: context.getValues(name))
			list.addItem(value);
	}
	
	@Override
	public String getWidth() {
		return "500px";
	}

	@Override
	public String getHeight() {
		return "100px";
	}
}
