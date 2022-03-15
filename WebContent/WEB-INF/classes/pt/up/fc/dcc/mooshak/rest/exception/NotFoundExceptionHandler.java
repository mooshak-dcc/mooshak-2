package pt.up.fc.dcc.mooshak.rest.exception;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import pt.up.fc.dcc.mooshak.rest.exception.model.ExceptionDetails;

@Provider
public class NotFoundExceptionHandler implements ExceptionMapper<NotFoundException> {

	@Context
	private UriInfo uriInfo;
	
	@Override
	public Response toResponse(NotFoundException e) {
		
		Status status = Status.NOT_FOUND;
		
		ExceptionDetails exceptionDetails = new ExceptionDetails();
		exceptionDetails.setStatus(status.getStatusCode());
		exceptionDetails.setTitle(status.getReasonPhrase());
		exceptionDetails.setMessage(e.getMessage());
		exceptionDetails.setPath(uriInfo.getAbsolutePath().getPath());
		
		return Response.status(status).entity(exceptionDetails).build();
	}
}
