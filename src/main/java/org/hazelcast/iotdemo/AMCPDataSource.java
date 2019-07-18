package org.hazelcast.iotdemo;


import com.hazelcast.jet.pipeline.SourceBuilder;
import com.hazelcast.jet.pipeline.StreamSource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;


public final class AMCPDataSource implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static final class AMCPParser implements Serializable
    {
        private static final long serialVersionUID = 1L;
        private static final long TIMESTAMP_SHIFT_PER_REPLAY = 15_000_000;

        private final List<String> lines;
        private int pos;
        private long intervalTimeMsec;
        private long timestampShiftMultipler = 0;
        private DataPoint lastDataPoint = null;

        public AMCPParser(List<String> csvLines, long intervalTimeMsec)
        {
            this.lines = csvLines;
            this.pos = 0;
            this.intervalTimeMsec = intervalTimeMsec;
        }

        public void fillBuffer(SourceBuilder.TimestampedSourceBuffer<DataPoint>
                buffer)
        {
            // The three lines below enable "auto-replay" functionality: they
            // allow the simulation to run forever by resetting all vehicles
            // to their starting positions and beginning the simulation again.
            // If this functionality is not desired, remove these lines.
            if (pos == lines.size( )) {
                pos = 0;
                timestampShiftMultipler++;
            }

            DataPoint d = new DataPoint(lines.get(pos));

            // One issue with the AMCP data set is that we don't actually care
            // about all of the features in the data set -- we only care about
            // driver id, speed, and position. The AMCP data set contains many
            // lines, however that capture features other than the ones we
            // care about at the same time points, and sometimes two
            // subsequent lines can produce an equal data point from our
            // perspective, even though they're distinct from the AMCP
            // perspective. We deal with this problem by only adding points to
            // the buffer that our distinct from the previous data point
            if (!d.equals(lastDataPoint)) {
                long timestamp = d.getMessageTime( ) + timestampShiftMultipler *
                        TIMESTAMP_SHIFT_PER_REPLAY;
                buffer.add(d, timestamp);
                lastDataPoint = d;
            }

            pos++;

            LockSupport.parkNanos(
                    TimeUnit.MILLISECONDS.toNanos(intervalTimeMsec));
        }
    }

    public static StreamSource<DataPoint> createSource(String csvPath,
            boolean csvHasHeaderRow, long intervalTimeMsec) throws IOException
    {
        BufferedReader csvReader = new BufferedReader(
                new FileReader(csvPath));

        ArrayList<String> lines = new ArrayList<>( );
        if (csvHasHeaderRow)
            csvReader.readLine( );
        String line = csvReader.readLine( );
        while (line != null) {
            lines.add(line);
            line = csvReader.readLine( );
        }

        return SourceBuilder.timestampedStream("amcp-data-source",
                context -> new AMCPParser(lines, intervalTimeMsec))
                .fillBufferFn(AMCPParser::fillBuffer)
                .build( );
    }
}
