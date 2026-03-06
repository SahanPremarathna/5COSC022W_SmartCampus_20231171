package com.mycompany.cosc022w_smartcampus_20231171.model;

/**
 * Represents a physical room in the smart campus.
 */
public class Room {

    private Integer id;
    private String name;
    private String building;
    private String floor;
    private String description;

    /**
     * Creates an empty room object for JSON deserialization.
     */
    public Room() {
    }

    /**
     * Creates a room with all fields populated.
     *
     * @param id unique room identifier
     * @param name room name
     * @param building building name
     * @param floor floor label/number
     * @param description optional room description
     */
    public Room(Integer id, String name, String building, String floor, String description) {
        this.id = id;
        this.name = name;
        this.building = building;
        this.floor = floor;
        this.description = description;
    }

    /**
     * @return room identifier
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id room identifier
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return room name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name room name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return building name
     */
    public String getBuilding() {
        return building;
    }

    /**
     * @param building building name
     */
    public void setBuilding(String building) {
        this.building = building;
    }

    /**
     * @return floor label/number
     */
    public String getFloor() {
        return floor;
    }

    /**
     * @param floor floor label/number
     */
    public void setFloor(String floor) {
        this.floor = floor;
    }

    /**
     * @return room description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description room description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
