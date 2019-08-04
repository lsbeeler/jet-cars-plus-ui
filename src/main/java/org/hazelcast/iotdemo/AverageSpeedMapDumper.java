package org.hazelcast.iotdemo;


import com.google.gson.Gson;
import com.hazelcast.core.IMap;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


public class AverageSpeedMapDumper
{
    private static ExecutorService RUNNER =
            Executors.newFixedThreadPool(1);
    private static final Gson GSON = new Gson( );

    private static class DumperTask implements Runnable
    {
        private static final Logger LOG = Logger.getLogger(
                "AverageSpeedMapDumper.DumperTask");

        private final IMap<Integer, Double> map;

        public DumperTask(final IMap<Integer, Double> map)
        {
            this.map = map;
        }

        @Override
        public void run( )
        {
            LOG.info("AverageSpeedMapDumper.DumperTask: thread started...");

            while (true) {
                Set<Integer> driverIds = map.keySet( );
                ArrayList<AverageSpeedJSONPeer> peers = new ArrayList<>( );

                for (int driverId : driverIds) {
                    AverageSpeedJSONPeer peer =
                            new AverageSpeedJSONPeer("Driver " + driverId,
                            map.get(driverId));
                    peers.add(peer);
                }

                String json = GSON.toJson(peers);
                LOG.info(json);

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // do nothing
                }
            }
        }
    }

    public static void start(
            final IMap<Integer, Double> speedMap)
    {
        RUNNER.submit(new AverageSpeedMapDumper.DumperTask(speedMap));
    }
}
