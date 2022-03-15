package pt.up.fc.dcc.mooshak.client.data.admin;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.client.data.admin.DataObject.Processor;
import pt.up.fc.dcc.mooshak.client.services.AdminCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.ListDataProvider;

/**
 * DataManager is a singleton containing DataObjects that reflect
 * PresistentObjects on server-side. If not already on cache,
 * DataObjects are fetched from the server using RPC calls.
 * DataProviders for the requested objects are immediately supplied
 * and update then when data is later received from the server.
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 */
public class DataManager extends LinkedHashMap<String, DataObject> {
	private static final long serialVersionUID = 1L;
	
	private static final int INITIAL_CAPACITY = 1000;
	private static final float LOAD_FACTOR = 0.75F;
	private static final boolean ACCESS_ORDER = true;
	private static int MAX_ENTRIES = 5*INITIAL_CAPACITY;
	
	
	private static DataManager dataManager = new DataManager();
	private static AdminCommandServiceAsync rpc;
	
	public static DataManager getInstance() {
		return dataManager;
	}
	
	/**
	 * Get the Administration RPC service
	 * @return the rpc
	 */
	public static AdminCommandServiceAsync getRpc() {
		return rpc;
	}

	/**
	 * Set the Administration RPC service 
	 * @param rpc the rpc to set
	 */
	public static void setRpc(AdminCommandServiceAsync rpc) {
		DataManager.rpc = rpc;
	}



	Logger logger = Logger.getLogger("");
	
	private DataManager() {
		super(INITIAL_CAPACITY, LOAD_FACTOR, ACCESS_ORDER);
	}
	
	protected boolean removeEldestEntry(
			Map.Entry<String,DataObject> eldest) {
		
		if(size() > MAX_ENTRIES) {
			return true;
		} else {
			return false;
		}
    }	
	
	/**
	 * Get a DataObject bound to given ID.
	 * This is the same as {@code getMooshakObject(id, null);}
	 * @param id of DataObject
	 * @return
	 */
	public DataObject getMooshakObject(String id) {
		return getMooshakObject(id, null);
	}
	
	/**
	 * Get a DataObject bound to given ID
	 * Process given method when data is available
	 * @param id of DataObject
	 * @return
	 */
	public DataObject getMooshakObject(String id,Processor processor) {
		DataObject dataObject;
		
		if(containsKey(id)) 
			dataObject = get(id);
		else 
			dataObject = freshDataObject(id);
			
		dataObject.processWhenDataAvailable(processor);
				
		return dataObject;
	}
	
	/**
	 * If this object is being used then update it 
	 * @param id
	 */
	public void updateObject(String id,Processor processor) {
		
		if(containsKey(id)) {
			DataObject dataObject = freshDataObject(id);
		
			dataObject.processWhenDataAvailable(processor);
		}
	}

	/**
	 * Get a fresh copy from the server of the data object with given id 
	 * @param id
	 * @return
	 */
	private DataObject freshDataObject(String id) {
		final DataObject dataObject = new DataObject(id);
		
		put(id,dataObject);
		
		rpc.getMooshakObject(id, new AsyncCallback<MooshakObject>() {
			
			@Override
			public void onSuccess(MooshakObject result) {
				dataObject.setData(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.SEVERE,"Retriving MooshakObject",caught);
			}
		});
		
		return dataObject;
	}
	
	
	/**
	 * Functional interface for notifying other objects
	 * Notifications occur after saving objects on server
	 */
	public interface Notifier {
		public void notify(String message);
	}
	
	/**
	 * Set mooshak object on the server
	 * @param id		of object
	 * @param notifier	to report the result of the operation.
	 */
	public void setMooshakObject(String id,final Notifier notifier) {
		
		if(containsKey(id)) {
			rpc.setMooshakObject(get(id).getData(), new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					logger.log(Level.SEVERE,"Sending MooshakObject",caught);
					notifier.notify("error:"+caught.getMessage());
				}

				@Override
				public void onSuccess(Void result) {
					notifier.notify("saved!");
				}
				
			});
		}
	}
	
	
	/**
	 * Get children of object with given ID as a DataProvider 
	 * @param value
	 * @return
	 */
	public ListDataProvider<String> getChildrenDataProvider(String value) {
		
		return getMooshakObject(value).getChildrenProvider();
		
	}
	
	/**
	 * Get object data as a FormDataProvider
	 * @param value
	 * @return
	 */
	public FormDataProvider getFormDataProvider(String value) {
		return getMooshakObject(value).getFormDataProvider();
	}
	
}
