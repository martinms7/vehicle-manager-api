/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.swiftlyassess.vehicle_manager_springboot.Models;

/**
 *
 * @author Needle
 */
public enum VehicleTypes {
    
      BUS("bus"),
      TRAIN("train"),
      STREETCAR("streetcar"),
      FERRY("ferry");

      final private String vehicleType;
      
    VehicleTypes(String type) {
        this.vehicleType = type;
    }
    
    public String getVehicleType(){
        return vehicleType;
    }
    
}
