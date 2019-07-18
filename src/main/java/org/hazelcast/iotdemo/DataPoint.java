package org.hazelcast.iotdemo;

import java.io.Serializable;
import java.util.Objects;


public final class DataPoint implements Serializable
{
    private int driverId;
    private long messageTime;
    private boolean valid;
    private double speed;
    private double longitude;
    private double latitude;

    private static double metersPerSecondToMph(final double mps)
    {
        return mps * 2.237;
    }

    public DataPoint(String csvRow)
    {
        String[ ] features = csvRow.split(",");

        try {
            this.driverId = Integer.parseInt(features[1]);
            this.messageTime = (long) (Double.parseDouble(features[2]));
            // Speed in the raw data is measured in meters per second, but
            // policy violations are detected in the more idiomatic (at least
            // in the USA) miles per hour, so convert.
            this.speed = metersPerSecondToMph(features[8].isEmpty( ) ? 0.0 :
                    Double.parseDouble(features[8]));
            this.latitude = Double.parseDouble(features[5]);
            this.longitude = Double.parseDouble(features[6]);

            valid = true;
        } catch (Throwable t) {
            // Catch a NumberFormatException or any other unchecked exception
            // which might be thrown during data parsing (the data isn't
            // guaranteed to be clean). If such an exception is generated,
            // mark this DataPoint invalid. Invalid DataPoint instances
            // will be filtered out.
            valid = false;
        }
    }

    public boolean isValid( )
    {
        return valid;
    }

    public long getMessageTime( )
    {
        return messageTime;
    }

    public int getDriverId( )
    {
        return driverId;
    }

    public double getSpeed( )
    {
        return speed;
    }

    public double getLongitude( )
    {
        return longitude;
    }

    public double getLatitude( )
    {
        return latitude;
    }

    @Override
    public String toString( )
    {
        return "DataPoint{" +
                "driverId=" + driverId +
                ", messageTime=" + messageTime +
                ", valid=" + valid +
                ", speed=" + speed +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass( ) != o.getClass( )) return false;
        DataPoint dataPoint = (DataPoint) o;
        return driverId == dataPoint.driverId &&
                messageTime == dataPoint.messageTime &&
                valid == dataPoint.valid &&
                Double.compare(dataPoint.speed, speed) == 0 &&
                Double.compare(dataPoint.longitude, longitude) == 0 &&
                Double.compare(dataPoint.latitude, latitude) == 0;
    }

    @Override
    public int hashCode( )
    {
        return Objects.hash(driverId, messageTime, valid, speed,
                longitude, latitude);
    }
}
