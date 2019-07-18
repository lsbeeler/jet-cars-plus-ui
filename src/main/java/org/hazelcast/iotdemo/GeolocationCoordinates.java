package org.hazelcast.iotdemo;


import java.io.Serializable;


public final class GeolocationCoordinates implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final double latitude;
    private final double longitude;

    public GeolocationCoordinates(DataPoint point)
    {
        this.latitude = point.getLatitude( );
        this.longitude = point.getLongitude( );
    }

    public GeolocationCoordinates(DataPointPolicyWrapper wrapper)
    {
        this(wrapper.getDataPoint( ));
    }

    @Override
    public String toString( )
    {
        return "GeolocationCoordinates{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
