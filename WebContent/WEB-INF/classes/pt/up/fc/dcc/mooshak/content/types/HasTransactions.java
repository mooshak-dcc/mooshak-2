package pt.up.fc.dcc.mooshak.content.types;

import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.TransactionQuota;

/**
 * Represents a type that has transactions
 * 
 * @author josepaiva
 */
public interface HasTransactions {
	
	/**
	 * Gets the transaction quota of a given user
	 * @param user
	 * @return
	 * @throws MooshakException
	 */
	public TransactionQuota getTransactionQuota(UseTransactions user)
			throws MooshakException;
	
	/**
	 * Resets transactions of a given user
	 * @param user
	 * @throws MooshakException
	 */
	public void resetTransactions(UseTransactions user) 
			throws MooshakException;
	
	/**
	 * Resets all users transactions of this type
	 * @throws MooshakException
	 */
	public void resetAllTransactions() 
			throws MooshakException;
	
	/**
	 * Makes a transaction of the given type
	 * @param user
	 * @throws MooshakException
	 */
	public void makeTransaction(UseTransactions user)
			throws MooshakException;
	
}
