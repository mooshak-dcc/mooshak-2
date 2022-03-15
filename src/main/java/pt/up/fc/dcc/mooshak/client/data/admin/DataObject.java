package pt.up.fc.dcc.mooshak.client.data.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;

import com.google.gwt.view.client.ListDataProvider;

/**
 * A DataObject reflects on client-side a server-side PersistentObject
 * transported as a MooshakObject shared type.
 * <p>
 * A DataObject is also a supplier of DataProviders for browsing
 * the object tree and  editing object fields.
 * 
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class DataObject {
	String id = null;
	MooshakObject data = null;
	List<Processor> pending = new ArrayList<Processor>();
	ListDataProvider<String> childrenProvider;
	MapFormDataProvider formDataProvider;
	
	/**
	 * Functional interface for processors of data objects.
	 * They are invoked once data objects are available
	 */
	public interface Processor {
		void process(DataObject dataObject);
	}
	
	DataObject(String id) {
		this.id=id;
		formDataProvider = new MapFormDataProvider();
		childrenProvider = new ListDataProvider<String>();
	}

	/**
	 * Get this DataObjects's id, a pathname
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set this DataObjects's id, a pathname
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get MooshakObject backing this DataObject
	 * @return the data
	 */
	public MooshakObject getData() {
		return data;
	}

	/**
	 * Set MooshakObject backing this DataObject
	 * This change is propagated to DataProviders created for this object
	 * @param data the data to set
	 */
	public void setData(MooshakObject data) {
		this.data = data;
		
		List<String> children = data.getChildren();
		Collections.sort(children);
		
		childrenProvider.setList(children);
		// shouldn't be needed 
		// and actually refresh has to be forced on CellBrowser 
		childrenProvider.refresh();		
		
		formDataProvider.setFieldValues(data.getValues());
		
		for(Processor processor: pending)
			processor.process(this);
		
		pending.clear();
	}

	/**
	 * Get a ListDataProvider bound to this object data
	 * @return the childrenProvider
	 */
	public ListDataProvider<String> getChildrenProvider() {
		return childrenProvider;
	}


	/**
	 * Get a FormDataProvider bound to this object data
	 * @return
	 */
	public FormDataProvider getFormDataProvider() {
		
		return formDataProvider;
	}		
	
	/**
	 * Add processor to pending list to activate when data is available
	 * @param processor
	 */
	public void addPending(Processor processor) {
		pending.add(processor);
	}

	/**
	 * Execute processor if data is available or add it to pending list 
	 * @param processor
	 */
	public void processWhenDataAvailable(Processor processor) {
		if(processor != null) 
			if(getData() == null)
				addPending(processor);
			else
				processor.process(this);
	}
}


