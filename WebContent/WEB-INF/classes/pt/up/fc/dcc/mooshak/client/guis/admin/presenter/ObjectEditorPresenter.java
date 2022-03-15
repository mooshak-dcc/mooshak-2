package pt.up.fc.dcc.mooshak.client.guis.admin.presenter;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.client.AbstractAppController;
import pt.up.fc.dcc.mooshak.client.Presenter;
import pt.up.fc.dcc.mooshak.client.data.admin.DataManager;
import pt.up.fc.dcc.mooshak.client.data.admin.DataManager.Notifier;
import pt.up.fc.dcc.mooshak.client.data.admin.DataObject;
import pt.up.fc.dcc.mooshak.client.data.admin.FormDataProvider;
import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.guis.admin.event.CopyObjectEvent;
import pt.up.fc.dcc.mooshak.client.guis.admin.view.ObjectEditorView;
import pt.up.fc.dcc.mooshak.client.services.AdminCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.client.utils.FileDownloader;
import pt.up.fc.dcc.mooshak.client.utils.Filenames;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType.MooshakMethod;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

/**
 * 
 * Object editor presenter of admin's app
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class ObjectEditorPresenter implements Presenter, 
		ObjectEditorView.Presenter {

	private static Logger LOGGER = Logger.getLogger("");
	
	private EventManager eventmanager = EventManager.getInstance();
	
	private AdminCommandServiceAsync rpcService;
	private HandlerManager eventBus;
	private ObjectEditorView view;
	private String type; 

	private DataManager dataManager = DataManager.getInstance();
	
	public ObjectEditorPresenter(
			  AdminCommandServiceAsync rpcService, 
		      HandlerManager eventBus, 
		      ObjectEditorView view,
		      String type
			 ) {
		    this.rpcService = rpcService;
		    this.eventBus = eventBus;
		    this.view = view;
		    this.type = type;
		    
		    this.view.setPresenter(this);
	}
	
	
	@Override
	public void go(final HasWidgets container) {
		// container.add(view.asWidget());// added in CardPanel by AppController
		setDependentData();
	}
	
	
	private void setDependentData() {
		rpcService.getMooshakType(type, new AsyncCallback<MooshakType>() {
			
			@Override
			public void onSuccess(MooshakType result) {
				view.setObjectType(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				LOGGER.log(Level.SEVERE,"Getting type "+type,caught);
			}
		});
		
	}

	
	public void setObjectId(String objectId) {
		view.setObjectId(objectId);
		isFrozen(objectId);
	}
	
	
	public void setDataProvider(FormDataProvider dataProvider) {
		view.setObjectDataProvider(dataProvider);
	}
	
	@Override
	public void setCopiedId(String copiedId) {
		view.setCopiedId(copiedId);
	}


	@Override
	public void canUndo(String id) {
		
		rpcService.canRecover(id, false, 
				new AsyncCallback<Boolean>() {
			
			@Override
			public void onSuccess(Boolean result) {
				view.setUndoEnabled(result.booleanValue());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				view.setUndoEnabled(false);
				view.setMessage(caught.getLocalizedMessage());
			}
		});
	}


	@Override
	public void canRedo(String id) {
		
		rpcService.canRecover(id, true, 
				new AsyncCallback<Boolean>() {
			
			@Override
			public void onSuccess(Boolean result) {
				view.setRedoEnabled(result.booleanValue());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				view.setRedoEnabled(false);
				view.setMessage(caught.getLocalizedMessage());
			}
		});
	}
	
	@Override
	public void onChange(final String objectId, MooshakValue value) {		
		DataObject dataObject = dataManager.getMooshakObject(objectId);

		dataObject.getData().setFieldValue(value.getField(), value);
		
		dataManager.setMooshakObject(objectId, new Notifier() {
			
			@Override
			public void notify(String message) {
				canUndo(objectId);
				canRedo(objectId);
				isFrozen(objectId);
				
				view.setMessage(message);
			}
		});
	}

	@Override
	public void onExecute(String id, 
			final MooshakMethod method,
			MethodContext context) {
		isFrozen(id);
		if (view.getFrozen()) {
			view.setMessage("Object frozen");
			return;
		}
		
		LOGGER.log(Level.INFO,"Executing operation "+method.getName()+
				" on "+id+" with context "+context);

		final Map<Style, Cursor> cursors = AbstractAppController.setCursorsToWait();
		
		rpcService.execute(id,method, context,
				new AsyncCallback<CommandOutcome>() {
			
			@Override
			public void onSuccess(CommandOutcome result) {
				
				LOGGER.log(Level.INFO,"Got result:"+result);
				
				view.showMethodResult(result);
				
				if(method.isUpdateEvents())
					eventmanager.refresh();
				
				AbstractAppController.resetCursors(cursors);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
				view.setMessage(caught.getLocalizedMessage());
				
				AbstractAppController.resetCursors(cursors);
			}
		});	
	}
	

	@Override
	public void onCreate(String id, final String name) {
		/*isFrozen(id);
		if (frozen) {
			view.setMessage("Object frozen");
			return;
		}
		*/
		rpcService.createMooshakObject(id, name, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setMessage(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Void result) {
				view.setMessage(name+" created");
				eventmanager.refresh();
			}
		});
	}
	

	@Override
	public void onDestroy(final String id) {

		rpcService.destroyMooshakObject(id, 
				new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setMessage(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Void result) {
				view.setMessage(id+" destroyed");
				eventmanager.refresh();
			}
		});
	}
	

	@Override
	public void onRename(final String id, final String name) {

		rpcService.renameMooshakObject(id, name,
				new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setMessage(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Void result) {
				view.setMessage(id+" renamed to " + name);
				eventmanager.refresh();
			}
		});
	}
	
	
	@Override
	public void onImport(final String id, final String name, 
			final byte[] content) {
		
		rpcService.importMooshakObject(id, name, content,
				new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setMessage(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Void result) {
				view.setMessage(id+" child imported");
				eventmanager.refresh();
			}
		});
	}
	
	
	@Override
	public void onExport(final String id) {
		
		rpcService.exportMooshakObject(id,
				new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setMessage(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(String result) {
				String archive = Filenames.extractLastPathname(id)
						.replaceAll("[^A-Za-z0-9_-]", "-") + ".zip";
				FileDownloader.downloadBinaryFile(archive, 
						Base64Coder.decode(result), "application/zip");
				view.setMessage(id+" exported");				
			}
		});
	}


	@Override
	public void onUndo(final String id) {

		isFrozen(id);
		if (view.getFrozen()) {
			view.setMessage("Object frozen");
			return;
		}
		
		rpcService.recover(id, false, 
				new AsyncCallback<MooshakObject>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setMessage(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(MooshakObject result) {
				view.setMessage(id+" recovered");
				canUndo(id);
				canRedo(id);
				eventmanager.refresh();
			}
		});
	}


	@Override
	public void onRedo(final String id) {

		isFrozen(id);
		if (view.getFrozen()) {
			view.setMessage("Object frozen");
			return;
		}
		
		rpcService.recover(id, true, 
				new AsyncCallback<MooshakObject>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setMessage(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(MooshakObject result) {
				view.setMessage(id+" recovered");
				canUndo(id);
				canRedo(id);
				eventmanager.refresh();
			}
		});
	}


	@Override
	public void onFreeze(final String id) {
		rpcService.freeze(id, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setMessage(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Void result) {
				isFrozen(id);
				view.setMessage("Frozen");
			}
		});
	}


	@Override
	public void onUnfreeze(final String id) {
		rpcService.unfreeze(id, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setMessage(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Void result) {
				isFrozen(id);
				view.setMessage("Unfrozen");
			}
		});
	}
	
	@Override
	public void isFrozen(final String id) {
		rpcService.isFrozen(id, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setFreezeEnabled(false);
				view.setUnfreezeEnabled(false);
				view.setMessage(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Boolean result) {
				view.setFrozenObjectType(result.booleanValue());
				canRedo(id);
				canUndo(id);
			}
		});
	}
	
	@Override
	public void onFind(String term) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onCopy(String id) {
		eventBus.fireEvent(new CopyObjectEvent(id));
	}


	@Override
	public void onPaste(String id, String copiedId) {
		rpcService.pasteMooshakObject(id, copiedId, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setMessage(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Void arg0) {
				view.setMessage("Pasted");
				eventmanager.refresh();
			}
		});
	}


	@Override
	public void isRenameable(String id) {
		rpcService.isRenameable(id, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setMessage(caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Boolean isRenameable) {
				view.setRenameableEnabled(isRenameable);
			}
		});
	}

}
