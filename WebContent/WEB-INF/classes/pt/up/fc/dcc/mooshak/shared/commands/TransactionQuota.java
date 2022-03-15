package pt.up.fc.dcc.mooshak.shared.commands;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Represents a transaction limit quota
 * 
 * @author josepaiva
 */
public class TransactionQuota implements IsSerializable {

	private int transactionsLimit;
	private int transactionsUsed;
	
	private long timeToReset;
	
	public TransactionQuota() {
		timeToReset = -1;
		transactionsUsed = 0;
		transactionsLimit = -1;
	}

	/**
	 * @param transactionsLimit
	 * @param transactionsUsed
	 * @param timeToReset
	 */
	public TransactionQuota(int transactionsLimit, int transactionsUsed,
			long timeToReset) {
		super();
		this.transactionsLimit = transactionsLimit;
		this.transactionsUsed = transactionsUsed;
		this.timeToReset = timeToReset;
	}

	/**
	 * @return the transactionsLimit
	 */
	public int getTransactionsLimit() {
		return transactionsLimit;
	}

	/**
	 * @param transactionsLimit the transactionsLimit to set
	 */
	public void setTransactionsLimit(int transactionsLimit) {
		this.transactionsLimit = transactionsLimit;
	}

	/**
	 * @return the transactionsUsed
	 */
	public int getTransactionsUsed() {
		return transactionsUsed;
	}

	/**
	 * @param transactionsUsed the transactionsUsed to set
	 */
	public void setTransactionsUsed(int transactionsUsed) {
		this.transactionsUsed = transactionsUsed;
	}

	/**
	 * @return the timeToReset
	 */
	public long getTimeToReset() {
		return timeToReset;
	}

	/**
	 * @param timeToReset the timeToReset to set
	 */
	public void setTimeToReset(long timeToReset) {
		this.timeToReset = timeToReset;
	}
	
	

}
