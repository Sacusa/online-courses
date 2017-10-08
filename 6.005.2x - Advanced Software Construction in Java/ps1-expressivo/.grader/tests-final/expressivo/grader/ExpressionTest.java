package expressivo.grader;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import expressivo.Expression;

@RunWith(Parameterized.class)
public class ExpressionTest {
    
    @Parameters(name="{0}")
    public static Iterable<Object[]> expressions() {
        return Arrays.asList(new Object[][] {
                { "same one variable",         "z", "z", true },
                { "same one operator",          "q + w", "q + w", true },
                { "same expression with numbers/variables/operators",        "7.2 * (x * y + 9 * foo)", "7.2 * (x * y + 9 * foo)", true },
                { "var and same var parenthesized",   "x", "((x))", true },
                { "one operator and same operator parenthesized",    "x + y", "((x + y))", true },
                { "* + * same as (*)+(*)", "(4.2 * x) + (y * 5)", "4.2 * x + y * 5", true },
                { "((*)+)* different from * + *",      "((3 * 5) + 7) * 2", "3 * 5 + 7 * 2", false },
                { "+ not commutative under structual equality",    "x + y", "y + x", false },
                { "* not commutative under structual equality",   "(x + y) * (z + foo)", "(z + foo) * (x + y)", false },
        });
    }
    
    private final String left;
    private final String right;
    private final boolean equal;
    
    public ExpressionTest(String name, String left, String right, boolean equal) {
        this.left = left;
        this.right = right;
        this.equal = equal;
    }
    
    @Test
    public void testEquals() {
        Object a = Expression.parse(left);
        Object b = Expression.parse(right);
        if (equal) {
            assertTrue("expected expr \"" + left + "\" equals expr \"" + right + "\"",
                    a.equals(b) && b.equals(a));
        } else {
            assertFalse("expected expr \"" + left + "\" not equals expr \"" + right + "\"",
                    a.equals(b) || b.equals(a));
        }
    }
    
    @Test
    public void testHashCode() {
        Object a = Expression.parse(left);
        Object b = Expression.parse(right);
        if (equal) {
            assertTrue("expected hashCode \"" + left + "\" equals hashCode \"" + right + "\", "
                    + "but " + a.hashCode() + " != " + b.hashCode(),
                    a.hashCode() == b.hashCode());
        }
        // else nothing to assert
    }
    
    @Test
    public void testToStringConsistent() {
        Object a = Expression.parse(left);
        Object b = Expression.parse(right);
        if (equal) {
            assertTrue("expected toString \"" + left + "\" equals toString \"" + right + "\", "
                    + "but " + a.toString() + " neq " + b.toString(),
                    a.toString().equals(b.toString()));
        } else {
            assertFalse("expected toString \"" + left + "\" not equals toString \"" + right + "\", "
                    + "but " + a.toString() + " eq " + b.toString(),
                    a.toString().equals(b.toString()));
        }
    }
}
