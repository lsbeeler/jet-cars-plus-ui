package org.hazelcast.iotdemo;


import com.google.gson.Gson;
import com.hazelcast.core.IMap;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


public class ViolationsMapDumper
{
    private static ExecutorService RUNNER =
            Executors.newFixedThreadPool(1);
    private static final Gson GSON = new Gson( );

    private static class DumperTask implements Runnable
    {
        private static final Logger LOG = Logger.getLogger("DumperTask");

        private final IMap<Integer, Long> map;

        public DumperTask(final IMap<Integer, Long> map)
        {
            this.map = map;
        }

        @Override
        public void run( )
        {
            LOG.info("DumperTask: thread started...");

            while (true) {
                Set<Integer> driverIds = map.keySet( );
                ArrayList<PolicyJSONPeer> peers = new ArrayList<>( );

                for (int driverId : driverIds) {
                    PolicyJSONPeer peer = new PolicyJSONPeer(driverId,
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
            final IMap<Integer, Long> coordsMap)
    {
        RUNNER.submit(new ViolationsMapDumper.DumperTask(coordsMap));
    }
}
