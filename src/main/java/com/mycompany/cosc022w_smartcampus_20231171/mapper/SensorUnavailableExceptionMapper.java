package com.mycompany.cosc022w_smartcampus_20231171.mapper;

import com.mycompany.cosc022w_smartcampus_20231171.exception.SensorUnavailableException;
import com.mycompany.cosc022w_smartcampus_20231171.model.ErrorResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Maps SensorUnavailableException to HTTP 403 with JSON error body.
 */
@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Context
    private UriInfo uriInfo;

    /**
     * Converts exception to API response.
     *
     * @param exception thrown exception
     * @return HTTP 403 response
     */
    @Override
    public Response toResponse(SensorUnavailableException exception) {
        // Include request path so clients can locate failing operation.
        String path = uriInfo == null ? "/api/v1/sensors" : uriInfo.getRequestUri().getPath();
        ErrorResponse error = ErrorResponse.of(403, "Forbidden", exception.getMessage(), path);
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
