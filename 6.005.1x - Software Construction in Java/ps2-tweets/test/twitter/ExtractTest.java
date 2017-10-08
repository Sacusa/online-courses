package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;

public class ExtractTest {

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");

    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));

        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }

    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));

        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    /*
     * Testing strategy for Extract.getTimespan()
     * 
     * Partition the inputs as follows:
     * tweets.size() 0, 1, > 1
     * 
     * Exhaustive Cartesian coverage of partitions.
     */

    // covers tweets.size() = 0
    //        tweets.getTimestamp().getEpochSecond() does not matter
    @Test
    public void timespanTweetsZero() {
        List<Tweet> tweets = new ArrayList<Tweet>();

        Timespan timespan = Extract.getTimespan(tweets);
        assertTrue(timespan.getStart().equals(timespan.getEnd()));
    }

    // covers tweets.size() = 1
    @Test
    public void timespanTweetsOne() {
        Instant date = Instant.parse("1980-06-26T10:00:00Z");
        Tweet tweet = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", date);
        Timespan timespan = new Timespan(Instant.parse("1980-06-26T10:00:00Z"), Instant.parse("1980-06-26T10:00:00Z"));

        List<Tweet> tweets = new ArrayList<Tweet>();
        tweets.add(tweet);

        assertEquals(timespan, Extract.getTimespan(tweets));
    }

    // covers tweets.size() = 20 (> 1)
    @Test
    public void timespanTweetsMany() {
        Instant date = Instant.parse("1980-06-26T10:00:00Z");

        List<Tweet> tweets = new ArrayList<Tweet>();
        for (int i = 0; i < 19; ++i) {
            Tweet tweet = new Tweet(i + 1, "alyssa", "is it reasonable to talk about rivest so much?", date.plusSeconds(10 * i));
            tweets.add(tweet);
        }
        Tweet tweet = new Tweet(20, "alyssa", "is it reasonable to talk about rivest so much?", date);
        tweets.add(tweet);

        Timespan timespan = new Timespan(date, date.plusSeconds(180));

        assertEquals(timespan, Extract.getTimespan(tweets));
    }

    // covers tweets.size() = 1
    @Test
    public void timespanMultipleSameTimespan() {
        Instant date = Instant.parse("1980-06-26T10:00:00Z");

        List<Tweet> tweets = new ArrayList<Tweet>();
        for (int i = 0; i < 20; ++i) {
            Tweet tweet = new Tweet(i + 1, "alyssa", "is it reasonable to talk about rivest so much?", date);
            tweets.add(tweet);
        }

        Timespan timespan = new Timespan(date, date);

        assertEquals(timespan, Extract.getTimespan(tweets));
    }
    
    /*
     * Testing strategy for Extract.getMentionedUsers()
     * 
     * Partition the inputs as follows:
     * tweets.size() 0, 1, > 1
     * users.size() 0, 1, > 1
     * 
     * Exhaustive Cartesian coverage of partitions.
     */

    // covers tweets.size() = 0
    //        users.size() does not matter
    @Test
    public void usersTweetsZero() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        Set<String> users = new HashSet<String>();

        assertEquals(users, Extract.getMentionedUsers(tweets));
    }

    // covers tweets.size() = 1
    //        users.size() = 0
    @Test
    public void usersTweetsOneUsersZero() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        Set<String> users = new HashSet<String>();

        tweets.add(tweet1);

        assertEquals(users, Extract.getMentionedUsers(tweets));
    }

    // covers tweets.size() = 1
    //        users.size() = 1
    @Test
    public void usersTweetsOneUsersOne() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        Set<String> users = new HashSet<String>();

        Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes @hype", d2);

        tweets.add(tweet2);
        users.add("hype");

        Set<String> answer = Extract.getMentionedUsers(tweets);
        String [] answerArray = answer.toArray(new String[0]);
        answer = new HashSet<String>();

        for (int i = 0; i < answerArray.length; ++i) {
            String mentionedUser = answerArray[i].toLowerCase();

            // check for duplicate entry
            if (answer.contains(mentionedUser)) {
                fail();
            }

            answer.add(mentionedUser);
        }

        assertTrue(users.equals(answer));
    }

    // covers tweets.size() = 3 (> 1)
    //        users.size() = 0
    @Test
    public void usersTweetsManyUsersZero() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        Set<String> users = new HashSet<String>();

        Tweet tweet2 = new Tweet(1, "david", "is it @ reasonable to talk about rivest so much?", d1);
        Tweet tweet3 = new Tweet(2, "chang", "Yes it is!", d2);

        tweets.add(tweet1);
        tweets.add(tweet2);
        tweets.add(tweet3);

        assertEquals(users, Extract.getMentionedUsers(tweets));
    }

    // covers tweets.size() = 3 (> 1)
    //        users.size() = 1
    @Test
    public void usersTweetsManyUsersOne() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        Set<String> users = new HashSet<String>();

        Tweet tweet1 = new Tweet(1, "david", "is it reasonable @mit.edu to talk about rivest so much?", d1);
        Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest @hype talk in 30 minutes @hype", d2);
        Tweet tweet3 = new Tweet(3, "chang", "Yes @HyPe it is!", d2);

        tweets.add(tweet1);
        tweets.add(tweet2);
        tweets.add(tweet3);

        users.add("hype");
        users.add("mit");

        Set<String> answer = Extract.getMentionedUsers(tweets);
        String [] answerArray = answer.toArray(new String[0]);
        answer = new HashSet<String>();

        for (int i = 0; i < answerArray.length; ++i) {
            String mentionedUser = answerArray[i].toLowerCase();

            // check for duplicate entry
            if (answer.contains(mentionedUser)) {
                fail();
            }

            answer.add(mentionedUser);
        }

        assertTrue(users.equals(answer));
    }

    // covers tweets.size() = 3 (> 1)
    //        users.size() = 2 (1 < users.size() <= tweets.size())
    @Test
    public void usersTweetsManyUsersMany() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        Set<String> users = new HashSet<String>();

        Tweet tweet1 = new Tweet(1, "david", "is it reasonable @HyPe to talk about rivest so much?", d1);
        Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest @david talk in 30 minutes @hype", d2);
        Tweet tweet3 = new Tweet(3, "chang", "Yes, @david it is!", d2);

        tweets.add(tweet1);
        tweets.add(tweet2);
        tweets.add(tweet3);

        users.add("hype");
        users.add("david");

        Set<String> answer = Extract.getMentionedUsers(tweets);
        String [] answerArray = answer.toArray(new String[0]);
        answer = new HashSet<String>();

        for (int i = 0; i < answerArray.length; ++i) {
            String mentionedUser = answerArray[i].toLowerCase();

            // check for duplicate entry
            if (answer.contains(mentionedUser)) {
                fail();
            }

            answer.add(mentionedUser);
        }

        assertTrue(users.equals(answer));
    }

    /*
     * END OF MY TESTS
     */

    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
