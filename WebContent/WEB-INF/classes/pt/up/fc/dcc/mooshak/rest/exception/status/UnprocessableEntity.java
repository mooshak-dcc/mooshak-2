package pt.up.fc.dcc.mooshak.rest.exception.status;

import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

/**
 * Status for unprocessable entities
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class UnprocessableEntity implements StatusType {

	@Override
	public int getStatusCode() {
		return 422;
	}

	@Override
	public Family getFamily() {
		return Family.CLIENT_ERROR;
	}

	@Override
	public String getReasonPhrase() {
		return "Unprocessable Entity";
	}
}
