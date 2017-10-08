package expressivo.staff;

import static expressivo.staff.Type.*;
import expressivo.staff.Lexer.Token;

/**
 * Expression parser.
 */
public class Parser {
    
    /** Parse an expression. */
    public static Polynomial parse(String expression) {
        return parse(expression, false);
    }
    
    /** Parse an expression. */
    public static Polynomial parse(String expression, boolean isSumOfProductsForm) {
        try {
            Lexer lexer = new Lexer(expression, isSumOfProductsForm);
            Parser parser = new Parser(lexer, isSumOfProductsForm);
            return parser.parse();
        } catch (BadCharacterError | SyntaxError e) {
            throw new SyntaxError(e.getMessage() +
                    " in " + (isSumOfProductsForm ? "isSumOfProductsForm " : "") + "expression" +
                    " \"" + expression + "\"", e);
        }
    }
    
    private final Lexer lexer;
    private Token next;
    
    private final boolean isSumOfProductsForm;

    /**
     * Creates a new parser that parses tokens from the passed lexer for input.
     * @param lexer the lexer
     * @param isSumOfProductsForm whether the input will be in isSumOfProductsForm form
     */
    public Parser(Lexer lexer, boolean isSumOfProductsForm) {
        this.isSumOfProductsForm = isSumOfProductsForm;
        this.lexer = lexer;
        next();
    }

    /** Parses and evaluates according to the initial production rule. */
    public Polynomial parse() {
        final Polynomial value = polynomial();
        eat(END_OF_INPUT);
        return value;
    }

    private Polynomial polynomial() {
        Polynomial p = term();

        while (next.type == PLUS) {
            next();
            int last = lexer.last();
            Polynomial q = term();
            if (isSumOfProductsForm && p.containsTerm(q)) {
                throw new SyntaxError(String.format(
                        "Character %d: duplicate term in isSumOfProductsForm expression", last));
            }
            p = p.plus(q);
        }

        return p;
    }

    /** Parse either any term. */
    private Polynomial term() {
        Polynomial p = factor();

        while (next.type == TIMES) {
            next();
            p = p.times(factor());
        }

        return p;
    }

    private Polynomial factor() {
        if (next.type == LEFT_PARENTHESIS) {
            next();
            final Polynomial p = polynomial();
            eat(RIGHT_PARENTHESIS);
            return p;
        }
        return unary();
    }

    private Polynomial unary() {
        if (next.type == VARIABLE) {
            return new Polynomial(eatVar());
        } else if (next.type == NUMBER) {
            return new Polynomial(eatNum());
        }
        throw new SyntaxError(String.format(
                    "Character %d: expected variable or number, got %s", lexer.last(), next));
    }

    /**
     * Gets the value of a variable.  Throws an exception if there is no variable.
     * @throws SyntaxError if there was no variable
     */
    private String eatVar() {
        if (next.type != VARIABLE) {
            throw new SyntaxError(lexer.last(), VARIABLE, next);
        }
        final String s = next.text;
        next();
        return s;
    }

    private double eatNum() {
        if (next.type != NUMBER) {
            throw new SyntaxError(lexer.last(), NUMBER, next);
        }
        final double d = next.value;
        next();
        return d;
    }

    /**
     * Eats the expected type.
     * @throws SyntaxError if the expected type was not the actual next type
     */
    private void eat(Type exp) {
        if (exp != next.type) {
            throw new SyntaxError(lexer.last(), exp, next);
        }
        next();
    }

    /** Updates {@link #next} by getting the lexer's next token. */
    private void next() { next = lexer.next(); }
}
