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


public class GeolocationServlet extends HttpServlet
{
    private static final Gson GSON = new Gson( );

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        if (AppConfiguration.HAZELCAST_INSTANCE != null) {
            IMap<Integer, GeolocationCoordinates> coordsMap =
                    AppConfiguration.HAZELCAST_INSTANCE.getMap(
                            AppConfiguration.COORDINATES_MAP);

            Set<Integer> driverIds = coordsMap.keySet( );
            ArrayList<GeolocationJSONPeer> peers = new ArrayList<>( );

            for (int driverId : driverIds) {
                GeolocationJSONPeer peer = new GeolocationJSONPeer(driverId,
                        coordsMap.get(driverId));
                peers.add(peer);
            }

            String json = GSON.toJson(peers);

            resp.setContentType("application/json");
            resp.getWriter( ).println(json);
        }
    }
}
