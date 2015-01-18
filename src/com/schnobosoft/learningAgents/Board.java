package com.schnobosoft.learningAgents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import com.schnobosoft.learningAgents.Agent.Rule;

/**
 * A quadratic board of n x n fields.
 * 
 * @author Carsten Schnober
 *
 */
public class Board
{
    /**
     * Define an output mode for printing the board.
     */
    public enum Output
    {
        /**
         * print all the agents (signal tables)
         */
        AGENTS, /**
         * print the similarities to neighbour fields
         */
        SIMILARITIES, /**
         * print the signal emitted for a specific event
         */
        EVENT, /**
         * print the 'language' for each field.
         */
        LANGUAGES
    }

    private static final Locale LOCALE = Locale.US;
    private Field[][] board;
    private char[][] boardStatus;

    /**
     * Default constructor: creates a board with x*x fields and assigns a new agent on each field.
     * 
     * @param boardDimension
     *            the board dimensionality
     */
    public Board(int boardDimension)
    {
        board = new Field[boardDimension][boardDimension];
        boardStatus = new char[boardDimension][boardDimension];

        for (int x = 0; x < boardDimension; x++) {
            for (int y = 0; y < boardDimension; y++) {
                board[x][y] = new Field(new Agent(Game.getNumberOfEvents(),
                        Game.getNumberOfSignals()));
                boardStatus[x][y] = '0';
            }
        }
    }

    /**
     * @return the board dimensionality
     */
    public int getBoardSize()
    {
        return board.length;
    }

    /**
     * @param x
     *            the x coordinate
     * @param y
     *            the y coordinate
     * @return the {@link Field} object at the given coordinates
     * @throws IllegalArgumentException
     *             if the coordinates lie outside the board boundaries (i.e. are less than 0 or
     *             larger than board size - 1)
     */
    public Field getField(int x, int y)
    {
        if (x < 0 || x >= getBoardSize() || y < 0 || y >= getBoardSize()) {
            throw new IllegalArgumentException();
        }
        else {
            return board[x][y];
        }
    }

    /**
     * For the field / agent at the given coordinates, return the agents in the adjacent fields.
     * 
     * @param x
     * @param y
     * @return a collection of agents in the adjacent fields.
     */
    public Collection<Agent> neighbourAgents(int x, int y)
    {
        Collection<Agent> neighbours = new ArrayList<>(8);
        for (Field field : neighbourFields(x, y)) {
            neighbours.add(field.getAgent());
        }
        return neighbours;
    }

    private Collection<Field> neighbourFields(int x, int y)
    {
        int maxNumNeighbours = 8;
        Collection<Field> neighbours = new ArrayList<>(maxNumNeighbours);
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (!(i == x && j == y) && i >= 0 && j >= 0 && i < getBoardSize()
                        && j < getBoardSize()) {
                    neighbours.add(board[i][j]);
                }
            }
        }
        return neighbours;
    }

    /**
     * For each field, print the agent's signal for a specified event.
     * 
     * @param event
     *            an event index
     */
    private void printSignals(int event)
    {
        printColumnHeaders();
        for (int y = 0; y < getBoardSize(); y++) {
            System.out.printf("%2d: ", y + 1);
            for (int x = 0; x < getBoardSize(); x++) {
                int signal = board[x][y].getAgent().getSignal(event, Rule.MAX);
                System.out.printf("%d:%d ", event, signal);
            }
            System.out.println();
        }
    }

    private void printColumnHeaders()
    {
        System.out.print("    ");
        for (int x = 0; x < getBoardSize(); x++) {
            System.out.printf(" %2d ", x + 1);
        }
        System.out.println();
    }

    /**
     * Print the board
     * 
     * @param mode
     *            an {@link Output} mode.
     */
    public void printBoard(Output mode)
    {
        int defaultEvent = 0; // for printout modes that only consider one event/signal pair

        switch (mode) {
        case AGENTS:
            printAgents();
            break;
        case SIMILARITIES:
            printNeighbourSimilarities();
            break;
        case EVENT:
            printSignals(defaultEvent);
            break;
        case LANGUAGES:
            printLanguages(defaultEvent);
            break;
        }
        System.out.println();
    }

    /**
     * For each field, print the agent's signal Table.
     */
    private void printAgents()
    {
        for (int y = 0; y < getBoardSize(); y++) {
            for (int x = 0; x < getBoardSize(); x++) {
                System.out.print(board[x][y].getAgent().toString() + " ");
            }
            System.out.println();
        }
    }

    /**
     * For each field, print the 'language': represent each agent by a symbol representing its
     * 'language'.
     * 
     * @param previousBoardStatus
     *            a matrix of signals emitted for the previous board in order to measure the
     *            changes.
     */
    private void printLanguages(int event)
    {
        assert Game.getNumberOfEvents() == 1;

        char[][] newBoardStatus = getBoardStatus(Rule.MAX);
        System.out.printf("Changes: %.2f%%\n",
                (1d - boardSimilarity(boardStatus, newBoardStatus)) * 100);

        for (int y = 0; y < getBoardSize(); y++) {
            for (int x = 0; x < getBoardSize(); x++) {
                char output = newBoardStatus[x][y];
                System.out.print(output + " ");
            }
            System.out.println();
        }
        boardStatus = newBoardStatus;
    }

    @Deprecated
    private char[][] getBoardStatus(int event, Rule rule)
    {
        char[][] status = new char[getBoardSize()][getBoardSize()];
        for (int y = 0; y < getBoardSize(); y++) {
            for (int x = 0; x < getBoardSize(); x++) {
                status[x][y] = (char) (65 + board[x][y].getAgent().getSignal(event, rule));
            }
        }
        return status;
    }

    private char[][] getBoardStatus(Rule rule)
    {
        assert Game.getNumberOfEvents() <= 2 && Game.getNumberOfSignals() <= 2;

        char[][] status = new char[getBoardSize()][getBoardSize()];
        for (int y = 0; y < getBoardSize(); y++) {
            for (int x = 0; x < getBoardSize(); x++) {
                int coefficient = 1;
                int output = 0;
                for (int event = 0; event < Game.getNumberOfEvents(); event++) {
                    output += coefficient * board[x][y].getAgent().getSignal(event, rule);
                    coefficient++;
                }
                status[x][y] = (char) (65 + output);
            }
        }
        return status;
    }

    private double boardSimilarity(char[][] board1, char[][] board2)
    {
        assert board1.length == board2.length && board1[0].length == board2[0].length;
        int equal = 0;
        int total = 0;
        for (int y = 0; y < board1[0].length; y++) {
            for (int x = 0; x < board1.length; x++) {
                total++;
                equal += board1[x][y] == board2[x][y] ? 1 : 0;
            }
        }
        return (double) equal / (double) total;
    }

    /**
     * For each field, print the agent's average similarity to all its neighbours.
     */
    private void printNeighbourSimilarities()
    {
        for (int y = 0; y < getBoardSize(); y++) {
            for (int x = 0; x < getBoardSize(); x++) {
                Agent agent = board[x][y].getAgent();
                Collection<Agent> neighbours = neighbourAgents(x, y);
                double[] similarities = new double[neighbours.size()];
                int i = 0;
                for (Agent neighbour : neighbours) {
                    similarities[i] = agent.similarity(neighbour);
                    i++;
                }
                System.out.printf(LOCALE, "%.4f ", mean(similarities));
            }
            System.out.println();
        }
    }

    /**
     * @param doubleArray
     * @return the mean of all the values in the given array
     */
    public static double mean(double[] doubleArray)
    {
        double sum = 0d;
        for (int i = 0; i < doubleArray.length; i++) {
            sum += doubleArray[i];
        }
        return sum / doubleArray.length;
    }
}
