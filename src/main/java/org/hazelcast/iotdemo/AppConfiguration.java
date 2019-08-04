package org.hazelcast.iotdemo;


import com.hazelcast.core.HazelcastInstance;


public final class AppConfiguration
{
    public static final String COORDINATES_MAP = "coords-map";
    public static final String VIOLATIONS_MAP = "violations-maps";
    public static final String AVERAGE_SPEED_MAP = "average-speed-map";
    public static volatile HazelcastInstance HAZELCAST_INSTANCE = null;
    public static final int SAMPLE_TICK_INTERVAL_MSEC = 5;
    public static final double POLICY_VIOLATION_THRESHOLD_MPH = 40.0;
}
