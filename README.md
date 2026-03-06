# Smart Campus REST API (5COSC022W)

## 1. Project Overview
This project is a Java 8, Maven, Jersey (JAX-RS) REST API for Smart Campus management.
It provides in-memory management of:
- Rooms
- Sensors linked to rooms
- Historical sensor readings

It is designed for deployment as a WAR on Apache Tomcat and uses `javax.ws.rs` imports only.

## 2. API Design Overview
- Base URL prefix: `/api/v1`
- Discovery endpoint: `GET /api/v1`
- Primary collections:
  - `/api/v1/rooms`
  - `/api/v1/sensors`
- Deep nesting:
  - `/api/v1/sensors/{sensorId}/readings`

Key design points:
- Versioned API entry point with `@ApplicationPath("/api/v1")`
- Shared thread-safe in-memory store (`DataStore`)
- Sub-resource locator for readings under sensors
- Custom exception mapping to structured JSON errors
- Global exception safety net (`ExceptionMapper<Throwable>`)
- Request/response logging filter using `java.util.logging.Logger`

## 3. Build Instructions
1. Ensure Java 8 and Maven are installed and available on PATH.
2. Open terminal in project root.
3. Run:

```bash
mvn clean package
```

4. WAR output will be generated in:
- `target/5COSC022W_SmartCampus_20231171-1.0-SNAPSHOT.war`

## 4. Tomcat Deploy and Run
1. Copy WAR to Tomcat `webapps` folder.
2. Start Tomcat.
3. Access API using context path:

```text
http://localhost:8080/5COSC022W_SmartCampus_20231171-1.0-SNAPSHOT/api/v1
```

If you rename WAR to `smartcampus.war`, base becomes:

```text
http://localhost:8080/smartcampus/api/v1
```

## 5. Package Structure
- `com.mycompany.cosc022w_smartcampus_20231171.config`
  - API bootstrap (`RestApplication`)
- `com.mycompany.cosc022w_smartcampus_20231171.resource`
  - JAX-RS resource classes and sub-resource handling
- `com.mycompany.cosc022w_smartcampus_20231171.model`
  - POJOs and enum models
- `com.mycompany.cosc022w_smartcampus_20231171.store`
  - Shared in-memory storage and business rule enforcement
- `com.mycompany.cosc022w_smartcampus_20231171.exception`
  - Custom runtime exceptions
- `com.mycompany.cosc022w_smartcampus_20231171.mapper`
  - Exception-to-HTTP JSON error mapping
- `com.mycompany.cosc022w_smartcampus_20231171.filter`
  - Cross-cutting request/response logging

## 6. Example JSON Request Bodies

### Create Room
```json
{
  "name": "Lab 1",
  "building": "Engineering Block",
  "floor": "2",
  "description": "Embedded systems and IoT laboratory"
}
```

### Create Sensor
```json
{
  "name": "CO2 Sensor A",
  "type": "CO2",
  "status": "ACTIVE",
  "roomId": 1,
  "currentValue": 415.0
}
```

### Add Sensor Reading
```json
{
  "value": 421.7
}
```

## 7. Sample curl Commands
Replace `BASE` with your deployment URL, for example:
`http://localhost:8080/5COSC022W_SmartCampus_20231171-1.0-SNAPSHOT/api/v1`

1. Discovery:
```bash
curl -X GET "$BASE"
```

2. Create room:
```bash
curl -X POST "$BASE/rooms" -H "Content-Type: application/json" -d "{\"name\":\"Lab 1\",\"building\":\"Engineering Block\",\"floor\":\"2\",\"description\":\"Embedded systems lab\"}"
```

3. List rooms:
```bash
curl -X GET "$BASE/rooms"
```

4. Get room by id:
```bash
curl -X GET "$BASE/rooms/1"
```

5. Create sensor linked to room:
```bash
curl -X POST "$BASE/sensors" -H "Content-Type: application/json" -d "{\"name\":\"CO2 Sensor A\",\"type\":\"CO2\",\"status\":\"ACTIVE\",\"roomId\":1,\"currentValue\":415.0}"
```

6. List all sensors:
```bash
curl -X GET "$BASE/sensors"
```

7. Filter sensors by type:
```bash
curl -X GET "$BASE/sensors?type=CO2"
```

8. Add sensor reading:
```bash
curl -X POST "$BASE/sensors/1/readings" -H "Content-Type: application/json" -d "{\"value\":421.7}"
```

9. Get sensor reading history:
```bash
curl -X GET "$BASE/sensors/1/readings"
```

10. Delete room (returns 409 if sensors still linked):
```bash
curl -X DELETE "$BASE/rooms/1"
```

## 8. Postman Testing Guide
1. Create a collection named `Smart Campus API`.
2. Add environment variable `baseUrl` with your deployed base URL ending in `/api/v1`.
3. Add requests for discovery, rooms, sensors, and readings.
4. Set `Content-Type: application/json` for POST requests.
5. Run in this order:
   - Create room
   - Create sensor
   - Add reading
   - Retrieve readings
   - Attempt room delete (to observe 409 business-rule behavior)
6. Verify error responses are JSON and include:
   - `status`
   - `error`
   - `message`
   - `path`
   - `timestamp`

## 9. Coursework Theory Answers

### 9.1 JAX-RS default lifecycle and shared in-memory state
By default, resource classes are per-request (new instance per HTTP request). Shared mutable state should not be stored in resource instance fields. Instead, shared in-memory state is kept in a dedicated singleton-like store (`DataStore`) using thread-safe collections.

### 9.2 HATEOAS and hypermedia links
HATEOAS means responses provide navigable links to related resources. Hypermedia links reduce hardcoded client routing assumptions and improve discoverability, evolvability, and client decoupling.

### 9.3 Returning IDs vs full room objects
Returning IDs keeps payloads smaller and avoids object duplication. Returning full objects can reduce extra client round-trips. For this coursework, sensors store `roomId`, which is simple and efficient.

### 9.4 Is DELETE idempotent?
Yes. DELETE is idempotent because repeating the same delete request should not create additional side effects after the first successful deletion.

### 9.5 @Consumes(JSON) with wrong content type
If a different content type is sent, JAX-RS will typically return `415 Unsupported Media Type` because no suitable message body reader matches the declared media type.

### 9.6 Why query filtering is better than path-embedded filters
Query parameters are the standard way to represent optional filters (`/sensors?type=CO2`). They keep URI structure stable and resource-oriented while allowing flexible combinations of filter criteria.

### 9.7 Benefits of Sub-Resource Locator
Sub-resource locators model hierarchical resources clearly (sensor -> readings), improve separation of concerns, and keep parent resource classes smaller and easier to maintain.

### 9.8 Why HTTP 422 can be better than 404 for missing linked references
`422 Unprocessable Entity` is accurate when the target endpoint exists and JSON is syntactically valid, but a semantic rule fails (for example, `roomId` points to a non-existent room). `404` is better for missing primary endpoint resources.

### 9.9 Cybersecurity risk of exposing Java stack traces
Stack traces leak internal class names, packages, and implementation details that can aid reconnaissance and targeted attacks. Production APIs should return safe generic errors and log internals server-side only.

### 9.10 Why filters for cross-cutting logging
Filters apply centrally to all requests/responses, avoiding duplicated logging in each endpoint method and preserving separation of concerns.

## 10. Notes on `web.xml`
This project uses annotation-driven JAX-RS bootstrap through:
- `RestApplication extends javax.ws.rs.core.Application`
- `@ApplicationPath("/api/v1")`

So an explicit Jersey servlet mapping in `web.xml` is not required. A minimal `web.xml` is included for Tomcat compatibility and project clarity.
