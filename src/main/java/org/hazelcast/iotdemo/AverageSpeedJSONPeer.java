package org.hazelcast.iotdemo;


public class AverageSpeedJSONPeer
{
    private final String driverId;
    private final double averageSpeed;

    public AverageSpeedJSONPeer(String driverId, double averageSpeed)
    {
        this.driverId = driverId;
        this.averageSpeed = (averageSpeed < 1.0 || averageSpeed > 250.0) ?
                0.0 : averageSpeed;
    }
}
