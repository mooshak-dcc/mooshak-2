package pt.up.fc.dcc.mooshak.rest.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class CORSFilter implements ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext req,
			ContainerResponseContext res) throws IOException {
		res.getHeaders().add("Access-Control-Allow-Origin", "*");
		res.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept,"
				+ " authorization, x-http-method-override");
		res.getHeaders().add("Access-Control-Allow-Credentials", "true");
		res.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD");
		res.getHeaders().add("Access-Control-Max-Age", "1209600");
	}
}
