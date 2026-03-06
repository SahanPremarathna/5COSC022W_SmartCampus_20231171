package com.mycompany.cosc022w_smartcampus_20231171.resource;

import com.mycompany.cosc022w_smartcampus_20231171.model.ErrorResponse;
import com.mycompany.cosc022w_smartcampus_20231171.model.Room;
import com.mycompany.cosc022w_smartcampus_20231171.store.DataStore;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Resource for room collection and room item operations.
 */
@Path("/rooms")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SensorRoomResource {

    private final DataStore dataStore = DataStore.getInstance();

    @Context
    private UriInfo uriInfo;

    /**
     * Returns all rooms.
     *
     * @return list of rooms
     */
    @GET
    public List<Room> getAllRooms() {
        return dataStore.getAllRooms();
    }

    /**
     * Creates a room.
     *
     * @param request incoming room payload
     * @return created room with HTTP 201
     */
    @POST
    public Response createRoom(Room request) {
        // Validate minimal required business field before persisting.
        if (request == null || isBlank(request.getName())) {
            throw badRequest("Room payload must include at least a non-empty name.");
        }

        // Persist in shared in-memory store and return created representation.
        Room created = dataStore.createRoom(request);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     * Returns one room by id.
     *
     * @param roomId room identifier
     * @return room
     */
    @GET
    @Path("/{roomId}")
    public Room getRoom(@PathParam("roomId") int roomId) {
        Room room = dataStore.getRoomById(roomId);
        // Convert missing room into structured API error.
        if (room == null) {
            throw notFound("Room " + roomId + " not found.");
        }
        return room;
    }

    /**
     * Deletes a room if no sensors are assigned.
     *
     * @param roomId room identifier
     * @return HTTP 204 when deleted
     */
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") int roomId) {
        // DataStore enforces room-not-empty rule and can throw RoomNotEmptyException.
        boolean deleted = dataStore.deleteRoom(roomId);
        if (!deleted) {
            throw notFound("Room " + roomId + " not found.");
        }
        return Response.noContent().build();
    }

    /**
     * Builds a 404 web exception with standard error body.
     *
     * @param message error message
     * @return web application exception
     */
    private WebApplicationException notFound(String message) {
        String path = uriInfo == null ? "/api/v1/rooms" : uriInfo.getRequestUri().getPath();
        ErrorResponse error = ErrorResponse.of(404, "Not Found", message, path);
        return buildWebException(Response.Status.NOT_FOUND, error);
    }

    /**
     * Builds a 400 web exception with standard error body.
     *
     * @param message error message
     * @return web application exception
     */
    private WebApplicationException badRequest(String message) {
        String path = uriInfo == null ? "/api/v1/rooms" : uriInfo.getRequestUri().getPath();
        ErrorResponse error = ErrorResponse.of(400, "Bad Request", message, path);
        return buildWebException(Response.Status.BAD_REQUEST, error);
    }

    /**
     * Builds a JSON web exception response.
     *
     * @param status HTTP status
     * @param error error payload
     * @return web application exception
     */
    private WebApplicationException buildWebException(Response.Status status, ErrorResponse error) {
        Response response = Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
        return new WebApplicationException(response);
    }

    /**
     * Returns true when a string is null/blank.
     *
     * @param value input value
     * @return true if blank
     */
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
