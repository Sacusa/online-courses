package expressivo.grader;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import expressivo.Expression;

@RunWith(Parameterized.class)
public class IllegalTest {
    
    @Parameters(name="{0}")
    public static Iterable<Object[]> expressions() {
        return Arrays.asList(new Object[][] {
                { "missing op",       "3 x" },
                { "adjacent vars",    "y x" },
                { "unbalanced open",  "(x * x" },
                { "unbalanced close", "x * x)" },
                { "empty parens",     "()" },
                { "empty nesting",    "((()))" },
                { "prefix op",        "(+ 3 x)" },
                { "postfix op",       "(x 3 +)" },
                { "leading op",       "(* 3 * x)" },
                { "trailing op",      "(x * 3 *)" },
                { "multiple decimal points", "1.2.3" },
                { "lone op",          "+" },
                { "lone decimal point", "." },
                { "lone nested op",   "(*)" },
        });
    }
    
    private final String expr;
    
    public IllegalTest(String name, String expr) {
        this.expr = expr;
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testParse() {
        Expression.parse(expr);
        throw new ThereWasNoException("on input \"" + expr + "\"");
    }
}

class ThereWasNoException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public ThereWasNoException(String msg) {
        super(msg);
    }
}
