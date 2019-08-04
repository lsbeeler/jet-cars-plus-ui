package org.hazelcast.iotdemo;


public class DataPointPolicyWrapper
{
    private final DataPoint dataPoint;
    private final boolean policyViolation;

    public DataPointPolicyWrapper(DataPoint dataPoint)
    {
        this.dataPoint = dataPoint;
        this.policyViolation = dataPoint.getSpeed( ) >
                AppConfiguration.POLICY_VIOLATION_THRESHOLD_MPH;
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
