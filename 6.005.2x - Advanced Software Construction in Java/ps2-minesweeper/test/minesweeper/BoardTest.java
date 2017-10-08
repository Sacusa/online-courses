/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

/**
 * Test cases for Board.
 */
public class BoardTest {

    /**
     * Testing strategy
     * ================
     * 
     * X  1, >1
     * Y  1, >1
     * 
     * Tested all possible combinations of flag, deflag and dig.
     * 
     * Exhaustive Cartesian partition of input space.
     */

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    int x = 0;
    int y = 0;
    List<Integer> values;
    String answer, result;
    Board board;

    // covers X = 1
    //        Y = 1
    // dig -> flag -> deflag
    @Test
    public void XOneYOneDigFlagDeflag() {
        x = 1;
        y = 1;
        values = Arrays.asList(0);
        board = new Board(x, y, values);

        // check constructor
        answer = "-";
        result = board.look();
        assertEquals(answer, result);

        // check dig
        answer = " ";
        result = board.dig(0, 0);
        assertEquals(answer, result);

        // check flag
        result = board.flag(0, 0);
        assertEquals(answer, result);

        // check deflag
        result = board.deflag(0, 0);
        assertEquals(answer, result);
    }

    // covers X = 1
    //        Y = 5 (>1)
    // dig -> deflag -> flag
    @Test
    public void XOneYManyDigDeflagFlag() {
        x = 1;
        y = 5;
        values = Arrays.asList(0,0,1,0,1);
        board = new Board(x, y, values);

        // check constructor
        answer = "";
        for (int i = 0; i < (y - 1); ++i) {
            answer += '-' +  String.format("%n");
        }
        answer += '-';
        result = board.look();
        assertEquals(answer, result);

        // check dig
        // dig (0,0)
        answer = " " + String.format("%n") +
                "1" + String.format("%n") +
                "-" + String.format("%n") +
                "-" + String.format("%n") +
                "-";
        result = board.dig(0, 0);
        assertEquals(answer, result);

        // dig (0,3)
        answer = " " + String.format("%n") +
                "1" + String.format("%n") +
                "-" + String.format("%n") +
                "2" + String.format("%n") +
                "-";
        result = board.dig(0, 3);
        assertEquals(answer, result);

        // dig (0,2)
        answer = "BOOM!";
        result = board.dig(0, 2);
        assertEquals(answer, result);

        // dig (0,4)
        result = board.dig(0, 4);
        assertEquals(answer, result);

        // check final state of the board
        answer = " " + String.format("%n") +
                " " + String.format("%n") +
                " " + String.format("%n") +
                " " + String.format("%n") +
                " ";
        result = board.look();
        assertEquals(answer, result);

        // check deflag
        for (int i = 0; i < y; ++i) {
            result = board.deflag(0, i);
        }
        assertEquals(answer, result);

        // check flag
        for (int i = 0; i < y; ++i) {
            result = board.flag(0, i);
        }
        assertEquals(answer, result);
    }

    // covers X = 5 (>1)
    //        Y = 1
    // deflag -> flag -> dig
    @Test
    public void XManyYOneDeflagFlagDig() {
        x = 5;
        y = 1;
        values = Arrays.asList(0,0,1,0,1);
        board = new Board(x, y, values);

        // check constructor
        answer = "- - - - -";
        result = board.look();
        assertEquals(answer, result);

        // check deflag
        for (int i = 0; i < x; ++i) {
            result = board.deflag(i, 0);
        }
        assertEquals(answer, result);

        // check flag
        answer = "F F F F F";
        for (int i = 0; i < 5; ++i) {
            result = board.flag(i, 0);
        }
        assertEquals(answer, result);

        // check dig
        // dig (2,0)
        result = board.dig(2, 0);
        assertEquals(answer, result);

        // dig (0,0)
        result = board.dig(0, 0);
        assertEquals(answer, result);

        // dig (4,0)
        result = board.dig(4, 0);
        assertEquals(answer, result);

        // check final state of the board
        answer = "F F F F F";
        result = board.look();
        assertEquals(answer, result);
    }

    // covers X = 5 (>1)
    //        Y = 1
    // deflag -> dig -> flag
    @Test
    public void XManyYOneDeflagDigFlag() {
        x = 5;
        y = 1;
        values = Arrays.asList(0,0,1,0,1);
        board = new Board(x, y, values);

        // check constructor
        answer = "- - - - -";
        result = board.look();
        assertEquals(answer, result);

        // check deflag
        for (int i = 0; i < x; ++i) {
            result = board.deflag(i, 0);
        }
        assertEquals(answer, result);

        // check dig
        // dig (0,0)
        answer = "  1 - - -";
        result = board.dig(0, 0);
        assertEquals(answer, result);
        
        // dig (3,0)
        answer = "  1 - 2 -";
        result = board.dig(3, 0);
        assertEquals(answer, result);
        
        // dig (2,0)
        answer = "BOOM!";
        result = board.dig(2, 0);
        assertEquals(answer, result);
        
        // dig (4,0)
        result = board.dig(4, 0);
        assertEquals(answer, result);

        // check final state of the board
        answer = "         ";
        result = board.look();
        assertEquals(answer, result);

        // check flag
        for (int i = 0; i < 5; ++i) {
            result = board.flag(i, 0);
        }
        assertEquals(answer, result);
    }

    // covers X = 5 (>1)
    //        Y = 5 (>1)
    // flag -> deflag -> dig
    // covers X = 5 (>1)

    // covers X = 5 (>1)
    //        Y = 5 (>1)
    // flag -> deflag -> dig
    @Test
    public void XManyYManyFlagDeflagDig() {
        x = 5;
        y = 5;
        values = Arrays.asList(1,0,0,0,1,
                0,0,1,0,0,
                1,0,0,0,1,
                0,0,1,0,0,
                1,0,0,0,1);
        board = new Board(x, y, values);

        // check constructor
        answer = "";
        for (int i = 0; i < (y - 1); ++i) {
            answer += "- - - - -" + String.format("%n");
        }
        answer += "- - - - -";
        result = board.look();
        assertEquals(answer, result);

        // check flag
        answer = "F - - - F" + String.format("%n") +
                "- - F - -" + String.format("%n") +
                "F - - - F" + String.format("%n") +
                "- - F - -" + String.format("%n") +
                "F - - - F";
        result = board.flag(0, 0);
        result = board.flag(0, 2);
        result = board.flag(0, 4);
        result = board.flag(2, 1);
        result = board.flag(2, 3);
        result = board.flag(4, 0);
        result = board.flag(4, 2);
        result = board.flag(4, 4);
        assertEquals(answer, result);

        // check deflag
        answer = "";
        for (int i = 0; i < (y - 1); ++i) {
            answer += "- - - - -" + String.format("%n");
        }
        answer += "- - - - -";
        for (int i = 0; i < x; ++i) {
            for (int j = 0; j < y; ++j) {
                result = board.deflag(i, j);
            }
        }
        assertEquals(answer, result);

        // check dig
        // dig non-bomb squares
        answer = "- 2 1 2 -" + String.format("%n") +
                "2 3 - 3 2" + String.format("%n") +
                "- 3 2 3 -" + String.format("%n") +
                "2 3 - 3 2" + String.format("%n") +
                "- 2 1 2 -";
        result = board.dig(0, 1);
        result = board.dig(0, 3);
        result = board.dig(2, 0);
        result = board.dig(2, 2);
        result = board.dig(2, 4);
        result = board.dig(4, 1);
        result = board.dig(4, 3);
        for (int i = 1; i <= 3; i += 2) {
            for (int j = 0; j < y; ++j) {
                result = board.dig(i, j);
            }
        }        
        assertEquals(answer, result);

        // dig (0,0)
        answer = "BOOM!";
        result = board.dig(0, 0);
        assertEquals(answer, result);

        answer = "  1 1 2 -" + String.format("%n") +
                "1 2 - 3 2" + String.format("%n") +
                "- 3 2 3 -" + String.format("%n") +
                "2 3 - 3 2" + String.format("%n") +
                "- 2 1 2 -";
        result = board.look();
        assertEquals(answer, result);

        // dig (0,2)
        answer = "BOOM!";
        result = board.dig(0, 2);
        assertEquals(answer, result);

        answer = "  1 1 2 -" + String.format("%n") +
                "  1 - 3 2" + String.format("%n") +
                "  2 2 3 -" + String.format("%n") +
                "1 2 - 3 2" + String.format("%n") +
                "- 2 1 2 -";
        result = board.look();
        assertEquals(answer, result);

        // dig (0,4)
        answer = "BOOM!";
        result = board.dig(0, 4);
        assertEquals(answer, result);

        answer = "  1 1 2 -" + String.format("%n") +
                "  1 - 3 2" + String.format("%n") +
                "  2 2 3 -" + String.format("%n") +
                "  1 - 3 2" + String.format("%n") +
                "  1 1 2 -";
        result = board.look();
        assertEquals(answer, result);

        // dig (2,1)
        answer = "BOOM!";
        result = board.dig(2, 1);
        assertEquals(answer, result);

        answer = "      1 -" + String.format("%n") +
                "      2 2" + String.format("%n") +
                "  1 1 2 -" + String.format("%n") +
                "  1 - 3 2" + String.format("%n") +
                "  1 1 2 -";
        result = board.look();
        assertEquals(answer, result);

        // dig (2,3)
        answer = "BOOM!";
        result = board.dig(2, 3);
        assertEquals(answer, result);

        answer = "      1 -" + String.format("%n") +
                "      2 2" + String.format("%n") +
                "      1 -" + String.format("%n") +
                "      2 2" + String.format("%n") +
                "      1 -";
        result = board.look();
        assertEquals(answer, result);

        // dig (4,0)
        answer = "BOOM!";
        result = board.dig(4, 0);
        assertEquals(answer, result);

        answer = "         " + String.format("%n") +
                "      1 1" + String.format("%n") +
                "      1 -" + String.format("%n") +
                "      2 2" + String.format("%n") +
                "      1 -";
        result = board.look();
        assertEquals(answer, result);

        // dig (4,2)
        answer = "BOOM!";
        result = board.dig(4, 2);
        assertEquals(answer, result);

        answer = "         " + String.format("%n") +
                "         " + String.format("%n") +
                "         " + String.format("%n") +
                "      1 1" + String.format("%n") +
                "      1 -";
        result = board.look();
        assertEquals(answer, result);

        // dig (4,4)
        answer = "BOOM!";
        result = board.dig(4, 4);
        assertEquals(answer, result);

        answer = "         " + String.format("%n") +
                "         " + String.format("%n") +
                "         " + String.format("%n") +
                "         " + String.format("%n") +
                "         ";
        result = board.look();
        assertEquals(answer, result);
    }

    // covers X = 5 (>1)

    // covers X = 5 (>1)
    //        Y = 5 (>1)
    // flag -> dig -> deflag
    @Test
    public void XManyYManyFlagDigDeflag() {
        x = 5;
        y = 5;
        values = Arrays.asList(1,0,0,0,1,
                0,0,1,0,0,
                1,0,0,0,1,
                0,0,1,0,0,
                1,0,0,0,1);
        board = new Board(x, y, values);

        // check constructor
        answer = "";
        for (int i = 0; i < (y - 1); ++i) {
            answer += "- - - - -" + String.format("%n");
        }
        answer += "- - - - -";
        result = board.look();
        assertEquals(answer, result);

        // check flag
        answer = "F - - - F" + String.format("%n") +
                "- - F - -" + String.format("%n") +
                "F - - - F" + String.format("%n") +
                "- - F - -" + String.format("%n") +
                "F - - - F";
        result = board.flag(0, 0);
        result = board.flag(0, 2);
        result = board.flag(0, 4);
        result = board.flag(2, 1);
        result = board.flag(2, 3);
        result = board.flag(4, 0);
        result = board.flag(4, 2);
        result = board.flag(4, 4);
        assertEquals(answer, result);

        // check dig
        // dig non-bomb squares
        answer = "F 2 1 2 F" + String.format("%n") +
                "2 3 F 3 2" + String.format("%n") +
                "F 3 2 3 F" + String.format("%n") +
                "2 3 F 3 2" + String.format("%n") +
                "F 2 1 2 F";
        result = board.dig(0, 1);
        result = board.dig(0, 3);
        result = board.dig(2, 0);
        result = board.dig(2, 2);
        result = board.dig(2, 4);
        result = board.dig(4, 1);
        result = board.dig(4, 3);
        for (int i = 1; i <= 3; i += 2) {
            for (int j = 0; j < y; ++j) {
                result = board.dig(i, j);
            }
        }        
        assertEquals(answer, result);

        // dig (0,0)
        result = board.dig(0, 0);
        assertEquals(answer, result);

        // dig (0,2)
        result = board.dig(0, 2);
        assertEquals(answer, result);

        // dig (0,4)
        result = board.dig(0, 4);
        assertEquals(answer, result);

        // dig (2,1)
        result = board.dig(2, 1);
        assertEquals(answer, result);

        // dig (2,3)
        result = board.dig(2, 3);
        assertEquals(answer, result);

        // dig (4,0)
        result = board.dig(4, 0);
        assertEquals(answer, result);

        // dig (4,2)
        result = board.dig(4, 2);
        assertEquals(answer, result);

        // dig (4,4)
        result = board.dig(4, 4);
        assertEquals(answer, result);

        // check deflag
        answer = "- 2 1 2 -" + String.format("%n") +
                "2 3 - 3 2" + String.format("%n") +
                "- 3 2 3 -" + String.format("%n") +
                "2 3 - 3 2" + String.format("%n") +
                "- 2 1 2 -";
        for (int i = 0; i < x; ++i) {
            for (int j = 0; j < y; ++j) {
                result = board.deflag(i, j);
            }
        }
        assertEquals(answer, result);
    }

}
