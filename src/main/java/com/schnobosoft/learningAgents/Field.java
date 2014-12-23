package com.schnobosoft.learningAgents;

/**
 * A field in the board, hosting an agent.
 * 
 * @author Carsten Schnober
 *
 */
public class Field
{
    private Agent agent; // the agent living in the field

    public Field(Agent agent)
    {
        this.agent = agent;
    }

    /**
     * @param agent
     *            the {@link Agent} to populate the field
     */
    public void setAgent(Agent agent)
    {
        this.agent = agent;
    }

    /**
     * @return the {@link Agent} at the given field
     */
    public Agent getAgent()
    {
        return agent;
    }
}
