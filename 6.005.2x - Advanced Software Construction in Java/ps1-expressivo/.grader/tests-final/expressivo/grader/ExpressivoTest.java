package expressivo.grader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import expressivo.Commands;
import expressivo.Expression;
import expressivo.staff.Parser;
import expressivo.staff.Polynomial;

@RunWith(Parameterized.class)
public class ExpressivoTest {
    
    @Parameters(name="{0}")
    public static Iterable<Object[]> expressions() {
        return Arrays.asList(new Object[][] {
                { "zero",                 "0",                "" },
                { "nonzero integer",      "5",                "" },
                { "decimal-fraction < 1", "0.1",              "" },
                { "decimal-fraction > 1", "2.5",              "" },
                { "trailing zeros",       "1.000000",         "" },
                { "no leading zero",      ".77",              "" },
                { "decimal-fraction very small", "0.000001",  "" },
                { "integer very large",   "9007199254740992", "" },
                { "x",                    "x",                "" },
                { "y",                    "y",                "" },
                { "other variable",       "letterzed",        "" },
                
                { "variable with parens",       "(x)",                       "" },
                { "variable with extra parens", "(((x)))",                   "" },
                { "add",                        "x + x",                     "" },
                { "add with extra parens",      "((((x)) + ((x))))",         "" },
                { "times",                      "x * x",                     "" },
                { "times with extra parens",    "((((x)) * ((x))))",         "" },
                { "* ( * )",                    "x * (x * x)",               "" },
                { "( * ) *",                    "(x * x) * x",               "" },
                { "( * ) * ( * )",              "(x * x) * (y * y)",         "" },
                { "* * * * *",                  "x * x * y * y * z * z * x", "" },
                
                { "integers add",                "(10 + 32 + 38 + 74) * x",        "" },
                { "integers times",              "83 * 28 * 1 * 30 * x",           "" },
                { "integers with various ops",   "((40 + 20) * 30 * 43 + 83 * 28 + 1 + 30) * x", "differentiate" },
                { "decimal-fractions add",       "(0.34 + 3.4 + 98.0 + 82.4) * x", "" },
                { "decimal-fractions times",     "(0.34 * 3.4 * 98.0 * 82.4) * x", "" },
                { "decimal-fractions with various ops", "(20.02 * 40.04 + 1.0 * 38.281 + 54.289 + 83.01) * x", "" },
                { "decimal-fractions integers add",     "(0.10 + 3 + 38.897 + 74) * x", "" },
                { "decimal-fractions integers times",   "(8.3 * 19.28 * 1 * 3.0) * x",  "" },
                { "decimal-fractions integers with various ops", "(40 + 0.2) * (3 + 43) * (23 + 2.8 * 0.4) * (1 + 30) * x", "differentiate" },
                
                { "various variable names", "x * foo * jigglywiggly * HeLlOwOrLd * x",     "" },
                { "nested left",            "(((((((a + b) * c) * d) + e) + f) * g) * x)", "differentiate" },
                { "nested center",          "(((a + b) * ((c * (d + e)) * f)) * (g + x))", "differentiate" },
                { "nested right",           "(a * (b + (c * (d * (e + (f * (g + x)))))))", "differentiate" },
                { "nested balanced",        "(((a + b) * (c * d)) + ((e * f) * (g + x)))", "differentiate" },
                { "polynomial in x/y/z",    "(x * y * (x + z) + x) * (x * x + y * z)",     "differentiate" },
                { "polynomial in x/y/z with extra parens", "((((x * y) * (x + z)) + x) * ((x * x) + (y * z)))", "differentiate" },
                { "polynomial in x/y/z and various other variables", "((((4.0 * variable) * (x + TiMiThY)) + timithy) * " +
                                                                     "((timtim * x) + (JackOfSpades * jamoozywashere)))", "differentiate" },
        });
    }
    
    private final String expr;
    private final Polynomial poly;
    private final Polynomial d_dx;
    private final Polynomial d_dy;
    private final String skip;
    
    public ExpressivoTest(String name, String expr, String skip) {
        this.expr = expr;
        this.poly = Parser.parse(expr);
        
        this.d_dx = poly.differentiate("x");
        this.d_dy = poly.differentiate("y");
        this.skip = skip;
    }
    
    @Test public void testParse() {
        if (skip.contains("parse")) { return; /* manual skip */ }
        
        final Expression student;
        final Polynomial result;
        try {
            student = Expression.parse(expr);
        } catch (Exception e) {
            throw new RuntimeException("error calling parse()", e);
        }
        try {
            result = Parser.parse(student.toString());
        } catch (Exception e) {
            throw new RuntimeException("invalid Expression.toString (staff parser could not parse)", e);
        }
        assertEquals("(after converting to canonical form)", poly.round(4), result.round(4));
    }
    
    @Test public void testDifferentiate_dx() {
        if (skip.contains("differentiate")) { return; /* manual skip */ }
        
        final String student;
        final Polynomial result;
        try {
            student = Commands.differentiate(expr, "x");
        } catch (Exception e) {
            throw new RuntimeException("error calling differentiate()", e);
        }
        try {
            result = Parser.parse(student);
        } catch (Exception e) {
            throw new RuntimeException("invalid derivative expression (staff parser could not parse)", e);
        }
        assertEquals("(after converting to canonical form)", d_dx.round(2), result.round(2));
    }
    
    @Test public void testDifferentiate_dy() {
        if (skip.contains("differentiate")) { return; /* manual skip */ }
        
        final String student;
        final Polynomial result;
        try {
            student = Commands.differentiate(expr, "y");
        } catch (Exception e) {
            throw new RuntimeException("error calling differentiate()", e);
        }
        try {
            result = Parser.parse(student);
        } catch (Exception e) {
            throw new RuntimeException("invalid derivative expression (staff parser could not parse)", e);
        }
        assertEquals("(after converting to canonical form)", d_dy.round(2), result.round(2));
    }
    
    @Test public void testSimplify_xyz() {
        if (skip.contains("simplify")) { return; /* manual skip */ }
        
        final String student;
        final Polynomial result;
              
        Map<String, Double> environment = new HashMap<String, Double>();
        environment.put("x", 2.0);
        environment.put("y", 3.0);
        environment.put("z", 4.0);
        try {
            student = Commands.simplify(expr, environment);
        } catch (Exception e) {
            throw new RuntimeException("error calling simplify()", e);
        }
        try {
            result = Parser.parse(student);
        } catch (Exception e) {
            throw new RuntimeException("invalid simplified expression (staff parser could not parse)", e);
        }
        Polynomial evaluatedPoly = poly.evaluate(environment);
        assertEquals("(after converting to canonical form)", poly.evaluate(environment).round(4), result.round(4));
        if (evaluatedPoly.isConstant()){
            assertTrue("Expected simplified expression " + student + " to be a constant.", 
                    student.replaceAll("[ ()]", "").matches("(\\d*\\.)?\\d+"));
        }
    }
}
