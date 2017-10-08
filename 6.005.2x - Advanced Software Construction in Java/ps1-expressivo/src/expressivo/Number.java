package expressivo;

import java.util.Map;

public class Number implements Expression {

    private Double number;
    // rep invariant:
    //   number represents a nonnegative decimal value.
    //
    // abstraction function
    //   represents a single number in an Expression.
    //
    // safety from rep exposure:
    //   number is private. Copies of number are returned.

    public Number(double number) {
        this.number = number;

        checkRep();
    }

    private void checkRep() {
        if (number < 0) {
            throw new RuntimeException("rep invariant failure");
        }
    }

    /**
     * Returns the String equivalent of number as-is.
     */
    @Override
    public String toString() {
        return number.toString();
    }

    @Override
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof Number)) return false;

        return number.equals(((Number) thatObject).number);
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }

    public void differentiate(String variable) {
        number = 0.0;
    }

    public void simplify(Map<String,Double> environment) {
        return;
    }

}
