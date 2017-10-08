/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import java.util.*;

/**
 * Board represents a Minesweeper game board. It maintains two copies of the game board, one which
 * contains all the bombs and numbers, and another which is seen by the user.
 * 
 * Board type is NOT THREAD-SAFE.
 * 
 * It supports the following operations:
 * + look: return the current board, as seen by the user
 * + dig: open the square at the given position (x,y), if not already dug
 * + flag: flag the square at the given position (x,y), if not already flagged
 * + deflag: remove flag from the square at the given position (x,y), if it has one
 */
public class Board {

    private final char [][] board;
    private final char [][] displayBoard;
    private final int X;
    private final int Y;
    // Abstraction function:
    //   represent a Minesweeper board of dimensions X x Y; displayBoard is the board the user
    //   interacts with, board is the backing board
    // Rep invariant:
    //   all values in board are either ' ', 'B' or an integer in range [1-8];
    //   all values in displayBoard are either '-', 'F', ' ' or an integer in range [1-8];
    //   X,Y >= 0
    // Safety from rep exposure:
    //   all fields are private and final

    /**
     * Creates a new Board object, initializing rep values as:
     * X: argument X
     * Y: argument Y
     * board: initializes with values in the List values. List must have values 0 or 1 only.
     *        1 represents presence of a bomb, while 0 does not. Corresponding values in the
     *        backing board are calculated.
     * displayBoard: initializes all values with '-'.
     * 
     * @param X  The X dimension of the Board
     * @param Y  The Y dimension of the Board.
     * @param values  The List with which to initialize Board.
     */
    public Board(int X, int Y, List<Integer> values) {
        this.X = X;
        this.Y = Y;
        board = new char[X][Y];
        displayBoard = new char[X][Y];

        // check values
        if (values.size() != (X * Y)) {
            throw new IllegalArgumentException("invalid values");
        }

        // initialize board with 0s and Bs (bombs)
        // initialize displayBoard
        for (int i = 0; i < Y; ++i) {
            for (int j = 0; j < X; ++j) {
                if (values.get((i * X) + j) == 0) {
                    board[j][i] = '0';
                }
                else if (values.get((i * X) + j) == 1) {
                    board[j][i] = 'B';
                }
                else {
                    throw new IllegalArgumentException("invalid values");
                }

                displayBoard[j][i] = '-';
            }
        }

        // replace 0s with appropriate values
        for (int i = 0; i < X; ++i) {
            for (int j = 0; j < Y; ++j) {
                // next iteration if at a bomb
                if (board[i][j] != '0') {
                    continue;
                }

                int count = 0;
                boolean leftExists = (i - 1) >= 0;
                boolean rightExists = (i + 1) < X;
                boolean bottomExists = (j - 1) >= 0;
                boolean topExists = (j + 1) < Y;

                // check i - 1
                if (leftExists) {
                    if (board[i - 1][j] == 'B') {
                        ++count;
                    }

                    if (bottomExists) {
                        if (board[i - 1][j - 1] == 'B') {
                            ++count;
                        }
                    }

                    if (topExists) {
                        if (board[i - 1][j + 1] == 'B') {
                            ++count;
                        }
                    }
                }

                // check i
                if (bottomExists) {
                    if (board[i][j - 1] == 'B') {
                        ++count;
                    }
                }
                if (topExists) {
                    if (board[i][j + 1] == 'B') {
                        ++count;
                    }
                }

                // check i + 1
                if (rightExists) {
                    if (board[i + 1][j] == 'B') {
                        ++count;
                    }

                    if (bottomExists) {
                        if (board[i + 1][j - 1] == 'B') {
                            ++count;
                        }
                    }

                    if (topExists) {
                        if (board[i + 1][j + 1] == 'B') {
                            ++count;
                        }
                    }
                }

                // update the value
                if (count == 0) {
                    board[i][j] = ' ';
                }
                else {
                    board[i][j] = Character.forDigit(count, 10);
                }
            }
        }

        checkRep();
    }

    /**
     * Makes sure that the rep invariant is true.
     * @throws IllegalStateException  If the Board is in invalid state, i.e. rep invariant
     *                                becomes false.
     */
    private void checkRep() throws IllegalStateException {
        // check X and Y
        if ((X < 0) || (Y < 0)) {
            throw new IllegalStateException("invalid Board state");
        }

        // check displayBoard and board
        for (int i = 0; i < X; ++i) {
            for (int j = 0; j < Y; ++j) {
                if ((board[i][j] < '1') && (board[i][j] > '8') &&
                        (board[i][j] != ' ') && (board[i][j] != 'B')) {
                    throw new IllegalStateException("invalid Board state");
                }
                if ((displayBoard[i][j] != '-') && (displayBoard[i][j] != 'F') &&
                        (displayBoard[i][j] > '8') && (displayBoard[i][j] < '1') &&
                        (displayBoard[i][j] != ' ')) {
                    throw new IllegalStateException("invalid Board state");
                }
            }
        }
    }

    /**
     * Returns the string representation of thecurrent state of displayBoard.
     * @return  A String representing the current state of displayBoard.
     */
    public String look() {
        String displayBoardString = "";

        for (int i = 0; i < (Y - 1); ++i) {
            for (int j = 0; j < (X - 1); ++j) {
                displayBoardString += displayBoard[j][i] + " ";
            }
            displayBoardString += displayBoard[X - 1][i] + String.format("%n");
        }

        for (int j = 0; j < (X - 1); ++j) {
            displayBoardString += displayBoard[j][Y - 1] + " ";
        }
        displayBoardString += displayBoard[X - 1][Y - 1];

        return displayBoardString;
    }

    /**
     * Open the square at position (x,y) in displayBoard, except if it is flagged.
     * 
     * If (x,y) is a bomb, it is removed, surrounding squares are updated, and a "BOOM"
     * message is returned.
     * 
     * If (x,y) is an empty square, adjoining non-bomb squares are uncovered recursively.
     * 
     * Except when a bomb is found, the string representing the current state of displayBoard
     * is returned.
     * 
     * @param x  X-coordinate of square to open.
     * @param y  Y-coordinate of square to open.
     * @return  "BOOM", if a bomb is dug. Otherwise, the string representing the current state of
     *          displayBoard is returned.
     */
    public String dig(int x, int y) {
        if ((x < 0) || (y < 0) || (x >= X) || (y >= Y)) {
            return this.look();
        }

        if (displayBoard[x][y] == '-') {
            if (board[x][y] == 'B') {
                removeBomb(x, y);
                return new String("BOOM!");
            }
            else if (board[x][y] == ' ') {
                uncoverBlank(x, y);
            }
            else {
                displayBoard[x][y] = board[x][y];
            }
        }

        checkRep();

        return this.look();
    }

    /**
     * Removes the bomb from board[x][y]. Requires that there must exist there.
     * Also updates the surrounding values in board and uncovers the bomb square and surrounding
     * non-bomb squares in displayBoard[x][y].
     * 
     * @param x  The x-index of the bomb.
     * @param y  The y-index of the bomb.
     */
    private void removeBomb(int x, int y) {
        boolean topExists = (y + 1) < Y;
        boolean bottomExists = (y - 1) >= 0;
        boolean leftExists = (x - 1) >= 0;
        boolean rightExists = (x + 1) < X;

        // update board
        board[x][y] = ' ';

        // check the column on the left, i.e. x - 1
        if (leftExists) {
            // update board[x - 1][y]
            if ((board[x - 1][y] >= '2') && (board[x - 1][y] <= '8')) {
                --board[x - 1][y];
            }
            else if (board[x - 1][y] == '1') {
                board[x - 1][y] = ' ';
            }

            // update board[x - 1][y - 1]
            if (bottomExists) {
                if ((board[x - 1][y - 1] >= '2') && (board[x - 1][y - 1] <= '8')) {
                    --board[x - 1][y - 1];
                }
                else if (board[x - 1][y - 1] == '1') {
                    board[x - 1][y - 1] = ' ';
                }
            }
            // update board[x - 1][y + 1]
            if (topExists) {
                if ((board[x - 1][y + 1] >= '2') && (board[x - 1][y + 1] <= '8')) {
                    --board[x - 1][y + 1];
                }
                else if (board[x - 1][y + 1] == '1') {
                    board[x - 1][y + 1] = ' ';
                }
            }
        }

        // update board[x][y - 1]
        if (bottomExists) {
            if ((board[x][y - 1] >= '2') && (board[x][y - 1] <= '8')) {
                --board[x][y - 1];
            }
            else if (board[x][y - 1] == '1') {
                board[x][y - 1] = ' ';
            }
        }
        // update board[x][y + 1]
        if (topExists) {
            if ((board[x][y + 1] >= '2') && (board[x][y + 1] <= '8')) {
                --board[x][y + 1];
            }
            else if (board[x][y + 1] == '1') {
                board[x][y + 1] = ' ';
            }
        }

        // check the column on the right, i.e. x + 1
        if (rightExists) {
            // update board[x + 1][y]
            if ((board[x + 1][y] >= '2') && (board[x + 1][y] <= '8')) {
                --board[x + 1][y];
            }
            else if (board[x + 1][y] == '1') {
                board[x + 1][y] = ' ';
            }

            // update board[x + 1][y - 1]
            if (bottomExists) {
                if ((board[x + 1][y - 1] >= '2') && (board[x + 1][y - 1] <= '8')) {
                    --board[x + 1][y - 1];
                }
                else if (board[x + 1][y - 1] == '1') {
                    board[x + 1][y - 1] = ' ';
                }
            }

            // update board[x + 1][y + 1]
            if (topExists) {
                if ((board[x + 1][y + 1] >= '2') && (board[x + 1][y + 1] <= '8')) {
                    --board[x + 1][y + 1];
                }
                else if (board[x + 1][y + 1] == '1') {
                    board[x + 1][y + 1] = ' ';
                }
            }
        }

        uncoverBlank(x, y);
    }

    /**
     * Uncovers a blank square recursively in displayBoard.
     * 
     * @param x  The x-index of the bomb.
     * @param y  The y-index of the bomb.
     */
    private void uncoverBlank(int x, int y) {
        boolean topExists = (y + 1) < Y;
        boolean bottomExists = (y - 1) >= 0;
        boolean leftExists = (x - 1) >= 0;
        boolean rightExists = (x + 1) < X;

        // nothing to do if already uncovered
        if (displayBoard[x][y] == ' ') {
            return;
        }

        // update displayBoard
        displayBoard[x][y] = ' ';

        // check the column on the left, i.e. x - 1
        if (leftExists) {
            // update board[x - 1][y]
            if (board[x - 1][y] == ' ') {
                uncoverBlank(x - 1, y);
            }
            else if (board[x - 1][y] != 'B') {
                displayBoard[x - 1][y] = board[x - 1][y];
            }

            // update board[x - 1][y - 1]
            if (bottomExists) {
                if (board[x - 1][y - 1] == ' ') {
                    uncoverBlank(x - 1, y - 1);
                }
                else if (board[x - 1][y - 1] != 'B') {
                    displayBoard[x - 1][y - 1] = board[x - 1][y - 1];
                }
            }
            // update board[x - 1][y + 1]
            if (topExists) {
                if (board[x - 1][y + 1] == ' ') {
                    uncoverBlank(x - 1, y + 1);
                }
                else if (board[x - 1][y + 1] != 'B') {
                    displayBoard[x - 1][y + 1] = board[x - 1][y + 1];
                }
            }
        }

        // update board[x][y - 1]
        if (bottomExists) {
            if (board[x][y - 1] == ' ') {
                uncoverBlank(x, y - 1);
            }
            else if (board[x][y - 1] != 'B') {
                displayBoard[x][y - 1] = board[x][y - 1];
            }
        }
        // update board[x][y + 1]
        if (topExists) {
            if (board[x][y + 1] == ' ') {
                uncoverBlank(x, y + 1);
            }
            else if (board[x][y + 1] != 'B') {
                displayBoard[x][y + 1] = board[x][y + 1];
            }
        }

        // check the column on the right, i.e. x + 1
        if (rightExists) {
            // update board[x + 1][y]
            if (board[x + 1][y] == ' ') {
                uncoverBlank(x + 1, y);
            }
            else if (board[x + 1][y] != 'B') {
                displayBoard[x + 1][y] = board[x + 1][y];
            }

            // update board[x + 1][y - 1]
            if (bottomExists) {
                if (board[x + 1][y - 1] == ' ') {
                    uncoverBlank(x + 1, y - 1);
                }
                else if (board[x + 1][y - 1] != 'B') {
                    displayBoard[x + 1][y - 1] = board[x + 1][y - 1];
                }
            }

            // update board[x + 1][y + 1]
            if (topExists) {
                if (board[x + 1][y + 1] == ' ') {
                    uncoverBlank(x + 1, y + 1);
                }
                else if (board[x + 1][y + 1] != 'B') {
                    displayBoard[x + 1][y + 1] = board[x + 1][y + 1];
                }
            }
        }
    }

    /**
     * Flag the square at position (x,y) in displayBoard.
     * If the square has already been dug, nothing happens.
     * 
     * @param x  X-coordinate of square to flag.
     * @param y  Y-coordinate of square to flag.
     * @return  The string representing the current state of displayBoard is returned.
     */
    public String flag(int x, int y) {
        if ((x < 0) || (y < 0) || (x >= X) || (y >= Y)) {
            return this.look();
        }

        if (displayBoard[x][y] == '-') {
            displayBoard[x][y] = 'F';
        }

        checkRep();

        return this.look();
    }

    /**
     * Deflag the square at position (x,y) in displayBoard.
     * If the square has not been flagged or already been dug, nothing happens.
     * 
     * @param x  X-coordinate of square to deflag.
     * @param y  Y-coordinate of square to deflag.
     * @return  The string representing the current state of displayBoard is returned.
     */
    public String deflag(int x, int y) {
        if ((x < 0) || (y < 0) || (x >= X) || (y >= Y)) {
            return this.look();
        }

        if (displayBoard[x][y] == 'F') {
            displayBoard[x][y] = '-';
        }

        checkRep();

        return this.look();
    }

}
