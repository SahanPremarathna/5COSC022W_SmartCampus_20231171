# Postman Test Requests — Smart Campus API

Base URL: `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1`

All POST requests require the header:
```
Content-Type: application/json
```

---

## Step 1 — Check the API is running

**GET** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1`

No body needed. Should return a discovery response with links.

---

## Step 2 — Create Rooms

### Create Room 1 (Lab)
**POST** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/rooms`
```json
{
  "name": "Lab A101",
  "building": "Engineering",
  "floor": "1",
  "description": "Smart sensor lab on ground floor"
}
```

### Create Room 2 (Lecture Theatre)
**POST** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/rooms`
```json
{
  "name": "Lecture Theatre B202",
  "building": "Science Block",
  "floor": "2",
  "description": "Main lecture theatre with air monitoring"
}
```

### Create Room 3 (Server Room)
**POST** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/rooms`
```json
{
  "name": "Server Room C001",
  "building": "IT Block",
  "floor": "Ground",
  "description": "Data centre server room"
}
```

---

## Step 3 — Get Rooms

### Get all rooms
**GET** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/rooms`

### Get room by ID
**GET** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/rooms/1`

---

## Step 4 — Create Sensors (linked to rooms above)

### Create CO2 Sensor in Room 1
**POST** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/sensors`
```json
{
  "name": "CO2 Sensor A",
  "type": "CO2",
  "status": "ACTIVE",
  "roomId": 1
}
```

### Create Temperature Sensor in Room 1
**POST** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/sensors`
```json
{
  "name": "Temp Sensor A",
  "type": "TEMPERATURE",
  "status": "ACTIVE",
  "roomId": 1
}
```

### Create Humidity Sensor in Room 2
**POST** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/sensors`
```json
{
  "name": "Humidity Sensor B",
  "type": "HUMIDITY",
  "status": "ACTIVE",
  "roomId": 2
}
```

### Create a Sensor in MAINTENANCE status in Room 2
**POST** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/sensors`
```json
{
  "name": "CO2 Sensor B",
  "type": "CO2",
  "status": "MAINTENANCE",
  "roomId": 2
}
```

### Create Temperature Sensor in Room 3 (Server Room)
**POST** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/sensors`
```json
{
  "name": "Server Temp Monitor",
  "type": "TEMPERATURE",
  "status": "ACTIVE",
  "roomId": 3
}
```

---

## Step 5 — Get Sensors

### Get all sensors
**GET** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/sensors`

### Filter sensors by type (CO2)
**GET** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/sensors?type=CO2`

### Filter sensors by type (TEMPERATURE)
**GET** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/sensors?type=TEMPERATURE`

---

## Step 6 — Add Readings to Sensors

### Add reading to Sensor 1 (CO2 Sensor A)
**POST** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/sensors/1/readings`
```json
{
  "value": 412.5
}
```

### Add another reading to Sensor 1
**POST** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/sensors/1/readings`
```json
{
  "value": 450.0
}
```

### Add reading to Sensor 2 (Temp Sensor A)
**POST** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/sensors/2/readings`
```json
{
  "value": 22.3
}
```

### Add reading to Sensor 3 (Humidity Sensor B)
**POST** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/sensors/3/readings`
```json
{
  "value": 65.7
}
```

### Add reading to Sensor 5 (Server Temp Monitor)
**POST** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/sensors/5/readings`
```json
{
  "value": 38.9
}
```

---

## Step 7 — Get Readings

### Get all readings for Sensor 1
**GET** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/sensors/1/readings`

### Get all readings for Sensor 2
**GET** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/sensors/2/readings`

---

## Step 8 — Error Scenarios (to test exception mappers)

### Try to delete Room 1 that has sensors — expects 409 Conflict
**DELETE** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/rooms/1`

### Try to create a sensor with a non-existent roomId — expects 422 Unprocessable Entity
**POST** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/sensors`
```json
{
  "name": "Ghost Sensor",
  "type": "CO2",
  "status": "ACTIVE",
  "roomId": 9999
}
```

### Try to add a reading to a MAINTENANCE sensor (Sensor 4) — expects 403 Forbidden
**POST** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/sensors/4/readings`
```json
{
  "value": 100.0
}
```

### Try to get a room that does not exist — expects 404 Not Found
**GET** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/rooms/9999`

### Trigger the test error endpoint — expects 500
**GET** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/test/error`

---

## Step 9 — Delete an empty room

First create a room with no sensors:

**POST** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/rooms`
```json
{
  "name": "Empty Room D999",
  "building": "Annex",
  "floor": "3",
  "description": "Temporary room with no sensors"
}
```

Then delete it (use the ID returned above, e.g. 4):

**DELETE** `http://localhost:8080/5COSC022W_SmartCampus_20231171/api/v1/rooms/4`

Should return 204 No Content.
