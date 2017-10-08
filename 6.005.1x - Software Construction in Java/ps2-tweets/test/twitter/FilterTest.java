package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import org.junit.Test;

public class FilterTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * Make sure you have partitions.
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }
    
    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }
    
    @Test
    public void testContaining() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }
    
    
    /*
     * Testing strategy for Filter.writtenBy()
     * 
     * Partition the inputs as follows:
     * tweets.size() 0, 1, > 1
     * return tweets.size() 0, 1, > 1
     * 
     * Exhaustive Cartesian coverage of partitions.
     */
    
    // covers tweets.size() = 0
    //        return tweets.size() = 0
    @Test
    public void writtenTweetsZeroReturnZero() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        
        assertEquals(tweets, Filter.writtenBy(tweets, "A-b_C"));
    }
    
    // covers tweets.size() = 1
    //        return tweets.size() = 0
    @Test
    public void writtenTweetsOneReturnZero() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<Tweet> returnTweets = new ArrayList<Tweet>();
        
        tweets.add(tweet1);
        
        assertEquals(returnTweets, Filter.writtenBy(tweets, "A-b_C"));
    }
    
    // covers tweets.size() = 1
    //        return tweets.size() = 1
    @Test
    public void writtenTweetsOneReturnOne() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<Tweet> returnTweets = new ArrayList<Tweet>();
        
        tweets.add(tweet1);
        returnTweets.add(tweet1);
        
        assertEquals(returnTweets, Filter.writtenBy(tweets, "AlYsSa"));
    }
    
    // covers tweets.size() = 5 (> 1)
    //        return tweets.size() = 0
    @Test
    public void writtenTweetsManyReturnZero() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<Tweet> returnTweets = new ArrayList<Tweet>();
        
        Tweet tweet3 = new Tweet(3, "alyssa", "is it interesting?", d1);
        Tweet tweet4 = new Tweet(4, "belittle", "yes it is! #hype", d2);
        Tweet tweet5 = new Tweet(5, "alyssa", "Oh yeah!", d1);
        
        tweets.add(tweet1);
        tweets.add(tweet2);
        tweets.add(tweet3);
        tweets.add(tweet4);
        tweets.add(tweet5);
        
        assertEquals(returnTweets, Filter.writtenBy(tweets, "A-b_C"));
    }
    
    // covers tweets.size() = 5 (> 1)
    //        return tweets.size() = 1
    @Test
    public void writtenTweetsManyReturnOne() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<Tweet> returnTweets = new ArrayList<Tweet>();
        
        Tweet tweet3 = new Tweet(3, "alyssa", "is it interesting?", d1);
        Tweet tweet4 = new Tweet(4, "belittle", "yes it is! #hype", d2);
        Tweet tweet5 = new Tweet(5, "alyssa", "Oh yeah!", d1);
        
        tweets.add(tweet1);
        tweets.add(tweet2);
        tweets.add(tweet3);
        tweets.add(tweet4);
        tweets.add(tweet5);
        returnTweets.add(tweet4);
        
        assertEquals(returnTweets, Filter.writtenBy(tweets, "belittle"));
    }
    
    // covers tweets.size() = 5 (> 1)
    //        return tweets.size() = 3 (> 1)
    @Test
    public void writtenTweetsManyReturnMany() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<Tweet> returnTweets = new ArrayList<Tweet>();
        
        Tweet tweet3 = new Tweet(3, "alyssa", "is it interesting?", d1);
        Tweet tweet4 = new Tweet(4, "belittle", "yes it is! #hype", d2);
        Tweet tweet5 = new Tweet(5, "alyssa", "Oh yeah!", d1);
        
        tweets.add(tweet1);
        tweets.add(tweet2);
        tweets.add(tweet3);
        tweets.add(tweet4);
        tweets.add(tweet5);
        returnTweets.add(tweet1);
        returnTweets.add(tweet3);
        returnTweets.add(tweet5);
        
        assertEquals(returnTweets, Filter.writtenBy(tweets, "alyssa"));
    }
    
    /*
     * Testing strategy for Filter.inTimespan()
     * 
     * Partition the inputs as follows:
     * tweets.size() 0, 1, > 1
     * return tweets.size() 0, 1, > 1
     * 
     * timespan.getStart() < timespan.getEnd()
     * timespan.getStart() = timespan.getEnd()
     * 
     * Exhaustive Cartesian coverage of partitions.
     */
    
    // covers tweets.size() = 0
    //        return tweets.size() = 0
    @Test
    public void timespanTweetsZeroReturnZero() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        Timespan timespan = new Timespan(Instant.parse("2016-02-17T09:00:00Z"), Instant.parse("2016-02-17T12:00:00Z"));
        
        assertEquals(tweets, Filter.inTimespan(tweets, timespan));
    }
    
    // covers tweets.size() = 1
    //        return tweets.size() = 0
    //        timespan.getStart() = timespan.getEnd()
    @Test
    public void timespanTweetsOneReturnZero() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        Timespan timespan = new Timespan(Instant.parse("2016-02-17T09:00:00Z"), Instant.parse("2016-02-17T09:00:00Z"));
        List<Tweet> returnTweets = new ArrayList<Tweet>();
        
        tweets.add(tweet1);
        
        assertEquals(returnTweets, Filter.inTimespan(tweets, timespan));
    }
    
    // covers tweets.size() = 1
    //        return tweets.size() = 1
    //        timespan.getStart() < timespan.getEnd()
    @Test
    public void timespanTweetsOneReturnOne() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        Timespan timespan = new Timespan(Instant.parse("2016-02-17T09:00:00Z"), Instant.parse("2016-02-17T12:00:00Z"));
        List<Tweet> returnTweets = new ArrayList<Tweet>();
        
        tweets.add(tweet1);
        returnTweets.add(tweet1);
        
        assertEquals(returnTweets, Filter.inTimespan(tweets, timespan));
    }
    
    // covers tweets.size() = 5 (> 1)
    //        return tweets.size() = 0
    //        timespan.getStart() = timespan.getEnd()
    @Test
    public void timespanTweetsManyReturnZero() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        Timespan timespan = new Timespan(Instant.parse("2016-02-17T09:00:00Z"), Instant.parse("2016-02-17T09:00:00Z"));
        List<Tweet> returnTweets = new ArrayList<Tweet>();
        
        Tweet tweet3 = new Tweet(3, "alyssa", "is it interesting?", d1);
        Tweet tweet4 = new Tweet(4, "belittle", "yes it is! #hype", d2);
        Tweet tweet5 = new Tweet(5, "alyssa", "Oh yeah!", d1);
        
        tweets.add(tweet1);
        tweets.add(tweet2);
        tweets.add(tweet3);
        tweets.add(tweet4);
        tweets.add(tweet5);
        
        assertEquals(returnTweets, Filter.inTimespan(tweets, timespan));
    }
    
    // covers tweets.size() = 5 (> 1)
    //        return tweets.size() = 1
    //        timespan.getStart() = timespan.getEnd()
    @Test
    public void timespanTweetsManyReturnOne() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        Timespan timespan = new Timespan(d1, d1);
        List<Tweet> returnTweets = new ArrayList<Tweet>();
        
        Tweet tweet3 = new Tweet(3, "alyssa", "is it interesting?", d2);
        Tweet tweet4 = new Tweet(4, "belittle", "yes it is! #hype", d2);
        Tweet tweet5 = new Tweet(5, "alyssa", "Oh yeah!", d2);
        
        tweets.add(tweet1);
        tweets.add(tweet2);
        tweets.add(tweet3);
        tweets.add(tweet4);
        tweets.add(tweet5);
        returnTweets.add(tweet1);
        
        assertEquals(returnTweets, Filter.inTimespan(tweets, timespan));
    }
    
    // covers tweets.size() = 5 (> 1)
    //        return tweets.size() = 3 (> 1)
    //        timespan.getStart() > timespan.getEnd()
    @Test
    public void timespanTweetsManyReturnMany() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        Timespan timespan = new Timespan(Instant.parse("2016-02-17T09:00:00Z"), Instant.parse("2016-02-17T10:30:00Z"));
        List<Tweet> returnTweets = new ArrayList<Tweet>();
        
        Tweet tweet3 = new Tweet(3, "alyssa", "is it interesting?", d1);
        Tweet tweet4 = new Tweet(4, "belittle", "yes it is! #hype", d2);
        Tweet tweet5 = new Tweet(5, "alyssa", "Oh yeah!", d1);
        
        tweets.add(tweet1);
        tweets.add(tweet2);
        tweets.add(tweet3);
        tweets.add(tweet4);
        tweets.add(tweet5);
        returnTweets.add(tweet1);
        returnTweets.add(tweet3);
        returnTweets.add(tweet5);
        
        assertEquals(returnTweets, Filter.inTimespan(tweets, timespan));
    }
    
    /*
     * Testing strategy for Filter.containing()
     * 
     * Partition the inputs as follows:
     * tweets.size() 0, 1, > 1
     * words.size() 0, 1, > 1
     * 
     * Exhaustive Cartesian coverage of partitions.
     */
    
    // covers tweets.size() = 0
    //        words.size() does not matter
    @Test
    public void containingTweetsZero() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<String> words = new ArrayList<String>();
        
        words.add("test");
        
        assertEquals(tweets, Filter.containing(tweets, words));
    }
    
    // covers tweets.size() = 1
    //        words.size() = 0
    @Test
    public void containingTweetsOneWordsZero() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<String> words = new ArrayList<String>();
        List<Tweet> returnTweets = new ArrayList<Tweet>();
        
        tweets.add(tweet1);
        
        assertEquals(returnTweets, Filter.containing(tweets, words));
    }
    
    // covers tweets.size() = 1
    //        words.size() = 1
    @Test
    public void containingTweetsOneWordsOne() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<String> words = new ArrayList<String>();
        List<Tweet> returnTweets = new ArrayList<Tweet>();
        
        tweets.add(tweet1);
        words.add("vest");
        
        assertEquals(returnTweets, Filter.containing(tweets, words));
    }
    
    // covers tweets.size() = 1
    //        words.size() = 5 (> 1)
    @Test
    public void containingTweetsOneWordsMany() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<String> words = new ArrayList<String>();
        List<Tweet> returnTweets = new ArrayList<Tweet>();
        
        tweets.add(tweet1);
        words.add("test1");
        words.add("test2");
        words.add("test3");
        words.add("test4");
        
        words.add("ReAsOnAbLe");
        
        returnTweets.add(tweet1);
        
        assertEquals(returnTweets, Filter.containing(tweets, words));
    }
    
    // covers tweets.size() = 5 (> 1)
    //        words.size() = 0
    @Test
    public void containingTweetsManyWordsZero() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<String> words = new ArrayList<String>();
        List<Tweet> returnTweets = new ArrayList<Tweet>();
        
        Tweet tweet3 = new Tweet(3, "alyssa", "is it interesting?", d1);
        Tweet tweet4 = new Tweet(4, "belittle", "yes it is! #hype", d2);
        Tweet tweet5 = new Tweet(5, "alyssa", "Oh yeah!", d1);
        
        tweets.add(tweet1);
        tweets.add(tweet2);
        tweets.add(tweet3);
        tweets.add(tweet4);
        tweets.add(tweet5);
        
        assertEquals(returnTweets, Filter.containing(tweets, words));
    }
    
    // covers tweets.size() = 5 (> 1)
    //        words.size() = 1
    @Test
    public void containingTweetsManyWordsOne() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<String> words = new ArrayList<String>();
        List<Tweet> returnTweets = new ArrayList<Tweet>();
        
        Tweet tweet3 = new Tweet(3, "alyssa", "is it interesting?", d1);
        Tweet tweet4 = new Tweet(4, "belittle", "yes it is! #hype", d2);
        Tweet tweet5 = new Tweet(5, "alyssa", "Oh yeah!", d1);
        
        tweets.add(tweet1);
        tweets.add(tweet2);
        tweets.add(tweet3);
        tweets.add(tweet4);
        tweets.add(tweet5);
        
        words.add("YeS");
        
        returnTweets.add(tweet4);
        
        assertEquals(returnTweets, Filter.containing(tweets, words));
    }
    
    // covers tweets.size() = 5 (> 1)
    //        words.size() = 5 (> 1)
    @Test
    public void containingTweetsManyWordsMany() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<String> words = new ArrayList<String>();
        List<Tweet> returnTweets = new ArrayList<Tweet>();
        
        Tweet tweet3 = new Tweet(3, "alyssa", "is it interesting?", d1);
        Tweet tweet4 = new Tweet(4, "belittle", "yes it is! #hype", d2);
        Tweet tweet5 = new Tweet(5, "alyssa", "Oh yeah!", d1);
        
        tweets.add(tweet1);
        tweets.add(tweet2);
        tweets.add(tweet3);
        tweets.add(tweet4);
        tweets.add(tweet5);
        
        words.add("test1");
        words.add("test2");
        words.add("test3");
        words.add("test4");
        words.add("test5");
        
        assertEquals(returnTweets, Filter.containing(tweets, words));
    }
    
    /*
     * END OF MY TESTS
     */

    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
