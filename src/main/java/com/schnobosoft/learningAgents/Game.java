package com.schnobosoft.learningAgents;

import java.util.Random;

/**
 * @author Carsten Schnober
 *
 */
public class Game
{
    private static final int N_PRINTS = 100;
    private static final int DEFAULT_FIELDSIZE = 10; // field dimensionality
    protected static final int DEFAULT_N_EVENTS = 4; // number of events/signals

    private Board board;
    private int rounds;
    private int printInterval;
    private Random random;

    Game(int rounds)
    {
        board = new Board(DEFAULT_FIELDSIZE);
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
                    int event = random.nextInt(DEFAULT_N_EVENTS);
                    int signal = board.getField(x, y).getAgent().speak(event);

                    for (Agent listener : board.neighbourAgents(x, y)) {
                        listener.hear(event, signal);
                    }
                }
            }
            if (round % printInterval == 0) {
                System.out.printf("Round %d/%d%n", round, rounds);
                // board.printBoard();
                board.printNeighbourSimilarities();
                System.out.println();
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Game game = new Game(10000);
        game.run();
    }

}
