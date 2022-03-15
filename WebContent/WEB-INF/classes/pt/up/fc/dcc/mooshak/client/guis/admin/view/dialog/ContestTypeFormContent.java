package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import java.util.ArrayList;
import java.util.List;

import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * Content of dialog box for selecting contest type
 * 
 *
 * @author josepaiva
 */
public class ContestTypeFormContent extends Composite 
	implements DialogContent {
	
	private static ContestTypeFormContentUiBinder uiBinder = 
			GWT.create(ContestTypeFormContentUiBinder.class);
	
	@UiTemplate("ContestTypeFormContent.ui.xml")
	interface ContestTypeFormContentUiBinder 
		extends UiBinder<Widget, ContestTypeFormContent> {}
	
	@UiField
	TableRowElement header;
	
	@UiField
	TableRowElement policy;
	
	@UiField
	TableRowElement hidelistings;
	
	@UiField
	TableRowElement virtual;
	
	@UiField
	TableRowElement service;
	
	@UiField
	TableRowElement challenge;
	
	@UiField
	TableRowElement printoutsList;
	
	@UiField
	TableRowElement balloonsList;
	
	@UiField
	TableRowElement submissionsFeedback;
	
	@UiField
	TableRowElement submissionsErrors;
	
	@UiField
	TableRowElement submissionsState;
	
	@UiField
	TableRowElement submissionsMultiAccepts;
	
	@UiField
	TableRowElement maxProg;
	
	private List<TableCellElement> rdTypes = new ArrayList<>();
	
	public ContestTypeFormContent() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public MethodContext getContext() {
		MethodContext context = new MethodContext();
	
		for (TableCellElement cell : rdTypes) {
			InputElement radio = (InputElement) cell.getFirstChildElement().getFirstChildElement();
			if(radio.isChecked()) {
				context.addPair("type",radio.getNextSiblingElement().getInnerText());
				break;
			}
		}
		
		return context;
	}

	@Override
	public void setContext(MethodContext context) {
		List<String> types = context.getValues("type");
		for (String type : types) {
			RadioButton radio = new RadioButton("types", type);
			if(type.equalsIgnoreCase(context.getValue("currentType"))) {
				radio.setValue(true);
			}
			
			TableCellElement cell = header.insertCell(header.getCells()
					.getLength());
			rdTypes.add(cell);
			cell.appendChild(radio.getElement());
			
			String values[] = context.getValue(type).split(",");
			policy.setInnerHTML(policy.getInnerHTML() 
					+ "<td>" + values[0] + "</td>");
			hidelistings.setInnerHTML(hidelistings.getInnerHTML() 
					+ "<td>" + values[1] + "</td>");
			virtual.setInnerHTML(virtual.getInnerHTML() 
					+ "<td>" + values[2] + "</td>");
			service.setInnerHTML(service.getInnerHTML()
					+ "<td>" + values[3] + "</td>");
			challenge.setInnerHTML(challenge.getInnerHTML() 
					+ "<td>" + values[4] + "</td>");
			printoutsList.setInnerHTML(printoutsList.getInnerHTML() 
					+ "<td>" + values[5] + "</td>");
			balloonsList.setInnerHTML(balloonsList.getInnerHTML() 
					+ "<td>" + values[6] + "</td>");
			submissionsFeedback.setInnerHTML(submissionsFeedback
					.getInnerHTML()	+ "<td>" + values[7] + "</td>");
			submissionsErrors.setInnerHTML(submissionsErrors
					.getInnerHTML()	+ "<td>" + values[8] + "</td>");
			submissionsState.setInnerHTML(submissionsState
					.getInnerHTML()	+ "<td>" + values[9] + "</td>");
			submissionsMultiAccepts.setInnerHTML(submissionsMultiAccepts
					.getInnerHTML()	+ "<td>" + values[10] + "</td>");
			maxProg.setInnerHTML(maxProg.getInnerHTML() 
					+ "<td>" + values[11] + "</td>");
		}
	}

	@Override
	public String getWidth() {
		return "900px";
	}

	@Override
	public String getHeight() {
		return "500px";
	}

}
