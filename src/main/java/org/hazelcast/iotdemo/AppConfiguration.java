package org.hazelcast.iotdemo;


import com.hazelcast.core.HazelcastInstance;


public final class AppConfiguration
{
    public static final String COORDINATES_MAP = "coords-map";
    public static final String VIOLATIONS_MAP = "violations-maps";
    public static volatile HazelcastInstance HAZELCAST_INSTANCE = null;
    public static final int SAMPLE_TICK_INTERVAL_MSEC = 1000 / 15;
}
