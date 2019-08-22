package org.hazelcast.iotdemo;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;

public final class AppConfiguration
{
    public static final String COORDINATES_MAP = "coords-map";
    public static final String VIOLATIONS_MAP = "violations-maps";
    public static final String AVERAGE_SPEED_MAP = "average-speed-map";
    public static final String DRIVER_ENRICHMENT_MAP = "driver-enrichment-map";
    public static final HazelcastInstance HAZELCAST_INSTANCE;
    public static final int SAMPLE_TICK_INTERVAL_MSEC = 5;
    public static final double POLICY_VIOLATION_THRESHOLD_MPH = 40.0;
    public static final JetInstance JET_INSTANCE;

    static {
        JET_INSTANCE = Jet.newJetInstance( );
        HAZELCAST_INSTANCE = JET_INSTANCE.getHazelcastInstance( );

        IMap<Integer, DriverProfile> driverMap =
                HAZELCAST_INSTANCE.getMap(DRIVER_ENRICHMENT_MAP);

        driverMap.put(32,
                new DriverProfile(27, "Male", "Porsche", "911 Turbo H6 FI",
                        2018));
        driverMap.put(26,
                new DriverProfile(44, "Female", "Dodge", "Grand Caravan 3.8L " +
                        "V6", 2003));
        driverMap.put(86,
                new DriverProfile(31, "Male", "Acura", "TSX 2.4L L4 Turbo",
                        2004));
        driverMap.put(37,
                new DriverProfile(56, "Male", "Toyota", "Prius 1.5L Hybrid",
                        2009));
        driverMap.put(20,
                new DriverProfile(67, "Female", "Toyota", "Camry XLE 2.4L L4",
                        2017));
        driverMap.put(14,
                new DriverProfile(49, "Male", "Ford", "Ranger Super Cab 3.0L " +
                        "V6", 1997));
        driverMap.put(15,
                new DriverProfile(19, "Female", "Honda", "Accord LX-i 2.0L " +
                        "L4 FI", 1989));
        driverMap.put(21,
                new DriverProfile(29, "Female", "Honda", "CR-V LX 2.4L",
                        2010));
        driverMap.put(27,
                new DriverProfile(33, "Female", "Honda", "Civic I4 1.3L Hybrid",
                        2007));
        driverMap.put(36,
                new DriverProfile(23, "Male", "Ford", "E-150 Econoline 4.6L V8",
                        2002));
    }
}
