package org.hazelcast.iotdemo;


public class DataPointPolicyWrapper
{
    private static final double POLICY_VIOLATION_THRESHOLD_MPH = 45.0;

    private final DataPoint dataPoint;
    private final boolean policyViolation;

    public DataPointPolicyWrapper(DataPoint dataPoint)
    {
        this.dataPoint = dataPoint;
        this.policyViolation = dataPoint.getSpeed( ) >
                POLICY_VIOLATION_THRESHOLD_MPH;
    }

    public DataPoint getDataPoint( )
    {
        return dataPoint;
    }

    public boolean isPolicyViolation( )
    {
        return policyViolation;
    }
}
