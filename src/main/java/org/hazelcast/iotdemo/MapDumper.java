package org.hazelcast.iotdemo;


import com.hazelcast.core.IMap;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


public class MapDumper
{
    private static ExecutorService RUNNER =
            Executors.newFixedThreadPool(32);

    private static class DumperTask implements Runnable
    {
        private static final Logger LOGGER = Logger.getLogger("DumperTask");

        private final IMap<Integer, Long> map;

        public DumperTask(final IMap<Integer, Long> map)
        {
            this.map = map;
        }

        @Override
        public void run( )
        {
            LOGGER.info("DumperTask: thread started...");

            while (true) {
                Set<Integer> driverIds = map.keySet( );
                for (Integer driverId : driverIds)
                    LOGGER.info(
                            "Entry: (key = " + driverId + ", value" +
                                    " = " + map.get(driverId) + ")" +
                                    ".");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // do nothing
                }
            }
        }
    }

    public static void start(final IMap<Integer, Long> violationsMap)
    {
        RUNNER.submit(new DumperTask(violationsMap));
    }
}
