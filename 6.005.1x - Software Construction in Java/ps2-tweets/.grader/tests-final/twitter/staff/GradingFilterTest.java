package twitter.staff;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import twitter.*;

public class GradingFilterTest {

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
    
    // Fri 14 Feb 2015 01:18:18 PM EST
    private static final Instant d1 = Instant.ofEpochMilli(1423937898000L);
    // Sat 15 Feb 2015 01:18:18 PM EST
    private static final Instant d2 = Instant.ofEpochMilli(1424024298000L);
    // Sun 16 Feb 2015 11:18:18 AM EST
    private static final Instant d3 = Instant.ofEpochMilli(1424103498000L);
    // Sun 16 Feb 2015 11:30:18 AM EST
    private static final Instant d4 = Instant.ofEpochMilli(1424104218000L);
    // Sun 16 Feb 2015 12:00:18 PM EST
    private static final Instant d5 = Instant.ofEpochMilli(1424106018000L);

    private static final Tweet tweet1 = new Tweet(0, "alyssa",
            "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(1, "alyssa",
            "rivest talk in 32123 seconds #hype", d2);
    private static final Tweet tweet3 = new Tweet(2, "BBitdiddle",
            "every day i'm hackin' it", d3);
    private static final Tweet tweet4 = new Tweet(3, "_reAsOnEr",
            "i know Rivest is an author of CLRS, but who else??", d4);
    private static final Tweet tweet5 = new Tweet(4, "reAsOnEr",
            "giving an optimization talk in 32-123 tomorrow", d5);

    private static final List<Tweet> tweets = Collections.unmodifiableList(
            Arrays.asList(tweet1, tweet2, tweet3, tweet4, tweet5));

    /**
     * Private tests for grading the Filter class.
     */

    /**
     * Tests for Filter.writtenBy
     */

    @Test
    public void testWrittenByEmptyList() {
        /*
         * An empty list of tweets should not return any tweets.
         */
        List<Tweet> filteredTweets = Filter.writtenBy(new ArrayList<Tweet>(),
                "test");

        assertTrue("expected empty list", filteredTweets.isEmpty());
    }

    @Test
    public void testWrittenByAuthorNotFound() {
        /*
         * If the author isn't found, no tweets should be returned.
         */
        List<Tweet> filteredTweets = Filter.writtenBy(tweets, "rbmllr");

        assertTrue("expected empty list", filteredTweets.isEmpty());
    }

    @Test
    public void testWrittenByAuthorFound() {
        /*
         * Checking to make sure tweets by authors are actually found.
         */
        List<Tweet> filteredTweets = Filter.writtenBy(tweets, "alyssa");
        List<Tweet> canonicalTweets = Arrays.asList(tweet1, tweet2);

        assertFalse("expected non-empty list", filteredTweets.isEmpty());
        assertEquals("list size", 2, filteredTweets.size());
        assertTrue("missing expected tweet(s)", filteredTweets.containsAll(canonicalTweets));
    }

    @Test
    public void testWrittenByMustBeCaseInsensitive() {
        /*
         * Checking to make sure case insensitivity is handled properly.
         */
        List<Tweet> filteredTweets = Filter.writtenBy(tweets, "bbitdiddle");

        assertFalse("expected non-empty list", filteredTweets.isEmpty());
        assertEquals("list size", 1, filteredTweets.size());
        assertTrue("missing expected tweet", filteredTweets.contains(tweet3));
    }

    @Test
    public void testWrittenByUnderscores() {
        /*
         * Checking to make sure underscores are handled properly.
         */
        List<Tweet> filteredTweets = Filter.writtenBy(tweets, "_reAsOnEr");

        assertFalse("expected non-empty list", filteredTweets.isEmpty());
        assertEquals("list size", 1, filteredTweets.size());
        assertTrue("missing expected tweet", filteredTweets.contains(tweet4));
    }

    /**
     * Tests for Filter.inTimespan
     */

    @Test
    public void testInTimespanEmptyList() {
        /*
         * If there aren't any tweets, there aren't any tweets in the timespan.
         */
        List<Tweet> filteredTweets = Filter.inTimespan(new ArrayList<Tweet>(),
                new Timespan(d1, d5));
        
        assertTrue("expected empty list", filteredTweets.isEmpty());
    }

    @Test
    public void testInTimespanNoTweetsInSpan() {
        /*
         * If no tweets fall in the timespan, then none should be returned.
         */
        Instant now = Instant.now();
        List<Tweet> filteredTweets = Filter.inTimespan(tweets, new Timespan(
                now, now));

        assertTrue("expected empty list", filteredTweets.isEmpty());
    }

    @Test
    public void testInTimespanSingleTweetTimespanInexact() {
        /*
         * Function should return the single tweet that is in the timespan.
         */

        // Dates are d3 +/- 10 seconds
        Instant start = Instant.ofEpochMilli(1424103488000L);
        Instant end = Instant.ofEpochMilli(1424103508000L);
        List<Tweet> filteredTweets = Filter.inTimespan(tweets, new Timespan(start, end));

        assertFalse("expected non-empty list", filteredTweets.isEmpty());
        assertEquals("list size", 1, filteredTweets.size());
        assertTrue("missing expected tweet", filteredTweets.contains(tweet3));
    }

    @Test
    public void testInTimespanSingleTweetTimespanExact() {
        /*
         * Function should return the single tweet in the timespan, even if the
         * timespan's start and end are the tweet's timestamp.
         */
        List<Tweet> filteredTweets = Filter.inTimespan(tweets, new Timespan(d3,
                d3));

        assertFalse("expected non-empty list", filteredTweets.isEmpty());
        assertEquals("list size", 1, filteredTweets.size());
        assertTrue("missing expected tweet", filteredTweets.contains(tweet3));
    }

    @Test
    public void testInTimespanMultipleTweetsInexact() {
        /*
         * Ensuring that multiple tweets are correctly filtered.
         */
        // Dates are: d2 - 10 seconds and d4 + 10 seconds
        Instant start = Instant.ofEpochMilli(1424024288000L);
        Instant end = Instant.ofEpochMilli(1424104318000L);
        List<Tweet> filteredTweets = Filter.inTimespan(tweets, new Timespan(start, end));
        List<Tweet> canonicalTweets = Arrays.asList(tweet2, tweet3, tweet4);

        assertFalse("expected non-empty list", filteredTweets.isEmpty());
        assertEquals("list size", 3, filteredTweets.size());
        assertTrue("missing expected tweet(s)", filteredTweets.containsAll(canonicalTweets));
    }

    @Test
    public void testInTimespanMultipleTweetsExact() {
        /*
         * Ensuring that multiple tweets are correctly filtered with exact
         * timestamps.
         */
        List<Tweet> filteredTweets = Filter.inTimespan(tweets, new Timespan(d2,
                d4));
        List<Tweet> canonicalTweets = Arrays.asList(tweet2, tweet3, tweet4);

        assertFalse("expected non-empty list", filteredTweets.isEmpty());
        assertEquals("list size", 3, filteredTweets.size());
        assertTrue("missing expected tweet(s)", filteredTweets.containsAll(canonicalTweets));
    }
    
    @Test
    public void testInTimespanTweetsOutOfOrder() {
        /*
         * Ensuring the functions do not rely on the order of tweets.
         */
        
        List<Tweet> filteredTweets = Filter.inTimespan(Arrays.asList(tweet1, tweet5, tweet2), new Timespan(d1, d2));
        List<Tweet> canonicalTweets = Arrays.asList(tweet1, tweet2);
        
        assertFalse("expected non-empty list", filteredTweets.isEmpty());
        assertEquals("list size", 2, filteredTweets.size());
        assertTrue("missing expected tweet(s)", filteredTweets.containsAll(canonicalTweets));
    }

    /**
     * Tests for Filter.containing
     */

    @Test
    public void testContainingEmptyList() {
        /*
         * Empty tweets -> return nothing
         */
        List<Tweet> filteredTweets = Filter.containing(new ArrayList<Tweet>(),
                Arrays.asList("test"));

        assertTrue("expected empty list", filteredTweets.isEmpty());
    }
    
    @Test
    public void testContainingNoWordsToCheck() {
        /*
         * Comparing tweets against no words -> return nothing
         */
        List<Tweet> filteredTweets = Filter.containing(tweets,
                new ArrayList<String>());

        assertTrue("expected empty list", filteredTweets.isEmpty());
    }


    @Test
    public void testContainingNoResults() {
        /*
         * No tweets containing keywords -> return nothing
         */
        List<Tweet> filteredTweets = Filter.containing(tweets,
                Arrays.asList("qwerty12345"));

        assertTrue("expected empty list", filteredTweets.isEmpty());
    }

    @Test
    public void testContainingSingleSearchWordSingleResult() {
        /*
         * Single term, single result. Shouldn't see anything strange.
         */
        List<Tweet> filteredTweets = Filter.containing(tweets,
                Arrays.asList("optimization"));

        assertFalse("expected non-empty list", filteredTweets.isEmpty());
        assertEquals("list size", 1, filteredTweets.size());
        assertTrue("missing expected tweet", filteredTweets.contains(tweet5));
    }

    @Test
    public void testContainingSingleSearchWordMultipleResult() {
        /*
         * Single term, multiple results. All of them should come back to us.
         */
        List<Tweet> filteredTweets = Filter.containing(tweets,
                Arrays.asList("talk"));

        assertFalse("expected non-empty list", filteredTweets.isEmpty());
        assertEquals("list size", 3, filteredTweets.size());
        assertTrue("missing expected tweet", filteredTweets.contains(tweet1));
        assertTrue("missing expected tweet", filteredTweets.contains(tweet2));
        assertTrue("missing expected tweet", filteredTweets.contains(tweet5));
    }

    @Test
    public void testContainingMustBeCaseInsensitive() {
        /*
         * Testing case insensitive searching.
         */
        List<Tweet> filteredTweets = Filter.containing(tweets,
                Arrays.asList("AUTHOR"));

        assertFalse("expected non-empty list", filteredTweets.isEmpty());
        assertEquals("list size", 1, filteredTweets.size());
        assertTrue("missing expected tweet", filteredTweets.contains(tweet4));
    }

    @Test
    public void testContainingPunctuation() {
        /*
         * Words are not terminated by punctuation characters, as per the spec -
         * string termination only.
         */
        List<Tweet> filteredTweets = Filter.containing(tweets, Arrays.asList("32-123"));
        
        assertFalse("expected non-empty list", filteredTweets.isEmpty());
        assertEquals("list size", 1, filteredTweets.size());
        assertTrue("missing expected tweet", filteredTweets.contains(tweet5));
    }

    @Test
    public void testContainingMultipleSearchWords() {
        /*
         * Testing multiple terms that occur in order. We should not see any
         * terms that match only a single term.
         */
        List<Tweet> filteredTweets = Filter.containing(tweets,
                Arrays.asList("optimization", "talk"));

        assertFalse("expected non-empty list", filteredTweets.isEmpty());
        assertEquals("list size", 3, filteredTweets.size());
        assertTrue("missing expected tweet", filteredTweets.contains(tweet1));
        assertTrue("missing expected tweet", filteredTweets.contains(tweet2));
        assertTrue("missing expected tweet", filteredTweets.contains(tweet5));
    }

    // NOT NEEDED FOR OR BUT IT IS NEEDED FOR AND (of words to search for)
//    @Test
//    public void testContainingMultipleSearchWordsOutOfOrder() {
//        /*
//         * Ensuring that out-of-order terms are caught as well.
//         */
//        List<Tweet> filteredTweets = Filter.containing(tweets,
//                Arrays.asList("rivest", "talk"));
//
//        assertFalse("expected non-empty list", filteredTweets.isEmpty());
//        assertEquals("list size", 2, filteredTweets.size());
//        assertTrue("missing expected tweet", filteredTweets.contains(tweet1));
//        assertTrue("missing expected tweet", filteredTweets.contains(tweet2));
//    }
    
    @Test
    public void testContainingNoFullWordMatch() {
        /*
         * Ensuring that substring matches aren't returned.
         */
        List<Tweet> filteredTweets = Filter.containing(tweets,
                Arrays.asList("hack"));
        
        assertTrue("expected empty list", filteredTweets.isEmpty());
        
    }
}
