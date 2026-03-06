package com.mycompany.cosc022w_smartcampus_20231171.filter;

import java.io.IOException;
import java.util.logging.Logger;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 * Logs inbound requests and outbound responses for all API calls.
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());

    /**
     * Logs request method and URI before resource execution.
     *
     * @param requestContext container request context
     * @throws IOException when filter processing fails
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOGGER.info(String.format(
                "Request method=%s uri=%s",
                requestContext.getMethod(),
                requestContext.getUriInfo().getRequestUri()
        ));
    }

    /**
     * Logs final response status after resource execution.
     *
     * @param requestContext container request context
     * @param responseContext container response context
     * @throws IOException when filter processing fails
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        LOGGER.info(String.format(
                "Response method=%s uri=%s status=%d",
                requestContext.getMethod(),
                requestContext.getUriInfo().getRequestUri(),
                responseContext.getStatus()
        ));
    }
}
