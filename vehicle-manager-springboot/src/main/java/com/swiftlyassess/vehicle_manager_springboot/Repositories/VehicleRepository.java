package com.swiftlyassess.vehicle_manager_springboot.Repositories;

import com.swiftlyassess.vehicle_manager_springboot.DTOs.VehicleDto;
import com.swiftlyassess.vehicle_manager_springboot.Models.VehicleTypes;
import jakarta.annotation.PreDestroy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class VehicleRepository {
    private static final String CREATE_AGENCY_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS agency (
                agency_Id INTEGER PRIMARY KEY,
                name TEXT NOT NULL UNIQUE
            )
            """;
    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS vehicles (
                vehicle_id TEXT PRIMARY KEY,
                agency_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                vehicle_type TEXT NOT NULL,
                seating_capacity INTEGER NOT NULL
            )
            """;
    private static final String INSERT_SQL = """
            INSERT OR IGNORE INTO vehicles
                (vehicle_id, agency_id, name, vehicle_type, seating_capacity)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String SELECT_COLUMNS = "v.vehicle_id, v.agency_id, v.name, "
            + "v.vehicle_type, v.seating_capacity, a.name AS agency_name";

    private final String databaseUrl;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    /** Creates the repository and initializes the configured SQLite database. */
    public VehicleRepository(@Value("${vehicle.database.url:jdbc:sqlite:data/vehicles.db}") String databaseUrl) {
        this.databaseUrl = databaseUrl;
        initializeDatabase();
    }

    /** Asynchronously retrieves vehicles for an agency. */
    public CompletableFuture<ArrayList<VehicleDto>> getVehicles(int agencyId) {
        return submit(() -> {
            ArrayList<VehicleDto> vehicles = new ArrayList<>();
                String sql = "SELECT " + SELECT_COLUMNS + " FROM vehicles v "
                    + "INNER JOIN agency a ON v.agency_id = a.agency_Id "
                    + "WHERE v.agency_id = ? ORDER BY v.vehicle_id";
            try (Connection connection = connection();
                    PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, agencyId);
                try (ResultSet results = statement.executeQuery()) {
                    while (results.next()) {
                        vehicles.add(toVehicle(results));
                    }
                }
            } catch (SQLException exception) {
                throw new DatabaseException("Unable to retrieve vehicles", exception);
            }
            return vehicles;
        });
    }

    /** Asynchronously inserts a vehicle and returns all stored vehicles. */
    public CompletableFuture<ArrayList<VehicleDto>> addVehicle(VehicleDto vehicle) {
        return submit(() -> {
            ensureAgencyExists(vehicle.getAgencyId());
            try (Connection connection = connection();
                    PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
                bindVehicle(statement, vehicle);
                statement.executeUpdate();
            } catch (SQLException exception) {
                throw new DatabaseException("Unable to add vehicle", exception);
            }
            return getAllVehicles();
        });
    }

    /** Asynchronously updates a vehicle by ID and returns all stored vehicles. */
    public CompletableFuture<ArrayList<VehicleDto>> updateVehicle(String vehicleId, VehicleDto vehicle) {
        return submit(() -> {
            ensureAgencyExists(vehicle.getAgencyId());
            String sql = "UPDATE vehicles SET agency_id = ?, name = ?, vehicle_type = ?, "
                    + "seating_capacity = ? WHERE vehicle_id = ?";
            try (Connection connection = connection();
                    PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, vehicle.getAgencyId());
                statement.setString(2, vehicle.getName());
                statement.setString(3, vehicle.getVehicleType());
                statement.setInt(4, vehicle.getSeatingCapacity());
                statement.setString(5, vehicleId);
                if (statement.executeUpdate() == 0) {
                    throw new VehicleNotFoundException(vehicleId);
                }
            } catch (SQLException exception) {
                throw new DatabaseException("Unable to update vehicle", exception);
            }
            return getAllVehicles();
        });
    }

    /** Asynchronously deletes a vehicle by ID and returns all stored vehicles. */
    public CompletableFuture<ArrayList<VehicleDto>> deleteVehicle(String vehicleId) {
        return submit(() -> {
            String sql = "DELETE FROM vehicles WHERE vehicle_id = ?";
            try (Connection connection = connection();
                    PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, vehicleId);
                if (statement.executeUpdate() == 0) {
                    throw new VehicleNotFoundException(vehicleId);
                }
            } catch (SQLException exception) {
                throw new DatabaseException("Unable to delete vehicle", exception);
            }
            return getAllVehicles();
        });
    }

    // Runs database work on the repository's serialized executor.
    private <T> CompletableFuture<T> submit(Supplier<T> operation) {
        return CompletableFuture.supplyAsync(operation, databaseExecutor);
    }

    // Creates the schema and inserts the initial vehicles before serving requests.
    private void initializeDatabase() {
        CompletableFuture.runAsync(() -> {
            try {
                createDatabaseDirectory();
                try (Connection connection = connection();
                        PreparedStatement createAgencyTable = connection.prepareStatement(CREATE_AGENCY_TABLE_SQL);
                        PreparedStatement createVehicleTable = connection.prepareStatement(CREATE_TABLE_SQL)) {
                    createAgencyTable.executeUpdate();
                    seedAgency(connection, 1, "Boston");
                    seedAgency(connection, 2, "NYC");
                    seedAgency(connection, 3, "Washington D.C.");
                    createVehicleTable.executeUpdate();
                    seed(connection, 1, "bos-303", "Boston 303", VehicleTypes.FERRY.getVehicleType(), 1);
                    seed(connection, 1, "bos-304", "Boston 304", VehicleTypes.STREETCAR.getVehicleType(), 90);
                    seed(connection, 1, "bos-305", "Boston 305", VehicleTypes.TRAIN.getVehicleType(), 250);
                }
            } catch (SQLException | java.io.IOException exception) {
                throw new DatabaseException("Unable to initialize vehicle database", exception);
            }
        }, databaseExecutor).join();
    }

    // Creates the parent directory for a file-backed SQLite database.
    private void createDatabaseDirectory() throws java.io.IOException {
        if (databaseUrl.startsWith("jdbc:sqlite:") && !databaseUrl.endsWith(":memory:")) {
            Path databasePath = Path.of(databaseUrl.substring("jdbc:sqlite:".length()));
            Path parent = databasePath.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        }
    }

    // Inserts one seed vehicle without duplicating an existing ID.
    private void seed(Connection connection, int agencyId, String vehicleId, String name,
            String vehicleType, int seatingCapacity) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setString(1, vehicleId);
            statement.setInt(2, agencyId);
            statement.setString(3, name);
            statement.setString(4, vehicleType);
            statement.setInt(5, seatingCapacity);
            statement.executeUpdate();
        }
    }

    // Inserts one agency without duplicating an existing ID.
    private void seedAgency(Connection connection, int agencyId, String name) throws SQLException {
        String sql = "INSERT OR IGNORE INTO agency (agency_Id, name) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, agencyId);
            statement.setString(2, name);
            statement.executeUpdate();
        }
    }

    // Reads every vehicle from the database in ID order. This is not ideal at scale.
    // All returns should be restricted by agencyId and full-stack logic should reflect this (later improvement)
    private ArrayList<VehicleDto> getAllVehicles() {
        ArrayList<VehicleDto> vehicles = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM vehicles v "
            + "INNER JOIN agency a ON v.agency_id = a.agency_Id ORDER BY v.vehicle_id";
        try (Connection connection = connection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                vehicles.add(toVehicle(results));
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to retrieve vehicles", exception);
        }
        return vehicles;
    }

    // Opens a new JDBC connection for one database operation.
    private Connection connection() throws SQLException {
        return DriverManager.getConnection(databaseUrl);
    }

    // Binds vehicle fields to an INSERT statement.
    private void bindVehicle(PreparedStatement statement, VehicleDto vehicle) throws SQLException {
        statement.setString(1, vehicle.getVehicleId());
        statement.setInt(2, vehicle.getAgencyId());
        statement.setString(3, vehicle.getName());
        statement.setString(4, vehicle.getVehicleType());
        statement.setInt(5, vehicle.getSeatingCapacity());
    }

    // Maps the current result-set row to a vehicle DTO.
    private VehicleDto toVehicle(ResultSet results) throws SQLException {
        return new VehicleDto(results.getInt("agency_id"), results.getString("agency_name"),
                results.getString("vehicle_id"),
                results.getString("name"), results.getString("vehicle_type"),
                results.getInt("seating_capacity"));
    }

    // Ensures a vehicle references an existing agency before writing it.
    private void ensureAgencyExists(int agencyId) {
        String sql = "SELECT 1 FROM agency WHERE agency_Id = ?";
        try (Connection connection = connection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, agencyId);
            try (ResultSet results = statement.executeQuery()) {
                if (!results.next()) {
                    throw new AgencyNotFoundException(agencyId);
                }
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Unable to validate agency", exception);
        }
    }

    /** Stops the database executor when the application shuts down. */
    @PreDestroy
    public void close() {
        databaseExecutor.shutdown();
    }

    public static class VehicleNotFoundException extends RuntimeException {
        public VehicleNotFoundException(String vehicleId) {
            super("Vehicle not found: " + vehicleId);
        }
    }

    public static class AgencyNotFoundException extends RuntimeException {
        public AgencyNotFoundException(int agencyId) {
            super("Agency not found: " + agencyId);
        }
    }

    private static class DatabaseException extends RuntimeException {
        private DatabaseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}