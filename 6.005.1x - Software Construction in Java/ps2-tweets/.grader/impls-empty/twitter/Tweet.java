package twitter;

import java.time.Instant;

/**
 * This immutable datatype represents a tweet from Twitter.
 * 
 * DO NOT CHANGE THIS CLASS.
 */
public class Tweet {

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

    private final long id;
    private final String author;
    private final String text;
    private final Instant timestamp;

    /**
     * Make a Tweet with a known unique id.
     * 
     * @param id
     *            unique identifier for the tweet, as assigned by Twitter.
     * @param author
     *            Twitter username who wrote this tweet.
     * @param text
     *            text of the tweet, at most 140 characters.
     * @param timestamp
     *            date/time when the tweet was sent.
     */
    public Tweet(final long id, final String author, final String text, final Instant timestamp) {
        this.id = id;
        this.author = author;
        this.text = text;
        this.timestamp = timestamp;
    }

    /**
     * @return unique identifier of this tweet
     */
    public long getId() {
        return id;
    }

    /**
     * @return Twitter username who wrote this tweet.
     *         A Twitter username is a nonempty sequence of letters (A-Z or
     *         a-z), digits, and underscores ("_").
     *         Twitter usernames are case-insensitive, so "rbmllr" and "RbMllr"
     *         are equivalent.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @return text of this tweet, at most 140 characters
     */
    public String getText() {
        return text;
    }

    /**
     * @return date/time when this tweet was sent
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /*
     * @see Object.toString()
     */
    @Override public String toString() {
        return "(" + this.getId()
                + " " + this.getTimestamp().toString()
                + " " + this.getAuthor()
                + ") " + this.getText();
    }

    /*
     * @see Object.equals()
     */
    @Override public boolean equals(Object thatObject) {
        if (!(thatObject instanceof Tweet)) {
            return false;
        }

        Tweet that = (Tweet) thatObject;
        return this.id == that.id;
    }

    /*
     * @see Object.hashCode()
     */
    @Override public int hashCode() {
        final int bitsInInt = 32;
        final int lower32bits = (int) id;
        final int upper32bits = (int) (id >> bitsInInt);
        return lower32bits ^ upper32bits;
    }
}
