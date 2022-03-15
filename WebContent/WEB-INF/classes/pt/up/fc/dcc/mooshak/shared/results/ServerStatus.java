package pt.up.fc.dcc.mooshak.shared.results;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Current status of the server, data to be displayed in administration profile
 * 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class ServerStatus implements IsSerializable {
	int sessionCount = 0;
	int persistentObjectCount = 0;
	int availableProcessors = 0;
	int evaluationsInProgress = 0;
	int activeThreadCount = 0;
	double systemLoadAverage = 0D;
	long openFileDescriptorCount = 0L;
	long maxFileDescriptorCount = 0L;
	long committedVirtualMemorySize = 0L;
	long freeMemory = 0L;
	long maxMemory = 0L;
	
	/**
	 * @return the sessionCount
	 */
	public int getSessionCount() {
		return sessionCount;
	}
	/**
	 * @param sessionCount the sessionCount to set
	 */
	public void setSessionCount(int sessionCount) {
		this.sessionCount = sessionCount;
	}
	/**
	 * @return the evaluationsInProgress
	 */
	public int getEvaluationsInProgress() {
		return evaluationsInProgress;
	}
	/**
	 * @param evaluationsInProgress the evaluationsInProgress to set
	 */
	public void setEvaluationsInProgress(int evaluationsInProgress) {
		this.evaluationsInProgress = evaluationsInProgress;
	}
	/**
	 * @return the persistentObjectCount
	 */
	public int getPersistentObjectCount() {
		return persistentObjectCount;
	}
	/**
	 * @param persistentObjectCount the persistentObjectCount to set
	 */
	public void setPersistentObjectCount(int persistentObjectCount) {
		this.persistentObjectCount = persistentObjectCount;
	}
	/**
	 * @return the openFileDescriptorCount
	 */
	public long getOpenFileDescriptorCount() {
		return openFileDescriptorCount;
	}
	/**
	 * @param openFileDescriptorCount the openFileDescriptorCount to set
	 */
	public void setOpenFileDescriptorCount(long openFileDescriptorCount) {
		this.openFileDescriptorCount = openFileDescriptorCount;
	}
	/**
	 * @return the maxFileDescriptorCount
	 */
	public long getMaxFileDescriptorCount() {
		return maxFileDescriptorCount;
	}
	/**
	 * @return the activeThreadCount
	 */
	public int getActiveThreadCount() {
		return activeThreadCount;
	}
	/**
	 * @param activeThreadCount the activeThreadCount to set
	 */
	public void setActiveThreadCount(int activeThreadCount) {
		this.activeThreadCount = activeThreadCount;
	}
	/**
	 * @param maxFileDescriptorCount the maxFileDescriptorCount to set
	 */
	public void setMaxFileDescriptorCount(long maxFileDescriptorCount) {
		this.maxFileDescriptorCount = maxFileDescriptorCount;
	}
	/**
	 * @return the committedVirtualMemorySize
	 */
	public long getCommittedVirtualMemorySize() {
		return committedVirtualMemorySize;
	}
	/**
	 * @param committedVirtualMemorySize the committedVirtualMemorySize to set
	 */
	public void setCommittedVirtualMemorySize(long committedVirtualMemorySize) {
		this.committedVirtualMemorySize = committedVirtualMemorySize;
	}
	/**
	 * @return the freeMemory
	 */
	public long getFreeMemory() {
		return freeMemory;
	}
	/**
	 * @param freeMemory the freeMemory to set
	 */
	public void setFreeMemory(long freeMemory) {
		this.freeMemory = freeMemory;
	}
	/**
	 * @return the maxMemory
	 */
	public long getMaxMemory() {
		return maxMemory;
	}
	/**
	 * @param maxMemory the maxMemory to set
	 */
	public void setMaxMemory(long maxMemory) {
		this.maxMemory = maxMemory;
	}
	/**
	 * @return the availableProcessors
	 */
	public int getAvailableProcessors() {
		return availableProcessors;
	}
	/**
	 * @param availableProcessors the availableProcessors to set
	 */
	public void setAvailableProcessors(int availableProcessors) {
		this.availableProcessors = availableProcessors;
	}
	/**
	 * @return the systemLoadAverage
	 */
	public double getSystemLoadAverage() {
		return systemLoadAverage;
	}
	/**
	 * @param systemLoadAverage the systemLoadAverage to set
	 */
	public void setSystemLoadAverage(double systemLoadAverage) {
		this.systemLoadAverage = systemLoadAverage;
	}

}
