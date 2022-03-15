package pt.up.fc.dcc.mooshak.client.guis.admin.view;

import java.util.ArrayList;
import java.util.List;

import pt.up.fc.dcc.mooshak.client.data.admin.FormDataProvider;
import pt.up.fc.dcc.mooshak.client.form.admin.ObjectForm;
import pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog.DialogSubmitEvent;
import pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog.DialogSubmitHandler;
import pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog.OperationDialog;
import pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog.OperationDialogFactory;
import pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog.OperationDialogFactoryImpl;
import pt.up.fc.dcc.mooshak.client.services.AdminCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.widgets.CustomImageButton;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType.MooshakField;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType.MooshakMethod;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Object editor implementation
 * A widget to view/edit mooshak objects
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class ObjectEditorViewImpl extends Composite implements ObjectEditorView{

	private static final int MESSAGE_VIEWING_TIME = 5*1000;

	private static ObjectEditorUiBinder uiBinder = 
			GWT.create(ObjectEditorUiBinder.class);
		
	@UiTemplate("ObjectEditorView.ui.xml")
	interface ObjectEditorUiBinder 
			extends UiBinder<Widget, ObjectEditorViewImpl> {}

	interface BaseStyle extends CssResource {
	    
	    String menu();
	}

	@UiField BaseStyle style;
	
	@UiField 
	ObjectForm form;
	
	@UiField 
	Label typeName;
	
	@UiField 
	Label idLabel;
	
	@UiField
	Label message;
	
	@UiField
	CustomImageButton createButton;
	
	@UiField
	CustomImageButton destroyButton;
	
	@UiField
	CustomImageButton renameButton;
	
	@UiField
	CustomImageButton copyButton;
	
	@UiField
	CustomImageButton pasteButton;
	
	@UiField
	CustomImageButton undoButton;
	
	@UiField
	CustomImageButton redoButton;
	
	@UiField
	CustomImageButton importButton;
	
	@UiField
	CustomImageButton exportButton;
	
	@UiField
	CustomImageButton freezeButton;
	
	@UiField
	CustomImageButton unfreezeButton;
	
	@UiField
	CustomImageButton menuButton;
	
	private Presenter presenter;
	
	private FormDataProvider provider;
	
	private OperationDialogFactory dialogFactory = 
			new OperationDialogFactoryImpl();


	private MooshakType mooshakType;
	
	public ObjectEditorViewImpl(AdminCommandServiceAsync rpc) {	
		initWidget(uiBinder.createAndBindUi(this));
		form.setRpcService(rpc);
	}	
	
	private CreateDialog createDialog = null;
    private DestroyDialog destroyDialog = null;	
    private RenameDialog renameDialog = null;	
	private ImportDialog importDialog = null;	
    private MenuDialog menuDialog = null; 
    private String id = null;
	private String contentType = null;
	private boolean frozen = false;
	
	private String copiedId = null;
	
	@Override
	protected void onLoad() {
		
		form.addValueChangeHandler(this); // update values first
		
		createButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(createDialog == null)
					createDialog = new CreateDialog();

				createDialog.start(contentType, id, presenter);
			}
		});
		
		destroyButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(destroyDialog == null)
					destroyDialog = new DestroyDialog();

				destroyDialog.start(id, presenter);
			}
		});
		
		renameButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(renameDialog == null)
					renameDialog = new RenameDialog();

				renameDialog.start(typeName.getText(), id, presenter);
			}
		});
		
		copyButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				copiedId = id;
				if(presenter != null)
					presenter.onCopy(id);
				
				setMessage("Copied");
			}
		});
		
		pasteButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(presenter != null)
					presenter.onPaste(id, copiedId);
			}
		});
		
		undoButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(presenter != null)
					presenter.onUndo(id);
			}
		});
		
		redoButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(presenter != null)
					presenter.onRedo(id);
			}
		});
		
		importButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(importDialog == null)
					importDialog = new ImportDialog();

				importDialog.start(contentType, id, presenter);
			}
		});
		
		exportButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(presenter != null)
					presenter.onExport(id);
			}
		});
		
		freezeButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(presenter != null)
					presenter.onFreeze(id);
			}
		});
		
		unfreezeButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(presenter != null)
					presenter.onUnfreeze(id);
			}
		});
		
		menuButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				menuDialog.showRelativeTo(menuButton);				
			}
		});
		
		
		createButton.setEnabled(false);
		destroyButton.setEnabled(false);
		renameButton.setEnabled(false);
		copyButton.setEnabled(false);
		pasteButton.setEnabled(false);
		undoButton.setEnabled(false);
		redoButton.setEnabled(false);
		freezeButton.setEnabled(false);
		unfreezeButton.setEnabled(false);
		menuButton.setEnabled(false);
	}
	
	@Override
	public void setRenameableEnabled(boolean value) {
		renameButton.setEnabled(value);
	}
	
	@Override
	public void setUndoEnabled(boolean value) {
		undoButton.setEnabled(value);
	}
	
	@Override
	public void setRedoEnabled(boolean value) {
		redoButton.setEnabled(value);
	}
	
	@Override
	public void setFreezeEnabled(boolean value) {
		freezeButton.setEnabled(value);
	}
	
	@Override
	public boolean getFrozen() {
		return frozen;
	}
	
	@Override
	public void setUnfreezeEnabled(boolean value) {
		unfreezeButton.setEnabled(value);
	}
		
	/**
	 * Get dialog factory used for creating dialogs to operations
	 * @return the dialogFactory
	 */
	public OperationDialogFactory getDialogFactory() {
		return dialogFactory;
	}


	/**
	 * Set dialog factory used for creating dialogs to operations
	 * @param dialogFactory the dialogFactory to set
	 */
	public void setDialogFactory(OperationDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setObjectDataProvider(FormDataProvider provider) {
		provider.addFormDataProvider(form);
		this.provider = provider;
	}


	@Override
	public void setObjectId(String objectId) {
		id = objectId;
		idLabel.setText(objectId);
		form.setObjectId(objectId);
	}
	
	@Override
	public void setObjectType(MooshakType type) {
		mooshakType = type;
		
		if(id != null)
			presenter.isFrozen(id);
		
		typeName.setText(type.getType());
		
		
		menuDialog = new MenuDialog(type.getMethods());
		
		contentType  = type.getContentType();
		form.setContainer(contentType != null);
		createButton.setEnabled(contentType != null);
		destroyButton.setEnabled(true);
		copyButton.setEnabled(id != null);
		pasteButton.setEnabled(copiedId != null && !copiedId.equals(id));
		
		if(id != null)
			presenter.isRenameable(id);
		
		if(id != null)
			presenter.canUndo(id);
		
		if(id != null)
			presenter.canRedo(id);
		
		
		menuButton.setEnabled(menuDialog.hasItems());
		
		if(provider != null)
			provider.refresh();
	}
	
	@Override
	public void setFrozenObjectType(boolean value) {
		setFreezeEnabled(!value);
		setUnfreezeEnabled(value);
		
		List<MooshakField> fields = new ArrayList<MooshakField>(); 
		
		if(value) {
			
			for (MooshakField mooshakField : mooshakType.getFields()) {
				MooshakField field = new MooshakField(mooshakField.getName(),
						AttributeType.LABEL);
				fields.add(field);
			}

		}
		else
			fields = mooshakType.getFields();

		form.setFieldTypes(fields);
		
		if(provider != null)
			provider.refresh();
		
		frozen = value;
	}
	
	@Override
	public void onValueChange(ValueChangeEvent<MooshakValue> event) {
		MooshakValue pair = event.getValue();
		
		presenter.onChange(idLabel.getText(),pair);
	}
	
	@Override
	public void setMessage(String text) {
		message.setText(text);
		
		resetMessage();
		
	}

	private Timer cleanupTimer = null;

	/**
	 * Reset message after some time
	 */
	private void resetMessage() {
		
		if(cleanupTimer != null)
			cleanupTimer.cancel();
		
		new Timer() {

			@Override
			public void run() {
				message.setText("");
				cleanupTimer = null;
			}
			
		}.schedule(MESSAGE_VIEWING_TIME);
	}

	/*--------------------------------------------------------------------*\
	 *                   Menus & Commands                                 *
	\*--------------------------------------------------------------------*/
	
	class MenuDialog extends PopupPanel {
		
		int itemCount = 0;
		
		MenuDialog(List<MooshakMethod> methods) {
			super(true);
			
			VerticalPanel panel = new VerticalPanel();
			Style popupPanelStyle = getElement().getStyle();
			Style panelStyle = panel.getElement().getStyle();
				
			panel.getElement().setClassName(style.menu());
				
			popupPanelStyle.setBorderWidth(0, Unit.PX);
			popupPanelStyle.setPadding(0, Unit.PX);
			popupPanelStyle.setMarginTop(-14, Unit.PX);
			
			panelStyle.setMargin(0, Unit.PX);
			panelStyle.setBorderWidth(1, Unit.PX);
			panelStyle.setPadding(0, Unit.PX);
			panelStyle.setBorderStyle(BorderStyle.NONE);
			panelStyle.setBackgroundColor("#EEE");
						
			createItems(panel,methods);
			
			setWidget(panel);
		}
		
		void createItems(VerticalPanel panel, List<MooshakMethod> methods) {
			for(final MooshakMethod method: methods) {
				if(! method.isShowable())
					continue;
				
				Button button = new Button(method.getName());
				final Style buttonStyle = button.getElement().getStyle();
				
				button.setTitle(method.getTip());
				panel.add(button);
				
				buttonStyle.setPadding(5, Unit.PX);
				buttonStyle.setWidth(100, Unit.PCT);
				buttonStyle.setBackgroundColor("#EEE");
				buttonStyle.setColor("#669");
				buttonStyle.setFontSize(14, Unit.PX);;
				buttonStyle.setFontWeight(FontWeight.BOLD);
				buttonStyle.setBorderWidth(0, Unit.PX);
				buttonStyle.setMargin(0, Unit.PX);
				buttonStyle.setTextAlign(TextAlign.LEFT);
				
				itemCount++;
								
				button.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						execute(method,null);
						hide();
					}
				});
				
			}

		}
				
		boolean hasItems() {
			return itemCount > 0;
		}
		
	}
	
	
	
	
	void execute(final MooshakMethod method, final MethodContext context) {
		if(method.isInputable()) {
			OperationDialog<?> dialog = 
					dialogFactory.getOperationDialog(method,context);
			
			dialog.addDialogSubmitHandler(new DialogSubmitHandler() {
				
				@Override
				public void onSubmit(DialogSubmitEvent event) {
					presenter.onExecute(
							idLabel.getText(),
							method,
							event.getContext());
				}
			});
		} else {
			presenter.onExecute(
					idLabel.getText(),
					method,
					null);
		}
		
	}

	@Override
	public void showMethodResult(CommandOutcome outcome) {
		if(outcome != null) {
			String continuation = outcome.getContinuation();
			String message = outcome.getMessage(); 
			MooshakMethod method = null;
			
			if(message != null)
				setMessage(outcome.getMessage());		
			
			if(continuation != null) {
				for(MooshakMethod someMethod: mooshakType.getMethods()) 
					if(continuation.equals(someMethod.getName()))
						method = someMethod;
				
				if(method != null)
					execute(method,outcome.getContext());
			}
		}
	}

	@Override
	public void setCopiedId(String copiedId) {
		this.copiedId = copiedId;
		pasteButton.setEnabled(copiedId != null && !copiedId.equals(id));
	}
}
