package expressivo;

import java.util.Map;

public class Variable implements Expression {

    private String name;
    // rep invariant:
    //   name is a non-null, non-empty String.
    //
    // abstraction function:
    //   name represents the name of a variable in an Expression.
    //
    // safety from rep exposure:
    //   name is private. Copies of name are returned.

    public Variable(String name) {
        this.name = name;

        checkRep();
    }

    private void checkRep() {
        if ((name == null) || (name.length() == 0)) {
            throw new RuntimeException("rep invariant failure");
        }
    }

    @Override
    public String toString() {
        return new String(name);
    }

    @Override
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof Variable)) return false;

        return name.equals(((Variable) thatObject).toString());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public void differentiate(String variable) {
        if (variable.equals(name)) {
            name = "1.0";
        }
        else {
            name = "0.0";
        }
    }

    public void simplify(Map<String,Double> environment) {
        // set variable name to its value if environment contains it
        if (environment.containsKey(name)) {
            name = environment.get(name).toString();
        }
    }

}
