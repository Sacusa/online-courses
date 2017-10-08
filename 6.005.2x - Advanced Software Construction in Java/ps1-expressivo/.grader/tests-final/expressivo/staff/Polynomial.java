package expressivo.staff;

import static java.math.RoundingMode.HALF_EVEN;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Polynomial {
    private final Map<VarList,Double> vls;

    private final double number; // number part of polynomial

    public Polynomial(double d) {
        number = d;
        vls = Collections.emptyMap();
    }

    public Polynomial(String var) {
        number = 0;
        vls = Collections.singletonMap(new VarList(var), 1.0);
    }

    public Polynomial(double d, Object... terms) {
        number = 0;
        Map<VarList,Double> vars = new HashMap<>();
        for (int i = 0; i < terms.length; i += 2) {
            vars.put(new VarList((String[])terms[i+1]), (Double)terms[i]);
        }
        vls = Collections.unmodifiableMap(vars);
    }

    private Polynomial(double d, Map<VarList,Double> vars) {
        number = d;
        vls = Collections.unmodifiableMap(vars);
    }

    public Polynomial plus(Polynomial that) {
        Map<VarList,Double> added = new HashMap<>(this.vls);

        for (VarList vl : that.vls.keySet()) {
            added.merge(vl, that.vls.get(vl), Double::sum);
        }

        return new Polynomial(this.number + that.number, added);
    }

    public Polynomial times(Polynomial that) {
        double d = this.number * that.number;
        Map<VarList,Double> vars = new HashMap<>();

        for (VarList here : this.vls.keySet()) {
            for (VarList there : that.vls.keySet()) {
                vars.merge(new VarList(here, there), this.vls.get(here) * that.vls.get(there), Double::sum);
            }
        }
        if (this.number != 0) {
            for (VarList vl : that.vls.keySet()) {
                vars.merge(vl, that.vls.get(vl) * this.number, Double::sum);
            }
        }
        if (that.number != 0) {
            for (VarList vl : this.vls.keySet()) {
                vars.merge(vl, this.vls.get(vl) * that.number, Double::sum);
            }
        }

        return new Polynomial(d, vars);
    }
    
    public Polynomial evaluate(Map<String, Double> environment){
        Polynomial p = new Polynomial(number);
        for (VarList vl : vls.keySet()){
            p = p.plus(vl.evaluate(environment).times(new Polynomial(vls.get(vl))));
        }
        return p;
    }
    
    /**
     * Tells whether or not this Polynomial is a constant, i.e no variables have nonzero coefficients.
     * @return true if this Polynomial is a constant, false otherwise.
     */
    public boolean isConstant(){
        return this.equals(new Polynomial(number));
    }

    public Polynomial round(int precision) {
        Map<VarList,Double> rounded = new HashMap<>(this.vls);
        rounded.replaceAll((vl, d) -> round(d, precision));
        return new Polynomial(round(number, precision), rounded);
    }

    private double round(double d, int precision) {
        return new BigDecimal(d).setScale(precision, HALF_EVEN).doubleValue();
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof Polynomial && equals((Polynomial)that);
    }

    private boolean equals(Polynomial that) {
        return this.number == that.number && this.vls.equals(that.vls);
    }

    @Override
    public String toString() {
        if (vls.size() == 0) {
            return String.format("%f", number);
        }

        VarList[] vlArr = vls.keySet().toArray(new VarList[vls.size()]);
        Arrays.sort(vlArr);

        StringBuilder sb = new StringBuilder();
        int i = 0;
        if (number != 0) {
            sb.append(number);
        } else {
            double v = vls.get(vlArr[0]);
            if (v - 1 > 0.001 || 1 - v > 0.001) {
                sb.append(v);
                sb.append("*");
            }
            sb.append(vlArr[0].toString());
            i = 1;
        }
        for (; i < vlArr.length; i++) {
            sb.append(" + ");
            double v = vls.get(vlArr[i]);
            if (v - 1 > 0.001 || 1 - v > 0.001) {
                sb.append(v);
                sb.append("*");
            }
            sb.append(vlArr[i].toString());
        }

        return sb.toString();
    }

    public Polynomial differentiate(String d) {
        VarList[] vlArr = vls.keySet().toArray(new VarList[vls.size()]);
        Arrays.sort(vlArr);
        Polynomial p = new Polynomial(0);
        for (VarList vl : vlArr) {
            p = p.plus(vl.differentiate(d).times(new Polynomial(vls.get(vl))));
        }
        return p;
    }

    public boolean containsTerm(Polynomial that) {
        for (VarList vl : that.vls.keySet()) {
            if (vls.keySet().contains(vl)) { return true; }
        }
        return false;
    }

    static class VarList implements Comparable<VarList> {

        public final String[] vars;

        public VarList(String var) {
            vars = new String[] { var };
        }

        public VarList(String... vl) {
            vars = Arrays.copyOf(vl, vl.length);
            Arrays.sort(vars);
        }

        public VarList(VarList... vls) {
            LinkedList<String> list = new LinkedList<String>();
            for (VarList vl : vls) {
                for (String var : vl.vars) {
                    list.add(var);
                }
            }
            vars = list.toArray(new String[list.size()]);
            Arrays.sort(vars);
        }

        @Override
        public boolean equals(Object that) {
            return that instanceof VarList && equals((VarList)that);
        }

        public boolean equals(VarList that) {
            return Arrays.equals(this.vars, that.vars);
        }

        public Polynomial differentiate(String d) {
            if (vars.length == 1) {
                return new Polynomial(vars[0].equals(d) ? 1 : 0);
            }

            if (vars.length == 2) {
                Polynomial p = new Polynomial(0);
                if (vars[0].equals(d)) {
                    p = p.plus(new Polynomial(vars[1]));
                }
                if (vars[1].equals(d)) {
                    p = p.plus(new Polynomial(vars[0]));
                }
                return p;
            }

            Polynomial p = new Polynomial(0);
            if (vars[0].equals(d)) {
                p = p.plus(new Polynomial(vars[1]));
                for (int i = 2; i < vars.length; i++) {
                    p = p.times(new Polynomial(vars[i]));
                }
            }
            for (int i = 1; i < vars.length; i++) {
                if (vars[i].equals(d)) {
                    Polynomial q = new Polynomial(vars[0]);
                    for (int j = 1; j < vars.length; j++) {
                        if (j == i) continue;
                        q = q.times(new Polynomial(vars[j]));
                    }
                    p = p.plus(q);
                }
            }
            return p;
        }
        
        public Polynomial evaluate(Map<String,Double> environment){
            Polynomial p = new Polynomial(1);
            double evaluatedDouble = 1;
            for (String var : vars){
                boolean evaluated = false;
                for (String envVar: environment.keySet()){
                    if (var.equals(envVar)){
                        evaluatedDouble *= environment.get(envVar);
                        evaluated = true;
                        break;
                    }
                }
                if (!evaluated){
                    p = p.times(new Polynomial(var));
                }               
            }
            p = p.times(new Polynomial(evaluatedDouble));
            return p;
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(vars[0]);
            for (int i = 1; i < vars.length; i++) {
                sb.append("*" + vars[i]);
            }
            return sb.toString();
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public int compareTo(VarList that) {
            return this.toString().compareTo(that.toString());
        }
    }
}
