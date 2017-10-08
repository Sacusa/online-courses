package expressivo;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import lib6005.parser.*;

/**
 * An immutable data type representing a polynomial expression of:
 *   + and *
 *   nonnegative integers and floating-point numbers
 *   variables (case-sensitive nonempty strings of letters)
 * 
 * <p>PS1 instructions: this is a required ADT interface.
 * You MUST NOT change its name or package or the names or type signatures of existing methods.
 * You may, however, add additional methods, or strengthen the specs of existing methods.
 * Declare concrete variants of Expression in their own Java source files.
 */
public interface Expression {

    // Datatype definition
    // Expression = Number(number:Double)
    //              + Variable(name:String)
    //              + Product(left:Expression, right:Expression)
    //              + Sum(left:Expression, right:Expression)

    enum ExpressionGrammar {ROOT, SUM, PRODUCT, PRIMITIVE, NUMBER, VARIABLE, WHITESPACE};

    /**
     * Parse an expression.
     * @param input expression to parse, as defined in the PS1 handout.
     * @return expression AST for the input
     * @throws IllegalArgumentException if the expression is invalid
     */
    public static Expression parse(String input) {
        try {
            // compile the grammar
            Parser<ExpressionGrammar> parser = GrammarCompiler.compile(new File
               ("/home/sacusa/programs/java/workspace/ps1-expressivo/src/expressivo/Expression.g"),
               ExpressionGrammar.ROOT);

            // construct a tree from input using the above compiled grammer 
            ParseTree<ExpressionGrammar> tree = parser.parse(input);

            return Expression.buildAST(tree);
        }

        // catch exceptions from GrammarCompiler
        catch (UnableToParseException | IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * A recursive function to build abstract syntax tree (AST) from the argument tree.
     * @param tree  The tree to build AST from.
     * @return      An Expression representing the AST.
     */
    public static Expression buildAST(ParseTree<ExpressionGrammar> tree) {

        // variables for Sum and Product
        boolean isFirstOperand = true;
        Expression result = null;

        switch(tree.getName()) {
        /**
         * Since tree is a ParseTree parameterized by the type ExpressionGrammar, tree.getName() 
         * returns an instance of the ExpressionGrammar enum. This allows the compiler to check
         * that we have covered all the cases.
         */
        case NUMBER:
            /**
             * A number will be a terminal containing a number.
             */
            return new Number(Double.parseDouble(tree.getContents()));

        case VARIABLE:
            /**
             * A variable will be a terminal containing a symbol variable.
             */
            return new Variable(tree.getContents());

        case PRIMITIVE:
            /**
             * A primitive will either have a number, a variable, a sum or a product as a child.
             * By checking which one, we can determine which case we are in.
             */
            if (!tree.childrenByName(ExpressionGrammar.PRODUCT).isEmpty()) {
                return Expression.buildAST(tree.childrenByName(ExpressionGrammar.PRODUCT).get(0));
            }
            if (!tree.childrenByName(ExpressionGrammar.SUM).isEmpty()) {
                return Expression.buildAST(tree.childrenByName(ExpressionGrammar.SUM).get(0));
            }
            if (!tree.childrenByName(ExpressionGrammar.NUMBER).isEmpty()) {
                return Expression.buildAST(tree.childrenByName(ExpressionGrammar.NUMBER).get(0));
            }
            if (!tree.childrenByName(ExpressionGrammar.VARIABLE).isEmpty()) {
                return Expression.buildAST(tree.childrenByName(ExpressionGrammar.VARIABLE).get(0));
            }            

        case PRODUCT:
            /**
             * A product will have one or more children that need to be multiplied together.
             * Note that we only care about the children that are primitive. There may also be 
             * some whitespace children which we want to ignore.
             */
            isFirstOperand = true;
            result = null;

            for (ParseTree<ExpressionGrammar> child :
                tree.childrenByName(ExpressionGrammar.PRIMITIVE)) {
                if (isFirstOperand) {
                    result = Expression.buildAST(child);
                    isFirstOperand = false;
                }
                else {
                    result = new Product(result, Expression.buildAST(child));
                }
            }

            if (isFirstOperand) {
                throw new RuntimeException("Product must have a non whitespace operand: " + tree);
            }

            return result;

        case SUM:
            /**
             * A sum will have one or more children that need to be summed together.
             * Note that we only care about the children that are products. There may also be 
             * some whitespace children which we want to ignore.
             */
            isFirstOperand = true;
            result = null;

            for (ParseTree<ExpressionGrammar> child :
                tree.childrenByName(ExpressionGrammar.PRODUCT)) {
                if (isFirstOperand) {
                    result = Expression.buildAST(child);
                    isFirstOperand = false;
                }
                else {
                    result = new Sum(result, Expression.buildAST(child));
                }
            }

            if (isFirstOperand) {
                throw new RuntimeException("Product must have a non whitespace operand: " + tree);
            }

            return result;
        
        case ROOT:
            /**
             * The root is a sum, along with (potentially) some whitespace. We are using
             * Sum-of-Products (SOP) representation.
             */
            return Expression.buildAST(tree.childrenByName(ExpressionGrammar.SUM).get(0));
        
        case WHITESPACE:
            /**
             * Since we are always avoiding calling Expression.buildAST with whitespace, 
             * the code should never make it here.
             */
            throw new RuntimeException("Reached unexpected whitespace: " + tree);
        }
        
        // we will never make it here
        return null;
    }

    /**
     * @return a parsable representation of this expression, such that
     * for all e:Expression, e.equals(Expression.parse(e.toString())).
     */
    @Override 
    public String toString();

    /**
     * @param thatObject any object
     * @return true if and only if this and thatObject are structurally-equal
     * Expressions, as defined in the PS1 handout.
     */
    @Override
    public boolean equals(Object thatObject);

    /**
     * @return hash code value consistent with the equals() definition of structural
     * equality, such that for all e1,e2:Expression,
     *     e1.equals(e2) implies e1.hashCode() == e2.hashCode()
     */
    @Override
    public int hashCode();

    /**
     * Differentiates the Expression with respect to variable.
     * @param variable  a non-null, non-empty string.
     */
    public void differentiate(String variable);
    
    /**
     * Simplify the expression with the variables and associated values
     * given in the environement.
     * @param environment  The set of variables and associated values.
     * @return  if the Expression resolves to a single value, it is returned.
     *          Otherwise, Double.POSITIVE_INFINITY is returned.
     */
    public void simplify(Map<String,Double> environment);

    /* Copyright (c) 2015-2017 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires permission of course staff.
     */
}
