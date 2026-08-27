package com.swiftlyassess.vehicle_manager_springboot.Repositories;

import com.swiftlyassess.vehicle_manager_springboot.DTOs.VehicleDto;
import com.swiftlyassess.vehicle_manager_springboot.Models.VehicleTypes;
import java.util.ArrayList;

public class VehicleRepository {
    private static final VehicleRepository INSTANCE = new VehicleRepository();
    private final ArrayList<VehicleDto> vehicles = new ArrayList<>();

    private VehicleRepository() {
        vehicles.add(new VehicleDto(1, "bos-303", "Boston 303", VehicleTypes.FERRY.getVehicleType(), 1));
        vehicles.add(new VehicleDto(1, "bos-304", "Boston 304", VehicleTypes.STREETCAR.getVehicleType(), 90));
        vehicles.add(new VehicleDto(1, "bos-305", "Boston 305", VehicleTypes.TRAIN.getVehicleType(), 250));
    }

    public static VehicleRepository getInstance() {
        return INSTANCE;
    }

    public ArrayList<VehicleDto> getVehicles() {
        return vehicles;
    }

    public ArrayList<VehicleDto> addVehicle(VehicleDto vehicle) {
        vehicles.add(vehicle);
        return vehicles;
    }

    public ArrayList<VehicleDto> updateVehicle(String vehicleId, VehicleDto vehicle) {
        for (int index = 0; index < vehicles.size(); index++) {
            if (vehicleId.equals(vehicles.get(index).getVehicleId())) {
                vehicle.setVehicleId(vehicleId);
                vehicles.set(index, vehicle);
                break;
            }
        }
        return vehicles;
    }

    public ArrayList<VehicleDto> deleteVehicle(String vehicleId) {
        vehicles.removeIf(vehicle -> vehicleId.equals(vehicle.getVehicleId()));
        return vehicles;
    }
}