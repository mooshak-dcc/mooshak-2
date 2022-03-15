package pt.up.fc.dcc.mooshak.rest.exception;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import pt.up.fc.dcc.mooshak.rest.exception.model.ExceptionDetails;
import pt.up.fc.dcc.mooshak.rest.exception.status.UnprocessableEntity;

@Provider
public class UnprocessableEntityExceptionHandler implements ExceptionMapper<UnprocessableEntityException> {

	@Context
	private UriInfo uriInfo;
	
	@Override
	public Response toResponse(UnprocessableEntityException e) {
		
		StatusType statusType = new UnprocessableEntity();
		
		ExceptionDetails exceptionDetails = new ExceptionDetails();
		exceptionDetails.setStatus(statusType.getStatusCode());
		exceptionDetails.setTitle(statusType.getReasonPhrase());
		exceptionDetails.setMessage(e.getMessage());
		exceptionDetails.setPath(uriInfo.getAbsolutePath().getPath());
		
		return Response.status(statusType).entity(exceptionDetails).build();
	}
}
