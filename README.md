# vehicle-manager-api
Swiftly Vehicle Manager API

## Installation

### Prerequisites

- Java Development Kit 26
- Git

The project includes the Maven Wrapper, so Maven does not need to be installed
separately.

Clone the repository and move into the Spring Boot application directory:

```powershell
git clone <repository-url>
Set-Location vehicle-manager-api
Set-Location vehicle-manager-springboot
```

On Windows, configure `JAVA_HOME` if it is not already set:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-26.0.2.1'
```

Install dependencies and build the application:

```powershell
.\mvnw.cmd clean install
```

## Running the application

Start the API from `vehicle-manager-springboot`:

```powershell
.\mvnw.cmd spring-boot:run
```

The API is available at `http://localhost:8080`. SQLite creates the configured
database file and its parent directory during application startup.

To run the packaged application instead:

```powershell
java -jar target\vehicle-manager-springboot-0.0.1-SNAPSHOT.jar
```

## SQLite configuration

The Spring Boot API uses SQLite through the configurable property below:

```properties
vehicle.database.url=jdbc:sqlite:data/vehicles.db
```

Change the path after `jdbc:sqlite:` to use another database file. The parent
directory is created when the application starts. Database operations run
asynchronously on a single managed executor so SQLite writes are serialized.

The repository creates these tables at startup:

```sql
CREATE TABLE IF NOT EXISTS agency (
	agency_Id INTEGER PRIMARY KEY,
	name TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS vehicles (
	vehicle_id TEXT PRIMARY KEY,
	agency_id INTEGER NOT NULL REFERENCES agency(agency_Id),
	name TEXT NOT NULL,
	vehicle_type TEXT NOT NULL,
	seating_capacity INTEGER NOT NULL
);
```

The initial agencies are inserted with `INSERT OR IGNORE`:

```sql
INSERT OR IGNORE INTO agency (agency_Id, name) VALUES
	(1, 'Boston'),
	(2, 'NYC'),
	(3, 'Washington D.C.');
```

The initial vehicles are inserted with `INSERT OR IGNORE`:

```sql
INSERT OR IGNORE INTO vehicles
	(vehicle_id, agency_id, name, vehicle_type, seating_capacity)
VALUES
	('bos-303', 1, 'Boston 303', 'ferry', 1),
	('bos-304', 1, 'Boston 304', 'streetcar', 90),
	('bos-305', 1, 'Boston 305', 'train', 250);
```

## Vehicle endpoints

- `GET /api/v1/vehicles?agencyId=1` retrieves vehicles for an agency.
- `POST /api/v1/vehicles` adds a vehicle from a `VehicleDto` JSON body.
- `PUT /api/v1/vehicles/{vehicleId}` updates a vehicle from a `VehicleDto` JSON body.
- `DELETE /api/v1/vehicles/{vehicleId}` deletes a vehicle.

PUT and DELETE return `404 Not Found` when the vehicle ID does not exist.
Vehicle responses include both `agencyId` and the joined Agency table value as `agencyName`.

## Test scope

Run the test suite with:

```powershell
.\mvnw.cmd test
```

The current automated test verifies that the Spring Boot application context
loads successfully. This exercises application startup, component discovery,
repository construction, SQLite schema initialization, and seed initialization.

The test suite does not yet provide dedicated endpoint assertions, CRUD
assertions, agency-name mapping assertions, invalid-input coverage, or
concurrency tests. Those are the next testing targets as the API grows.

## Tech stack

- Java 26
- Spring Boot 4.1.1
- Spring MVC for REST endpoints
- SQLite for local persistence
- Xerial SQLite JDBC driver for database access
- Maven Wrapper for dependency management, builds, and tests
- JUnit and Spring Boot test support for automated testing

## Improvements

- Replace the `getAllVehicles()` full-table read after every write. The current
	implementation is not ideal at scale; return only the affected agency's
	vehicles or the persisted record where appropriate.
- Keep all list responses consistently scoped by `agencyId`, including the
	results returned after POST, PUT, and DELETE operations.
- Add database migrations instead of relying only on startup schema creation.
- Add request validation and clearer 4xx responses for duplicate IDs, missing
	required fields, and unknown agencies.
- Add filtering and sorting for enterprise-scale vehicle collections.
- Add dedicated repository, controller, and concurrent CRUD tests. These are
	currently omitted due to time and priority; the code is better suited to
	integration testing than isolated unit testing.
- Consider a connection pool or maintained connection if the
  application usage grows or the database footprint increases beyond a single
  local SQLite instance.

## AI tools used

GitHub Copilot in VS Code was used to:

- Inspect the existing Spring Boot project and identify the controller,
	repository, DTO, configuration, and test surfaces.
- Plan and implement the asynchronous SQLite repository, schema initialization,
	seed data, agency joins, and CRUD behavior.
- Draft and revise this README.
- Run Maven tests, then help diagnose a startup
	compilation issue in the repository initialization code.

No external AI coding tool was used for this project. All generated changes
were reviewed against the local source and validated with the available test
suite.

Most non-repository code was written by hand using NetBeans.
