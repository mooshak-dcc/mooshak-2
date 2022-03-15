package pt.up.fc.dcc.mooshak.client.guis.judge.presenter;

import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.client.Presenter;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.data.admin.DataManager;
import pt.up.fc.dcc.mooshak.client.data.admin.DataManager.Notifier;
import pt.up.fc.dcc.mooshak.client.data.admin.DataObject;
import pt.up.fc.dcc.mooshak.client.data.admin.FormDataProvider;
import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.client.guis.judge.view.RegisterDeliveryView;
import pt.up.fc.dcc.mooshak.client.services.JudgeCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class RegisterDeliveryPresenter implements Presenter, RegisterDeliveryView.Presenter {

	private static final Logger LOGGER = Logger.getLogger("");

	private RegisterDeliveryView view;

	private DataManager dataManager = DataManager.getInstance();

	private JudgeCommandServiceAsync rpcService;
	private String id;
	private Kind kind;

	public RegisterDeliveryPresenter(JudgeCommandServiceAsync rpcService,
			HandlerManager eventBus, RegisterDeliveryView view, String id, 
			Kind kind) {
		
		this.rpcService = rpcService;
		this.id = id;
		this.kind = kind;
		
		this.view = view;

		this.view.setPresenter(this);
	}

	@Override
	public void go(HasWidgets container) {
	}

	void processfailure(Throwable caught) {
		Window.alert(caught.getMessage());
		AuthenticationPresenter.logout(caught);
	}

	public void setObjectId(String objectId) {
		view.setObjectId(objectId);
	}

	public void setDataProvider(FormDataProvider dataProvider) {
		view.setFormDataProvider(dataProvider);
	}
	
	@Override
	public void onChange(String objectId, MooshakValue value) {		
		DataObject dataObject = dataManager.getMooshakObject(objectId);

		dataObject.getData().setFieldValue(value.getField(), value);

		dataManager.setMooshakObject(objectId, new Notifier() {
			
			@Override
			public void notify(String message) {
				
				view.setMessage(message);
			}
		});
		
		LOGGER.info("!! Saving data on "+objectId);
		broadcastChange();
		view.refreshProviders();
	}

	protected void broadcastChange() {
		String type = kind.toString().toLowerCase();
		rpcService.broadcastRowChange(type, id, 
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						LOGGER.severe("Could not broadcast change: " 
								+ caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(Void result) {
						EventManager.getInstance().refresh();
						view.refreshProviders();
					}
		});
	}

}
