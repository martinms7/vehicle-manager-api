/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.swiftlyassess.vehicle_manager_springboot.Controllers;

import com.swiftlyassess.vehicle_manager_springboot.DTOs.VehicleDto;
import com.swiftlyassess.vehicle_manager_springboot.Repositories.VehicleRepository;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Needle
 */

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class VehicleManagerController {
    private final VehicleRepository vehicleRepository;

    /** Creates a controller backed by the vehicle repository. */
    public VehicleManagerController(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /** Retrieves all vehicles belonging to the requested agency. */
    @GetMapping("/api/v1/vehicles")
    public CompletableFuture<ResponseEntity<ArrayList<VehicleDto>>> GetVehiclesById(
            @RequestParam int agencyId){
        return okOrNotFound(vehicleRepository.getVehicles(agencyId));
    }

    /** Adds a vehicle and returns the updated vehicle list. */
    @PostMapping("/api/v1/vehicles")
    public CompletableFuture<ResponseEntity<ArrayList<VehicleDto>>> AddVehicle(
            @RequestBody VehicleDto vehicle) {
        return okOrNotFound(vehicleRepository.addVehicle(vehicle));
    }

    /** Updates a vehicle by ID and returns the updated vehicle list. */
    @PutMapping("/api/v1/vehicles/{vehicleId}")
    public CompletableFuture<ResponseEntity<ArrayList<VehicleDto>>> UpdateVehicle(
            @PathVariable String vehicleId, @RequestBody VehicleDto vehicle) {
        return okOrNotFound(vehicleRepository.updateVehicle(vehicleId, vehicle));
    }

    /** Deletes a vehicle by ID and returns the updated vehicle list. */
    @DeleteMapping("/api/v1/vehicles/{vehicleId}")
    public CompletableFuture<ResponseEntity<ArrayList<VehicleDto>>> DeleteVehicle(
            @PathVariable String vehicleId) {
        return okOrNotFound(vehicleRepository.deleteVehicle(vehicleId));
    }

    // Converts repository results into successful or not-found HTTP responses.
    private CompletableFuture<ResponseEntity<ArrayList<VehicleDto>>> okOrNotFound(
            CompletableFuture<ArrayList<VehicleDto>> operation) {
        return operation.thenApply(ResponseEntity::ok)
                .exceptionally(exception -> {
                    Throwable cause = exception.getCause();
                    if (cause instanceof VehicleRepository.VehicleNotFoundException) {
                        return ResponseEntity.notFound().build();
                    }
                    throw new RuntimeException(cause);
                });
    }
}
