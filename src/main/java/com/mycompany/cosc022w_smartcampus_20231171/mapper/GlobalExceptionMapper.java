package com.mycompany.cosc022w_smartcampus_20231171.mapper;

import com.mycompany.cosc022w_smartcampus_20231171.model.ErrorResponse;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Global safety-net mapper for unhandled exceptions.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Context
    private UriInfo uriInfo;

    /**
     * Converts uncaught exceptions into safe API responses.
     *
     * @param exception uncaught throwable
     * @return mapped HTTP response
     */
    @Override
    public Response toResponse(Throwable exception) {
        // Preserve explicit web exceptions generated with custom status/body.
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        }

        // Log full stack trace server-side only.
        LOGGER.log(Level.SEVERE, "Unhandled API exception", exception);
        String path = uriInfo == null ? "/api/v1" : uriInfo.getRequestUri().getPath();
        // Return generic message to avoid leaking implementation details.
        ErrorResponse error = ErrorResponse.of(
                500,
                "Internal Server Error",
                "An unexpected server error occurred. Please contact the administrator.",
                path
        );
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}
