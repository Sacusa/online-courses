package expressivo.staff;

import expressivo.staff.Lexer.Token;

public class SyntaxError extends RuntimeException {

    private static final long serialVersionUID = 5076829635500863052L;

    public SyntaxError(String msg) { super(msg); }

    public SyntaxError(String msg, Throwable cause) { super(msg, cause); }

    public SyntaxError(int pos, Type expected, Token actual) {
        super(String.format("Character %d: expected %s, got %s",
                    pos, expected.toWords(), actual));
    }
}
