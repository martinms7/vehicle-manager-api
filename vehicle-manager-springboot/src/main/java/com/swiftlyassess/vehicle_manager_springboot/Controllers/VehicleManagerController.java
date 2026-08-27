/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.swiftlyassess.vehicle_manager_springboot.Controllers;

import com.swiftlyassess.vehicle_manager_springboot.DTOs.VehicleDto;
import com.swiftlyassess.vehicle_manager_springboot.Models.VehicleTypes;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Needle
 */

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class VehicleManagerController {
    
    @GetMapping("/api/v1/vehicles/{agencyId}")
    public ArrayList<VehicleDto> TestGet(@PathVariable int agencyId){
        VehicleDto returnValue = 
                new VehicleDto(agencyId, "bos-303", "Boston 303", VehicleTypes.FERRY.getVehicleType(), 1);
        ArrayList<VehicleDto> vehicles = new ArrayList<VehicleDto>();
        vehicles.add(returnValue);
        return vehicles;
    }
}
