package pt.up.fc.dcc.mooshak.balancer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MooshakWorker {
	private static final Logger LOGGER = Logger.getLogger("");
	
	private LoadBalancingService master = null;

	public MooshakWorker() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
	}

	public LoadBalancingService getLoadBalancingService(String url) {
		
		if(master == null) 
			try {
				master = (LoadBalancingService) Naming.lookup(url);

			} catch (RemoteException 
					| MalformedURLException 
					| NotBoundException cause) {
				LOGGER.log(Level.SEVERE,"RmiClient exception:",cause);
			}

		return master;
	}

}
