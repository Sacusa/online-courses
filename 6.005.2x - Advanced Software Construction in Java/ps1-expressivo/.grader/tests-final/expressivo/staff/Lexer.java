package expressivo.staff;

import static expressivo.staff.Type.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Expression lexical analyzer.
 * Performs tokenization in a single pass with a single character look-ahead.
 */
public class Lexer {

    /** Token in the stream. */
    static class Token {
        /** Type of this token. */
        public final Type type;

        /** The text of this token. */
        public final String text;

        /** Value of numbers. */
        public final Double value;

        /**
         * Creates a new token with the passed type and text.
         * @param type The type.
         * @param text The text.
         */
        Token(String text) {
            this.type = VARIABLE;
            this.text = text;
            this.value = Double.NaN;
        }

        /**
         * Creates a new 
         * @param type
         * @param value
         */
        Token(Double value) {
            this.type = NUMBER;
            this.text = "";
            this.value = value;
        }

        /**
         * Creates a new token of the passed type with no text.  Same as calling
         * {@code new Type(type, null)}.
         * @param type The type.
         */
        Token(Type type) {
            this.type = type;
            this.text = "";
            this.value = Double.NaN;
        }

        public String toString() {
            if (type == VARIABLE) {
                return text;
            } else if (type == NUMBER) {
                return value.toString();
            } else {
                return type.toWords();
            }
        }

        /**
         * Determines if this object is equal to that object.
         * @param that That object.
         * @return Whether they're equal.
         */
        public boolean equals(Object that) {
            return that instanceof Token && equals((Token)that);
        }

        /**
         * Determines if this Token is equal to that token.
         * @param that That token.
         * @return Whether they're equal.
         */
        public boolean equals(Token that) {
            return this.type == that.type &&
                this.text.equals(that.text) &&
                this.value.equals(that.value);

        }
    }

    /** End of file token returned when end of file is reached. */
    public static final Token EOF_TOKEN = new Token(END_OF_INPUT);

    /** Place to read the file from. */
    private final BufferedReader reader;

    /** Next character to start lexing at. */
    private int next;

    /** Position of last token returned */
    private int last;

    /** Position we are in the input. */
    private int pos = 0;

    private final boolean simplified;

    /**
     * Creates a new Lexer over the input.
     * @param input The input string.
     * @param simplified Whether the input is in simplified form.
     * @throws IOException If the string can't be read for some reason.
     */
    public Lexer(String input, boolean simplified) {
        this.simplified = simplified;
        StringReader sr = new StringReader(input);
        reader = new BufferedReader(sr);
        skipToNextCharacter();
    }

    /**
     * Returns the position of the last character returned.
     * @return The last position.
     */
    public int last() { return last-1; }

    /**
     * Updates {@link #next} to the next element in the input.
     * @throws IOException If the next char can't be read.
     */
    private void skipToNextCharacter() {
        // Read characters until we get one that isn't whitespace.
        do {
            getNext();
        } while (Character.isWhitespace(next));
    }

    private void getNext() {
        try {
            next = reader.read();
            pos++;
        } catch (IOException ioe) {
            throw new BadCharacterError(pos, ioe);
        }
    }

    /**
     * Encapsulation of a method for deciding whether a character satisfies a
     * predicate.
     */
    private interface CharacterSelector {
        public boolean matches(int n);
    }

    /** Determines if a character is a digit. */
    private static CharacterSelector digitSelector = new CharacterSelector() {
        @Override public boolean matches(int n) {
            return Character.isDigit(n) || (char)n == '.';
        }
    };

    /** Determines if something is a letter. */
    private static CharacterSelector letterSelector = new CharacterSelector() {
        @Override public boolean matches(int n) { return Character.isLetter(n); }
    };

    /**
     * Reads the longest character sequence, starting at the current position,
     * such that all member characters satisfy a predicate.
     * @param selector The predicate that characters must satisfy.
     */
    private String readSequence(CharacterSelector selector) {
        final StringBuilder sb = new StringBuilder();

        assert selector.matches(next) : "readSequence: !pred(next)";

            do {
                sb.append((char)next);
                getNext();
            } while (selector.matches(next));

            if (Character.isWhitespace(next)) {
                skipToNextCharacter();
            }
            return sb.toString();
    }

    /**
     * Generates a new token from the input stream.
     * @return the next token from the input stream or null if there is none.
     * @throws BadCharacterError If no token can be matched to the input.
     */
    Token next() {
        last = pos;

        switch (next) {
            case -1:
                return EOF_TOKEN;
            case '(':
                if (simplified) { throw new BadCharacterError(pos); }
                skipToNextCharacter();
                return new Token(LEFT_PARENTHESIS);
            case ')':
                if (simplified) { throw new BadCharacterError(pos); }
                skipToNextCharacter();
                return new Token(RIGHT_PARENTHESIS);
            case '+':
                skipToNextCharacter();
                return new Token(PLUS);
            case '*':
                skipToNextCharacter();
                return new Token(TIMES);
        }

        // Remaining legal case: a sequence of either digits (numeric constant)
        // or letters (variable), which we read into this buffer
        if (digitSelector.matches(next)) {
            return new Token(Double.parseDouble(readSequence(digitSelector)));
        } else if (letterSelector.matches(next)) {
            return new Token(readSequence(letterSelector));
        }

        throw new BadCharacterError(pos);
    }
}
