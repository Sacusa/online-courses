package warmup;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class QuadraticTest {

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

    /*
     * Testing strategy (following the example of the Testing reading):
     * 
     * Partitions:
     *    a>0, a<0, a=0
     *    b>0, b<0, b=0
     *    c>0, c<0, c=0
     *        (but note that we can't have a=b=c=0, because the spec forbids that)
     *    
     *    # roots returned: 0, 1, or 2  (a quadratic equation can't have more than 2 roots)
     *    a root is positive, negative, or zero
     *    absolute value of root is small or large (as close to Integer.MAX_VALUE as possible)
     * 
     * Each of these parts is covered by at least one test case below.
     */

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    // covers a>0, b<0, c>0, 2 small positive roots
    @Test
    public void testPositiveRoots() {
        assertEquals(makeSet(5, 8), Quadratic.roots(1, -(5+8), 5*8));
    }
 
    // covers b>0, c<0, 2 small negative roots
    @Test
    public void testNegativeRoots() {
        assertEquals(makeSet(-8, -2), Quadratic.roots(1, -(-8+-2), -8*-2));
    }
 
    // covers a<0, one root positive, other root negative
    @Test
    public void testOppositeSignRoots() {
        assertEquals(makeSet(11, 3), Quadratic.roots(-7, -7*-1*(11+3), -7*11*3));
    }

    // covers c=0, one root zero, other root nonzero
    @Test
    public void testOneZeroRoot() {
        assertEquals(makeSet(0, 9), Quadratic.roots(1, -9, 0));
    }

    // covers one nonzero root
    @Test
    public void testOneRoot() {
        assertEquals(makeSet(13), Quadratic.roots(1, -2*13, 13*13));
    }

    // covers b=0, both roots zero
    @Test
    public void testBothZeroRoots() {
        assertEquals(makeSet(0), Quadratic.roots(1, 0, 0));
    }

    // covers roots as large as possible
    @Test
    public void testLargeRoots() {
        int r1 =  45_000; // a root ~45,000 means c is ~2,000,000,000, which is close to the maximum integer 2^31 - 1
        int r2 = -45_000;
        assertEquals(makeSet(r1, r2), Quadratic.roots(1, -r1-r2, r1*r2));
    }

    // returns a set of the integers passed as parameters
    private static Set<Integer> makeSet(int... elements) {
        Set<Integer> set = new HashSet<>();
        for (int x: elements) {
            set.add(x);
        }
        return set;
    }
}
