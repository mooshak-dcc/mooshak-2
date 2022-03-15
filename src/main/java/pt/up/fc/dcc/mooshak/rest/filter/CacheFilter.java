package pt.up.fc.dcc.mooshak.rest.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

@Provider
public class CacheFilter implements ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext reqontext, ContainerResponseContext res)
			throws IOException {
		res.getHeaders().add(HttpHeaders.CACHE_CONTROL, "public, max-age=3600, s-maxage=18000");
	}
}
