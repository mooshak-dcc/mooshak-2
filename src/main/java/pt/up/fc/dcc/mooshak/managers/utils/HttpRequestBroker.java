package pt.up.fc.dcc.mooshak.managers.utils;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class HttpRequestBroker {
	
	public enum HttpMethod { GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD }
	
	private static HttpRequestBroker instance = null;
	
	private Client client = null;

	private HttpRequestBroker() {
		client = new HttpClientFactory().getHttpClient();
	}
	
	/**
	 * Get a single instance of this class
	 * 
	 * @return
	 */
	public static HttpRequestBroker getInstance() {
		if (instance == null) {
			instance = new HttpRequestBroker();
		}
		return instance;
	}
	
	/**
	 * Make an HTTP GET request
	 * 
	 * @param host {@link String} Host address 
	 * @param endpoint {@link String} endpoint to call
	 * @param responseType {@link Class<T>} Type of the response
	 * @param queryParams {@link String...} array of key value for query parameters
	 * @return {@code T} Entity of the response
	 * @throws MooshakException - if an error occurs in the request
	 */
	public <T> T get(String host, String endpoint, Class<T> responseType, String...queryParams) 
			throws MooshakException {
		
		return makeRequest(HttpMethod.GET, host, endpoint, null, responseType, queryParams);
	}
	
	/**
	 * Make an HTTP POST request
	 * 
	 * @param host {@link String} Host address 
	 * @param endpoint {@link String} endpoint to call
	 * @param entity {@link Entity<?>} entity to send in the request body
	 * @param responseType {@link Class<T>} Type of the response
	 * @param queryParams {@link String...} array of key value for query parameters
	 * @return {@code T} Entity of the response
	 * @throws MooshakException - if an error occurs in the request
	 */
	public <T> T post(String host, String endpoint, Entity<?> entity, Class<T> responseType,
			String...queryParams) throws MooshakException {
		
		return makeRequest(HttpMethod.POST, host, endpoint, entity, responseType,
				queryParams);
	}
	
	/**
	 * Make an HTTP PUT request
	 * 
	 * @param host {@link String} Host address 
	 * @param endpoint {@link String} endpoint to call
	 * @param entity {@link Entity<?>} entity to send in the request body
	 * @param responseType {@link Class<T>} Type of the response
	 * @param queryParams {@link String...} array of key value for query parameters
	 * @return {@code T} Entity of the response
	 * @throws MooshakException - if an error occurs in the request
	 */
	public <T> T put(String host, String endpoint, Entity<?> entity, Class<T> responseType,
			String...queryParams) throws MooshakException {
		
		return makeRequest(HttpMethod.PUT, host, endpoint, entity, responseType,
				queryParams);
	}
	
	/**
	 * Make an HTTP DELETE request
	 * 
	 * @param host {@link String} Host address 
	 * @param endpoint {@link String} endpoint to call
	 * @param responseType {@link Class<T>} Type of the response
	 * @param queryParams {@link String...} array of key value for query parameters
	 * @return {@code T} Entity of the response
	 * @throws MooshakException - if an error occurs in the request
	 */
	public <T> T delete(String host, String endpoint, Class<T> responseType,
			String...queryParams) throws MooshakException {
		
		return makeRequest(HttpMethod.DELETE, host, endpoint, null, responseType,
				queryParams);
	}
	
	/**
	 * Make an HTTP PATCH request
	 * 
	 * @param host {@link String} Host address 
	 * @param endpoint {@link String} endpoint to call
	 * @param entity {@link Entity<?>} entity to send in the request body
	 * @param responseType {@link Class<T>} Type of the response
	 * @param queryParams {@link String...} array of key value for query parameters
	 * @return {@code T} Entity of the response
	 * @throws MooshakException - if an error occurs in the request
	 */
	public <T> T patch(String host, String endpoint, Entity<?> entity, Class<T> responseType,
			String...queryParams) throws MooshakException {
		
		return makeRequest(HttpMethod.PATCH, host, endpoint, entity, responseType,
				queryParams);
	}
	
	/**
	 * Make an HTTP OPTIONS request
	 * 
	 * @param host {@link String} Host address 
	 * @param endpoint {@link String} endpoint to call
	 * @param responseType {@link Class<T>} Type of the response
	 * @param queryParams {@link String...} array of key value for query parameters
	 * @return {@code T} Entity of the response
	 * @throws MooshakException - if an error occurs in the request
	 */
	public <T> T options(String host, String endpoint, Class<T> responseType, 
			String...queryParams) throws MooshakException {
		
		return makeRequest(HttpMethod.OPTIONS, host, endpoint, null, responseType,
				queryParams);
	}
	
	/**
	 * Make an HTTP HEAD request
	 * 
	 * @param host {@link String} Host address 
	 * @param endpoint {@link String} endpoint to call
	 * @param queryParams {@link String...} array of key value for query parameters
	 * @return {@code Response} Response sent by the server
	 * @throws MooshakException - if an error occurs in the request
	 */
	public Response head(String host, String endpoint, String...queryParams)
			throws MooshakException {
		
		return makeRequest(HttpMethod.HEAD, host, endpoint, null, Response.class,
				queryParams);
	}

	@SuppressWarnings("unchecked")
	public <T> T makeRequest(HttpMethod method, String host, String endpoint, Entity<?> entity,
			Class<T> responseType, String...queryParams) throws MooshakException {
		
		if (host == null)
			throw new MooshakException("Host cannot be null.");
		
		if (endpoint == null)
			endpoint = "";
		
		if (queryParams.length % 2 != 0)
			throw new MooshakException("Invalid query parameters provided to make request!");
		
		WebTarget resource = client.target(host + endpoint);
		for (int i = 0; i < queryParams.length; i += 2) {
			resource = resource.queryParam(queryParams[i], queryParams[i + 1]);
		}
				
		Builder requestBuilder = resource.request();
		
		Response response;
		switch (method) {
		case GET:
			response = requestBuilder.get();
			break;
		case POST:
			response = requestBuilder.post(entity);
			break;
		case PUT:
			response = requestBuilder.put(entity);
			break;
		case DELETE:
			response = requestBuilder.delete();
			break;
		case PATCH:
			response = requestBuilder.method(HttpMethod.PATCH.toString(), entity);
			break;
		case OPTIONS:
			response = requestBuilder.options();
			break;
		case HEAD:
			return (T) requestBuilder.head();
		default:
			throw new MooshakException("Unknown HTTP method.");
		}
		
		processStatus(response.getStatusInfo());
		
		if (responseType.isAssignableFrom(Response.class)) {
			
			if (response.hasEntity())
				response.bufferEntity();
			
			return (T) response;
		}
		
		if (responseType.isAssignableFrom(Void.class)) {
			
			if (response.hasEntity())
				response.bufferEntity();
			
			response.close();
			
			return null;
		}
		
		if (!response.hasEntity()) {
			response.close();
			throw new MooshakException("Expecting an entity, but found nothing.");
		}
		
		T responseEntity;
		try {
			responseEntity = response.readEntity(responseType);
		} catch (ProcessingException e) {
			throw new MooshakException("Could not parse entity received.", e);
		} catch (IllegalStateException e) {
			throw new MooshakException("Could not read entity received.", e);
		} finally {
			response.close();
		}
		
		return responseEntity;
	}

	private void processStatus(StatusType statusType) throws MooshakException {
		
		switch (statusType.getFamily()) {
		case CLIENT_ERROR:
			throw new MooshakException(String.format("An error ocurred due to a "
					+ "malformed request. Status: %d. Reason: %s.", 
					statusType.getStatusCode(),
					statusType.getReasonPhrase()));
		case SERVER_ERROR:
			throw new MooshakException(String.format("An error ocurred processing "
					+ "your request. Status: %d. Reason: %s.", 
					statusType.getStatusCode(),
					statusType.getReasonPhrase()));
		case OTHER:
			throw new MooshakException(String.format("Unrecognized HTTP status code. "
					+ "Status: %d. Reason: %s.", statusType.getStatusCode(),
					statusType.getReasonPhrase()));
		/*case REDIRECTION:
			throw new MooshakException("Server sent a redirect status code, which "
					+ "is not supported by this broker."); */

		default:
			break;
		}
	}
}
