package org.hazelcast.iotdemo;


public class GeolocationJSONPeer
{
    private int driverId;
    private double latitude;
    private double longitude;

    public GeolocationJSONPeer(int driverId, GeolocationCoordinates coords)
    {
        this.driverId = driverId;
        this.latitude = coords.getLatitude( );
        this.longitude = coords.getLongitude( );
    }

    public GeolocationJSONPeer( )
    {
        this.driverId = -1;
        this.latitude = 0.0;
        this.longitude = 0.0;
    }

    @Override
    public String toString( )
    {
        return "GeolocationJSONPeer{" +
                "driverId=" + driverId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
