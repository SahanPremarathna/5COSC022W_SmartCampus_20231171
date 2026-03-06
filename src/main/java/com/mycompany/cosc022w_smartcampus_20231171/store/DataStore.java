package com.mycompany.cosc022w_smartcampus_20231171.store;

import com.mycompany.cosc022w_smartcampus_20231171.exception.LinkedResourceNotFoundException;
import com.mycompany.cosc022w_smartcampus_20231171.exception.RoomNotEmptyException;
import com.mycompany.cosc022w_smartcampus_20231171.exception.SensorUnavailableException;
import com.mycompany.cosc022w_smartcampus_20231171.model.Room;
import com.mycompany.cosc022w_smartcampus_20231171.model.Sensor;
import com.mycompany.cosc022w_smartcampus_20231171.model.SensorReading;
import com.mycompany.cosc022w_smartcampus_20231171.model.SensorStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Central in-memory store for rooms, sensors, and sensor readings.
 * Uses thread-safe collections plus synchronized write operations.
 */
public class DataStore {

    private static final DataStore INSTANCE = new DataStore();

    private final AtomicInteger roomIdGenerator = new AtomicInteger(0);
    private final AtomicInteger sensorIdGenerator = new AtomicInteger(0);
    private final AtomicInteger readingIdGenerator = new AtomicInteger(0);

    private final Map<Integer, Room> rooms = new ConcurrentHashMap<Integer, Room>();
    private final Map<Integer, Sensor> sensors = new ConcurrentHashMap<Integer, Sensor>();
    private final Map<Integer, List<SensorReading>> sensorReadings = new ConcurrentHashMap<Integer, List<SensorReading>>();

    /**
     * Private constructor for singleton pattern.
     */
    private DataStore() {
    }

    /**
     * Returns the shared singleton store instance.
     *
     * @return datastore singleton
     */
    public static DataStore getInstance() {
        return INSTANCE;
    }

    /**
     * Returns all rooms sorted by id.
     *
     * @return room list
     */
    public List<Room> getAllRooms() {
        List<Room> results = new ArrayList<Room>(rooms.values());
        // Keep output deterministic for clients and tests.
        Collections.sort(results, Comparator.comparing(Room::getId));
        return results;
    }

    /**
     * Finds one room by id.
     *
     * @param roomId room identifier
     * @return room or null
     */
    public Room getRoomById(int roomId) {
        return rooms.get(roomId);
    }

    /**
     * Creates and stores a new room with generated id.
     *
     * @param request requested room payload
     * @return created room
     */
    public synchronized Room createRoom(Room request) {
        // Generate monotonic id in a thread-safe manner.
        int id = roomIdGenerator.incrementAndGet();
        Room room = new Room(id, request.getName(), request.getBuilding(), request.getFloor(), request.getDescription());
        rooms.put(id, room);
        return room;
    }

    /**
     * Deletes a room when there are no linked sensors.
     *
     * @param roomId room identifier
     * @return true if deleted, false when room does not exist
     */
    public synchronized boolean deleteRoom(int roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            return false;
        }

        // Enforce business rule: room cannot be removed while sensors are assigned.
        for (Sensor sensor : sensors.values()) {
            if (sensor.getRoomId() != null && sensor.getRoomId() == roomId) {
                throw new RoomNotEmptyException("Room " + roomId + " still has active sensors assigned.");
            }
        }

        // Safe to remove once no sensor linkage is found.
        rooms.remove(roomId);
        return true;
    }

    /**
     * Returns sensors optionally filtered by type (case-insensitive).
     *
     * @param type optional sensor type
     * @return sensor list
     */
    public List<Sensor> getAllSensors(String type) {
        List<Sensor> results = new ArrayList<Sensor>();
        for (Sensor sensor : sensors.values()) {
            if (type == null || type.trim().isEmpty()) {
                results.add(sensor);
            } else if (sensor.getType() != null && sensor.getType().equalsIgnoreCase(type.trim())) {
                results.add(sensor);
            }
        }
        // Keep output deterministic for clients and tests.
        Collections.sort(results, Comparator.comparing(Sensor::getId));
        return results;
    }

    /**
     * Finds one sensor by id.
     *
     * @param sensorId sensor identifier
     * @return sensor or null
     */
    public Sensor getSensorById(int sensorId) {
        return sensors.get(sensorId);
    }

    /**
     * Creates and stores a new sensor after validating linked room existence.
     *
     * @param request requested sensor payload
     * @return created sensor
     */
    public synchronized Sensor createSensor(Sensor request) {
        Integer roomId = request.getRoomId();
        // Semantic validation for linked resource references.
        if (roomId == null || !rooms.containsKey(roomId)) {
            throw new LinkedResourceNotFoundException("Room " + roomId + " does not exist.");
        }

        // Generate id and assign default status when absent.
        int id = sensorIdGenerator.incrementAndGet();
        SensorStatus status = request.getStatus() == null ? SensorStatus.ACTIVE : request.getStatus();
        Sensor sensor = new Sensor(id, request.getName(), request.getType(), status, request.getRoomId(), request.getCurrentValue());
        sensors.put(id, sensor);
        // Initialize reading history list for this sensor.
        sensorReadings.put(id, Collections.synchronizedList(new ArrayList<SensorReading>()));
        return sensor;
    }

    /**
     * Returns reading history for a sensor.
     *
     * @param sensorId sensor identifier
     * @return reading list (defensive copy)
     */
    public List<SensorReading> getReadingsForSensor(int sensorId) {
        List<SensorReading> readings = sensorReadings.get(sensorId);
        if (readings == null) {
            return Collections.emptyList();
        }
        // Synchronize around list copy because list is shared mutable state.
        synchronized (readings) {
            return new ArrayList<SensorReading>(readings);
        }
    }

    /**
     * Adds a new sensor reading and updates sensor current value.
     *
     * @param sensorId sensor identifier
     * @param request reading payload
     * @return created reading or null when sensor is missing
     */
    public synchronized SensorReading addReading(int sensorId, SensorReading request) {
        Sensor sensor = sensors.get(sensorId);
        if (sensor == null) {
            return null;
        }
        // Enforce MAINTENANCE state rule before accepting readings.
        if (sensor.getStatus() == SensorStatus.MAINTENANCE) {
            throw new SensorUnavailableException("Sensor " + sensorId + " is in MAINTENANCE mode and cannot accept readings.");
        }

        List<SensorReading> readings = sensorReadings.get(sensorId);
        if (readings == null) {
            // Backfill list when missing unexpectedly.
            readings = Collections.synchronizedList(new ArrayList<SensorReading>());
            sensorReadings.put(sensorId, readings);
        }

        // Generate id and normalize timestamp.
        int readingId = readingIdGenerator.incrementAndGet();
        long timestamp = request.getTimestamp() == null ? System.currentTimeMillis() : request.getTimestamp();
        SensorReading reading = new SensorReading(readingId, sensorId, request.getValue(), timestamp);
        readings.add(reading);

        // Side effect required by coursework: update parent sensor current value.
        sensor.setCurrentValue(reading.getValue());
        sensors.put(sensorId, sensor);
        return reading;
    }
}
