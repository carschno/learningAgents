package com.schnobosoft.learningAgents;

import java.util.Random;

/**
 * @author Carsten Schnober
 *
 */
public class Game
{
    private enum Output
    {
        AGENTS, SIMILARITIES, EVENT, LANGUAGES
    }

    private static final int N_PRINTS = 100;
    private static final int DEFAULT_FIELD_DIMENSIONALITY = 10; // field dimensionality
    private static final Output output = Output.LANGUAGES;
    private static final int N_EVENTS = 1;
    private static final int N_SIGNALS = 2;

    private Board board;
    private int rounds;
    private int printInterval;
    private Random random;

    Game(int rounds)
    {
        board = new Board(DEFAULT_FIELD_DIMENSIONALITY);
        this.rounds = rounds;
        random = new Random();
        printInterval = rounds / N_PRINTS;
    }

    private void run()
    {
        for (int round = 0; round <= rounds; round++) {
            /* iterate over fields */
            for (int x = 0; x < board.getBoardSize(); x++) {
                for (int y = 0; y < board.getBoardSize(); y++) {
                    int event = random.nextInt(getEvents());
                    int signal = board.getField(x, y).getAgent().speak(event);

                    for (Agent listener : board.neighbourAgents(x, y)) {
                        listener.hear(event, signal);
                    }
                }
            }
            if (round % printInterval == 0) {
                printOutput(round);

            }
        }
    }

    private void printOutput(int round)
    {
        System.out.printf("Round %d/%d%n", round, rounds);
        switch (output) {
        case AGENTS:
            board.printBoard();
            break;
        case SIMILARITIES:
            board.printNeighbourSimilarities();
            break;
        case EVENT:
            board.printBoard(0);
            break;
        case LANGUAGES:
            board.printLanguages();
            break;
        }
        System.out.println();
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Game game = new Game(10000);
        game.run();
    }

    /**
     * @return the number of events
     */
    public static int getEvents()
    {
        return N_EVENTS;
    }

    /**
     * @return the number of signals
     */
    public static int getSignals()
    {
        return N_SIGNALS;
    }

}
