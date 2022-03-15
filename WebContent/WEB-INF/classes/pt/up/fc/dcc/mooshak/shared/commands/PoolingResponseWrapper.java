package pt.up.fc.dcc.mooshak.shared.commands;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Wraps an object which requires timed polling with an adaptive interval for
 * the next pooling
 * 
 * @author josepaiva
 *
 * @param <T>
 *            Type of the object wrapped
 */
public class PoolingResponseWrapper<T> implements IsSerializable {
	private static final long DEFAULT_POOLING_TIME = 30 * 1000;

	private T object = null;
	private long interval = DEFAULT_POOLING_TIME;

	public PoolingResponseWrapper() {
	}

	public PoolingResponseWrapper(T object, long interval) {
		this.object = object;
		this.interval = interval;
	}

	/**
	 * @return the object
	 */
	public T getObject() {
		return object;
	}

	/**
	 * @param object the object to set
	 */
	public void setObject(T object) {
		this.object = object;
	}

	/**
	 * @return the interval
	 */
	public long getInterval() {
		return interval;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(long interval) {
		this.interval = interval;
	}
	
}
