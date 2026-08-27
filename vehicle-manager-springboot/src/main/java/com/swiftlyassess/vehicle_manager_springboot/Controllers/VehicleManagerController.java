/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.swiftlyassess.vehicle_manager_springboot.Controllers;

import com.swiftlyassess.vehicle_manager_springboot.DTOs.VehicleDto;
import com.swiftlyassess.vehicle_manager_springboot.Repositories.VehicleRepository;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final VehicleRepository vehicleRepository = VehicleRepository.getInstance();
    
    @GetMapping("/api/v1/vehicles")
    public ArrayList<VehicleDto> GetVehiclesById(@RequestParam int agencyId){
        return vehicleRepository.getVehicles();
    }

    @PostMapping("/api/v1/vehicles")
    public ArrayList<VehicleDto> AddVehicle(@RequestBody VehicleDto vehicle) {
        return vehicleRepository.addVehicle(vehicle);
    }

    @PutMapping("/api/v1/vehicles/{vehicleId}")
    public ArrayList<VehicleDto> UpdateVehicle(
            @PathVariable String vehicleId, @RequestBody VehicleDto vehicle) {
        return vehicleRepository.updateVehicle(vehicleId, vehicle);
    }

    @DeleteMapping("/api/v1/vehicles/{vehicleId}")
    public ArrayList<VehicleDto> DeleteVehicle(@PathVariable String vehicleId) {
        return vehicleRepository.deleteVehicle(vehicleId);
    }
}
