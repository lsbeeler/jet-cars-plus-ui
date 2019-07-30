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


public class PolicyServlet extends HttpServlet
{
    private static final Gson GSON = new Gson( );

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        if (AppConfiguration.HAZELCAST_INSTANCE != null) {
            IMap<Integer, Long> violationsMap =
                    AppConfiguration.HAZELCAST_INSTANCE.getMap(
                            AppConfiguration.VIOLATIONS_MAP);

            Set<Integer> driverIds = violationsMap.keySet( );
            ArrayList<PolicyJSONPeer> peers = new ArrayList<>( );

            for (int driverId : driverIds) {
                PolicyJSONPeer peer = new PolicyJSONPeer(driverId,
                        violationsMap.get(driverId));
                peers.add(peer);
            }

            String json = GSON.toJson(peers);

            resp.setContentType("application/json");
            resp.getWriter( ).println(json);
        }
    }
}
