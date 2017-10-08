package expressivo;

import java.util.Map;

public class Sum implements Expression {

    private Expression left;
    private Expression right;
    // rep invariant:
    //   left and right are non-null operands of a sum.
    //
    // abstraction function:
    //   left and right represent left and right operands of a sum.
    //
    // safety from rep exposure:
    //   left and right are private. Copies of left and right are returned.

    public Sum(Expression left, Expression right) {
        this.left = left;
        this.right = right;

        checkRep();
    }

    private void checkRep() {
        if ((left == null) || (right == null)) {
            throw new RuntimeException("rep invariant failure");
        }
    }

    /**
     * Returns the String equivalent of a product as:
     *   left + right 
     */
    @Override
    public String toString() {
        Expression infinity = new Number(Double.POSITIVE_INFINITY);

        // right = infinity implies simplified value
        if (right.equals(infinity)) {
            return left.toString();
        }

        return "(" + left.toString() + " + " + right.toString() + ")";
    }

    @Override
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof Sum)) return false;

        Sum that = (Sum) thatObject;

        return left.equals(that.left) && right.equals(that.right);
    }

    @Override
    public int hashCode() {
        return (37 * left.hashCode() + 13) + (31 * right.hashCode() + 11);
    }

    public void differentiate(String variable) {
        left.differentiate(variable);
        right.differentiate(variable);
    }

    public void simplify(Map<String,Double> environment) {
        left.simplify(environment);
        right.simplify(environment);
        String leftValue = left.toString();
        String rightValue = right.toString();

        // set left value to a Number if it has resolved to a double
        if (leftValue.matches("\\d+(\\.\\d+)?")) {
            left = new Number(Double.parseDouble(leftValue));
            leftValue = left.toString();
        }

        // set right value to a Number if it has resolved to a double
        if (rightValue.matches("\\d+(\\.\\d+)?")) {
            right = new Number(Double.parseDouble(rightValue));
            rightValue = right.toString();
        }

        // if both left and right are doubles, set left = sum and right = positive infinity
        if (leftValue.matches("\\d+(\\.\\d+)?") && rightValue.matches("\\d+(\\.\\d+)?")) {
            left = new Number(Double.parseDouble(leftValue) + Double.parseDouble(rightValue));
            right = new Number(Double.POSITIVE_INFINITY);
        }
    }

}
