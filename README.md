# vehicle-manager-api
Swiftly Vehicle Manager API

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
