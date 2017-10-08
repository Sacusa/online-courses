package expressivo.beta;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import expressivo.Commands;
import expressivo.Expression;

public class JavaInterfaceTest {
    
    @Test(expected=IllegalArgumentException.class)
    public void testParseInvalid() {
        Expression.parse("!moo");
    }
    
    @Test
    public void testParseEquals() {
        Object expr1 = Expression.parse("x * x");
        Object expr2 = Expression.parse("x * x");
        assertEquals("Expected identical expressions to be equal",
                expr1, expr2);
    }
    
    @Test
    public void testParseToString() {
        Expression expr = Expression.parse("1 + 2");
        String out = expr.toString();
        assertEquals("(after removing whitespace, parens, and \"\\.0*\" from actual output \"" + out + "\")",
                "1+2", out.replaceAll("[ ()]", "")
                          .replaceAll("\\.0*", ""));
    }
    
    @Test
    public void testDifferentiate() {
        String deriv = Commands.differentiate("x", "x");
        assertEquals("(after removing whitespace, parens, \"\\.0*\", and permutations of \"\\+0\\*x\" from actual output \"" + deriv + "\")",
                "1", deriv.replaceAll("[ ()]", "")
                          .replaceAll("\\.0*", "")
                          .replaceFirst("\\+0\\*x|\\+x\\*0|\\+0|0\\*x\\+|x\\*0\\+|0\\+", ""));
    }
    
    @Test
    public void testSimplify() {
        Map<String, Double> environment = new HashMap<String, Double>();
        environment.put("x", 2.0);
        String simple = Commands.simplify("x + x", environment);
        assertEquals("(after removing whitespace and \"\\.0*\" from actual output \"" + simple + "\")",
                "4", simple.replace(" ", "")
                           .replaceFirst("\\.0*", ""));
    }

    @Test
    public void testSimplifyTwice(){
        Map<String, Double> environment = new HashMap<String, Double>();
        environment.put("x", 2.0);
        environment.put("y", 5.0);
        String partiallySimple = Commands.simplify("x + z", environment);
        Map<String, Double> environmentZ = new HashMap<String, Double>();
        environmentZ.put("z", 7.0);
        String completelySimple = Commands.simplify(partiallySimple, environmentZ);
        assertEquals("(from " + partiallySimple + " with z = " + environmentZ.get("z") +
                " after removing whitespace and \"\\.0*\" from actual output \"" + completelySimple + "\")",
                "9", completelySimple.replace(" ","")
                                     .replaceAll("\\.0*",""));
    }
}
