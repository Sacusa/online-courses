package expressivo.staff;

/**
 * Token types, used by lexer and parser.
 */
public enum Type {
    LEFT_PARENTHESIS,
    RIGHT_PARENTHESIS,
    NUMBER,
    PLUS,
    TIMES,
    VARIABLE,
    END_OF_INPUT;
    
    public String toWords() {
        return this.name().toLowerCase();
    }
}
