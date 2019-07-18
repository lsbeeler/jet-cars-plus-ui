package org.hazelcast.iotdemo;


import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;


public final class JetLauncher implements Serializable
{
    private static final Logger LOG = Logger.getLogger("JetLauncher");
    private static final String COORDINATES_MAP = "coords-map";

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
                    100);
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

        // We fork the stream into two forks, the violation detection fork,
        // which detects speed violations and stores the number of violations
        // per vehicle in an IMap, and the geolocation fork, which passes
        // all valid data points through whether they represent speed
        // violations or not such that all vehicle movements are reflected
        // on the map widget in the web dashboard
        StreamStage<DataPoint> violationDetectionFork = preforkStage;
        StreamStage<DataPoint> geolocationFork = preforkStage;

        // Handle the geolocation fork
        geolocationFork
                .map(pt -> new GeolocationEntry(pt.getDriverId( ),
                        new GeolocationCoordinates(pt)))
                .drainTo(Sinks.map(COORDINATES_MAP));

        //preforkStage.drainTo(Sinks.logger( ));

        JetInstance jet = Jet.newJetInstance( );

        //MapDumper.start(jet.getHazelcastInstance().getMap(COORDINATES_MAP));

        try {
            jet.newJob(p, configureJob( )).join( );
        } finally {
            jet.shutdown( );
        }
    }
}