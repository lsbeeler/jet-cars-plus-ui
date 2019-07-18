package org.hazelcast.iotdemo;


public final class Action
{
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
    public String toString( )
    {
        return "Action{" +
                "actionId='" + actionId + '\'' +
                '}';
    }
}
