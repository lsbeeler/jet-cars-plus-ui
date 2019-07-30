package org.hazelcast.iotdemo;


public class PolicyJSONPeer
{
    private final String driverId;
    private final long violationCount;

    public PolicyJSONPeer(int driverId, long violationCount)
    {
        this.driverId = "Driver " + driverId;
        this.violationCount = violationCount;
    }

    @Override
    public String toString( )
    {
        return "PolicyJSONPeer{" +
                "driverId='" + driverId + '\'' +
                ", violationCount=" + violationCount +
                '}';
    }
}
