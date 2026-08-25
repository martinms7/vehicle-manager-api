/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.swiftlyassess.vehicle_manager_springboot;

import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Needle
 */

@RestController
public class VehicleManagerController {
    
    @GetMapping("/myget")
    public String TestGet(){
        return "Hello Swiftly";
    }
}
