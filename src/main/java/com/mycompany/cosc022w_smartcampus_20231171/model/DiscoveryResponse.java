package com.mycompany.cosc022w_smartcampus_20231171.model;

import java.util.Map;

/**
 * Response body for API discovery endpoint.
 */
public class DiscoveryResponse {

    private String version;
    private AdminContact admin;
    private Map<String, String> resources;

    /**
     * Creates an empty discovery response object.
     */
    public DiscoveryResponse() {
    }

    /**
     * Creates a populated discovery response.
     *
     * @param version API version label
     * @param admin administrator contact details
     * @param resources primary resource links
     */
    public DiscoveryResponse(String version, AdminContact admin, Map<String, String> resources) {
        this.version = version;
        this.admin = admin;
        this.resources = resources;
    }

    /**
     * @return API version label
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version API version label
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return admin contact details
     */
    public AdminContact getAdmin() {
        return admin;
    }

    /**
     * @param admin admin contact details
     */
    public void setAdmin(AdminContact admin) {
        this.admin = admin;
    }

    /**
     * @return resource name-to-link map
     */
    public Map<String, String> getResources() {
        return resources;
    }

    /**
     * @param resources resource name-to-link map
     */
    public void setResources(Map<String, String> resources) {
        this.resources = resources;
    }
}
