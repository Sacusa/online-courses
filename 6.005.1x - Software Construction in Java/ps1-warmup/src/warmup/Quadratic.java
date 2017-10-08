package warmup;

import java.util.Set;
import java.util.TreeSet;
import java.lang.Math;

public class Quadratic {

    /**
     * Find the integer roots of a quadratic equation, ax^2 + bx + c = 0.
     * @param a coefficient of x^2
     * @param b coefficient of x
     * @param c constant term.  Requires that a, b, and c are not ALL zero.
     * @return all integers x such that ax^2 + bx + c = 0.
     */
    public static Set<Integer> roots(int a, int b, int c) {
        
        // test the pre-condition(s)
        assert ((a > 0) || (b > 0) || (c > 0)) : "All coefficients are <= 0";
        
        final Set<Integer> roots = new TreeSet<Integer>();
        
        // test for degeneracy
        if (a == 0) {
            
            // the root is (-c/b)
            roots.add(-c/b);
            
            return roots;
        }
        
        // find the discriminant
        final double D = (double)b*b - (double)4*a*c;
        
        // find roots based on the value of D
        if (D > 0) {
            final double [] root = new double[2];
            
            root[0] = (double)(-b - Math.sqrt(D)) / (2 * a);
            root[1] = (double)(-b + Math.sqrt(D)) / (2 * a);
            
            // add the calculated roots to 'roots' set if they are integral
            for (int i = 0; i < 2; i++) {
                if (root[i] == (int)root[i]) {
                    roots.add((int)root[i]);
                }
            }
        }
        else if (D == 0) {
            final double root = (double)(-b) / (2 * a);
            
            // add the calculated root to 'roots' set if it is integral
            if (root == (int)root) {
                roots.add((int)root);
            }
        }
        
        // test the post-condition(s)
        for (int root : roots) {
            assert ((a*root*root + b*root + c) <= 0.1) : "Inaccurate roots";
        }
        
        return roots;
    }

    
    /**
     * Main function of program.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("For the equation x^2 - 4x + 3 = 0, the possible solutions are:");
        Set<Integer> result = roots(1, -4, 3);
        System.out.println(result);
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
