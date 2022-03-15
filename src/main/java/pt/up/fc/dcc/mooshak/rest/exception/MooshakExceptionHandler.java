package pt.up.fc.dcc.mooshak.rest.exception;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import pt.up.fc.dcc.mooshak.rest.exception.model.ExceptionDetails;

@Provider
public class MooshakExceptionHandler implements ExceptionMapper<Exception> {

	@Context
	private UriInfo uriInfo;
	
	@Override
	public Response toResponse(Exception e) {
		
		Status status = Status.INTERNAL_SERVER_ERROR;
		
		ExceptionDetails exceptionDetails = new ExceptionDetails();
		exceptionDetails.setStatus(status.getStatusCode());
		exceptionDetails.setTitle(status.getReasonPhrase());
		exceptionDetails.setMessage(e.getMessage());
		exceptionDetails.setPath(uriInfo.getAbsolutePath().getPath());
		
		return Response.status(status).entity(exceptionDetails).build();
	}
}
