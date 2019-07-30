package org.hazelcast.iotdemo;


import com.google.gson.Gson;
import com.hazelcast.core.IMap;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


public final class GeolocationMapDumper<K, V>
{
    private static ExecutorService RUNNER =
            Executors.newFixedThreadPool(1);
    private static final Gson GSON = new Gson( );

    private static class DumperTask implements Runnable
    {
        private static final Logger LOG = Logger.getLogger("DumperTask");

        private final IMap<Integer, GeolocationCoordinates> map;

        public DumperTask(final IMap<Integer, GeolocationCoordinates> map)
        {
            this.map = map;
        }

        @Override
        public void run( )
        {
            LOG.info("DumperTask: thread started...");

            while (true) {
                Set<Integer> driverIds = map.keySet( );
                ArrayList<GeolocationJSONPeer> peers = new ArrayList<>( );

                for (int driverId : driverIds) {
                    GeolocationJSONPeer peer = new GeolocationJSONPeer(driverId,
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
            final IMap<Integer, GeolocationCoordinates> coordsMap)
    {
        RUNNER.submit(new DumperTask(coordsMap));
    }
}
