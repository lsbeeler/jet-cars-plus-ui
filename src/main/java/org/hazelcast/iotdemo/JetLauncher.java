package org.hazelcast.iotdemo;


import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.aggregate.AggregateOperations;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;


public final class JetLauncher implements Serializable
{
    private static final Logger LOG = Logger.getLogger("JetLauncher");

    public static final long serialVersionUID = 1L;

    private JetLauncher( )
    {
    }

    private static JobConfig configureJob( )
    {
        JobConfig result = new JobConfig( );

        result.addClass(JetLauncher.class);
        result.addClass(DataPoint.class);
        result.addClass(AMCPDataSource.class);
        result.addClass(AMCPDataSource.AMCPParser.class);
        result.addClass(DataPointPolicyWrapper.class);
        result.addClass(GeolocationCoordinates.class);
        result.addClass(GeolocationEntry.class);

        return result;
    }

    private static StreamSource<DataPoint> createSource(String inputCSVPath)
    {
        StreamSource<DataPoint> amcpDataSource = null;

        try {
            amcpDataSource = AMCPDataSource.createSource(inputCSVPath, true,
                    AppConfiguration.SAMPLE_TICK_INTERVAL_MSEC);
        } catch (IOException e) {
            LOG.severe("JetLauncher: createSource( ): unable to read AMCP " +
                    "input data CSV file at " + inputCSVPath + ": " +
                    e.getMessage( ));
        }

        return amcpDataSource;
    }

    public static void launch(String inputCSVPath)
    {
        StreamSource<DataPoint> amcpDataSource = createSource(inputCSVPath);

        Pipeline p = Pipeline.create( );

        StreamStage<DataPoint> preforkStage = p
                .drawFrom(amcpDataSource)
                .withNativeTimestamps(5000)
                .filter(DataPoint::isValid);

        // We fork the initial stream into three forks, the violation detection
        // fork, the geolocation fork, and the average speed fork. The
        // violations detection fork detects speed violations and stores the
        // number of violations per vehicle in an IMap. The geolocation fork
        // extracts the latitudes and longitude coordinates of each vehicle
        // from the underlying DataPoint and places them in a map where a
        // RESTful servlet can feed them to the client UI. The average speed
        // fork computes a rolling average of vehicle speed over a 30-second
        // window and places this information in a map where a servlet makes
        // it available to the client UI to draw a side-scrolling, time-
        // series widget.
        StreamStage<DataPoint> violationDetectionFork = preforkStage;
        StreamStage<DataPoint> geolocationFork = preforkStage;
        StreamStage<DataPoint> averageSpeedFork = preforkStage;


        // Handle the geolocation fork
        geolocationFork
                .map(pt -> new GeolocationEntry(pt.getDriverId( ),
                        new GeolocationCoordinates(pt)))
                .drainTo(Sinks.map(AppConfiguration.COORDINATES_MAP));

        // Handle the policy violation detection fork
        violationDetectionFork
                .map(DataPointPolicyWrapper::new)
                .filter(DataPointPolicyWrapper::isPolicyViolation)
                .groupingKey(wrapper -> wrapper.getDataPoint( ).getDriverId( ))
                .rollingAggregate(AggregateOperations.counting( ))
                .drainTo(Sinks.map(AppConfiguration.VIOLATIONS_MAP));

        // Handle the average speed fork
        averageSpeedFork
                .groupingKey(DataPoint::getDriverId)
                .window(WindowDefinition.sliding(30000, 3000))
                .aggregate(AggregateOperations.averagingDouble(
                        DataPoint::getSpeed))
                .drainTo(Sinks.map(AppConfiguration.AVERAGE_SPEED_MAP));

        try {
            AppConfiguration.JET_INSTANCE.newJob(p, configureJob( )).join( );
        } finally {
            AppConfiguration.JET_INSTANCE.shutdown( );
        }
    }
}
