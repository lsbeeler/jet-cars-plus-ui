package org.hazelcast.iotdemo;


import com.hazelcast.core.IMap;


public class PolicyJSONPeer
{
    private final String driverId;
    private final long violationCount;
    private final int driverAge;
    private final String driverGender;
    private final String vehicleMake;
    private final String vehicleModel;
    private final int vehicleYear;

    public PolicyJSONPeer(int driverId, long violationCount)
    {
        this.driverId = "Driver " + driverId;
        this.violationCount = violationCount;

        IMap<Integer, DriverProfile> driverEnrichmentMap =
                AppConfiguration.HAZELCAST_INSTANCE.getMap(
                        AppConfiguration.DRIVER_ENRICHMENT_MAP);

        DriverProfile profile = driverEnrichmentMap.get(driverId);

        this.driverAge = profile.getDriverAge( );
        this.driverGender = profile.getDriverGender( );
        this.vehicleMake = profile.getVehicleMake( );
        this.vehicleModel = profile.getVehicleModel( );
        this.vehicleYear = profile.getVehicleYear( );
    }

    @Override
    public String toString( )
    {
        return "PolicyJSONPeer{" +
                "driverId='" + driverId + '\'' +
                ", violationCount=" + violationCount +
                ", driverAge=" + driverAge +
                ", driverGender='" + driverGender + '\'' +
                ", vehicleMake='" + vehicleMake + '\'' +
                ", vehicleModel='" + vehicleModel + '\'' +
                ", vehicleYear=" + vehicleYear +
                '}';
    }
}
