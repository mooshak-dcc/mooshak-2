package pt.up.fc.dcc.mooshak.content.types;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakAttribute.YesNo;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.MooshakOperation;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.TransactionQuota;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo.ColumnType;

/**
 * A collection of printout requested by teams during a particular contest
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 * @since July 2013
 * @version 2.0
 *
 *        Recoded in Java in July 2013 
 *         From a Tcl module coded in April 2005
 */
public class Printouts extends PersistentContainer<Printout> 
	implements HasListingRows, Preparable, HasTransactions {
	private static final long serialVersionUID = 1L;
	
	private static final int HOURS_IN_MILLIS = 3600 * 1000;

	@MooshakAttribute( 
			name="Active",
			type=AttributeType.MENU,
			tip = "Accept jobs for printing in the teams interface"
			)
	private YesNo active;
	
	@MooshakAttribute(
			name="Printer",
			tip ="Printer queue name (empty for default printer)"
			)
	private String printer;
	
	@MooshakAttribute(
			name="Template",
			type=AttributeType.FILE,
			complement=".html",
			tip = "HTML file with question template")
	private Path template;
	
	@MooshakAttribute(
			name="Config",
			type=AttributeType.FILE,
			complement=".css",
			tip = "CSS file for configuring HTML template")
	private Path config;

	@MooshakAttribute( 
			name="List-Pending",
			type=AttributeType.MENU,
			tip = "Include printouts in Pending listing")
	private YesNo listPending;
	
	@MooshakAttribute(
			name="TransactionLimit",
			type=AttributeType.INTEGER,
			tip="Maximum number of printouts to each user")
	private Integer transactionLimit;

	@MooshakAttribute(
			name="TransactionLimitTime",
			type=AttributeType.DOUBLE,
			tip="Time to reset the number of printouts of each\n"
					+ " user (hours ex.: 1.5)")
	private Double transactionLimitTime;
	
	@MooshakAttribute(
			name="NextTransactionReset",
			type=AttributeType.DATE,
			tip="Represents the date of the next transaction \n"
					+ "reset")
	private Date nextTransactionReset;
	
	@MooshakAttribute( 
			name="Printout",
			type=AttributeType.CONTENT)
	private Void printout;
	
	/****************************************************************\
	 * 						Operations								*
	 ****************************************************************/
	
	@MooshakOperation(
			name="Test Printing",
			inputable=false,
			tip="Print an example page")
	private CommandOutcome testPrinting() {
		CommandOutcome outcome = new CommandOutcome();
		
		try {
			Printout test = find("test");
			if(test == null)
				test = create("test", Printout.class);
			
			test.createTestPrintout();
			
			outcome = test.print();
			
			test.delete();
			
		} catch (Exception e) {
			outcome.setMessage("Error: "+e.getLocalizedMessage());
		}
		
		return outcome;
	}
	
	@MooshakOperation(
			name="Default Config",
			inputable=false,
			tip="CSS File for configuring HTML Template")
	CommandOutcome createDefaultConfig() {
		CommandOutcome outcome = new CommandOutcome();
		
		try {
			Path config = getAbsoluteFile("Config.css");
			
			String content = "BODY {\n"
					+ "\tfont-size: 12px;\n"
					+ "}\n"
					+ "pre { font-family: \"Courier New\", Courier, monospace; white-space: pre-wrap;"
					+ "line-height:14px;margin-top:20px;}\n.pagebreak { page-break-before: always; }";
			
			executeIgnoringFSNotifications( 
					() -> Files.write(config, content.getBytes())
			);
			
			setConfig(config);
			
			save();
			
			outcome.setMessage("Config created");
		} catch (IOException | MooshakContentException e) {
			outcome.setMessage("Error:"+e.getLocalizedMessage());
		}
		
		return outcome;
	}
	
	@MooshakOperation(
			name="Default Template",
			inputable=false,
			tip="HTML file with printout template")
	CommandOutcome createDefaultTemplate() {
		CommandOutcome outcome = new CommandOutcome();
		
		try {
			Path template = getAbsoluteFile("Template.html");
			
			String content = "<table width=\"100%\" border=\"1\" "
					+ "cellspacing=\"10\" cellpading=\"10\">"
					+ "\n<tr><th valign=\"right\">Problem</th>"
					+ "<td>$Problem</td></tr>"
					+ "\n<tr><th valign=\"right\">Team</th>"
					+ "<td>$Team</td></tr>"
					+ "\n<tr><th valign=\"right\">Name</th>"
					+ "<td>$Name</td></tr>"
					+ "\n<tr><th valign=\"right\">Location</th>"
					+ "<td>$Location</td></tr>"
					+ "\n<tr><th valign=\"right\">Program</th>"
					+ "<td>$Program</td></tr>"
					+ "\n<tr><th valign=\"right\">AbsoluteTime</th>"
					+ "<td>$AbsoluteTime</td></tr>"
					+ "\n<tr><th valign=\"right\">ContestTime</th>"
					+ "<td>$ContestTime</td></tr>"
					+ "\n</table>"
					+ "\n<div class=\"pagebreak\"></div>"
					+ "\n\n<pre>"
					+ "\n$Content"
					+ "\n</pre>";
			
			executeIgnoringFSNotifications( 
					() -> Files.write(template, content.getBytes())
			);
			
			setTemplate(template);
			
			save();
			
			outcome.setMessage("Template created");
		} catch (IOException | MooshakContentException e) {
			outcome.setMessage("Error:"+e.getLocalizedMessage());
		}
		
		return outcome;
	}
	
	/**
	 * Is printing active?
	 * @return
	 */
	public boolean isActive() {
		if(YesNo.YES.equals(active))
			return true;
		else
			return false;
	}
	
	/**
	* Set Printing as active
	*/
	public void setActive(boolean active) {
		if(active)
			this.active = YesNo.YES;
		else
			this.active = YesNo.NO;
	}
	
	/**
	 * Get printer queue name (empty for default printer)
	 * @return
	 */
	public String getPrinter() {
		if(printer == null) 
			return "";
		else
			return printer;
	}
	
	/**
	 * Set printer queue name (empty or null for default printer)
	 * @param printer
	 */
	public void setPrinter(String printer) {
		this.printer = printer;
	}
	
	/**
	 * Get HTML file with question template
	 * @return
	 */
	public Path getTemplate() {
		if(template == null)
			return null;
		else
			return getPath().resolve(template);
	}
	
	/**
	 * Set HTML file with question template
	 * @return
	 */
	public void setTemplate(Path template) {
		this.template = template.getFileName();
	}

	/**
	 * Get CSS file for configuring HTML template
	 * @return
	 */
	public Path getConfig() {
		if(config==null)
			return config;
		else
			return getPath().resolve(config);
	}
	
	/**
	 * Set CSS file for configuring HTML template
	 * @return
	 */
	public void setConfig(Path config) {
		this.config = config.toAbsolutePath();
	}
	
	/**
	 * Are question entries included pending listing? 
	 * @return
	 */
	public boolean isListPending() {
		if(YesNo.YES.equals(listPending))
			return true;
		else
			return false;
	}

	/**
	 * Define if printouts entries are included pending listing
	 * @param listPending
	 */
	public void setListPending(boolean listPending) {
		if(listPending)
			this.listPending = YesNo.YES;
		else
			this.listPending = YesNo.NO;
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
		
		List<ColumnInfo> columns = new ArrayList<>();
		
		columns.add(new ColumnInfo("time", 20, ColumnType.TIME));
		columns.addAll(ColumnInfo.addColumns("id"));
		columns.add(new ColumnInfo("team", 20, ColumnType.TEAM));
		columns.addAll(ColumnInfo.addColumns("group"));
		columns.add(new ColumnInfo("problem", 20, ColumnType.PROBLEM));
		columns.addAll(ColumnInfo.addColumns("state"));
		
		return columns;
	}

	@Override
	public void prepare() throws MooshakException {
		
		try(POStream<Printout> stream = newPOStream()) {
			for(Printout printout: stream)
				printout.delete();
		} catch(Exception cause) {
			throw new MooshakException("Error iterating over submissions",cause);
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
		
		quota.setTransactionsUsed(user.getTransactions("Printouts"));
		
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
		user.resetTransactions("Printouts");
	}

	@Override
	public void resetAllTransactions() throws MooshakException {
		Groups groups = getParent().open("groups");
		
		try(POStream<Team> stream = groups.newPOStream()) {
			for (Team team : stream) {
				resetTransactions(team);
			}
		} catch(Exception cause) {
			throw new MooshakException("Error iterating over submissions",cause);
		}
	}

	@Override
	public void makeTransaction(UseTransactions user) throws MooshakException {
		
		TransactionQuota quota = getTransactionQuota(user);
		
		if(quota == null)
			return;
		
		if(quota.getTransactionsUsed() < quota.getTransactionsLimit()) {
			user.makeTransaction("printouts");
			return;
		}
		
		throw new MooshakException("Transaction limit exceeded for this team");
			
	}	

	
}
