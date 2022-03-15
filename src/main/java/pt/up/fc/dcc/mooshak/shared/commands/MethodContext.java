package pt.up.fc.dcc.mooshak.shared.commands;

import java.util.ArrayList;
import java.util.List;

import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome.Pair;
import pt.up.fc.dcc.mooshak.shared.events.Recipient;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A method context is a name/value list with possible repetitions of names
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class MethodContext implements IsSerializable {
	private List<Pair> pairs = new ArrayList<Pair>();
	private Recipient recipient;
	
	public MethodContext() {
		// TODO Auto-generated constructor stub
	}
	
	public void addPair(String name, String value) {
		pairs.add(new Pair(name,value));
	}
	
	/**
	 * Get a list with declared names. use {@code getValue()} to get associated 
	 * value
	 * @return lis of names
	 */
	public List<String> getNames() {
		List<String> names = new ArrayList<>();
		
		for(Pair pair: pairs)
			names.add(pair.name);
		
		return names;
	}
	
	/**
	 * Get a value associated with name, or {@code null} if none exists 
	 * or name is  {@code null}
	 * @param name a non-null string
	 * @return value oh name
	 */
	public String getValue(String name) {
		if(name == null)
			return null;
		
		for(Pair pair: pairs)
			if(name.equals(pair.name))
				return pair.value;
		return null;
	}
	

	
	
	/**
	 * Get a list of values assigned to name
	 * @param name
	 * @return list of values
	 */
	public List<String> getValues(String name) {
		List<String> values = new ArrayList<String>();
		
		for(Pair pair: pairs)
			if(name.equals(pair.name))
				values.add(pair.value);
		return values;
	}
	
	

	/**
	 * Get recipient (who gets the events) of this method context
	 * @return the recipient
	 */
	public Recipient getRecipient() {
		return recipient;
	}

	/**
	 * Set recipient (who gets the events) of this method context
	 * @param recipient the recipient to set
	 */
	public void setRecipient(Recipient recipient) {
		this.recipient = recipient;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MethodContext [");
		builder.append("recipient=");
		builder.append(recipient);
		for(Pair pair: pairs) {
			builder.append(",");
			builder.append(pair.name);
			builder.append("=");
			builder.append(pair.value);
		}
		
		builder.append("]");
		return builder.toString();
	}

	
}