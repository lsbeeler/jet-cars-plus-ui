package org.hazelcast.iotdemo;


import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Job;
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

        preforkStage.drainTo(Sinks.logger( ));

        JetInstance jet = Jet.newJetInstance( );

        try {
            jet.newJob(p, configureJob( )).join( );
        } finally {
            jet.shutdown( );
        }
    }
}
