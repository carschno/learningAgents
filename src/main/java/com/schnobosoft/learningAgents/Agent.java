package com.schnobosoft.learningAgents;

import java.util.Random;

/**
 * An agent has two main action: speak and hear. In its internal signal table, it stores the
 * frequency of event / signal pairs it has hears. When it speaks about a specific event, it uses
 * that signal table to generate a signal.
 * <p>
 * The agent has a {@link Rule} according to which it generates signals for events.
 * 
 * @author Carsten Schnober
 *
 */
public class Agent
{
    /**
     * The rule to use for emitting a signal on an event.
     * 
     * @author Carsten Schnober
     *
     */
    public static enum Rule
    {
        /**
         * a combination of random and learned signal
         */
        RANDOMIZED,
        /**
         * the most frequently seen signal for the event
         */
        MAX,
        /**
         * a random signal
         */
        PURE_RANDOM
    };

    private static int MAX_INIT_VALUE = 10;
    private static Rule DEFAULT_RULE = Rule.RANDOMIZED;

    private int[][] signalTable; // event X signal
    private double[][] normalizedSignalTable;
    private Random random;
    private Rule rule;

    /**
     * Default constructor: create a event / signal table of the given size and initialise with
     * random values.
     * 
     * @param events
     *            the number of events/signals to be defined
     */
    Agent(int events)
    {
        random = new Random();
        rule = DEFAULT_RULE;
        initSignalTable(events);
    }

    Agent(int events, Rule rule)
    {
        random = new Random();
        this.rule = rule;
        initSignalTable(events);
    }

    private void initSignalTable(int events)
    {
        signalTable = new int[events][events];
        for (int e = 0; e < events; e++) {
            for (int s = 0; s < events; s++) {
                signalTable[e][s] = random.nextInt(MAX_INIT_VALUE);
            }
        }
        normalizedSignalTable = new double[events][events];
        computeNormalizedSignalTable();
    }

    /**
     * Get a signal according to the given {@link Rule}.
     * 
     * @param event
     *            an event index
     * @param rule
     *            a {@link Rule} element to define the rule to apply
     * @return a signal index
     * @throws IllegalArgumentException
     *             if the event is less than 0 or larger than the number of defined events/signals
     * @throws IllegalStateException
     *             if the rule is not handled properly
     */
    public int getSignal(int event, Rule rule)
    {
        if (event < 0 || event >= signalTable.length) {
            throw new IllegalArgumentException();
        }
        else {
            switch (rule) {
            case MAX:
                return getSignalMax(event);
            case PURE_RANDOM:
                return getSignalRandom(event);
            case RANDOMIZED:
                return getSignalRandomized(event);
            default:
                throw new IllegalStateException();
            }
        }
    }

    private int getSignalMax(int event)
    {
        int maxValue = -1;
        int maxArg = -1;
        for (int i = 0; i < signalTable.length; i++) {
            int value = signalTable[event][i];
            if (value > maxValue) {
                maxValue = value;
                maxArg = i;
            }
        }
        return maxArg;
    }

    private int getSignalRandomized(int event)
    {
        double maxValue = -1;
        int maxArg = -1;
        for (int i = 0; i < signalTable.length; i++) {
            double value = signalTable[event][i] * random.nextDouble();
            if (value > maxValue) {
                maxValue = value;
                maxArg = i;
            }
        }
        return maxArg;
    }

    private int getSignalRandom(int event)
    {
        return random.nextInt(signalTable.length);
    }

    /**
     * Return a signal for the given event, according to the agent's defined {@link Rule}.
     * 
     * @param event
     *            an event index
     * @return a signal index
     */
    public int speak(int event)
    {
        return getSignal(event, rule);
    }

    /**
     * Process a event / signal pair by storing it in the frequency table.
     * <p>
     * If this agent would have uttered the same signal for the event, return true. If not, return
     * false.
     * 
     * @param event
     *            an event index
     * @param signal
     *            a signal index
     * @return true if this agent would have uttered the same signal for the given event.
     */
    public boolean hear(int event, int signal)
    {
        boolean understood = speak(event) == signal;
        signalTable[event][signal]++;
        computeNormalizedSignalTable();
        return understood;
    }

    /**
     * For each event, return the most frequently seen signal index.
     */
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int event = 0; event < signalTable.length; event++) {
            sb.append(String.format("%d:%d, ", event, getSignal(event, Rule.MAX)));
        }
        sb.setLength(sb.length() - 2);
        sb.append("]");
        return sb.toString();
    }

    /**
     * The average similarity between this agent and the given agent for all events.
     * 
     * @param agent
     *            another {@link Agent}
     * @return a similarity value
     */
    public double similarity(Agent agent)
    {
        double similarity = 1d;
        for (int event = 0; event < signalTable.length; event++) {
            similarity *= similarityEvent(agent, event);
        }
        return similarity;
    }

    /**
     * Compute the cosine similarity for the signals learned for the given event.
     * 
     * @param agent2
     *            another {@link Agent}
     * @param event
     *            an event index
     * @return cosine similarity between 0.0 and 1.0
     */
    private double similarityEvent(Agent agent2, int event)
    {
        double dotProduct = 0d;
        double magnitude1 = 0d;
        double magnitude2 = 0d;

        for (int i = 0; i < signalTable.length; i++) {
            dotProduct += normalizedSignalTable[event][i]
                    * agent2.getNormalizedSignalTable()[event][i];
            magnitude1 += Math.pow(normalizedSignalTable[event][i], 2);
            magnitude2 += Math.pow(agent2.getNormalizedSignalTable()[event][i], 2);
        }

        return dotProduct / (Math.sqrt(magnitude1) * Math.sqrt(magnitude2));

    }

    /**
     * From the absolute frequencies of signals learned for each event, compute relative counts
     * normalized to sum up to 1.0.
     */
    private void computeNormalizedSignalTable()
    {
        for (int event = 0; event < signalTable.length; event++) {
            int eventTotal = 0;
            for (int signal = 0; signal < signalTable.length; signal++) {
                eventTotal += signalTable[event][signal];
            }
            for (int signal = 0; signal < signalTable.length; signal++) {
                normalizedSignalTable[event][signal] = (double) signalTable[event][signal]
                        / (double) eventTotal;
            }
        }
    }

    private double[][] getNormalizedSignalTable()
    {
        return normalizedSignalTable;
    }
}
