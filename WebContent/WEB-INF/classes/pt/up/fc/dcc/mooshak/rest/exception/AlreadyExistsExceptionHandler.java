package pt.up.fc.dcc.mooshak.rest.exception;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import pt.up.fc.dcc.mooshak.rest.exception.model.ExceptionDetails;

@Provider
public class AlreadyExistsExceptionHandler implements ExceptionMapper<AlreadyExistsException> {

	@Context
	private UriInfo uriInfo;
	
	@Override
	public Response toResponse(AlreadyExistsException e) {
		
		Status status = Status.CONFLICT;
		
		ExceptionDetails exceptionDetails = new ExceptionDetails();
		exceptionDetails.setStatus(status.getStatusCode());
		exceptionDetails.setTitle(status.getReasonPhrase());
		exceptionDetails.setMessage(e.getMessage());
		exceptionDetails.setPath(uriInfo.getAbsolutePath().getPath());
		
		return Response.status(status).entity(exceptionDetails).build();
	}
}
