package pt.up.fc.dcc.mooshak.client.guis.admin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import pt.up.fc.dcc.mooshak.client.AbstractAppController;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.data.admin.DataManager;
import pt.up.fc.dcc.mooshak.client.data.admin.DataObject;
import pt.up.fc.dcc.mooshak.client.data.admin.DataObject.Processor;
import pt.up.fc.dcc.mooshak.client.data.admin.FormDataProvider;
import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.guis.admin.event.CopyObjectEvent;
import pt.up.fc.dcc.mooshak.client.guis.admin.event.CopyObjectEventHandler;
import pt.up.fc.dcc.mooshak.client.guis.admin.event.ShowObjectEvent;
import pt.up.fc.dcc.mooshak.client.guis.admin.event.ShowObjectEventHandler;
import pt.up.fc.dcc.mooshak.client.guis.admin.presenter.ObjectEditorPresenter;
import pt.up.fc.dcc.mooshak.client.guis.admin.presenter.TopLevelPresenter;
import pt.up.fc.dcc.mooshak.client.guis.admin.view.ObjectEditorView;
import pt.up.fc.dcc.mooshak.client.guis.admin.view.ObjectEditorViewImpl;
import pt.up.fc.dcc.mooshak.client.guis.admin.view.TopLevelView;
import pt.up.fc.dcc.mooshak.client.guis.admin.view.TopLevelViewImpl;
import pt.up.fc.dcc.mooshak.client.services.AdminCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.widgets.CardPanel;
import pt.up.fc.dcc.mooshak.shared.events.ListingUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.MooshakEventListener;
import pt.up.fc.dcc.mooshak.shared.events.ObjectUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.ReplayUpdate;
import pt.up.fc.dcc.mooshak.shared.results.ServerStatus;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

/**
 * App controller for Admin
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class AppController extends AbstractAppController
	implements ValueChangeHandler<String> {

	private static final int DELAY = 10 *1000; // 10 seconds
	private AdminCommandServiceAsync rpc;
	private HandlerManager eventBus;
	private HasWidgets container;
	
	private Map<String,ObjectEditorPresenter> objectEditorPresenters = 
			new HashMap<String,ObjectEditorPresenter>();
	
	private DataManager dataManager = DataManager.getInstance();
	
	private EventManager eventManager = EventManager.getInstance();
	
	private TopLevelView topLevelView = null;
	private ObjectEditorView objectView = null;
	private String currentObjectId = null;
	
	private String copiedObjectId = null;
	
	public AppController(AdminCommandServiceAsync rpcService,
			HandlerManager eventBus) {
		this.rpc = rpcService;
		this.eventBus = eventBus;
		bind();
	}

	public void go(final HasWidgets container) {
		this.container = container;
		
		History.fireCurrentHistoryState();
		
	}
	
	private void bind() {
		History.addValueChangeHandler(this);

		eventBus.addHandler(ShowObjectEvent.TYPE,
				new ShowObjectEventHandler() {
			public void onShowObject(ShowObjectEvent event) {
				History.newItem(event.getObjectId());
			}
		});  

		eventBus.addHandler(CopyObjectEvent.TYPE,
				new CopyObjectEventHandler() {
			public void onCopyObject(CopyObjectEvent event) {
				copiedObjectId = event.getObjectId();
			}
		});  
		
		eventManager.addListener(ObjectUpdateEvent.class,
				new MooshakEventListener<ObjectUpdateEvent>() {

					@Override
					public void receiveEvent(ObjectUpdateEvent event) {
						final String id = event.getId();
						
						LOGGER.log(Level.INFO,"Object updated:"+id);
						dataManager.updateObject(event.getId(),new Processor() {

							@Override
							public void process(DataObject dataObject) {
								if(topLevelView != null)
									topLevelView.updateCellBrowser(id);
								if(id != null && id.equals(currentObjectId))
									updateObjectEditor(dataObject);
							}
							
						});
					}
			});
	
		new ListenerManager<ReplayUpdate>().
		addEventListener(ReplayUpdate.class,Kind.REPLAY,"replay");
		
		new Timer() {
	            
	            @Override
	            public void run() {
	            	rpc.getServerStatus(new AsyncCallback<ServerStatus>() {
						
						@Override
						public void onSuccess(ServerStatus status) {
							if(topLevelView != null)
								topLevelView.setServerStatus(status);
							
						}
						
						@Override
						public void onFailure(Throwable caught) {
							if(objectView != null)
								objectView.setMessage("Server Info Update:"
										+caught.getMessage());
						}
					});
	            }
		 }.scheduleRepeating(DELAY);
	}

	/**
	 * Listener creator for ListingUpdateEvent.
	 * Data on the received events is forward to a ListingDataProvider 
	 * of the given kind.
	 *
	 * @param <T> a type of event extending ListingUpdateEvent
	 */
	private class ListenerManager<T extends ListingUpdateEvent> {
	
		void  addEventListener(
				final Class<T> type,
				final Kind kind,
				final String label) {
		
			final ListingDataProvider provider =
					ListingDataProvider.getDataProvider(kind);
			
			eventManager.addListener(type,
				new MooshakEventListener<T>() {

					@Override
					public void receiveEvent(T event) {
						LOGGER.log(Level.INFO,"Processing "+label+" event");
						provider.addOrChangeRow(event.getId(),event.getRecord());
					}
			
			});
		}
	}

		
		

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		
		final String objectId = event.getValue();
		
		currentObjectId  = objectId;
		
		if("".equals(objectId) || topLevelView == null) {
			makeTopLevel();
			makeObjectEditor("Top");
		} else {
			final CardPanel cardPanel = topLevelView.getContent();

			showWaitCursor();
			
			dataManager.getMooshakObject(objectId, new Processor() {

				// process this when data is available
				public void process(DataObject dataObject) {
					String type = dataObject.getData().getType();
					if(!cardPanel.hasCard(type))
						makeObjectEditor(type);
					cardPanel.showCard(type);
					
					updateObjectEditor(dataObject);					
				}

			});
			
			showDefaultCursor();
		}
	}


	private void makeTopLevel() {

		if (topLevelView == null) 
			topLevelView = new TopLevelViewImpl();
		new TopLevelPresenter(rpc, eventBus, topLevelView).go(container);
	}
	
	private void makeObjectEditor(final String type) {

		if(topLevelView != null) {
			CardPanel cardPanel = topLevelView.getContent();

			objectView = new ObjectEditorViewImpl(rpc);
			objectView.setCopiedId(copiedObjectId);

			ObjectEditorPresenter presenter = new ObjectEditorPresenter(
					rpc,
					eventBus,
					objectView,
					type);

			objectEditorPresenters.put(type,presenter);
			cardPanel.addCard(type, objectView.asWidget());
			presenter.go(cardPanel);

		}

	}
	
	/**
	 * @param objectId
	 * @param dataObject
	 * @param type
	 */
	private void updateObjectEditor(DataObject dataObject) {
	
		String objectId = dataObject.getId();
		String type = dataObject.getData().getType();
		ObjectEditorPresenter presenter = objectEditorPresenters.get(type);
		FormDataProvider provider = dataObject.getFormDataProvider();
		
		presenter.setObjectId(objectId);
		presenter.setCopiedId(copiedObjectId);
		presenter.setDataProvider(provider);
		provider.refresh();
	}
	
	
}
