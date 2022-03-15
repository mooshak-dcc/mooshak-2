package pt.up.fc.dcc.mooshak.balancer;

import java.rmi.Remote;
import java.rmi.RemoteException;

import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.evaluation.EvaluationQueue;

public interface LoadBalancingService extends Remote, EvaluationQueue {

	final static String RMI_URL = "//localhost/balancer";
	final static int RMI_PORT = 1099;
	
	/**
	 * Get a persistent object from the master that is required in the worker
	 * @param path
	 * @return
	 */
	public <T extends PersistentObject> T getPersistentObject(String path) 
			throws RemoteException;

	
}
