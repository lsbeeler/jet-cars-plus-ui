package org.hazelcast.iotdemo;


import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;


/**
 * A RESTful HTTP servlet that accepts HTTP POST messages to its endpoint and,
 * upon receipt of such a message, starts running the Jet pipeline. This
 * servlet's endpoint should be hit by the "Start" button in the frontend web
 * UI. Note that this endpoint only accepts one payload in its POST request
 * body: it must be a JSON object that specifies an actionId of "START", i.e.,
 * the entirety of the POST message body should be "{ "actionId": "START" }".
 */
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

        if (postedAction.equals(Action.START)) {
            resp.setStatus(200);

            String inputCSVPath = this.getServletContext( ).getRealPath(
                    "WEB-INF/resources/AMCP-Probe-Data.csv");
            JetLauncher.launch(inputCSVPath);
        } else {
            resp.setStatus(400);
            resp.getWriter( ).println("Unrecognized request: " +
                    stringifyRequestBody(req));
        }
    }

    @Override
    public void init( ) throws ServletException
    {
        LOG.info("initialized StartServlet");
        super.init( );
    }
}
