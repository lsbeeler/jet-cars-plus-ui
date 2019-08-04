package org.hazelcast.iotdemo;


import com.google.gson.Gson;
import com.hazelcast.core.IMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;


public class AverageSpeedServlet extends HttpServlet
{
    private static final Gson GSON = new Gson( );

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        if (AppConfiguration.HAZELCAST_INSTANCE != null) {
            IMap<Integer, Double> avgSpeedMap =
                    AppConfiguration.HAZELCAST_INSTANCE.getMap(
                            AppConfiguration.AVERAGE_SPEED_MAP);

            Set<Integer> driverIds = avgSpeedMap.keySet( );

            ArrayList<AverageSpeedJSONPeer> peers = new ArrayList<>( );

            for (int driverId : driverIds) {
                AverageSpeedJSONPeer peer =
                        new AverageSpeedJSONPeer("Driver " + driverId,
                        avgSpeedMap.get(driverId));
                peers.add(peer);
            }

            String json = GSON.toJson(peers);

            resp.setContentType("application/json");
            resp.getWriter( ).println(json);
        }
    }
}
