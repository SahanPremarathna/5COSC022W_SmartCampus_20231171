package com.mycompany.cosc022w_smartcampus_20231171.mapper;

import com.mycompany.cosc022w_smartcampus_20231171.exception.LinkedResourceNotFoundException;
import com.mycompany.cosc022w_smartcampus_20231171.model.ErrorResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Maps linked-resource validation failures to HTTP 422 JSON errors.
 */
@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Context
    private UriInfo uriInfo;

    /**
     * Converts exception to API response.
     *
     * @param exception thrown exception
     * @return HTTP 422 response
     */
    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        // Keep response body informative but safe.
        String path = uriInfo == null ? "/api/v1/sensors" : uriInfo.getRequestUri().getPath();
        ErrorResponse error = ErrorResponse.of(422, "Unprocessable Entity", exception.getMessage(), path);
        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
