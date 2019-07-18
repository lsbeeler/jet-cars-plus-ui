package org.hazelcast.iotdemo;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


public class DemoServlet extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        resp.setContentType("text/html");

        PrintWriter writer = resp.getWriter( );

        writer.println("<html>");
        writer.println("<body>");
        writer.println("<h1>Pubes!</h1>");
        writer.println("</body>");
        writer.println("</html>");

        writer.flush( );
    }
}
