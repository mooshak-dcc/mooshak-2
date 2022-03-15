package pt.up.fc.dcc.mooshak.rest.contest.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude;

import pt.up.fc.dcc.mooshak.rest.model.PoModel;

/**
 * Model for a contest
 * 
 * @author José Carlos Paiva <josepaiva94@gmail.com>
 */
@XmlRootElement(name = "contest")
@XmlType(name = "contest")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContestModel extends PoModel {
	
	private String contestType;
	private String contestStatus;
	
	private String designation;
	private String organizes;
	private String email;
	
	private String open;
	private String start;
	private String stop;
	private String close;
	
	private Integer hideListings;
	
	private String policy;
	private String virtual;
	private String register;
	private Integer transactionLimit;
	private Double transactionLimitTime;
	private String service;
	private String notes;
	
	private String fatal;
	private String warning;

	public ContestModel() {
	}

	/**
	 * @return the contestType
	 */
	public String getContestType() {
		return contestType;
	}

	/**
	 * @param contestType the contestType to set
	 */
	public void setContestType(String contestType) {
		this.contestType = contestType;
	}

	/**
	 * @return the contestStatus
	 */
	public String getContestStatus() {
		return contestStatus;
	}

	/**
	 * @param contestStatus the contestStatus to set
	 */
	public void setContestStatus(String contestStatus) {
		this.contestStatus = contestStatus;
	}

	/**
	 * @return the designation
	 */
	public String getDesignation() {
		return designation;
	}

	/**
	 * @param designation the designation to set
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
	}

	/**
	 * @return the organizes
	 */
	public String getOrganizes() {
		return organizes;
	}

	/**
	 * @param organizes the organizes to set
	 */
	public void setOrganizes(String organizes) {
		this.organizes = organizes;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the open
	 */
	public String getOpen() {
		return open;
	}

	/**
	 * @param open the open to set
	 */
	public void setOpen(String open) {
		this.open = open;
	}

	/**
	 * @return the start
	 */
	public String getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(String start) {
		this.start = start;
	}

	/**
	 * @return the stop
	 */
	public String getStop() {
		return stop;
	}

	/**
	 * @param stop the stop to set
	 */
	public void setStop(String stop) {
		this.stop = stop;
	}

	/**
	 * @return the close
	 */
	public String getClose() {
		return close;
	}

	/**
	 * @param close the close to set
	 */
	public void setClose(String close) {
		this.close = close;
	}

	/**
	 * @return the hideListings
	 */
	public Integer getHideListings() {
		return hideListings;
	}

	/**
	 * @param hideListings the hideListings to set
	 */
	public void setHideListings(Integer hideListings) {
		this.hideListings = hideListings;
	}

	/**
	 * @return the policy
	 */
	public String getPolicy() {
		return policy;
	}

	/**
	 * @param policy the policy to set
	 */
	public void setPolicy(String policy) {
		this.policy = policy;
	}

	/**
	 * @return the virtual
	 */
	public String getVirtual() {
		return virtual;
	}

	/**
	 * @param virtual the virtual to set
	 */
	public void setVirtual(String virtual) {
		this.virtual = virtual;
	}

	/**
	 * @return the register
	 */
	public String getRegister() {
		return register;
	}

	/**
	 * @param register the register to set
	 */
	public void setRegister(String register) {
		this.register = register;
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

	/**
	 * @return the service
	 */
	public String getService() {
		return service;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(String service) {
		this.service = service;
	}

	/**
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * @param notes the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * @return the fatal
	 */
	public String getFatal() {
		return fatal;
	}

	/**
	 * @param fatal the fatal to set
	 */
	public void setFatal(String fatal) {
		this.fatal = fatal;
	}

	/**
	 * @return the warning
	 */
	public String getWarning() {
		return warning;
	}

	/**
	 * @param warning the warning to set
	 */
	public void setWarning(String warning) {
		this.warning = warning;
	}
}
