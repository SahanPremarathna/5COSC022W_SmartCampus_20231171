# Smart Campus REST API : 5COSC022W Coursework

**Student ID:** 20231171  
**Module:** Client-Server Architectures  
**Technology:** Java 8, JAX-RS (Jersey), Apache Tomcat

---

## 1. API Design Overview

This project implements a RESTful API for managing rooms, sensors, and sensor readings across a smart university campus. The system is built entirely with JAX-RS (Jersey) and uses in-memory data structures : no database is used.

### Resource Hierarchy

```
GET  /api/v1                            Discovery endpoint
GET  /api/v1/rooms                      List all rooms
POST /api/v1/rooms                      Create a room
GET  /api/v1/rooms/{roomId}             Get a room by ID
DELETE /api/v1/rooms/{roomId}           Delete a room (blocked if sensors are assigned)

GET  /api/v1/sensors                    List all sensors (optional ?type= filter)
POST /api/v1/sensors                    Create a sensor (validates roomId exists)
GET  /api/v1/sensors/{sensorId}/readings        List readings for a sensor
POST /api/v1/sensors/{sensorId}/readings        Add a reading for a sensor
```

### Key Design Decisions

- Versioned API entry point via `@ApplicationPath("/api/v1")`
- Shared thread-safe in-memory store (`DataStore` singleton) using `ConcurrentHashMap` and `synchronized` write methods
- Sub-resource locator pattern for `/sensors/{id}/readings` delegated to `SensorReadingResource`
- All errors return a structured JSON body: `status`, `error`, `message`, `path`, `timestamp`
- Global exception safety net (`ExceptionMapper<Throwable>`) prevents any stack trace reaching the client
- Request and response logging via a JAX-RS `ContainerRequestFilter` / `ContainerResponseFilter`

### Package Structure

| Package | Purpose |
|---|---|
| `config` | JAX-RS bootstrap (`RestApplication`) |
| `resource` | Resource classes and sub-resource locator |
| `model` | POJOs: Room, Sensor, SensorReading, SensorStatus, ErrorResponse |
| `store` | Singleton in-memory store with business rule enforcement |
| `exception` | Custom runtime exceptions |
| `mapper` | Exception-to-HTTP JSON error mappers |
| `filter` | Cross-cutting request/response logging filter |

---

## 2. Build Instructions

### Prerequisites

- Java 8 (JDK)
- Apache Maven 3.6+
- Apache Tomcat 9 or 10

### Step 1 : Clone the repository

```bash
git clone https://github.com/<your-username>/5COSC022W_SmartCampus_20231171.git
cd 5COSC022W_SmartCampus_20231171
```

### Step 2 : Build the WAR file

```bash
mvn clean package
```

The WAR file will be created at:

```
target/5COSC022W_SmartCampus_20231171-1.0-SNAPSHOT.war
```

### Step 3 : Deploy to Tomcat

1. Copy the WAR file into your Tomcat `webapps` folder:

```bash
cp target/5COSC022W_SmartCampus_20231171-1.0-SNAPSHOT.war /path/to/tomcat/webapps/
```

2. Start Tomcat:

```bash
/path/to/tomcat/bin/startup.sh
```

On Windows:

```bash
\path\to\tomcat\bin\startup.bat
```

### Step 4 : Verify the server is running

Open a browser or send a GET request to:

```
http://localhost:8080/5COSC022W_SmartCampus_20231171-1.0-SNAPSHOT/api/v1
```

You should receive a JSON discovery response confirming the API is live.

> **Tip:** To use a shorter URL, rename the WAR to `smartcampus.war` before copying it to webapps. The base URL then becomes `http://localhost:8080/smartcampus/api/v1`.

---

## 3. Sample curl Commands

Set the base URL first to keep commands short:

```bash
BASE="http://localhost:8080/5COSC022W_SmartCampus_20231171-1.0-SNAPSHOT/api/v1"
```

### 1. Discovery endpoint

```bash
curl -X GET "$BASE"
```

### 2. Create a room

```bash
curl -X POST "$BASE/rooms" \
  -H "Content-Type: application/json" \
  -d '{"name":"Lab A101","building":"Engineering","floor":1,"description":"Smart sensor lab"}'
```

### 3. Get a room by ID

```bash
curl -X GET "$BASE/rooms/1"
```

### 4. Create a sensor linked to a room

```bash
curl -X POST "$BASE/sensors" \
  -H "Content-Type: application/json" \
  -d '{"name":"CO2 Sensor A","type":"CO2","status":"ACTIVE","roomId":1}'
```

### 5. Filter sensors by type

```bash
curl -X GET "$BASE/sensors?type=CO2"
```

### 6. Add a sensor reading

```bash
curl -X POST "$BASE/sensors/1/readings" \
  -H "Content-Type: application/json" \
  -d '{"value":412.5}'
```

### 7. Get reading history for a sensor

```bash
curl -X GET "$BASE/sensors/1/readings"
```

### 8. Attempt to delete a room that has sensors (triggers 409)

```bash
curl -X DELETE "$BASE/rooms/1"
```

### 9. Create a sensor with a non-existent roomId (triggers 422)

```bash
curl -X POST "$BASE/sensors" \
  -H "Content-Type: application/json" \
  -d '{"name":"Ghost Sensor","type":"CO2","roomId":9999}'
```

### 10. Post a reading to a MAINTENANCE sensor (triggers 403)

```bash
curl -X POST "$BASE/sensors/2/readings" \
  -H "Content-Type: application/json" \
  -d '{"value":100.0}'
```

---

## 4. Report : Answers to Coursework Questions

---

### Part 1.1 : JAX-RS Resource Lifecycle & Data Synchronization

By default, JAX-RS creates a brand new instance of each resource class for every incoming HTTP request. This is called per-request scope. It means no state is shared between requests through instance fields of the resource class itself, which avoids many concurrency bugs.

However, because multiple requests can arrive simultaneously, they all share the same in-memory data store. In this implementation, `DataStore` is a singleton : a single shared instance accessed by all resource instances. To prevent race conditions, the `DataStore` uses `ConcurrentHashMap` for its core collections, which allows safe concurrent reads. Write operations such as `createRoom`, `deleteRoom`, `createSensor`, and `addReading` are marked `synchronized`, ensuring only one thread can modify the data at a time. Without this synchronization, two simultaneous POST requests could generate the same ID or corrupt the sensor readings list, leading to data loss or inconsistent state.

---

### Part 1.2 : HATEOAS and Hypermedia

HATEOAS (Hypermedia as the Engine of Application State) means an API response includes links to related actions and resources, not just raw data. For example, the discovery endpoint returns the URLs for `/api/v1/rooms` and `/api/v1/sensors` directly in its response body.

This benefits client developers because they do not need to hardcode URLs or rely on external documentation that may become outdated. The client can navigate the entire API dynamically by following links from the root. If the server changes a path, the client adapts automatically. Static documentation requires manual updates and forces tight coupling between the client and server, which makes both harder to evolve independently.

---

### Part 2.1 : Returning IDs vs Full Room Objects

Returning only IDs in a list response forces the client to make a separate GET request for each room to retrieve its details. For a campus with hundreds of rooms, this creates a large number of round trips, significantly increasing network overhead and latency. This pattern is sometimes called the N+1 problem.

Returning full room objects in the list response costs more bandwidth per response but gives the client everything it needs in a single call. For most use cases : such as displaying a room directory : this is the better trade-off. If bandwidth is genuinely a concern for very large collections, pagination or sparse fieldsets can be introduced, but for this system the full object approach is appropriate.

---

### Part 2.2 : Is DELETE Idempotent?

Yes, the DELETE operation is idempotent in this implementation. Idempotency means that making the same request multiple times produces the same result as making it once.

If a client sends `DELETE /rooms/5` and the room exists and has no sensors, it is deleted and the server returns 204 No Content. If the same request is sent again, the room no longer exists and the server returns 404 Not Found. The outcome is the same in terms of system state : room 5 does not exist after either call. The HTTP status code differs between the first and second call, but the resource state is identical, which satisfies the definition of idempotency. This is important because clients may retry DELETE requests due to network timeouts without needing to worry about accidentally deleting something twice.

---

### Part 3.1 : @Consumes and Content-Type Mismatch

The `@Consumes(MediaType.APPLICATION_JSON)` annotation tells JAX-RS that the POST endpoint only accepts requests with a `Content-Type: application/json` header. If a client sends a request with `Content-Type: text/plain` or `Content-Type: application/xml`, JAX-RS cannot find a matching resource method and immediately returns a `415 Unsupported Media Type` response. The method body is never executed : JAX-RS handles the rejection before the request reaches the application code. This protects the API from unexpected input formats and makes the contract between client and server explicit.

---

### Part 3.2 : @QueryParam vs Path Segment for Filtering

Using a query parameter (`GET /sensors?type=CO2`) is superior to embedding the filter in the path (`GET /sensors/type/CO2`) for several reasons.

Firstly, the path `/api/v1/sensors` represents the sensors collection as a resource. A query parameter narrows that collection without implying a different resource exists. `/sensors/type/CO2` implies `type` is a sub-resource of sensors, which is semantically incorrect.

Secondly, query parameters are optional by nature. `GET /sensors` returns all sensors, `GET /sensors?type=CO2` returns a filtered subset : this is natural and composable. Path segments are mandatory, which makes it awkward to express "no filter".

Thirdly, multiple filters can be combined easily with query parameters (`?type=CO2&status=ACTIVE`), whereas encoding multiple filters in the path quickly becomes unreadable and non-standard.

---

### Part 4.1 : Benefits of the Sub-Resource Locator Pattern

The sub-resource locator pattern delegates responsibility for a nested URL path to a dedicated class. In this implementation, `SensorResource` handles `/sensors` and routes `/{sensorId}/readings` to `SensorReadingResource`, which handles all reading-related operations independently.

The key benefit is separation of concerns. Each class has one clear responsibility. `SensorResource` manages the sensor collection; `SensorReadingResource` manages reading history for a specific sensor. If both were combined into one class, that class would need to handle sensor creation, sensor filtering, reading history, and reading creation : growing into an unmanageable controller.

It also allows the locator to perform validation before delegating. In this implementation, the locator verifies the sensor exists and returns 404 if not, before `SensorReadingResource` is even instantiated. This means the sub-resource class can assume its parent always exists and focus purely on its own logic. In large APIs with deep nesting, this pattern keeps each layer focused, testable, and independently maintainable.

---

### Part 5.2 : Why HTTP 422 is More Accurate than 404

A 404 Not Found means the requested URL does not exist on the server. When a client posts a new sensor with a `roomId` that does not exist, the URL `/api/v1/sensors` is perfectly valid : the problem is not with the endpoint but with the content of the request body.

HTTP 422 Unprocessable Entity means the server understood the request format and parsed the JSON correctly, but the semantic content of the payload is invalid : in this case, it references a room that does not exist. This is a more precise and honest status code because it tells the client exactly what went wrong: the data was structurally correct but logically invalid. A 404 would mislead the client into thinking it called the wrong URL, making debugging unnecessarily difficult.

---

### Part 5.4 : Security Risks of Exposing Java Stack Traces

Exposing a Java stack trace in an API response gives an attacker a detailed map of the application's internals. Specifically, they can gather:

- **Class and package names** : revealing the framework, library versions, and internal structure of the codebase, which can be matched against known CVEs.
- **File names and line numbers** : pinpointing exactly where in the source code an error occurred, making targeted attacks easier.
- **Library versions** : from fully qualified class names (e.g., `org.glassfish.jersey`) an attacker can identify the exact dependency versions in use and look up published vulnerabilities for those versions.
- **Application logic** : the call stack reveals the flow of execution, which can expose business logic, authentication paths, or data access patterns.

In this implementation, `GlobalExceptionMapper` logs the full stack trace server-side using `java.util.logging.Logger` so developers can debug it, but the client only ever receives a generic message: *"An unexpected server error occurred. Please contact the administrator."* This ensures no internal information leaks through the API.

---

### Part 5.5 : Why JAX-RS Filters Are Better than Manual Logging

Using a JAX-RS filter that implements `ContainerRequestFilter` and `ContainerResponseFilter` centralises logging in one place. Every request and response passes through the filter automatically, regardless of which resource method handles it.

If logging were added manually inside each resource method, it would need to be duplicated across every class and method. This violates the DRY (Don't Repeat Yourself) principle and creates maintenance problems : if the log format needs to change, every resource class must be updated. It also means any new endpoint added in the future could easily miss logging if the developer forgets to add it.

Filters implement cross-cutting concerns : behaviour that applies uniformly across the entire API : at the framework level rather than the application level. This makes the codebase cleaner, the logging consistent, and the resource classes focused purely on their business logic.
