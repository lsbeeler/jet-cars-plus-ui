package org.hazelcast.iotdemo;


import java.util.Objects;


/**
 * Represents an application control action. In the future, actions might be
 * included to stop and pause the Jet pipeline, but for right now, only one
 * action (the "START" action that launches the Jet pipeline) is supported.
 */
public final class Action
{
    public static final Action START = new Action("START");

    private String actionId;

    public Action( )
    {
        this.actionId = "NOOP";
    }

    public Action(String actionId)
    {
        this.actionId = actionId;
    }

    public String getActionId( )
    {
        return actionId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass( ) != o.getClass( )) return false;
        Action action = (Action) o;
        return Objects.equals(actionId, action.actionId);
    }

    @Override
    public int hashCode( )
    {
        return Objects.hash(actionId);
    }

    @Override
    public String toString( )
    {
        return "Action{" +
                "actionId='" + actionId + '\'' +
                '}';
    }
}
