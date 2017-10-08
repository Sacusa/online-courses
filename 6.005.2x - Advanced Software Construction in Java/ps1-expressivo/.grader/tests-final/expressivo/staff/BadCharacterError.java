package expressivo.staff;

public class BadCharacterError extends RuntimeException {
    private static final long serialVersionUID = 98205848315889545L;

    public BadCharacterError(int pos) {
        super("Unrecognized character at position " + pos);
    }

    public BadCharacterError(int pos, Throwable cause) {
        super("Unrecognized character at position " + pos, cause);
    }
}
