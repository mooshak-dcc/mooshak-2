package pt.up.fc.dcc.mooshak.managers.utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

/**
 * Factory of HTTP clients
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class HttpClientFactory {

	private static final Logger LOGGER = Logger.getLogger(HttpClientFactory.class.getCanonicalName());
	
	private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 200;

	private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = DEFAULT_MAX_TOTAL_CONNECTIONS;

	private static final int DEFAULT_CONNECTION_TIMEOUT_MILLISECONDS = (30 * 1000);
	private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = (60 * 1000);
	private static final int DEFAULT_VALIDATE_INACTIVITY_MILLISECONDS = (5 * 1000);

	private static final int DEFAULT_KEEP_ALIVE_MILLISECONDS = (5 * 60 * 1000);

	/*private static final String DEFAULT_CHARSET = "UTF-8";

	private static final int DEFAULT_RETRY_COUNT = 2;*/

	private int keepAlive = DEFAULT_KEEP_ALIVE_MILLISECONDS;

	private int maxTotalConnections = DEFAULT_MAX_TOTAL_CONNECTIONS;
	private int maxConnectionsPerRoute = DEFAULT_MAX_CONNECTIONS_PER_ROUTE;

	private PoolingHttpClientConnectionManager connManager;

	private Client httpClient = null;

	/*private ConnectionKeepAliveStrategy keepAliveStrategy = new ConnectionKeepAliveStrategy() {
		@Override
		public long getKeepAliveDuration(HttpResponse response,
				HttpContext context) {
			HeaderElementIterator it = new BasicHeaderElementIterator(
					response.headerIterator(HTTP.CONN_KEEP_ALIVE));
			while (it.hasNext()) {
				HeaderElement he = it.nextElement();
				String param = he.getName();
				String value = he.getValue();
				if (value != null && param.equalsIgnoreCase("timeout")) {
					return Long.parseLong(value) * 1000;
				}
			}
			return keepAlive;
		}
	};*/

	public HttpClientFactory() {

		Registry<ConnectionSocketFactory> socketFactoryRegistry = 
				RegistryBuilder.<ConnectionSocketFactory>create()
						.register("http", PlainConnectionSocketFactory.getSocketFactory())
						.register("https", SSLConnectionSocketFactory.getSystemSocketFactory())
						.build();
		
		connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		// Increase max total connection
		connManager.setMaxTotal(maxTotalConnections);
		// Increase default max connection per route
		connManager.setDefaultMaxPerRoute(maxConnectionsPerRoute);
		// Set the inactivity time after which it should validate the connection
		connManager.setValidateAfterInactivity(DEFAULT_VALIDATE_INACTIVITY_MILLISECONDS);

		ClientConfig clientConfig = new ClientConfig();
		clientConfig.property(ClientProperties.READ_TIMEOUT, DEFAULT_READ_TIMEOUT_MILLISECONDS);
		clientConfig.property(ClientProperties.CONNECT_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT_MILLISECONDS);
		clientConfig.property(ApacheClientProperties.CONNECTION_MANAGER, connManager);
		clientConfig.connectorProvider(new ApacheConnectorProvider());
	
		httpClient = ClientBuilder.newClient(clientConfig);
		
		// detect idle and expired connections and close them
		IdleConnectionMonitorThread staleMonitor = new IdleConnectionMonitorThread(
				connManager);
		staleMonitor.start();
	}
	
	/**
	 * @return the httpClient
	 */
	public Client getHttpClient() {
		return httpClient;
	}

	public void shutdown() throws IOException {
		httpClient.close();
	}

	public int getKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(int keepAlive) {
		this.keepAlive = keepAlive;
	}

	public int getMaxTotalConnections() {
		return maxTotalConnections;
	}

	public void setMaxTotalConnections(int maxTotalConnections) {
		this.maxTotalConnections = maxTotalConnections;
	}

	public int getMaxConnectionsPerRoute() {
		return maxConnectionsPerRoute;
	}

	public void setMaxConnectionsPerRoute(int maxConnectionsPerRoute) {
		this.maxConnectionsPerRoute = maxConnectionsPerRoute;
	}

	public PoolingHttpClientConnectionManager getConnManager() {
		return connManager;
	}

	public void setConnManager(PoolingHttpClientConnectionManager connManager) {
		this.connManager = connManager;
	}
	
	public static class IdleConnectionMonitorThread extends Thread {

		private final HttpClientConnectionManager connMgr;
		private volatile boolean shutdown;

		public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
			super();
			this.connMgr = connMgr;
		}

		@Override
		public void run() {
			try {
				while (!shutdown) {
					synchronized (this) {
						wait(5000);
						// Close expired connections
						connMgr.closeExpiredConnections();
						// Optionally, close connections
						// that have been idle longer than 60 sec
						connMgr.closeIdleConnections(60, TimeUnit.SECONDS);
					}
				}
			} catch (InterruptedException ex) {
				// terminate
				shutdown();
			}
		}

		public void shutdown() {
			shutdown = true;
			synchronized (this) {
				notifyAll();
			}
		}

	}
}
/*
	// the timeout in milliseconds until a connection is established.
	private static final int CONNECT_TIMEOUT = 30000;

	// the timeout for waiting for data
	private static final int READ_TIMEOUT = 60000;

	public static Client create() {
		ClientConfig clientConfig = new ClientConfig();

		clientConfig.property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT);
		clientConfig.property(ClientProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);

		Registry<ConnectionSocketFactory> socketFactoryRegistry = 
				RegistryBuilder.<ConnectionSocketFactory>create()
						.register("http", PlainConnectionSocketFactory.getSocketFactory())
						.register("https", SSLConnectionSocketFactory.getSystemSocketFactory())
						.build();
		PoolingHttpClientConnectionManager connectionManager =
				new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		connectionManager.setMaxTotal(100);
		connectionManager.setDefaultMaxPerRoute(40);
		connectionManager.

		clientConfig.property(ApacheClientProperties.CONNECTION_MANAGER, connectionManager);

	    clientConfig.connectorProvider(new ApacheConnectorProvider());

	    // Feature feature = new LoggingFeature(LOGGER, Level.SEVERE, null, null);

		Client client = ClientBuilder.newClient(clientConfig);
		// client.register(feature);

		return client;
	}
}
*/