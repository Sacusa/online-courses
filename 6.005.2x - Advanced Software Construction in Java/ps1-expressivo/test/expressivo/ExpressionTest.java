/* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;

/**
 * Tests for the Expression abstract data type.
 */
public class ExpressionTest {

    /**
     * Testing strategy
     * =================
     * 
     * Expression  single number, single variable, compound expression
     * 
     * Tested with each form of expression on both sides of the operators + and *.
     */

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    String input;
    Map<String, Double> environment = new HashMap<String, Double>();

    // covers single number
    @Test
    public void testSingleNum() {
        input = "3";

        Expression expr1 = Expression.parse(input);
        Expression expr2 = Expression.parse(input);

        // check toString()
        assertEquals("3.0", expr1.toString());

        // check equals()
        assertTrue(expr1.equals(Expression.parse(input)));

        // check hashCode()
        assertEquals(expr1.hashCode(), Expression.parse(input).hashCode());

        // check differentiate()
        expr1.differentiate("x");
        assertEquals("0.0", expr1.toString());

        // check simplify()
        environment.clear();
        environment.put("x", 5.0);
        environment.put("y", 3.0);
        expr2.simplify(environment);
        assertEquals(3, Double.parseDouble(expr2.toString()), 0.0001);
    }

    // covers single variable
    @Test
    public void testSingleVar() {
        input = "x";

        Expression expr1 = Expression.parse(input);
        Expression expr2 = Expression.parse(input);

        // check toString()
        assertEquals(input, expr1.toString());

        // check equals()
        assertTrue(expr1.equals(Expression.parse(input)));

        // check hashCode()
        assertEquals(expr1.hashCode(), Expression.parse(input).hashCode());

        // check differentiate()
        expr1.differentiate(input);
        assertEquals("1.0", expr1.toString());

        // check simplify()
        environment.clear();
        environment.put("x", 5.0);
        environment.put("y", 3.0);
        expr2.simplify(environment);
        assertEquals(5.0, Double.parseDouble(expr2.toString()), 0.0001);
    }

    // covers single var + single num
    @Test
    public void testVarPlusNum() {
        input = "(x + 4.7)";

        Expression expr1 = Expression.parse(input);
        Expression expr2 = Expression.parse(input);

        // check toString()
        assertEquals(input, expr1.toString());

        // check equals()
        assertTrue(expr1.equals(Expression.parse(input)));

        // check hashCode()
        assertEquals(expr1.hashCode(), Expression.parse(input).hashCode());

        // check differentiate()
        expr1.differentiate("x");
        assertEquals("(1.0 + 0.0)", expr1.toString());

        // check simplify()
        environment.clear();
        environment.put("x", 5.0);
        environment.put("y", 3.0);
        expr2.simplify(environment);
        assertEquals(9.7, Double.parseDouble(expr2.toString()), 0.0001);
    }

    // covers single num * single var
    @Test
    public void testNumTimesVar() {
        input = "4.7*x";

        Expression expr1 = Expression.parse(input);
        Expression expr2 = Expression.parse(input);

        // check toString()
        assertEquals(input, expr1.toString());

        // check equals()
        assertTrue(expr1.equals(Expression.parse(input)));

        // check hashCode()
        assertEquals(expr1.hashCode(), Expression.parse(input).hashCode());

        // check differentiate()
        expr1.differentiate("x");
        assertEquals("(4.7*1.0 + x*0.0)*1.0", expr1.toString());

        // check simplify()
        environment.clear();
        environment.put("x", 5.0);
        environment.put("y", 3.0);
        expr2.simplify(environment);
        assertEquals(23.5, Double.parseDouble(expr2.toString()), 0.0001);
    }

    // covers single num + compound expression
    @Test
    public void testNumPlusExpr() {
        input = "(1.9 + (4.7 * y))";

        Expression expr1 = Expression.parse(input);
        Expression expr2 = Expression.parse(input);

        // check toString()
        assertEquals("(1.9 + 4.7*y)", expr1.toString());

        // check equals()
        assertTrue(expr1.equals(Expression.parse(input)));

        // check hashCode()
        assertEquals(expr1.hashCode(), Expression.parse(input).hashCode());

        // check differentiate()
        expr1.differentiate("y");
        assertEquals("(0.0 + (4.7*1.0 + y*0.0)*1.0)", expr1.toString());

        // check simplify()
        environment.clear();
        environment.put("x", 5.0);
        environment.put("y", 3.0);
        expr2.simplify(environment);
        assertEquals(16.0, Double.parseDouble(expr2.toString()), 0.0001);
    }

    // covers compound expression * single num
    @Test
    public void testExprTimesNum() {
        input = "(4.7 + y) * 1.9";

        Expression expr1 = Expression.parse(input);
        Expression expr2 = Expression.parse(input);

        // check toString()
        assertEquals("(4.7 + y)*1.9", expr1.toString());

        // check equals()
        assertTrue(expr1.equals(Expression.parse(input)));

        // check hashCode()
        assertEquals(expr1.hashCode(), Expression.parse(input).hashCode());

        // check differentiate()
        expr1.differentiate("y");
        assertEquals("((4.7 + y)*0.0 + 1.9*(0.0 + 1.0))*1.0", expr1.toString());

        // check simplify()
        environment.clear();
        environment.put("x", 5.0);
        environment.put("y", 3.0);
        expr2.simplify(environment);
        assertEquals(14.6299, Double.parseDouble(expr2.toString()), 0.0001);
    }

    // covers compound expression + single var
    @Test
    public void testExprPlusVar() {
        input = "(4.7 + x) + y";

        Expression expr1 = Expression.parse(input);
        Expression expr2 = Expression.parse(input);
        Expression expr3 = Expression.parse(input);

        // check toString()
        boolean isCorrect = expr1.toString().equals("((4.7 + x) + y)") ||
                expr1.toString().equals("(4.7 + (x + y))");
        assertTrue(isCorrect);

        // check equals()
        assertTrue(expr1.equals(Expression.parse(input)));

        // check hashCode()
        assertEquals(expr1.hashCode(), Expression.parse(input).hashCode());

        // check differentiate()
        expr1.differentiate("x");
        expr2.differentiate("y");
        assertEquals("((0.0 + 1.0) + 0.0)", expr1.toString());
        assertEquals("((0.0 + 1.0) + 0.0)", expr1.toString());

        // check simplify()
        environment.clear();
        environment.put("x", 5.0);
        environment.put("y", 3.0);
        expr3.simplify(environment);
        assertEquals(12.7, Double.parseDouble(expr3.toString()), 0.0001);
    }

    // covers single var * compound expression
    @Test
    public void testVarTimesExpr() {
        input = "x * (4.7 * 1.9)";

        Expression expr1 = Expression.parse(input);
        Expression expr2 = Expression.parse(input);

        // check toString()
        assertEquals("x*4.7*1.9", expr1.toString());

        // check equals()
        assertTrue(expr1.equals(Expression.parse(input)));

        // check hashCode()
        assertEquals(expr1.hashCode(), Expression.parse(input).hashCode());

        // check differentiate()
        expr1.differentiate("x");
        assertEquals("(x*(4.7*0.0 + 1.9*0.0)*1.0 + 4.7*1.9*1.0)*1.0", expr1.toString());

        // check simplify()
        environment.clear();
        environment.put("x", 5.0);
        environment.put("y", 3.0);
        expr2.simplify(environment);
        assertEquals(44.65, Double.parseDouble(expr2.toString()), 0.0001);
    }

    // covers compound expression + compound expression
    @Test
    public void testExprPlusExpr() {
        input = "(2.7 * x) + (y + 9.8)";

        Expression expr1 = Expression.parse(input);
        Expression expr2 = Expression.parse(input);
        Expression expr3 = Expression.parse(input);

        // check toString()
        assertEquals("(2.7*x + (y + 9.8))", expr1.toString());

        // check equals()
        assertTrue(expr1.equals(Expression.parse(input)));

        // check hashCode()
        assertEquals(expr1.hashCode(), Expression.parse(input).hashCode());

        // check differentiate()
        expr1.differentiate("x");
        expr2.differentiate("y");
        assertEquals("((2.7*1.0 + x*0.0)*1.0 + (0.0 + 0.0))", expr1.toString());
        assertEquals("((2.7*0.0 + x*0.0)*1.0 + (1.0 + 0.0))", expr2.toString());

        // check simplify()
        environment.clear();
        environment.put("x", 5.0);
        environment.put("y", 3.0);
        expr3.simplify(environment);
        assertEquals(26.3, Double.parseDouble(expr3.toString()), 0.0001);
    }

    // covers compound expression * compound expression
    @Test
    public void testExprTimesExpr() {
        input = "(2.7 * x) * (y + 9.8)";

        Expression expr1 = Expression.parse(input);
        Expression expr2 = Expression.parse(input);
        Expression expr3 = Expression.parse(input);

        // check toString()
        assertEquals("2.7*x*(y + 9.8)", expr1.toString());

        // check equals()
        assertTrue(expr1.equals(Expression.parse(input)));

        // check hashCode()
        assertEquals(expr1.hashCode(), Expression.parse(input).hashCode());

        // check differentiate()
        expr1.differentiate("x");
        expr2.differentiate("y");
        assertEquals("(2.7*x*(0.0 + 0.0) + (y + 9.8)*(2.7*1.0 + x*0.0)*1.0)*1.0",
                expr1.toString());
        assertEquals("(2.7*x*(1.0 + 0.0) + (y + 9.8)*(2.7*0.0 + x*0.0)*1.0)*1.0",
                expr2.toString());

        // check simplify()
        environment.clear();
        environment.put("x", 5.0);
        environment.put("y", 3.0);
        expr3.simplify(environment);
        assertEquals(172.8, Double.parseDouble(expr3.toString()), 0.0001);
    }

}
