package org.hazelcast.iotdemo;


import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;


public class StartServlet extends HttpServlet
{
    private static final Logger LOG = Logger.getLogger("StartServlet");
    private static final Gson GSON = new Gson( );

    private String stringifyRequestBody(HttpServletRequest request)
    {
        StringBuilder resultBuilder = new StringBuilder( );
        try {
            BufferedReader reader = request.getReader( );
            String line = reader.readLine( );

            while (line != null) {
                resultBuilder.append(line);
                resultBuilder.append('\n');

                line = reader.readLine( );
            }
        } catch (IOException e) {
            LOG.severe("StartServlet: stringifyRequestBody( ) generated " +
                    "IOException: " + e.getMessage( ));
        }

        return resultBuilder.toString( );
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        Action postedAction = GSON.fromJson(req.getReader( ), Action.class);

        if (postedAction != null &&
                postedAction.getActionId( ).equalsIgnoreCase("START")) {
            String inputCSVPath = this.getServletContext( ).getRealPath(
                    "WEB-INF/resources/AMCP-Probe-Data.csv");
            JetLauncher.launch(inputCSVPath);
        }
    }

    @Override
    public void init( ) throws ServletException
    {
        LOG.info("initialized StartServlet");
        super.init( );
    }
}
