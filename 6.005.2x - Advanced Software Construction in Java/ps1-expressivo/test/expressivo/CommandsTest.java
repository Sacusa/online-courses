/* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Tests for the static methods of Commands.
 */
public class CommandsTest {

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
        
        assertEquals("0.0", Commands.differentiate(input, "x"));
        
        environment.clear();
        environment.put("x", 5.0);
        environment.put("y", 3.0);
        assertEquals(3, Double.parseDouble(Commands.simplify(input, environment)), 0.0001);
    }

    // covers single variable
    @Test
    public void testSingleVar() {
        input = "x";

        assertEquals("1.0", Commands.differentiate(input, "x"));
        
        environment.clear();
        environment.put("x", 5.0);
        environment.put("y", 3.0);
        assertEquals(5, Double.parseDouble(Commands.simplify(input, environment)), 0.0001);
    }

    // covers single var + single num
    @Test
    public void testVarPlusNum() {
        input = "(x + 4.7)";
        
        assertEquals("(1.0 + 0.0)", Commands.differentiate(input, "x"));
        
        environment.clear();
        environment.put("x", 5.0);
        environment.put("y", 3.0);
        assertEquals(9.7, Double.parseDouble(Commands.simplify(input, environment)), 0.0001);
    }

    // covers single num * single var
    @Test
    public void testNumTimesVar() {
        input = "4.7*x";

        assertEquals("(4.7*1.0 + x*0.0)*1.0", Commands.differentiate(input, "x"));
        
        environment.clear();
        environment.put("x", 5.0);
        environment.put("y", 3.0);
        assertEquals(23.5, Double.parseDouble(Commands.simplify(input, environment)), 0.0001);
    }

    // covers single num + compound expression
    @Test
    public void testNumPlusExpr() {
        input = "(1.9 + (4.7 * y))";
        
        assertEquals("(0.0 + (4.7*1.0 + y*0.0)*1.0)", Commands.differentiate(input, "y"));
        
        environment.clear();
        environment.put("x", 5.0);
        environment.put("y", 3.0);
        assertEquals(16, Double.parseDouble(Commands.simplify(input, environment)), 0.0001);
    }

    // covers compound expression * single num
    @Test
    public void testExprTimesNum() {
        input = "(4.7 + y) * 1.9";
        
        assertEquals("((4.7 + y)*0.0 + 1.9*(0.0 + 1.0))*1.0", Commands.differentiate(input, "y"));
        
        environment.clear();
        environment.put("x", 5.0);
        environment.put("y", 3.0);
        assertEquals(14.6299, Double.parseDouble(Commands.simplify(input, environment)), 0.0001);
    }

    // covers compound expression + single var
    @Test
    public void testExprPlusVar() {
        input = "(4.7 + x) + y";
        
        assertEquals("((0.0 + 1.0) + 0.0)", Commands.differentiate(input, "x"));
        assertEquals("((0.0 + 0.0) + 1.0)", Commands.differentiate(input, "y"));
        
        environment.clear();
        environment.put("x", 5.0);
        environment.put("y", 3.0);
        assertEquals(12.7, Double.parseDouble(Commands.simplify(input, environment)), 0.0001);
    }

    // covers single var * compound expression
    @Test
    public void testVarTimesExpr() {
        input = "x * (4.7 * 1.9)";
        
        assertEquals("(x*(4.7*0.0 + 1.9*0.0)*1.0 + 4.7*1.9*1.0)*1.0", Commands.differentiate(input, "x"));
        
        environment.clear();
        environment.put("x", 5.0);
        environment.put("y", 3.0);
        assertEquals(44.65, Double.parseDouble(Commands.simplify(input, environment)), 0.0001);
    }

    // covers compound expression + compound expression
    @Test
    public void testExprPlusExpr() {
        input = "(2.7 * x) + (y + 9.8)";
        
        assertEquals("((2.7*1.0 + x*0.0)*1.0 + (0.0 + 0.0))", Commands.differentiate(input, "x"));
        assertEquals("((2.7*0.0 + x*0.0)*1.0 + (1.0 + 0.0))", Commands.differentiate(input, "y"));
        
        environment.clear();
        environment.put("x", 5.0);
        environment.put("y", 3.0);
        assertEquals(26.3, Double.parseDouble(Commands.simplify(input, environment)), 0.0001);
    }

    // covers compound expression * compound expression
    @Test
    public void testExprTimesExpr() {
        input = "(2.7 * x) * (y + 9.8)";
        
        assertEquals("(2.7*x*(0.0 + 0.0) + (y + 9.8)*(2.7*1.0 + x*0.0)*1.0)*1.0",
                Commands.differentiate(input, "x"));
        assertEquals("(2.7*x*(1.0 + 0.0) + (y + 9.8)*(2.7*0.0 + x*0.0)*1.0)*1.0",
                Commands.differentiate(input, "y"));
        
        environment.clear();
        environment.put("x", 5.0);
        environment.put("y", 3.0);
        assertEquals(172.8, Double.parseDouble(Commands.simplify(input, environment)), 0.0001);
    }
    
}
