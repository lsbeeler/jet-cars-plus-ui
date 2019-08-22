package org.hazelcast.iotdemo;


import java.io.Serializable;


public class DriverProfile implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final int driverAge;
    private final String driverGender;
    private final String vehicleMake;
    private final String vehicleModel;
    private final int vehicleYear;

    public DriverProfile(int driverAge, String driverGender, String vehicleMake,
            String vehicleModel, int vehicleYear)
    {
        this.driverAge = driverAge;
        this.driverGender = driverGender;
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
        this.vehicleYear = vehicleYear;
    }

    public int getDriverAge( )
    {
        return driverAge;
    }

    public String getDriverGender( )
    {
        return driverGender;
    }

    public String getVehicleMake( )
    {
        return vehicleMake;
    }

    public String getVehicleModel( )
    {
        return vehicleModel;
    }

    public int getVehicleYear( )
    {
        return vehicleYear;
    }
}
