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
    private static final Locale LOCALE = Locale.US;
    private Field[][] board;

    /**
     * Default constructor: creates a board with x*x fields and assigns a new agent on each field.
     * 
     * @param boardDimension
     *            the board dimensionality
     */
    public Board(int boardDimension)
    {
        board = new Field[boardDimension][boardDimension];
        for (int x = 0; x < boardDimension; x++) {
            for (int y = 0; y < boardDimension; y++) {
                board[x][y] = new Field(new Agent(Game.DEFAULT_N_EVENTS));
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
        Collection<Field> neighbours = new ArrayList<>(8);
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
    public void printBoard(int event)
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
     * For each field, print the agent's signal Table.
     */
    public void printBoard()
    {
        for (int y = 0; y < getBoardSize(); y++) {
            for (int x = 0; x < getBoardSize(); x++) {
                Agent agent = board[x][y].getAgent();
                System.out.print(agent.toString() + " ");
            }
            System.out.println();
        }
    }

    /**
     * For each field, print the agent's average similarity to all its neighbours.
     */
    public void printNeighbourSimilarities()
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
        double sum = 0;
        for (int i = 0; i < doubleArray.length; i++) {
            sum += doubleArray[i];
        }
        return sum / doubleArray.length;
    }
}
