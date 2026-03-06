package com.mycompany.cosc022w_smartcampus_20231171.mapper;

import com.mycompany.cosc022w_smartcampus_20231171.exception.RoomNotEmptyException;
import com.mycompany.cosc022w_smartcampus_20231171.model.ErrorResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Maps RoomNotEmptyException to HTTP 409 with structured JSON error body.
 */
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Context
    private UriInfo uriInfo;

    /**
     * Converts exception to API response.
     *
     * @param exception thrown exception
     * @return HTTP 409 response
     */
    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        // Capture current request path for client troubleshooting.
        String path = uriInfo == null ? "/api/v1/rooms" : uriInfo.getRequestUri().getPath();
        ErrorResponse error = ErrorResponse.of(409, "Conflict", exception.getMessage(), path);
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
