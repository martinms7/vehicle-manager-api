/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.swiftlyassess.vehicle_manager_springboot.DTOs;

/**
 *
 * @author Needle
 */
public class VehicleDto {
    private String vehicleId;
    private String name;
    private String vehicleType;
    private int agencyId;
    private String agencyName;
    private int seatingCapacity;

    public VehicleDto(){
    }
    
    public VehicleDto(int agencyId){
        this.agencyId = agencyId;
    }
    public VehicleDto(int agencyId, String vehicleId, String name, String vehicleType,int seatingCapacity) {
        this(agencyId);
        this.vehicleId = vehicleId;
        this.name = name;
        this.vehicleType = vehicleType;
        this.seatingCapacity = seatingCapacity;
    }

    public VehicleDto(int agencyId, String agencyName, String vehicleId, String name,
            String vehicleType, int seatingCapacity) {
        this(agencyId, vehicleId, name, vehicleType, seatingCapacity);
        this.agencyName = agencyName;
    }
    
    

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setAgencyId(int agencyId) {
        this.agencyId = agencyId;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public void setSeatingCapacity(int seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getName() {
        return name;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public int getAgencyId() {
        return agencyId;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public int getSeatingCapacity() {
        return seatingCapacity;
    }
    
}
