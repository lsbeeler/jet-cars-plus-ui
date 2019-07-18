package org.hazelcast.iotdemo;


import java.util.Map;


public final class GeolocationEntry
        implements Map.Entry<Integer, GeolocationCoordinates>
{
    private final int key;
    private final GeolocationCoordinates value;

    public GeolocationEntry(int key, GeolocationCoordinates value)
    {
        this.key = key;
        this.value = value;
    }

    @Override
    public Integer getKey( )
    {
        return key;
    }

    @Override
    public GeolocationCoordinates getValue( )
    {
        return value;
    }

    @Override
    public GeolocationCoordinates setValue(GeolocationCoordinates value)
    {
        throw new UnsupportedOperationException("GeolocationEntry instances " +
                "are immutable");
    }
}
