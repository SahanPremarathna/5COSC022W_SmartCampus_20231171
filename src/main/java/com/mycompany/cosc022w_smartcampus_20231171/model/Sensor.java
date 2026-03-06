package com.mycompany.cosc022w_smartcampus_20231171.model;

/**
 * Represents a sensor device installed in a room.
 */
public class Sensor {

    private Integer id;
    private String name;
    private String type;
    private SensorStatus status;
    private Integer roomId;
    private Double currentValue;

    /**
     * Creates an empty sensor object for JSON deserialization.
     */
    public Sensor() {
    }

    /**
     * Creates a sensor with all fields populated.
     *
     * @param id unique sensor identifier
     * @param name sensor name
     * @param type sensor type (e.g., CO2, TEMP)
     * @param status current operational status
     * @param roomId linked room id
     * @param currentValue latest value recorded by sensor
     */
    public Sensor(Integer id, String name, String type, SensorStatus status, Integer roomId, Double currentValue) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.roomId = roomId;
        this.currentValue = currentValue;
    }

    /**
     * @return sensor identifier
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id sensor identifier
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return sensor name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name sensor name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return sensor type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type sensor type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return sensor status
     */
    public SensorStatus getStatus() {
        return status;
    }

    /**
     * @param status sensor status
     */
    public void setStatus(SensorStatus status) {
        this.status = status;
    }

    /**
     * @return linked room id
     */
    public Integer getRoomId() {
        return roomId;
    }

    /**
     * @param roomId linked room id
     */
    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    /**
     * @return latest sensor value
     */
    public Double getCurrentValue() {
        return currentValue;
    }

    /**
     * @param currentValue latest sensor value
     */
    public void setCurrentValue(Double currentValue) {
        this.currentValue = currentValue;
    }
}
