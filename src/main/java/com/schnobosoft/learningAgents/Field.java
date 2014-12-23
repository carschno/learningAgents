package com.schnobosoft.learningAgents;

/**
 * @author Carsten Schnober
 *
 */
public class Field {
	private Agent agent; // the agent living in the field

	/**
	 * @param agent
	 *            the {@link Agent} to populate the field
	 */
	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	/**
	 * @return the {@link Agent} at the given field
	 */
	public Agent getAgent() {
		return agent;
	}
}
