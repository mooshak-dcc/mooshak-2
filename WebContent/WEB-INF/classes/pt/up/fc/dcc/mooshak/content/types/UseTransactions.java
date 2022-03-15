package pt.up.fc.dcc.mooshak.content.types;

import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Interface implemented by types that make
 * transactions
 * 
 * @author josepaiva
 */
public interface UseTransactions {
	
	/**
	 * Returns the number of transactions of the given type
	 * made by the implementing type
	 * @return
	 * @throws MooshakException
	 */
	public int getTransactions(String type) 
			throws MooshakException;
	
	/**
	 * Resets the number of transactions of the given type
	 * @param type
	 * @throws MooshakException
	 */
	public void resetTransactions(String type) 
			throws MooshakException;
	
	/**
	 * Increases the number of transactions of the given type
	 * @param type
	 * @throws MooshakException
	 */
	public void makeTransaction(String type) 
			throws MooshakException;
}
