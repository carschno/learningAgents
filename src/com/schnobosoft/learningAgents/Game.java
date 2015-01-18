package com.schnobosoft.learningAgents;

import java.util.Random;

/**
 * @author Carsten Schnober
 *
 */
public class Game
{
    private static final int N_ROUNDS = 50000;

    private static final int N_PRINTS = 100;
    private static final int DEFAULT_FIELD_DIMENSIONALITY = 20; // field dimensionality
    private static final Board.Output outputMode = Board.Output.LANGUAGES;
    private static final int N_EVENTS = 2;
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
                    int event = random.nextInt(getNumberOfEvents());
                    int signal = board.getField(x, y).getAgent().speak(event);

                    for (Agent neighbour : board.neighbourAgents(x, y)) {
                        neighbour.hear(event, signal);
                    }
                }
            }
            if (round % printInterval == 0) {
                System.out.printf("Round %d/%d%n", round, rounds);
                board.printBoard(outputMode);
            }
        }
    }

    /**
     * @return the number of events
     */
    public static int getNumberOfEvents()
    {
        return N_EVENTS;
    }

    /**
     * @return the number of signals
     */
    public static int getNumberOfSignals()
    {
        return N_SIGNALS;
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Game game = new Game(N_ROUNDS);
        game.run();
    }

}
