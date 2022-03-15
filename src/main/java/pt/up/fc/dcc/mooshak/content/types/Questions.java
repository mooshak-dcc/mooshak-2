package pt.up.fc.dcc.mooshak.content.types;

import java.util.Date;
import java.util.List;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakAttribute.YesNo;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.commands.TransactionQuota;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo.ColumnType;

/**
 * Collection of questions asked by team during a particular contest
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 * @version 2.0
 * @since July 2013
 */
public class Questions extends PersistentContainer<Question> 
	implements HasListingRows, Preparable, HasTransactions {
	private static final long serialVersionUID = 1L;
	
	private static final int HOURS_IN_MILLIS = 3600 * 1000;
	
	@MooshakAttribute( 
			name="Active",
			type=AttributeType.MENU,
			tip = "Accept questions in the teams interface"
			)
	private YesNo active;
	
	@MooshakAttribute( 
			name="Forward",
			type=AttributeType.MENU,
			tip = "Forward questions to the contest's email address"
			)
	private YesNo forward;
	
	@MooshakAttribute(
			name="TransactionLimit",
			type=AttributeType.INTEGER,
			tip="Maximum number of questions to each user")
	private Integer transactionLimit;

	@MooshakAttribute(
			name="TransactionLimitTime",
			type=AttributeType.DOUBLE,
			tip="Time to reset the number of questions of each\n"
					+ " user (hours ex.: 1.5)")
	private Double transactionLimitTime;
	
	@MooshakAttribute(
			name="NextTransactionReset",
			type=AttributeType.DATE,
			tip="Represents the date of the next transaction \n"
					+ "reset")
	private Date nextTransactionReset;
	
	@MooshakAttribute( 
			name="Question",
			type=AttributeType.CONTENT)
	private Void question;
	
	/**
	 * Is questioning active?
	 * @return
	 */
	public boolean isActive() {
		if(YesNo.YES.equals(active))
			return true;
		else
			return false;
	}
	
	/**
	* Set questioning as active
	*/
	public void setActive(boolean active) {
		if(active)
			this.active = YesNo.YES;
		else
			this.active = YesNo.NO;
	}
	

	/**
	 * Is forwarding active?
	 * @return
	 */
	public boolean isForward() {
		if(YesNo.YES.equals(forward))
			return true;
		else
			return false;
	}
	
	/**
	* Set forwarding as active
	*/
	public void setForward(boolean forward) {
		if(forward)
			this.forward = YesNo.YES;
		else
			this.forward = YesNo.NO;
	}
	
	/**
	 * @return the transactionLimit
	 */
	public Integer getTransactionLimit() {
		return transactionLimit;
	}

	/**
	 * @param transactionLimit the transactionLimit to set
	 */
	public void setTransactionLimit(Integer transactionLimit) {
		this.transactionLimit = transactionLimit;
	}

	/**
	 * @return the transactionLimitTime
	 */
	public Double getTransactionLimitTime() {
		return transactionLimitTime;
	}

	/**
	 * @param transactionLimitTime the transactionLimitTime to set
	 */
	public void setTransactionLimitTime(Double transactionLimitTime) {
		this.transactionLimitTime = transactionLimitTime;
	}
	
	/*---------------------------------------------------------------------*\
	 *                   Listing support                                   *
	 *                                                                     *
	 *                                                                     *
	 * (non-Javadoc)                                                       *
	 * @see pt.up.fc.dcc.mooshak.content.types.HasListingRows#getRows()    *
	 * @see pt.up.fc.dcc.mooshak.content.types.HasListingRows#getColumns() *
	\*---------------------------------------------------------------------*/
	
	@Override
	public POStream<? extends HasListingRow> getRows() {
		
		return  newPOStream();
	}
	
	@Override
	public List<ColumnInfo> getColumns() {

		List<ColumnInfo> columns = ColumnInfo.addColumns();
				
		columns.add(new ColumnInfo("time", 10, ColumnType.TIME));
		ColumnInfo.addColumns(columns,10,"id","team","group","problem");
		ColumnInfo.addColumns(columns,40,"subject");
		ColumnInfo.addColumns(columns,20,"state");
		
		return columns;
	}

	@Override
	public void prepare() throws MooshakException {
		
		try(POStream<Question> stream = newPOStream()) {
			for(Question question: stream)
				question.delete();
		} catch (Exception cause) {
			throw new MooshakException("Error iterating over questions",cause);
		} 
		
	}	

	@Override
	public TransactionQuota getTransactionQuota(UseTransactions user)
			throws MooshakException {
		
		TransactionQuota quota = new TransactionQuota();
		
		Double transactionLimitTime = this.transactionLimitTime; 
		
		if(transactionLimit == null) {
			Contest contest = getParent();
			
			if(contest.getTransactionLimit() == null)
				return null;
			
			if(transactionLimitTime == null)
				transactionLimitTime = contest.getTransactionLimitTime();
			
			quota.setTransactionsLimit(contest.getTransactionLimit());
		}
		else {
			quota.setTransactionsLimit(getTransactionLimit());
		}
		
		quota.setTransactionsUsed(user.getTransactions("Questions"));
		
		if(transactionLimitTime == null)
			return quota;
		
		try {
			Date now = new Date();
			
			synchronized (this) {
				if(nextTransactionReset == null)
					nextTransactionReset = new Date(0);
				
				long time = nextTransactionReset.getTime() - now.getTime();
				if(time < 0) {
					resetAllTransactions();
					quota.setTransactionsUsed(0);
					
					long r = (long) (time % (transactionLimitTime 
							* HOURS_IN_MILLIS));
					
					Date nextReset = new Date((long) (now.getTime() + 
							(transactionLimitTime *	HOURS_IN_MILLIS + r)));
					
					nextTransactionReset = nextReset;
					time = nextReset.getTime() - now.getTime();
					save();
				}
			}
			
			
			quota.setTimeToReset(nextTransactionReset.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return quota;
	}

	@Override
	public void resetTransactions(UseTransactions user)
			throws MooshakException {
		user.resetTransactions("Questions");
	}

	@Override
	public void resetAllTransactions() throws MooshakException {
		Groups groups = getParent().open("groups");
		
		try(POStream<Team> stream = groups.newPOStream()) {
			for (Team team : stream) 
				resetTransactions(team);
			
		} catch (Exception cause) {
			throw new MooshakException("Error iterating over teams",cause);
		}
	}

	@Override
	public void makeTransaction(UseTransactions user) throws MooshakException {
		
		TransactionQuota quota = getTransactionQuota(user);
		
		if(quota == null)
			return;
		
		if(quota.getTransactionsUsed() < quota.getTransactionsLimit()) {
			user.makeTransaction("questions");
			return;
		}
		
		throw new MooshakException("Transaction limit exceeded for this team");
			
	}	

}
