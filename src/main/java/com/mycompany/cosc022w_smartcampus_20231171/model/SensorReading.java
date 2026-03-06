package com.mycompany.cosc022w_smartcampus_20231171.model;

/**
 * Represents one historical reading produced by a sensor.
 */
public class SensorReading {

    private Integer id;
    private Integer sensorId;
    private Double value;
    private Long timestamp;

    /**
     * Creates an empty sensor reading object for JSON deserialization.
     */
    public SensorReading() {
    }

    /**
     * Creates a sensor reading with all fields populated.
     *
     * @param id unique reading identifier
     * @param sensorId parent sensor id
     * @param value reading value
     * @param timestamp epoch-millis timestamp
     */
    public SensorReading(Integer id, Integer sensorId, Double value, Long timestamp) {
        this.id = id;
        this.sensorId = sensorId;
        this.value = value;
        this.timestamp = timestamp;
    }

    /**
     * @return reading identifier
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id reading identifier
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return parent sensor identifier
     */
    public Integer getSensorId() {
        return sensorId;
    }

    /**
     * @param sensorId parent sensor identifier
     */
    public void setSensorId(Integer sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * @return reading value
     */
    public Double getValue() {
        return value;
    }

    /**
     * @param value reading value
     */
    public void setValue(Double value) {
        this.value = value;
    }

    /**
     * @return reading timestamp (epoch millis)
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp reading timestamp (epoch millis)
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
