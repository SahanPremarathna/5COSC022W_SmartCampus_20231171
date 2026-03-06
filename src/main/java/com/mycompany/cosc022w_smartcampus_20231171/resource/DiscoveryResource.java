package com.mycompany.cosc022w_smartcampus_20231171.resource;

import com.mycompany.cosc022w_smartcampus_20231171.model.AdminContact;
import com.mycompany.cosc022w_smartcampus_20231171.model.DiscoveryResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Root discovery endpoint exposing API metadata and top-level links.
 */
@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    /**
     * Returns version, admin details, and discoverable collection links.
     *
     * @return discovery response payload
     */
    @GET
    public DiscoveryResponse discover() {
        // Keep deterministic link ordering for readable output.
        Map<String, String> resources = new LinkedHashMap<String, String>();
        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");

        // Basic contact block for administration/support.
        AdminContact admin = new AdminContact("Sahan Premarathna", "admin@example.com");
        return new DiscoveryResponse("v1", admin, resources);
    }
}
